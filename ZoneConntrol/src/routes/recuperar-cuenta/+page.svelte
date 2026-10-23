<script lang="ts">
	import { solicitarRecuperacion } from '../../lib/auth';

	let email = $state('');
	let cargando = $state(false);
	let error = $state('');
	let enviado = $state(false);

	async function enviar(event: SubmitEvent) {
		event.preventDefault();
		if (cargando) return;
		cargando = true;
		error = '';
		enviado = false;
		try {
			await solicitarRecuperacion(email);
			enviado = true;
		} catch (cause) {
			error = cause instanceof Error ? cause.message : 'No se pudo procesar la solicitud.';
		} finally {
			cargando = false;
		}
	}
</script>

<svelte:head>
	<title>Recuperar cuenta | Zone Control</title>
</svelte:head>

<section class="account-page">
	<div class="account-card">
		<h1>Recuperar cuenta</h1>
		<p>Ingresa el correo registrado en tu cuenta. Si coincide, recibirás un enlace para restablecer la contraseña.</p>
		<form onsubmit={enviar}>
			<label for="email">Correo electrónico</label>
			<input id="email" name="email" type="email" bind:value={email} autocomplete="email" required maxlength="254" />
			<button type="submit" disabled={cargando}>{cargando ? 'Enviando...' : 'Enviar enlace'}</button>
		</form>
		{#if error}<p class="alert error" role="alert">{error}</p>{/if}
		{#if enviado}
			<p class="alert success" role="status">Si el correo corresponde a una cuenta, recibirás instrucciones para restablecerla.</p>
		{/if}
		<a href="/iniciar-sesion">Volver a iniciar sesión</a>
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
	a { color: #1e6f9e; }
	.alert { padding: 0.75rem; border-radius: 8px; }
	.error { color: #991b1b; background: #fee2e2; }
	.success { color: #166534; background: #dcfce7; }
</style>
