<script lang="ts">
	import { onMount } from 'svelte';
	import { apiFetch, apiJson, getApiError } from '../../lib/auth';
	import DataTable from '../../lib/components/DataTable.svelte';
	import Modal from '../../lib/components/Modal.svelte';
	import FormModal from '../../lib/components/FormModal.svelte';
	import RowActionIcon from '../../lib/components/RowActionIcon.svelte';

	interface Usuario {
		id_usuario: number;
		nombre_usuario: string;
		email: string | null;
		id_rol: number;
		rol: string;
		personal_nombre: string | null;
		numero_identificacion: string | null;
	}
	interface Rol {
		idRol?: number;
		id_rol?: number;
		nombre: string;
	}
	interface Departamento {
		idDepartamento: number;
		nombre: string;
	}

	let usuarios = $state<Usuario[]>([]);
	let roles = $state<Rol[]>([]);
	let departamentos = $state<Departamento[]>([]);
	let nombreUsuario = $state('');
	let nombres = $state('');
	let apellidos = $state('');
	let numeroIdentificacion = $state('');
	let idDepartamento = $state('');
	let email = $state('');
	let contrasena = $state('');
	let idRol = $state('');
	let editandoId = $state<number | null>(null);
	let cargando = $state(false);
	let error = $state('');
	let mensaje = $state('');
	let usuarioPorEliminar = $state<Usuario | null>(null);
	let modalFormularioAbierto = $state(false);

	onMount(async () => {
		try {
			[usuarios, roles, departamentos] = await Promise.all([
				apiJson<Usuario[]>('/usuarios-internos'),
				apiJson<Rol[]>('/roles'),
				apiJson<Departamento[]>('/departamentos')
			]);
		} catch (cause) {
			error = cause instanceof Error ? cause.message : 'No se pudieron cargar los usuarios.';
		}
	});

	function editar(usuario: Usuario) {
		editandoId = usuario.id_usuario;
		nombreUsuario = usuario.nombre_usuario;
		email = usuario.email ?? '';
		idRol = String(usuario.id_rol);
		contrasena = '';
		error = '';
		mensaje = '';
		modalFormularioAbierto = true;
	}

	function limpiar() {
		editandoId = null;
		nombreUsuario = '';
		nombres = '';
		apellidos = '';
		numeroIdentificacion = '';
		idDepartamento = '';
		email = '';
		contrasena = '';
		idRol = '';
		error = '';
	}

	function abrirRegistro() {
		limpiar();
		modalFormularioAbierto = true;
	}

	function cerrarFormulario() {
		if (cargando) return;
		modalFormularioAbierto = false;
		limpiar();
	}

	async function eliminar(usuario: Usuario) {
		if (cargando) return;
		usuarioPorEliminar = usuario;
	}

	async function confirmarEliminacion() {
		const usuario = usuarioPorEliminar;
		usuarioPorEliminar = null;
		if (!usuario || cargando) return;
		cargando = true;
		error = '';
		mensaje = '';
		try {
			const response = await apiFetch(`/usuarios-internos/${usuario.id_usuario}`, { method: 'DELETE' });
			if (!response.ok) throw new Error(await getApiError(response));
			usuarios = usuarios.filter((item) => item.id_usuario !== usuario.id_usuario);
			if (editandoId === usuario.id_usuario) limpiar();
			mensaje = 'Usuario eliminado correctamente.';
		} catch (cause) {
			error = cause instanceof Error ? cause.message : 'No se pudo eliminar el usuario.';
		} finally {
			cargando = false;
		}
	}

	async function guardar(event: SubmitEvent) {
		event.preventDefault();
		if (cargando) return;
		if (
			!nombreUsuario.trim() ||
			!idRol ||
			(!editandoId &&
				(!contrasena.trim() ||
					!nombres.trim() ||
					!apellidos.trim() ||
					!numeroIdentificacion.trim() ||
					!idDepartamento))
		) {
			error = 'Complete los datos de la persona, la identificación, el usuario, la contraseña, el departamento y el rol.';
			return;
		}

		cargando = true;
		error = '';
		mensaje = '';
		try {
			const payload: Record<string, string | number> = {
				nombre_usuario: nombreUsuario.trim(),
				id_rol: Number(idRol),
				email: email.trim()
			};
			if (!editandoId) {
				payload.nombres = nombres.trim();
				payload.apellidos = apellidos.trim();
				payload.numero_identificacion = numeroIdentificacion.trim();
				payload.id_departamento = Number(idDepartamento);
			}
			if (contrasena.trim()) payload.contrasena = contrasena;
			const saved = await apiJson<Usuario>(
				editandoId ? `/usuarios-internos/${editandoId}` : '/usuarios-internos',
				{
					method: editandoId ? 'PUT' : 'POST',
					body: JSON.stringify(payload)
				}
			);
			if (editandoId) {
				usuarios = usuarios.map((usuario) =>
					usuario.id_usuario === saved.id_usuario ? saved : usuario
				);
			} else {
				usuarios = [...usuarios, saved];
			}
			mensaje = editandoId ? 'Usuario actualizado correctamente.' : 'Usuario creado correctamente.';
			modalFormularioAbierto = false;
			limpiar();
		} catch (cause) {
			error = cause instanceof Error ? cause.message : 'No se pudo guardar el usuario.';
		} finally {
			cargando = false;
		}
	}
</script>

<div class="page">
	<div class="page-header">
		<h1>Usuarios internos</h1>
		<button class="btn-new" type="button" onclick={abrirRegistro}>+ Registrar usuario</button>
	</div>

	{#if error}<p role="alert">{error}</p>{/if}
	{#if mensaje}<p class="success-box" role="status">{mensaje}</p>{/if}

	<DataTable>
			<thead>
				<tr><th>Usuario</th><th>Nombre de la persona</th><th>Documento</th><th>Correo</th><th>Rol</th><th>Acción</th></tr>
			</thead>
			<tbody>
				{#each usuarios as usuario (usuario.id_usuario)}
					<tr>
						<td>{usuario.nombre_usuario}</td>
						<td>{usuario.personal_nombre ?? 'No vinculado'}</td>
						<td>{usuario.numero_identificacion ?? '—'}</td>
						<td>{usuario.email ?? 'Sin correo'}</td>
						<td>{usuario.rol}</td>
						<td>
							<RowActionIcon action="edit" onAction={() => editar(usuario)} />
							<RowActionIcon action="delete" onAction={() => eliminar(usuario)} disabled={cargando} />
						</td>
					</tr>
				{:else}
					<tr><td colspan="6">{cargando ? 'Cargando...' : 'No hay usuarios registrados.'}</td></tr>
				{/each}
			</tbody>
	</DataTable>

	{#if modalFormularioAbierto}
		<FormModal
			title={editandoId ? 'Actualizar usuario' : 'Registrar usuario'}
			onClose={cerrarFormulario}
		>
			{#if error}<p class="form-error" role="alert">{error}</p>{/if}
		<form class="form" onsubmit={guardar}>
			{#if !editandoId}
				<div class="form-group">
					<label for="nombres-persona" class="form-label">Nombres de la persona</label>
					<input id="nombres-persona" name="nombres-persona" type="text" bind:value={nombres} class="form-input" required maxlength="100" autocomplete="given-name" />
				</div>
				<div class="form-group">
					<label for="apellidos-persona" class="form-label">Apellidos de la persona</label>
					<input id="apellidos-persona" name="apellidos-persona" type="text" bind:value={apellidos} class="form-input" required maxlength="100" autocomplete="family-name" />
				</div>
				<div class="form-group">
					<label for="documento-persona" class="form-label">ID / Documento</label>
					<input id="documento-persona" name="documento-persona" type="text" bind:value={numeroIdentificacion} class="form-input" required maxlength="20" />
				</div>
				<div class="form-group">
					<label for="departamento-persona" class="form-label">Departamento</label>
					<select id="departamento-persona" name="departamento-persona" bind:value={idDepartamento} class="form-input" required>
						<option value="">Seleccione un departamento</option>
						{#each departamentos as departamento}
							<option value={departamento.idDepartamento}>{departamento.nombre}</option>
						{/each}
					</select>
				</div>
			{/if}
			<div class="form-group">
				<label for="usuario" class="form-label">Usuario</label>
				<input id="usuario" name="usuario" type="text" bind:value={nombreUsuario} class="form-input" required minlength="3" maxlength="50" />
			</div>
			<div class="form-group">
				<label for="email" class="form-label">Correo para recuperación</label>
				<input id="email" name="email" type="email" bind:value={email} class="form-input" maxlength="254" autocomplete="email" />
				<small>El enlace para restablecer la contraseña se enviará a este correo.</small>
			</div>
			<div class="form-group">
				<label for="contrasena" class="form-label">Contraseña {editandoId ? '(opcional)' : ''}</label>
				<input id="contrasena" name="contrasena" type="password" bind:value={contrasena} class="form-input" required={!editandoId} minlength="4" autocomplete="new-password" />
			</div>
			<div class="form-group">
				<label for="rol" class="form-label">Rol</label>
				<select id="rol" name="rol" bind:value={idRol} class="form-input" required>
					<option value="">Seleccione un rol</option>
					{#each roles as rol}
						<option value={rol.idRol ?? rol.id_rol}>{rol.nombre}</option>
					{/each}
				</select>
			</div>
			<div class="form-actions">
				<button type="submit" class="btn-save" disabled={cargando}>{cargando ? 'Guardando...' : 'Guardar'}</button>
				<button type="button" class="btn-cancel" onclick={cerrarFormulario} disabled={cargando}>Cancelar</button>
			</div>
		</form>
		</FormModal>
	{/if}
	<Modal
		open={usuarioPorEliminar !== null}
		title="Eliminar usuario interno"
		message={usuarioPorEliminar ? `¿Confirma eliminar al usuario ${usuarioPorEliminar.nombre_usuario}?` : ''}
		confirmText="Eliminar"
		onConfirm={confirmarEliminacion}
		onCancel={() => (usuarioPorEliminar = null)}
	/>
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
		gap: 2rem;
	}

	.page-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.page-header h1 {
		font-size: 1.75rem;
		font-weight: 700;
		color: var(--lab-text);
	}

	.btn-new {
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
		display: inline-flex;
		align-items: center;
		gap: 0.5rem;
	}

	.btn-new:hover {
		background: var(--lab-primary-dark);
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

	.form-error {
		margin: 1rem 1.5rem 0;
		padding: 0.75rem 1rem;
		border: 1px solid #fecaca;
		border-radius: 0.5rem;
		background: #fef2f2;
		color: #991b1b;
	}

	.form {
		display: grid;
		grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
		gap: 1.25rem;
		padding: 1.5rem;
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
		grid-column: 1 / -1;
		display: flex;
		justify-content: flex-end;
		gap: 0.75rem;
		margin-top: 0.5rem;
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

	@media (max-width: 768px) {
		.page {
			padding: 1rem;
		}

		.page-header {
			flex-direction: column;
			align-items: flex-start;
			gap: 1rem;
		}

		.form {
			grid-template-columns: 1fr;
		}
	}
</style>