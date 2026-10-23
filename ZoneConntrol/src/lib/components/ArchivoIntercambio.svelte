<script lang="ts">
	import { onMount } from 'svelte';
	import { apiFetch, apiJson, getApiError } from '../auth';

	interface Reporte {
		departamento: string;
		total_intentos: number;
		autorizados: number;
		denegados: number;
		no_registrados: number;
	}

	interface Exportacion {
		id_exportacion: number;
		fecha_hora_generacion: string;
		periodo_inicio: string;
		periodo_fin: string;
		formato: string;
		correo_destino: string | null;
		nombre_usuario: string | null;
	}

	let desde = $state('');
	let hasta = $state('');
	let reporte = $state<Reporte[]>([]);
	let periodoGenerado = $state('');
	let formato = $state<'CSV' | 'XLSX'>('CSV');
	let correoDestino = $state('');
	let cargando = $state(false);
	let exportando = $state(false);
	let enviando = $state(false);
	let exportaciones = $state<Exportacion[]>([]);
	let cargandoExportaciones = $state(false);
	let errorExportaciones = $state('');
	let error = $state('');
	let mensaje = $state('');

	const reporteDisponible = $derived(
		reporte.length > 0 && periodoGenerado === `${desde}|${hasta}`
	);

	function datosPeriodo() {
		return { periodo_inicio: desde, periodo_fin: hasta };
	}

	async function cargarExportaciones() {
		cargandoExportaciones = true;
		errorExportaciones = '';
		try {
			exportaciones = await apiJson<Exportacion[]>('/archivo-intercambio/exportaciones');
		} catch (cause) {
			errorExportaciones =
				cause instanceof Error ? cause.message : 'No se pudo cargar el historial de reportes.';
		} finally {
			cargandoExportaciones = false;
		}
	}

	onMount(() => {
		void cargarExportaciones();
	});

	async function generar() {
		if (cargando || exportando || enviando) return;
		if (!desde || !hasta) {
			error = 'Seleccione las fechas de inicio y fin del periodo.';
			return;
		}
		if (desde > hasta) {
			error = 'La fecha de inicio no puede ser posterior a la fecha de fin.';
			return;
		}
		cargando = true;
		error = '';
		mensaje = '';
		try {
			reporte = await apiJson<Reporte[]>('/archivo-intercambio/generar', {
				method: 'POST',
				body: JSON.stringify(datosPeriodo())
			});
			periodoGenerado = `${desde}|${hasta}`;
		} catch (cause) {
			reporte = [];
			periodoGenerado = '';
			error = cause instanceof Error ? cause.message : 'No se pudo generar el reporte.';
		} finally {
			cargando = false;
		}
	}

	async function exportar() {
		if (cargando || exportando || enviando || !reporteDisponible) return;
		exportando = true;
		error = '';
		mensaje = '';
		try {
			const extension = formato.toLowerCase();
			const response = await apiFetch(
				`/archivo-intercambio/generar?descargar=true&formato=${formato}`,
				{
					method: 'POST',
					body: JSON.stringify(datosPeriodo()),
					headers: { Accept: formato === 'CSV' ? 'text/csv' : 'application/octet-stream' }
				}
			);
			if (!response.ok) throw new Error(await getApiError(response));
			const blob = await response.blob();
			await apiJson('/archivo-intercambio/exportar', {
				method: 'POST',
				body: JSON.stringify({ ...datosPeriodo(), formato })
			});
			await cargarExportaciones();

			const url = URL.createObjectURL(blob);
			const anchor = document.createElement('a');
			anchor.href = url;
			anchor.download = `archivo_intercambio.${extension}`;
			anchor.click();
			window.setTimeout(() => URL.revokeObjectURL(url), 0);
			mensaje = `Archivo ${extension.toUpperCase()} exportado y registro guardado correctamente.`;
		} catch (cause) {
			error = cause instanceof Error ? cause.message : 'No se pudo exportar el archivo.';
		} finally {
			exportando = false;
		}
	}

	async function enviarReporte() {
		if (cargando || exportando || enviando || !reporteDisponible) return;
		if (!/^[A-Z0-9._%+-]+@gmail\.com$/i.test(correoDestino.trim())) {
			error = 'Ingrese una dirección válida de Gmail (usuario@gmail.com).';
			return;
		}
		enviando = true;
		error = '';
		mensaje = '';
		try {
			await apiJson('/archivo-intercambio/enviar', {
				method: 'POST',
				body: JSON.stringify({
					...datosPeriodo(),
					formato,
					correo_destino: correoDestino.trim()
				})
			});
			await cargarExportaciones();
			mensaje = `Reporte ${formato} enviado a ${correoDestino.trim()}.`;
		} catch (cause) {
			error = cause instanceof Error ? cause.message : 'No se pudo enviar el reporte.';
		} finally {
			enviando = false;
		}
	}
</script>

<div class="page">
	<div class="page-header">
		<h1>Archivo de intercambio con el socio internacional</h1>
		<p class="subtitle">Resumen de la actividad de acceso por departamento</p>
	</div>

	{#if error}<p role="alert">{error}</p>{/if}
	{#if mensaje}<div class="success-box" role="status"><span class="success-icon">✓</span>{mensaje}</div>{/if}
	<div class="card">
		<div class="form-row">
			<div class="form-group"><label for="desde">Periodo desde</label><input id="desde" type="date" bind:value={desde} class="form-input" /></div>
			<div class="form-group"><label for="hasta">Periodo hasta</label><input id="hasta" type="date" bind:value={hasta} class="form-input" /></div>
		</div>
		<div class="form-row export-options">
			<div class="form-group">
				<label for="formato">Formato del archivo</label>
				<select id="formato" bind:value={formato} class="form-input">
					<option value="CSV">CSV</option>
					<option value="XLSX">Excel (.xlsx)</option>
				</select>
			</div>
			<div class="form-group">
				<label for="correo-destino">Correo Gmail del socio</label>
				<input id="correo-destino" type="email" bind:value={correoDestino} class="form-input" placeholder="socio@gmail.com" autocomplete="email" />
			</div>
		</div>
		<button class="btn-generar" type="button" onclick={generar} disabled={cargando || exportando || enviando}>{cargando ? 'Generando...' : 'Generar vista previa'}</button>

		<table class="data-table">
			<thead>
				<tr><th>Departamento</th><th>Total de intentos</th><th>Autorizados</th><th>Denegados</th><th>No registrados</th></tr>
			</thead>
			<tbody>
				{#each reporte as fila}
					<tr><td>{fila.departamento}</td><td>{fila.total_intentos}</td><td>{fila.autorizados}</td><td>{fila.denegados}</td><td>{fila.no_registrados}</td></tr>
				{:else}
					<tr><td colspan="5">{cargando ? 'Generando reporte...' : 'Seleccione un periodo y genere el reporte.'}</td></tr>
				{/each}
			</tbody>
		</table>

		<div class="form-actions">
			<button class="btn-exportar" type="button" onclick={exportar} disabled={cargando || exportando || enviando || !reporteDisponible}>{exportando ? 'Exportando...' : `Descargar ${formato}`}</button>
			<button class="btn-enviar" type="button" onclick={enviarReporte} disabled={cargando || exportando || enviando || !reporteDisponible || !correoDestino.trim()}>{enviando ? 'Enviando...' : 'Enviar reporte por correo'}</button>
		</div>
	</div>

	<section class="history-card" aria-labelledby="history-title">
		<div class="history-header">
			<div>
				<h2 id="history-title">Historial de reportes</h2>
				<p>Fecha, formato y destinatario de cada exportación o envío.</p>
			</div>
			<button type="button" class="btn-refresh" onclick={cargarExportaciones} disabled={cargandoExportaciones}>
				{cargandoExportaciones ? 'Actualizando...' : 'Actualizar'}
			</button>
		</div>
		{#if errorExportaciones}<p class="history-error" role="alert">{errorExportaciones}</p>{/if}
		<div class="history-table-wrap">
			<table class="history-table">
				<thead>
					<tr>
						<th>Fecha de generación</th>
						<th>Formato</th>
						<th>Periodo del reporte</th>
						<th>Correo destinatario</th>
					</tr>
				</thead>
				<tbody>
					{#each exportaciones as exportacion (exportacion.id_exportacion)}
						<tr>
							<td>{new Date(exportacion.fecha_hora_generacion).toLocaleString()}</td>
							<td><span class="format-badge">{exportacion.formato}</span></td>
							<td>{exportacion.periodo_inicio} — {exportacion.periodo_fin}</td>
							<td>{exportacion.correo_destino ?? 'Solo descargado'}</td>
						</tr>
					{:else}
						<tr>
							<td colspan="4">
								{cargandoExportaciones ? 'Cargando historial...' : 'Todavía no hay reportes registrados.'}
							</td>
						</tr>
					{/each}
				</tbody>
			</table>
		</div>
	</section>
</div>

<style>
	* {
		margin: 0;
		padding: 0;
		box-sizing: border-box;
	}

	:root {
		--lab-primary: #1e6f9e;
		--lab-primary-dark: #155a7a;
		--lab-surface: #ffffff;
		--lab-text: #0f172a;
		--lab-text-muted: #64748b;
		--lab-border: #e2e8f0;
		--lab-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.08),
			0 2px 4px -1px rgba(0, 0, 0, 0.04);
		--lab-radius: 12px;
	}

	:global(body) {
		font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu,
			Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
		background-color: #f8fafc;
		color: var(--lab-text);
	}

	.page {
		max-width: 1000px;
		margin: 0 auto;
		padding: 2rem;
		display: flex;
		flex-direction: column;
		gap: 1.5rem;
	}

	.page-header {
		display: flex;
		flex-direction: column;
		gap: 0.5rem;
	}

	.page-header h1 {
		font-size: 1.75rem;
		font-weight: 700;
		color: var(--lab-text);
		margin: 0;
	}

	.form-row {
		display: flex;
		flex-wrap: wrap;
		gap: 1rem;
	}

	.form-group {
		display: flex;
		flex: 1;
		flex-direction: column;
		gap: 0.5rem;
		min-width: 220px;
		margin: 0;
	}

	.form-group label {
		font-size: 0.9rem;
		font-weight: 600;
	}

	.form-input {
		width: 100%;
		padding: 0.7rem 0.8rem;
		border: 1px solid var(--lab-border);
		border-radius: 0.6rem;
		font: inherit;
	}

	.btn-enviar {
		background: #15803d;
		color: #ffffff;
		border: none;
		padding: 0.75rem 1.5rem;
		border-radius: 0.75rem;
		font-size: 1rem;
		font-weight: 600;
		font-family: inherit;
		cursor: pointer;
		transition: background 0.2s;
	}

	.btn-enviar:hover {
		background: #166534;
	}

	.btn-exportar:disabled,
	.btn-enviar:disabled,
	.btn-generar:disabled {
		cursor: not-allowed;
		opacity: 0.55;
	}

	.subtitle {
		color: var(--lab-text-muted);
		font-size: 1rem;
		margin: 0;
	}

	.card {
		background: var(--lab-surface);
		border: 1px solid var(--lab-border);
		border-radius: var(--lab-radius);
		padding: 1.5rem;
		box-shadow: var(--lab-shadow);
		display: flex;
		flex-direction: column;
		gap: 1.5rem;
	}

	.btn-generar {
		background: var(--lab-primary);
		color: #ffffff;
		border: none;
		padding: 0.75rem 1.5rem;
		border-radius: 0.75rem;
		font-size: 1rem;
		font-weight: 600;
		font-family: inherit;
		cursor: pointer;
		transition: background 0.2s;
		align-self: flex-start;
	}

	.btn-generar:hover {
		background: var(--lab-primary-dark);
	}

	.data-table {
		width: 100%;
		border-collapse: collapse;
		background: var(--lab-surface);
		border: 1px solid var(--lab-border);
		border-radius: var(--lab-radius);
		overflow: hidden;
	}

	.data-table th {
		background: #f1f5f9;
		text-align: left;
		padding: 0.875rem 1rem;
		font-size: 0.875rem;
		font-weight: 600;
		color: var(--lab-text);
		border-bottom: 1px solid var(--lab-border);
	}

	.data-table td {
		padding: 0.875rem 1rem;
		font-size: 0.95rem;
		border-bottom: 1px solid var(--lab-border);
	}

	.form-actions {
		display: flex;
		justify-content: space-between;
		align-items: center;
		flex-wrap: wrap;
		gap: 1rem;
	}

	.btn-exportar {
		background: var(--lab-primary);
		color: #ffffff;
		border: none;
		padding: 0.75rem 1.5rem;
		border-radius: 0.75rem;
		font-size: 1rem;
		font-weight: 600;
		font-family: inherit;
		cursor: pointer;
		transition: background 0.2s;
	}

	.btn-exportar:hover {
		background: var(--lab-primary-dark);
	}

	.success-box {
		background: #dcfce7;
		border: 1px solid #86efac;
		border-radius: var(--lab-radius);
		padding: 1rem 1.25rem;
		display: flex;
		align-items: center;
		gap: 0.75rem;
	}

	.history-card {
		padding: 1.5rem;
		border: 1px solid var(--lab-border);
		border-radius: var(--lab-radius);
		background: var(--lab-surface);
		box-shadow: var(--lab-shadow);
	}

	.history-header {
		display: flex;
		align-items: center;
		justify-content: space-between;
		gap: 1rem;
		margin-bottom: 1rem;
	}

	.history-header h2 {
		margin: 0 0 0.25rem;
		font-size: 1.2rem;
	}

	.history-header p {
		margin: 0;
		color: var(--lab-text-muted);
	}

	.btn-refresh {
		padding: 0.55rem 0.85rem;
		border: 1px solid var(--lab-border);
		border-radius: 0.6rem;
		background: #fff;
		color: var(--lab-primary);
		font: inherit;
		font-weight: 600;
		cursor: pointer;
	}

	.btn-refresh:disabled {
		cursor: wait;
		opacity: 0.6;
	}

	.history-error {
		margin-bottom: 0.75rem;
		color: #b91c1c;
	}

	.history-table-wrap {
		overflow-x: auto;
	}

	.history-table {
		width: 100%;
		border-collapse: collapse;
	}

	.history-table th,
	.history-table td {
		padding: 0.8rem 0.9rem;
		border-bottom: 1px solid var(--lab-border);
		text-align: left;
		white-space: nowrap;
	}

	.history-table th {
		background: #f1f5f9;
		font-size: 0.875rem;
	}

	.format-badge {
		display: inline-block;
		padding: 0.2rem 0.5rem;
		border-radius: 999px;
		background: #e0f2fe;
		color: #075985;
		font-size: 0.8rem;
		font-weight: 700;
	}

	.success-icon {
		color: #16a34a;
		font-weight: bold;
		font-size: 1.1rem;
	}

	.success-box span {
		color: #166534;
		font-size: 0.95rem;
		font-weight: 500;
	}

	@media (max-width: 640px) {
		.page {
			padding: 1rem;
		}

		.form-actions {
			flex-direction: column;
			align-items: stretch;
		}

		.btn-exportar {
			width: 100%;
			text-align: center;
		}

		.history-header {
			align-items: stretch;
			flex-direction: column;
		}
	}
</style>