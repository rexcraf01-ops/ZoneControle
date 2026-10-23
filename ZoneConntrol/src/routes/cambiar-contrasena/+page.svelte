<script lang="ts">
	import { cambiarContrasena } from '../../lib/auth';

	let contrasenaActual = $state('');
	let nuevaContrasena = $state('');
	let confirmarContrasena = $state('');
	let cargando = $state(false);
	let error = $state('');

	async function guardar(event: SubmitEvent) {
		event.preventDefault();
		if (cargando) return;
		error = '';
		if (nuevaContrasena !== confirmarContrasena) {
			error = 'Las contraseñas nuevas no coinciden.';
			return;
		}
		cargando = true;
		try {
			await cambiarContrasena(contrasenaActual, nuevaContrasena);
			window.location.assign(
				'/iniciar-sesion?mensaje=' +
					encodeURIComponent('La contraseña se actualizó. Inicia sesión de nuevo.')
			);
		} catch (cause) {
			error = cause instanceof Error ? cause.message : 'No se pudo cambiar la contraseña.';
		} finally {
			cargando = false;
		}
	}
</script>

<svelte:head>
	<title>Cambiar contraseña | Zone Control</title>
</svelte:head>

<section class="account-page">
	<div class="account-card">
		<h1>Cambiar contraseña</h1>
		<p>Por seguridad, confirma tu contraseña actual. Al actualizarla, se cerrarán las demás sesiones.</p>
		<form onsubmit={guardar}>
			<label for="actual">Contraseña actual</label>
			<input id="actual" name="actual" type="password" bind:value={contrasenaActual} autocomplete="current-password" required />
			<label for="nueva">Nueva contraseña</label>
			<input id="nueva" name="nueva" type="password" bind:value={nuevaContrasena} autocomplete="new-password" minlength="8" maxlength="128" required />
			<label for="confirmar">Confirmar contraseña nueva</label>
			<input id="confirmar" name="confirmar" type="password" bind:value={confirmarContrasena} autocomplete="new-password" minlength="8" maxlength="128" required />
			<button type="submit" disabled={cargando}>{cargando ? 'Actualizando...' : 'Cambiar contraseña'}</button>
		</form>
		{#if error}<p class="alert error" role="alert">{error}</p>{/if}
	</div>
</section>

<style>
	.account-page { min-height: 55vh; display: grid; place-items: center; padding: 2rem; }
	.account-card { width: min(100%, 440px); padding: 2rem; display: grid; gap: 1rem; background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; box-shadow: 0 4px 12px #0f172a12; }
	h1 { font-size: 1.5rem; color: #0f172a; }
	p { color: #475569; }
	form { display: grid; gap: 0.75rem; }
	label { font-weight: 600; }
	input { padding: 0.75rem; border: 1px solid #cbd5e1; border-radius: 8px; font: inherit; }
	button { padding: 0.75rem; color: white; background: #1e6f9e; border: 0; border-radius: 8px; font: inherit; cursor: pointer; }
	button:disabled { opacity: 0.65; cursor: wait; }
	.alert { padding: 0.75rem; border-radius: 8px; }
	.error { color: #991b1b; background: #fee2e2; }
</style>
