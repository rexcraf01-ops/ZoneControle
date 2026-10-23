<script lang="ts">
	import { onMount } from 'svelte';
	import { apiJson } from '../auth';
	import FormInput from './FormInput.svelte';

	let { onSaved }: { onSaved?: () => void } = $props();

	interface Departamento {
		idDepartamento: number;
		nombre: string;
	}

	let departamentos = $state<Departamento[]>([]);
	let identificacion = $state('');
	let nombres = $state('');
	let apellidos = $state('');
	let idDepartamento = $state('');
	let cargando = $state(false);
	let error = $state('');
	let mensaje = $state('');

	onMount(async () => {
		try {
			departamentos = await apiJson<Departamento[]>('/departamentos');
		} catch (cause) {
			error = cause instanceof Error ? cause.message : 'No se pudieron cargar los departamentos.';
		}
	});

	async function guardar(event: SubmitEvent) {
		event.preventDefault();
		if (cargando) return;
		if (!identificacion.trim() || !nombres.trim() || !apellidos.trim() || !idDepartamento) {
			error = 'Complete todos los campos para registrar a la persona.';
			return;
		}
		cargando = true;
		error = '';
		mensaje = '';
		try {
			await apiJson('/personal', {
				method: 'POST',
				body: JSON.stringify({
					numero_identificacion: identificacion.trim(),
					nombres: nombres.trim(),
					apellidos: apellidos.trim(),
					id_departamento: Number(idDepartamento)
				})
			});
			mensaje = 'Personal registrado correctamente.';
			onSaved?.();
			identificacion = '';
			nombres = '';
			apellidos = '';
			idDepartamento = '';
		} catch (cause) {
			error = cause instanceof Error ? cause.message : 'No se pudo registrar el personal.';
		} finally {
			cargando = false;
		}
	}
</script>

<div class="page">
	<div class="page-header">
		<h1>Registrar personal autorizado</h1>
	</div>

	{#if error}<p role="alert">{error}</p>{/if}
	{#if mensaje}<div class="success-box" role="status"><span class="success-icon">✓</span>{mensaje}</div>{/if}

	<form class="form" onsubmit={guardar}>
		<div class="form-row">
			<FormInput id="identificacion" label="Número de identificación interno" bind:value={identificacion} required maxlength={20} />
			<div class="form-group">
				<label for="departamento" class="form-label">Departamento de producción</label>
				<select id="departamento" name="departamento" bind:value={idDepartamento} class="form-input" required>
					<option value="">Seleccione un departamento</option>
					{#each departamentos as departamento}
						<option value={departamento.idDepartamento}>{departamento.nombre}</option>
					{/each}
				</select>
			</div>
		</div>

		<div class="form-row">
			<div class="form-group">
				<label for="nombres" class="form-label">Nombres</label>
				<input id="nombres" name="nombres" type="text" bind:value={nombres} class="form-input" required maxlength="100" />
			</div>
			<div class="form-group">
				<label for="apellidos" class="form-label">Apellidos</label>
				<input id="apellidos" name="apellidos" type="text" bind:value={apellidos} class="form-input" required maxlength="100" />
			</div>
		</div>

		<div class="form-actions">
			<button type="submit" class="btn-save" disabled={cargando}>{cargando ? 'Guardando...' : 'Guardar'}</button>
			<button type="reset" class="btn-cancel" onclick={() => { identificacion = ''; nombres = ''; apellidos = ''; idDepartamento = ''; }}>Cancelar</button>
		</div>
	</form>

	<div class="note-box">
		<p>
			<strong>Nota:</strong> el estado inicial de la autorización de una persona recién registrada
			está pendiente de definir; por eso no aparece como campo.
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

	.form {
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
		grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
		gap: 1.5rem;
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

	.form-input,
	select {
		padding: 0.75rem 1rem;
		border: 1px solid var(--lab-border);
		border-radius: 0.5rem;
		font-size: 0.95rem;
		font-family: inherit;
		background: #f8fafc;
	}

	.form-input:focus,
	select:focus {
		outline: none;
		border-color: var(--lab-primary);
		box-shadow: 0 0 0 3px rgba(30, 111, 158, 0.15);
	}

	.form-actions {
		display: flex;
		justify-content: flex-start;
		gap: 1rem;
	}

	.btn-save {
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

	.btn-save:hover {
		background: var(--lab-primary-dark);
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

	.success-box {
		background: #dcfce7;
		border: 1px solid #86efac;
		border-radius: var(--lab-radius);
		padding: 1rem 1.25rem;
		display: flex;
		align-items: center;
		gap: 0.75rem;
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

		.form-row {
			grid-template-columns: 1fr;
		}
	}
</style>