<script lang="ts">
	import { apiFetch, apiJson, getApiError } from '../auth';

	interface Intento {
		id_intento: number;
		numero_identificacion: string | null;
		persona: string;
		departamento: string;
		resultado: string;
		fecha_hora: string;
	}

	let identificacion = $state('');
	let desde = $state('');
	let hasta = $state('');
	let intentos = $state<Intento[]>([]);
	let cargando = $state(false);
	let descargando = $state(false);
	let error = $state('');
	let consultado = $state(false);

	function filtros() {
		const params = new URLSearchParams();
		if (identificacion.trim()) params.set('idPersonal', identificacion.trim());
		if (desde) params.set('desde', desde);
		if (hasta) params.set('hasta', hasta);
		return params;
	}

	async function consultar(event?: SubmitEvent) {
		event?.preventDefault();
		if (cargando) return;
		if (desde && hasta && desde > hasta) {
			error = 'La fecha inicial no puede ser posterior a la fecha final.';
			return;
		}
		cargando = true;
		error = '';
		consultado = true;
		try {
			const params = filtros();
			intentos = await apiJson<Intento[]>(`/historial-accesos${params.size ? `?${params}` : ''}`);
		} catch (cause) {
			error = cause instanceof Error ? cause.message : 'No se pudo consultar el historial.';
			intentos = [];
		} finally {
			cargando = false;
		}
	}

	async function descargar() {
		if (descargando) return;
		descargando = true;
		error = '';
		try {
			const params = filtros();
			const suffix = params.size ? `?${params}` : '';
			const response = await apiFetch(`/historial-accesos/exportar${suffix}`);
			if (!response.ok) throw new Error(await getApiError(response));
			const blob = await response.blob();
			const url = URL.createObjectURL(blob);
			const anchor = document.createElement('a');
			anchor.href = url;
			anchor.download = 'historial_accesos.csv';
			anchor.click();
			URL.revokeObjectURL(url);
		} catch (cause) {
			error = cause instanceof Error ? cause.message : 'No se pudo descargar el historial.';
		} finally {
			descargando = false;
		}
	}

	function fecha(value: string) {
		return new Intl.DateTimeFormat('es', { dateStyle: 'short', timeStyle: 'short' }).format(new Date(value));
	}
</script>

<div class="page">
	<div class="page-header">
		<h1>Historial de accesos</h1>
	</div>

	{#if error}<p role="alert">{error}</p>{/if}
	<form class="search-form" onsubmit={consultar}>
		<div class="form-row">
			<div class="form-group">
				<label for="identificacion" class="form-label">Buscar por identificación o número de personal</label>
				<input id="identificacion" type="search" bind:value={identificacion} placeholder="Identificación o ID de personal" class="form-input" />
			</div>
			<div class="form-group">
				<label for="desde" class="form-label">Fecha inicial (opcional)</label>
				<div class="date-input">
					<input id="desde" type="date" bind:value={desde} class="form-input" />
				</div>
			</div>
			<div class="form-group">
				<label for="hasta" class="form-label">Fecha final (opcional)</label>
				<div class="date-input">
					<input id="hasta" type="date" bind:value={hasta} class="form-input" />
				</div>
			</div>
		</div>

		<div class="form-actions">
			<button type="submit" class="btn-consultar" disabled={cargando}>{cargando ? 'Consultando...' : 'Consultar'}</button>
		</div>
	</form>

	{#if intentos.length > 0}
		<div class="person-info">
			{intentos.length} intento(s) de acceso
			<button class="btn-download" type="button" onclick={descargar} disabled={descargando}>{descargando ? 'Descargando...' : 'Descargar historial'}</button>
		</div>
	{/if}

	<div class="table-container">
		<table class="data-table">
			<thead>
				<tr>
					<th>Fecha y hora</th>
					<th>Identificación ingresada</th>
					<th>Resultado</th>
				</tr>
			</thead>
			<tbody>
				{#each intentos as intento (intento.id_intento)}
					<tr>
						<td>{fecha(intento.fecha_hora)}</td>
						<td>{intento.numero_identificacion ?? 'No registrado'}</td>
						<td>{intento.resultado}</td>
					</tr>
				{:else}
					<tr><td colspan="3">{cargando ? 'Cargando...' : 'No hay accesos para los filtros seleccionados.'}</td></tr>
				{/each}
			</tbody>
		</table>
	</div>
	{#if consultado && intentos.length === 0 && !cargando}<p>No hay registros que coincidan con los filtros.</p>{/if}
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
		max-width: 1200px;
		margin: 0 auto;
		padding: 2rem;
		display: flex;
		flex-direction: column;
		gap: 1.5rem;
	}

	.page-header h1 {
		font-size: 1.75rem;
		font-weight: 700;
		color: var(--lab-text);
		margin-bottom: 0.5rem;
	}

	.search-form {
		background: var(--lab-surface);
		border: 1px solid var(--lab-border);
		border-radius: var(--lab-radius);
		padding: 1.5rem;
		box-shadow: var(--lab-shadow);
		display: flex;
		flex-direction: column;
		gap: 1.5rem;
	}

	.form-row {
		display: grid;
		grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
		gap: 1rem;
	}

	.form-group {
		display: flex;
		flex-direction: column;
		gap: 0.5rem;
	}

	.form-label {
		font-size: 0.9rem;
		font-weight: 600;
		color: var(--lab-text);
	}

	.form-input {
		padding: 0.75rem 1rem;
		border: 1px solid var(--lab-border);
		border-radius: 0.5rem;
		font-size: 0.95rem;
		font-family: inherit;
		background: #f8fafc;
	}

	.form-input:focus {
		outline: none;
		border-color: var(--lab-primary);
		box-shadow: 0 0 0 3px rgba(30, 111, 158, 0.15);
	}

	.date-input {
		display: flex;
		align-items: center;
		gap: 0.5rem;
	}

	.form-actions {
		display: flex;
		justify-content: flex-start;
	}

	.btn-consultar {
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

	.btn-consultar:hover {
		background: var(--lab-primary-dark);
	}

	.person-info {
		background: var(--lab-surface);
		border: 1px solid var(--lab-border);
		border-radius: var(--lab-radius);
		padding: 1rem 1.25rem;
		display: flex;
		justify-content: space-between;
		align-items: center;
		box-shadow: var(--lab-shadow);
		font-size: 0.95rem;
	}

	.btn-download {
		background: var(--lab-surface);
		border: 1px solid var(--lab-border);
		color: var(--lab-text);
		padding: 0.6rem 1.25rem;
		border-radius: 0.75rem;
		font-size: 0.95rem;
		font-weight: 600;
		font-family: inherit;
		cursor: pointer;
		transition: all 0.2s;
	}

	.btn-download:hover {
		border-color: var(--lab-primary);
		color: var(--lab-primary);
		background: rgba(30, 111, 158, 0.05);
	}

	.table-container {
		overflow-x: auto;
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

	@media (max-width: 768px) {
		.page {
			padding: 1rem;
		}

		.form-row {
			grid-template-columns: 1fr;
		}

		.person-info {
			flex-direction: column;
			align-items: flex-start;
			gap: 1rem;
		}
	}
</style>