<script lang="ts">
	import { page } from '$app/state';
	import { normalizarRol } from '../../lib/auth';
	import type { PageProps } from './$types';

	let { data }: PageProps = $props();
	let role = $derived(normalizarRol(data.user?.rol));
	let accessError = $derived(page.url.searchParams.get('error'));
</script>

<div class="page">
	<div class="page-header">
		<h1>Bienvenido, {data.user?.nombre_usuario ?? 'usuario'}</h1>
		<p>Rol: {data.user?.rol ?? 'No disponible'}</p>
	</div>

	{#if accessError}
		<div class="note-box" role="alert"><p>{accessError}</p></div>
	{/if}

	<div class="roles-grid">
		{#if role === 'ADMIN'}
			<section class="role-card">
				<div class="role-header">Administrador del sistema</div>
				<div class="role-actions">
					<a href="/usuarios-internos" class="role-btn">Administrar usuarios internos</a>
				</div>
			</section>
		{:else if role === 'GESTOR'}
			<section class="role-card">
				<div class="role-header">Gestor de personal</div>
				<div class="role-actions">
					<a href="/buscar-personal" class="role-btn">Buscar y gestionar personal</a>
				</div>
			</section>
		{:else if role === 'SUPERVISOR'}
			<section class="role-card">
				<div class="role-header">Supervisor/Auditor</div>
				<div class="role-actions">
					<a href="/auditoria" class="role-btn">Auditoría y archivos de intercambio</a>
				</div>
			</section>
		{:else}
			<div class="note-box" role="alert"><p>Su cuenta no tiene un rol reconocido.</p></div>
		{/if}
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
		max-width: 1200px;
		margin: 0 auto;
		padding: 2rem;
	}

	.page-header {
		margin-bottom: 2.5rem;
	}

	.page-header h1 {
		font-size: 1.75rem;
		font-weight: 700;
		color: var(--lab-text);
	}

	.roles-grid {
		display: grid;
		grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
		gap: 1.5rem;
		margin-bottom: 2rem;
	}

	.role-card {
		background: var(--lab-surface);
		border: 1px solid var(--lab-border);
		border-radius: var(--lab-radius);
		box-shadow: var(--lab-shadow);
		display: flex;
		flex-direction: column;
	}

	.role-header {
		background: #f1f5f9;
		border-bottom: 1px solid var(--lab-border);
		padding: 1rem 1.25rem;
		font-size: 1rem;
		font-weight: 600;
		color: var(--lab-text);
	}

	.role-actions {
		padding: 1.25rem;
		display: flex;
		flex-direction: column;
		gap: 0.75rem;
	}

	.role-btn {
		background: #ffffff;
		border: 1px solid var(--lab-border);
		border-radius: 0.75rem;
		padding: 0.875rem 1rem;
		text-align: center;
		text-decoration: none;
		color: var(--lab-text);
		font-size: 0.95rem;
		font-weight: 500;
		transition: all 0.2s;
		cursor: pointer;
	}

	.role-btn:hover {
		background: rgba(30, 111, 158, 0.06);
		border-color: var(--lab-primary);
		color: var(--lab-primary);
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
</style>