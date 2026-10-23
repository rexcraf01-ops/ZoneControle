<script lang="ts">
	import { onMount } from 'svelte';
	import { apiJson } from '../auth';

	let { initialId = null, onSaved }: { initialId?: number | null; onSaved?: () => void } = $props();

	interface Personal {
		id_personal: number;
		numero_identificacion: string;
		nombres: string;
		apellidos: string;
		id_departamento: number;
		id_estado_autorizacion: number;
	}
	interface Departamento {
		idDepartamento: number;
		nombre: string;
	}
	interface EstadoAutorizacion {
		idEstadoAutorizacion: number;
		nombre: string;
	}

	let personaId = $state<number | null>(null);
	let identificacion = $state('');
	let nombres = $state('');
	let apellidos = $state('');
	let idDepartamento = $state<number | ''>('');
	let idEstado = $state('');
	let estadoOriginal = $state('');
	let departamentos = $state<Departamento[]>([]);
	let estados = $state<EstadoAutorizacion[]>([]);
	let cargando = $state(false);
	let error = $state('');
	let mensaje = $state('');

	onMount(async () => {
		try {
			[departamentos, estados] = await Promise.all([
				apiJson<Departamento[]>('/departamentos'),
				apiJson<EstadoAutorizacion[]>('/estados-autorizacion')
			]);
			const id = initialId === null ? new URLSearchParams(window.location.search).get('id') : String(initialId);
			if (id && /^\d+$/.test(id)) await cargarPorId(Number(id));
		} catch (cause) {
			error = cause instanceof Error ? cause.message : 'No se pudieron cargar los catálogos.';
		}
	});

	function llenar(persona: Personal) {
		personaId = persona.id_personal;
		identificacion = persona.numero_identificacion;
		nombres = persona.nombres;
		apellidos = persona.apellidos;
		idDepartamento = persona.id_departamento;
		idEstado = String(persona.id_estado_autorizacion);
		estadoOriginal = idEstado;
	}

	async function cargarPorId(id: number) {
		const persona = await apiJson<Personal>(`/personal/${id}`);
		llenar(persona);
	}

	async function buscar() {
		if (!identificacion.trim() || cargando) {
			error = 'Ingrese el número de identificación.';
			return;
		}
		cargando = true;
		error = '';
		mensaje = '';
		try {
			const params = new URLSearchParams({ q: identificacion.trim() });
			const resultados = await apiJson<Personal[]>(`/personal?${params}`);
			const persona = resultados.find((item) => item.numero_identificacion === identificacion.trim());
			if (!persona) {
				personaId = null;
				error = 'No se encontró una persona con esa identificación.';
				return;
			}
			llenar(persona);
		} catch (cause) {
			error = cause instanceof Error ? cause.message : 'No se pudo buscar el personal.';
		} finally {
			cargando = false;
		}
	}

	async function guardar(event: SubmitEvent) {
		event.preventDefault();
		if (cargando) return;
		if (personaId === null) {
			error = 'Busque y seleccione una persona antes de guardar.';
			return;
		}
		if (!identificacion.trim() || !nombres.trim() || !apellidos.trim() || !idDepartamento || !idEstado) {
			error = 'Complete todos los campos obligatorios.';
			return;
		}
		if (idEstado !== estadoOriginal && !window.confirm('¿Confirma el cambio del estado de autorización?')) {
			return;
		}
		cargando = true;
		error = '';
		mensaje = '';
		try {
			const persona = await apiJson<Personal>(`/personal/${personaId}`, {
				method: 'PUT',
				body: JSON.stringify({
					numero_identificacion: identificacion.trim(),
					nombres: nombres.trim(),
					apellidos: apellidos.trim(),
					id_departamento: idDepartamento,
					id_estado_autorizacion: Number(idEstado)
				})
			});
			llenar(persona);
			mensaje = 'Información actualizada correctamente.';
			onSaved?.();
		} catch (cause) {
			error = cause instanceof Error ? cause.message : 'No se pudo actualizar el personal.';
		} finally {
			cargando = false;
		}
	}
</script>

<div class="page">
	<div class="page-header">
		<h1>Actualizar información del personal</h1>
	</div>

	{#if error}<p role="alert">{error}</p>{/if}
	{#if mensaje}<div class="success-box" role="status"><span class="success-icon">✓</span>{mensaje}</div>{/if}

	<form class="form" onsubmit={guardar}>
		<div class="form-row">
			<div class="form-group">
				<label for="identificacion" class="form-label">Número de identificación interno</label>
				<input id="identificacion" name="identificacion" type="text" bind:value={identificacion} class="form-input" required maxlength="20" />
				<button type="button" class="btn-cancel" onclick={buscar} disabled={cargando}>Buscar</button>
			</div>
			<div class="form-group">
				<label for="departamento" class="form-label">Departamento de producción</label>
				<select id="departamento" bind:value={idDepartamento} class="form-input" required>
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

		<fieldset class="form-group full">
			<legend class="form-label">Autorización de ingreso a las áreas restringidas</legend>
			<div class="radio-group">
				{#each estados as estado}
					<label class="radio-label">
						<input type="radio" name="autorizacion" value={String(estado.idEstadoAutorizacion)} bind:group={idEstado} />
						{estado.nombre}
					</label>
				{/each}
		</div>
		</fieldset>

		<div class="form-actions">
		<button type="submit" class="btn-save" disabled={cargando}>{cargando ? 'Guardando...' : 'Guardar cambios'}</button>
		<button type="button" class="btn-cancel" onclick={() => { personaId = null; identificacion = ''; nombres = ''; apellidos = ''; idDepartamento = ''; idEstado = ''; estadoOriginal = ''; }}>Cancelar</button>
		</div>
	</form>
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

	.form-group.full {
		grid-column: 1 / -1;
	}

	fieldset.form-group {
		min-width: 0;
		padding: 0;
		border: 0;
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

	.radio-group {
		display: flex;
		gap: 1rem;
		flex-wrap: wrap;
	}

	.radio-label {
		display: flex;
		align-items: center;
		gap: 0.5rem;
		font-size: 0.95rem;
		color: var(--lab-text);
		cursor: pointer;
		padding: 0.6rem 1rem;
		border: 1px solid var(--lab-border);
		border-radius: 0.75rem;
		background: #ffffff;
		transition: all 0.2s;
	}

	.radio-label:hover {
		border-color: var(--lab-primary);
		background: rgba(30, 111, 158, 0.05);
	}

	.radio-label input[type="radio"]:checked {
		accent-color: var(--lab-primary);
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

	@media (max-width: 640px) {
		.page {
			padding: 1rem;
		}

		.form-row {
			grid-template-columns: 1fr;
		}
	}
</style>