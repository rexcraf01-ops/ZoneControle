<script lang="ts">
	import { onMount } from 'svelte';
	import type { PageProps } from './$types';
	import { apiJson } from '../../lib/auth';
	import DataTable from '../../lib/components/DataTable.svelte';
	import FormModal from '../../lib/components/FormModal.svelte';
	import RegistrarPersonalForm from '../../lib/components/RegistrarPersonalForm.svelte';
	import CargaMasivaForm from '../../lib/components/CargaMasivaForm.svelte';
	import ActualizarPersonalForm from '../../lib/components/ActualizarPersonalForm.svelte';
	import RowActionIcon from '../../lib/components/RowActionIcon.svelte';

	let { data }: PageProps = $props();
	let rolUsuario = $derived(data.user?.rol?.toUpperCase() ?? '');
	let puedeCargarMasivo = $derived(rolUsuario.includes('ADMIN'));
	interface Departamento {
		idDepartamento: number;
		nombre: string;
	}
	interface Personal {
		id_personal: number;
		numero_identificacion: string;
		nombres: string;
		apellidos: string;
		departamento: string;
		estado_autorizacion: string;
	}

	let departamentos = $state<Departamento[]>([]);
	let personas = $state<Personal[]>([]);
	let valorBusqueda = $state('');
	let idDepartamento = $state('');
	let cargando = $state(false);
	let error = $state('');
	let consultado = $state(false);
	let modal = $state<'registrar' | 'carga' | 'actualizar' | null>(null);
	let personaSeleccionada = $state<number | null>(null);
	let cambiosPendientes = $state(false);

	onMount(async () => {
		try {
			departamentos = await apiJson<Departamento[]>('/departamentos');
			await buscar();
		} catch (cause) {
			error = cause instanceof Error ? cause.message : 'No se pudieron cargar los datos.';
		}
	});

	async function buscar(event?: SubmitEvent) {
		event?.preventDefault();
		if (cargando) return;
		if (event && valorBusqueda.trim() === '' && idDepartamento === '') {
			error = 'Ingrese un valor de búsqueda o seleccione un departamento.';
			return;
		}
		cargando = true;
		error = '';
		consultado = true;
		try {
			const params = new URLSearchParams();
			if (valorBusqueda.trim()) params.set('q', valorBusqueda.trim());
			if (idDepartamento) params.set('idDepartamento', idDepartamento);
			personas = await apiJson<Personal[]>(`/personal${params.size ? `?${params}` : ''}`);
		} catch (cause) {
			error = cause instanceof Error ? cause.message : 'No se pudo buscar el personal.';
			personas = [];
		} finally {
			cargando = false;
		}
	}

	function limpiar() {
		valorBusqueda = '';
		idDepartamento = '';
		error = '';
		void buscar();
	}

	function abrirEdicion(id: number) {
		personaSeleccionada = id;
		modal = 'actualizar';
	}

	function cerrarModal() {
		modal = null;
		personaSeleccionada = null;
		if (cambiosPendientes) {
			cambiosPendientes = false;
			void buscar();
		}
	}

	function guardarCompletado() {
		cambiosPendientes = true;
		cerrarModal();
	}
</script>

<div class="page">
	<div class="page-header">
		<h1>Buscar personal</h1>
	</div>

	<div class="toolbar" aria-label="Acciones de personal">
		<button type="button" class="btn-action btn-primary" onclick={() => (modal = 'registrar')}>Registrar personal</button>
		{#if puedeCargarMasivo}
			<button type="button" class="btn-action btn-secondary" onclick={() => (modal = 'carga')}>Carga masiva</button>
		{/if}
		<button type="button" class="btn-action btn-secondary" onclick={() => (modal = 'actualizar')}>Actualizar personal</button>
	</div>

	{#if error}<p role="alert">{error}</p>{/if}
	<form class="search-form" onsubmit={buscar}>
		<div class="form-row">
			<div class="form-group">
				<label for="valor" class="form-label">Buscar por identificación, nombres o apellidos</label>
				<input id="valor" name="valor" type="search" bind:value={valorBusqueda} placeholder="Identificación, nombre o apellido" class="form-input" />
			</div>
			<div class="form-group">
				<label for="departamento" class="form-label">Departamento</label>
				<select id="departamento" bind:value={idDepartamento} class="form-input">
					<option value="">Todos</option>
					{#each departamentos as departamento}
						<option value={departamento.idDepartamento}>{departamento.nombre}</option>
					{/each}
				</select>
			</div>
		</div>

		<div class="form-actions">
			<button type="submit" class="btn-buscar" disabled={cargando}>{cargando ? 'Buscando...' : 'Buscar'}</button>
			<button type="button" class="btn-limpiar" onclick={limpiar}>Limpiar</button>
		</div>
	</form>

	<DataTable>
			<thead>
				<tr>
					<th>Identificación</th>
					<th>Nombres</th>
					<th>Apellidos</th>
					<th>Departamento</th>
					<th>Autorización</th>
					<th>Acciones</th>
				</tr>
			</thead>
			<tbody>
				{#each personas as persona (persona.id_personal)}
					<tr>
						<td>{persona.numero_identificacion}</td>
						<td>{persona.nombres}</td>
						<td>{persona.apellidos}</td>
						<td>{persona.departamento}</td>
						<td>{persona.estado_autorizacion}</td>
						<td><RowActionIcon action="edit" onAction={() => abrirEdicion(persona.id_personal)} /></td>
					</tr>
				{:else}
					<tr><td colspan="6">{cargando ? 'Cargando...' : 'No se encontraron resultados.'}</td></tr>
				{/each}
			</tbody>
	</DataTable>
	{#if consultado && personas.length === 0 && !cargando}<div class="empty-box">No se encontraron resultados.</div>{/if}
</div>

{#if modal === 'registrar'}
	<FormModal title="Registrar personal" onClose={cerrarModal}>
		<RegistrarPersonalForm onSaved={guardarCompletado} />
	</FormModal>
{:else if modal === 'carga'}
	<FormModal title="Carga masiva de personal" onClose={cerrarModal}>
		<CargaMasivaForm onSaved={guardarCompletado} />
	</FormModal>
{:else if modal === 'actualizar'}
	<FormModal title="Actualizar personal" onClose={cerrarModal}>
		<ActualizarPersonalForm initialId={personaSeleccionada} onSaved={guardarCompletado} />
	</FormModal>
{/if}

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

	.toolbar {
		display: flex;
		flex-wrap: wrap;
		gap: 0.75rem;
	}

	.btn-action {
		padding: 0.75rem 1rem;
		border: 1px solid transparent;
		border-radius: 0.75rem;
		font: inherit;
		font-weight: 600;
		cursor: pointer;
		transition: background 0.2s, color 0.2s, border-color 0.2s;
	}

	.btn-primary {
		background: var(--lab-primary);
		color: #ffffff;
	}

	.btn-primary:hover {
		background: var(--lab-primary-dark);
	}

	.btn-secondary {
		border-color: var(--lab-border);
		background: #ffffff;
		color: #172033;
	}

	.btn-secondary:hover {
		border-color: var(--lab-primary);
		color: #1d4ed8;
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

	.btn-buscar {
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

	.btn-buscar:hover {
		background: var(--lab-primary-dark);
	}

	.btn-limpiar {
		background: #ffffff;
		border: 1px solid var(--lab-border);
		color: #172033;
		padding: 0.75rem 1.5rem;
		border-radius: 0.75rem;
		font-size: 1rem;
		font-weight: 600;
		font-family: inherit;
		cursor: pointer;
		transition: all 0.2s;
	}

	.btn-limpiar:hover {
		background: #f1f5f9;
	}

	:global(.table-container) {
		overflow-x: auto;
	}

	:global(.data-table) {
		width: 100%;
		border-collapse: collapse;
		background: var(--lab-surface);
		border: 1px solid var(--lab-border);
		border-radius: var(--lab-radius);
		overflow: hidden;
	}

	:global(.data-table th) {
		background: #f1f5f9;
		text-align: left;
		padding: 0.875rem 1rem;
		font-size: 0.875rem;
		font-weight: 600;
		color: var(--lab-text);
		border-bottom: 1px solid var(--lab-border);
	}

	:global(.data-table td) {
		padding: 0.875rem 1rem;
		font-size: 0.95rem;
		border-bottom: 1px solid var(--lab-border);
	}

	.empty-box {
		background: #fffdf3;
		border: 1px solid #fcd34d;
		border-radius: var(--lab-radius);
		padding: 1rem 1.25rem;
		color: #7c6a12;
		font-size: 0.95rem;
	}

	@media (max-width: 768px) {
		.page {
			padding: 1rem;
		}

		.form-row {
			grid-template-columns: 1fr;
		}
	}
</style>