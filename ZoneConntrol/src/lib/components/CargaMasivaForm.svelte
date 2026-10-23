<script lang="ts">
	import { apiJson } from '../auth';

	let { onSaved }: { onSaved?: () => void } = $props();

	interface CargaMasiva {
		total_procesadas: number;
		filas_incorporadas: number;
		filas_con_error: number;
		errores: Array<{ fila: number; identificacion: string; motivo: string }>;
	}

	let archivo = $state<File | null>(null);
	let resumen = $state<CargaMasiva | null>(null);
	let cargando = $state(false);
	let error = $state('');
	let mensaje = $state('');

	async function procesar(event: SubmitEvent) {
		event.preventDefault();
		if (cargando) return;
		if (!archivo) {
			error = 'Seleccione un archivo CSV.';
			return;
		}
		if (!archivo.name.toLowerCase().endsWith('.csv')) {
			error = 'El archivo debe tener extensión .csv.';
			return;
		}
		cargando = true;
		error = '';
		mensaje = '';
		resumen = null;
		try {
			const form = new FormData();
			form.append('file', archivo);
			resumen = await apiJson<CargaMasiva>('/personal/carga-masiva', {
				method: 'POST',
				body: form
			});
			mensaje = `Proceso terminado: ${resumen.filas_incorporadas} registro(s) incorporado(s).`;
			if (resumen.filas_incorporadas > 0) onSaved?.();
		} catch (cause) {
			error = cause instanceof Error ? cause.message : 'No se pudo procesar el archivo.';
		} finally {
			cargando = false;
		}
	}
</script>

<div class="page">
	<div class="page-header">
		<h1>Carga masiva de personal</h1>
	</div>

	{#if error}<p role="alert">{error}</p>{/if}
	{#if mensaje}<div class="success-box" role="status">{mensaje}</div>{/if}
	<form class="card" onsubmit={procesar}>
		<h2>1. Seleccionar el archivo plano</h2>
		<div class="dropzone">
			<p class="dropzone-text">Selecciona el archivo plano con la información del personal</p>
			<input type="file" accept=".csv,text/csv" onchange={(event) => { archivo = event.currentTarget.files?.[0] ?? null; resumen = null; mensaje = ''; }} />
			{#if archivo}<p>{archivo.name}</p>{/if}
		</div>

		<button class="btn-validar" type="submit" disabled={cargando}>{cargando ? 'Procesando...' : 'Validar e incorporar filas válidas'}</button>

		{#if resumen}
			<h2>2. Resultado del procesamiento</h2>
			<div class="result-row">
				<div class="result-card result-valid">Filas procesadas: {resumen.total_procesadas}</div>
				<div class="result-card result-valid">Incorporadas: {resumen.filas_incorporadas}</div>
				<div class="result-card result-error">Con error: {resumen.filas_con_error}</div>
			</div>

			<div class="table-container">
				<table class="data-table">
					<thead><tr><th>Fila</th><th>Identificación</th><th>Motivo del error</th></tr></thead>
					<tbody>
						{#each resumen.errores as filaError}
							<tr><td>{filaError.fila}</td><td>{filaError.identificacion}</td><td>{filaError.motivo}</td></tr>
						{:else}
							<tr><td colspan="3">No se reportaron errores.</td></tr>
						{/each}
					</tbody>
				</table>
			</div>
		{/if}

		<div class="form-actions">
			<button class="btn-cancel" type="reset" onclick={() => { archivo = null; resumen = null; mensaje = ''; error = ''; }}>Limpiar</button>
		</div>
	</form>

	<div class="note-box">
		<p>
			<strong>Formato:</strong> numero_identificacion,nombres,apellidos,departamento. El servidor valida e incorpora únicamente las filas válidas y detalla los errores.
		</p>
	</div>
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
		max-width: 900px;
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

	.card h2 {
		font-size: 1.1rem;
		font-weight: 600;
		color: var(--lab-text);
		margin-top: 0;
	}

	.dropzone {
		border: 2px dashed var(--lab-border);
		border-radius: var(--lab-radius);
		padding: 2rem;
		text-align: center;
		background: #f8fafc;
	}

	.dropzone-text {
		color: var(--lab-text-muted);
		font-size: 0.95rem;
		margin-bottom: 1rem;
	}

	.btn-validar {
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

	.btn-validar:hover {
		background: var(--lab-primary-dark);
	}

	.result-row {
		display: flex;
		gap: 1rem;
	}

	.result-card {
		flex: 1;
		padding: 1rem;
		border-radius: 0.75rem;
		font-size: 0.95rem;
		font-weight: 500;
		text-align: center;
	}

	.result-valid {
		background: #dcfce7;
		border: 1px solid #86efac;
		color: #166534;
	}

	.result-error {
		background: #fef2f2;
		border: 1px solid #fecaca;
		color: #7f1d1d;
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
		justify-content: flex-start;
		gap: 1rem;
	}

	.btn-cancel {
		background: #ffffff;
		border: 1px solid var(--lab-border);
		color: var(--lab-text);
		padding: 0.75rem 1.5rem;
		border-radius: 0.75rem;
		font-size: 1rem;
		font-weight: 600;
		font-family: inherit;
		cursor: pointer;
		transition: all 0.2s;
	}

	.btn-cancel:hover {
		background: #f1f5f9;
	}

	.note-box {
		background: #fffdf3;
		border: 1px dashed #d4a017;
		border-radius: var(--lab-radius);
		padding: 1.25rem 1.5rem;
	}

	.note-box p {
		color: #7c6a12;
		font-size: 0.95rem;
		margin: 0;
	}

	@media (max-width: 640px) {
		.page {
			padding: 1rem;
		}

		.result-row {
			flex-direction: column;
		}
	}
</style>