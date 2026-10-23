<script lang="ts">
	import { apiJson } from '../../lib/auth';
	import DataCard from '../../lib/components/DataCard.svelte';

	interface Lectura {
		resultado: 'AUTORIZADO' | 'DENEGADO' | 'NO_REGISTRADO';
		mensaje: string;
		numero_identificacion: string;
		persona?: string;
		departamento?: string;
		fecha_hora?: string;
	}

	let identificacion = $state('');
	let resultado = $state<Lectura | null>(null);
	let cargando = $state(false);
	let error = $state('');

	async function validar(event: SubmitEvent) {
		event.preventDefault();
		if (cargando) return;
		if (!identificacion.trim()) {
			error = 'Ingrese el número de identificación.';
			return;
		}
		cargando = true;
		error = '';
		resultado = null;
		try {
			resultado = await apiJson<Lectura>('/acceso/lectura', {
				method: 'POST',
				body: JSON.stringify({ numero_identificacion: identificacion.trim() })
			});
		} catch (cause) {
			error = cause instanceof Error ? cause.message : 'No se pudo validar la credencial.';
		} finally {
			cargando = false;
		}
	}

	const estiloResultado = (value: Lectura['resultado']) =>
		value === 'AUTORIZADO'
			? 'result-authorized'
			: value === 'NO_REGISTRADO'
				? 'result-unregistered'
				: 'result-denied';
</script>

<div class="page">
	<DataCard className="card">
		<h2>Lectura de credenciales</h2>
		<form onsubmit={validar} class="form-group">
			<label for="identificacion" class="form-label">Número de identificación interno</label>
			<input id="identificacion" name="identificacion" type="text" bind:value={identificacion} placeholder="Número de identificación" class="form-input" required maxlength="20" />
			<button class="btn-validar" type="submit" disabled={cargando}>{cargando ? 'Validando...' : 'Validar ingreso'}</button>
		</form>
	</DataCard>

	{#if error}<p role="alert">{error}</p>{/if}
	{#if resultado}
		<div class="results" role="status">
			<div class={`result-card ${estiloResultado(resultado.resultado)}`}>
				<span class="icon">{resultado.resultado === 'AUTORIZADO' ? '✓' : resultado.resultado === 'NO_REGISTRADO' ? '?' : '✕'}</span>
				<div class="text">
					<strong>{resultado.mensaje}</strong>
					{#if resultado.persona}<p>{resultado.persona}</p>{/if}
					{#if resultado.departamento}<p>{resultado.departamento}</p>{/if}
				</div>
			</div>
		</div>
	{/if}

	<div class="note-box">
		<p>
			<strong>Nota:</strong> cada intento, exitoso o no, incluso el de una persona no registrada, se
			guarda con su fecha y hora 
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

	.card {
		background: var(--lab-surface);
		border: 1px solid var(--lab-border);
		border-radius: var(--lab-radius);
		padding: 1.5rem;
		box-shadow: var(--lab-shadow);
		display: flex;
		flex-direction: column;
		gap: 1rem;
	}

	:global(.card h2) {
		font-size: 1.1rem;
		font-weight: 600;
		color: var(--lab-text);
		margin-top: 0;
		text-align: center;
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

	.btn-validar {
		background: var(--lab-primary);
		color: #ffffff;
		border: none;
		padding: 0.8rem 1.5rem;
		border-radius: 0.75rem;
		font-size: 1rem;
		font-weight: 600;
		font-family: inherit;
		cursor: pointer;
		transition: background 0.2s;
		align-self: center;
	}

	.btn-validar:hover {
		background: var(--lab-primary-dark);
	}

	.result-grid {
		display: flex;
		gap: 1rem;
		flex-wrap: wrap;
	}

	.result-card {
		flex: 1;
		min-width: 250px;
		padding: 1.25rem;
		border-radius: var(--lab-radius);
		display: flex;
		align-items: center;
		gap: 1rem;
	}

	.result-authorized {
		background: #dcfce7;
		border: 1px solid #86efac;
		color: #166534;
	}

	.result-denied {
		background: #fef2f2;
		border: 1px solid #fecaca;
		color: #7f1d1d;
	}

	.result-unregistered {
		background: #fffdf3;
		border: 1px solid #fcd34d;
		color: #7c6a12;
	}

	.icon {
		font-size: 1.5rem;
		font-weight: bold;
	}

	.text strong {
		display: block;
		font-size: 1rem;
		margin-bottom: 0.25rem;
	}

	.text p {
		font-size: 0.9rem;
		margin: 0;
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

		.result-grid {
			flex-direction: column;
		}
	}
</style>