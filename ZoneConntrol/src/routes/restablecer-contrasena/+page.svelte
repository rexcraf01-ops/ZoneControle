<script lang="ts">
	import { page } from '$app/state';
	import { goto } from '$app/navigation';
	import { restablecerContrasena } from '../../lib/auth';

	let nuevaContrasena = $state('');
	let confirmarContrasena = $state('');
	let cargando = $state(false);
	let error = $state('');
	let completado = $state(false);
	let token = $derived(page.url.searchParams.get('token') ?? '');

	async function guardar(event: SubmitEvent) {
		event.preventDefault();
		if (cargando) return;
		error = '';
		if (!token) {
			error = 'El enlace de recuperación no es válido o está incompleto.';
			return;
		}
		if (nuevaContrasena !== confirmarContrasena) {
			error = 'Las contraseñas no coinciden.';
			return;
		}
		cargando = true;
		try {
			await restablecerContrasena(token, nuevaContrasena);
			completado = true;
		} catch (cause) {
			error = cause instanceof Error ? cause.message : 'No se pudo restablecer la contraseña.';
		} finally {
			cargando = false;
		}
	}
</script>

<svelte:head>
	<title>Restablecer contraseña | Zone Control</title>
</svelte:head>

<section class="account-page">
	<div class="account-card">
		<h1>Restablecer contraseña</h1>
		{#if completado}
			<p class="alert success" role="status">La contraseña se actualizó. Ya puedes iniciar sesión.</p>
			<button type="button" onclick={() => goto('/iniciar-sesion')}>Ir a iniciar sesión</button>
		{:else}
			<form onsubmit={guardar}>
				<label for="nueva">Nueva contraseña</label>
				<input id="nueva" name="nueva" type="password" bind:value={nuevaContrasena} autocomplete="new-password" minlength="8" maxlength="128" required />
				<label for="confirmar">Confirmar contraseña</label>
				<input id="confirmar" name="confirmar" type="password" bind:value={confirmarContrasena} autocomplete="new-password" minlength="8" maxlength="128" required />
				<button type="submit" disabled={cargando}>{cargando ? 'Guardando...' : 'Actualizar contraseña'}</button>
			</form>
			{#if error}<p class="alert error" role="alert">{error}</p>{/if}
		{/if}
		<a href="/iniciar-sesion">Volver a iniciar sesión</a>
	</div>
</section>

<style>
	.account-page { min-height: 55vh; display: grid; place-items: center; padding: 2rem; }
	.account-card { width: min(100%, 440px); padding: 2rem; display: grid; gap: 1rem; background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; box-shadow: 0 4px 12px #0f172a12; }
	h1 { font-size: 1.5rem; color: #0f172a; }
	form { display: grid; gap: 0.75rem; }
	label { font-weight: 600; }
	input { padding: 0.75rem; border: 1px solid #cbd5e1; border-radius: 8px; font: inherit; }
	button { padding: 0.75rem; color: white; background: #1e6f9e; border: 0; border-radius: 8px; font: inherit; cursor: pointer; }
	button:disabled { opacity: 0.65; cursor: wait; }
	a { color: #1e6f9e; }
	.alert { padding: 0.75rem; border-radius: 8px; }
	.error { color: #991b1b; background: #fee2e2; }
	.success { color: #166534; background: #dcfce7; }
</style>
