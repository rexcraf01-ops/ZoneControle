<script lang="ts">
	import { goto, invalidateAll } from '$app/navigation';
	import { page } from '$app/state';
	import { iniciarSesion, rutaInicialPorRol } from '../../lib/auth';

	let nombreUsuario = $state('');
	let contrasena = $state('');
	let error: string | null = $state(null);
	let cargando = $state(false);
	let mensaje = $derived(page.url.searchParams.get('mensaje'));

	async function manejarLogin(e: SubmitEvent) {
		e.preventDefault();
		if (cargando) return;
		error = null;
		cargando = true;
		try {
			const resultado = await iniciarSesion(nombreUsuario, contrasena);
			if (!resultado.ok) {
				error = resultado.mensaje ?? 'Error al iniciar sesión';
				return;
			}
			await invalidateAll();
			await goto(rutaInicialPorRol(resultado.user?.rol));
		} catch (cause) {
			error = cause instanceof Error ? cause.message : 'No se pudo iniciar sesión.';
		} finally {
			cargando = false;
		}
	}
</script>

<svelte:head>
	<title>Iniciar sesión | Zone Control</title>
</svelte:head>

<div class="login-page">
	<div class="login-decor" aria-hidden="true">
		<div class="decor-circle"></div>
		<div class="decor-line"></div>
		<div class="decor-loop decor-loop-one"></div>
		<div class="decor-loop decor-loop-two"></div>
		<div class="decor-wave"></div>
	</div>

	<div class="login-card">
		<div class="login-header">
			<img src="/logo.png" alt="Laboratorio XYZ" class="logo-img" />
			<h1>Iniciar sesión</h1>
			<p class="login-subtitle">Acceso al sistema Zone Control – Laboratorio XYZ</p>
		</div>

		{#if mensaje}
			<div class="error-alert" role="alert"><span class="error-message">{mensaje}</span></div>
		{/if}

		<form class="login-form" onsubmit={manejarLogin}>
			<div class="form-group">
				<label for="nombre_usuario" class="form-label">Usuario</label>
				<input
					type="text"
					id="nombre_usuario"
					name="nombre_usuario"
					placeholder="nombre_usuario"
					bind:value={nombreUsuario}
					class="form-input"
					autocomplete="username"
					required
				/>
			</div>
			<div class="form-group">
				<label for="contrasena" class="form-label">Contraseña</label>
				<input
					type="password"
					id="contrasena"
					name="contrasena"
					placeholder="••••••••"
					bind:value={contrasena}
					class="form-input"
					autocomplete="current-password"
					required
				/>
			</div>
			<button type="submit" class="btn-ingresar" disabled={cargando}>
				{cargando ? 'Autenticando...' : 'Ingresar'}
			</button>
		</form>

		<a href="/recuperar-cuenta" class="recuperar-enlace">¿Olvidaste tu contraseña?</a>
		{#if error}
			<div class="error-alert" role="alert">
				<span class="error-icon" aria-hidden="true">!</span>
				<span class="error-message">{error}</span>
			</div>
		{/if}
		<a href="/" class="volver-enlace">← Volver al portal</a>
	</div>
</div>

<style>
	.login-page,
	.login-page *,
	.login-page *::before,
	.login-page *::after {
		box-sizing: border-box;
	}

	:global(html:has(.login-page)) {
		height: 100%;
		overflow: hidden;
	}

	:global(body:has(.login-page)) {
		height: 100%;
		margin: 0;
		overflow: hidden;
	}

	:global(body:has(.login-page) .lab-header),
	:global(body:has(.login-page) .lab-footer) {
		display: none;
	}

	:global(body:has(.login-page) .lab-main) {
		overflow: visible;
		padding: 0;
	}

	.login-page {
		isolation: isolate;
		position: fixed;
		inset: 0;
		display: grid;
		min-height: 100vh;
		min-height: 100svh;
		margin: 0;
		padding: 1.5rem;
		place-items: center;
		overflow: hidden;
		z-index: 1000;
		background:
			radial-gradient(ellipse at 50% 45%, rgba(19, 142, 248, 0.92), transparent 52%),
			linear-gradient(125deg, #03365f 0%, #075da7 46%, #003864 100%);
		color: #ffffff;
	}

	.login-page::before,
	.login-page::after {
		position: absolute;
		z-index: -1;
		border: 1px solid rgba(180, 225, 255, 0.16);
		border-radius: 50%;
		content: '';
		pointer-events: none;
	}

	.login-page::before {
		top: -22rem;
		left: -12rem;
		width: 48rem;
		height: 48rem;
		box-shadow:
			0 0 0 3rem rgba(125, 205, 255, 0.035),
			0 0 0 7rem rgba(125, 205, 255, 0.025);
	}

	.login-page::after {
		right: -18rem;
		bottom: -32rem;
		width: 58rem;
		height: 58rem;
		box-shadow:
			0 0 0 3rem rgba(125, 205, 255, 0.04),
			0 0 0 8rem rgba(125, 205, 255, 0.025);
	}

	.login-decor {
		position: absolute;
		inset: 0;
		z-index: -1;
		overflow: hidden;
		pointer-events: none;
	}

	.decor-circle {
		position: absolute;
		top: 9%;
		left: 36%;
		width: 6rem;
		height: 6rem;
		border: 0.8rem solid rgba(23, 210, 255, 0.55);
		border-right-color: transparent;
		border-bottom-color: rgba(23, 210, 255, 0.18);
		border-radius: 50%;
		filter: drop-shadow(0 0 1rem rgba(17, 204, 255, 0.65));
		transform: rotate(25deg);
	}

	.decor-line {
		position: absolute;
		top: 31%;
		right: 14%;
		width: 8rem;
		height: 14rem;
		border: 1.25rem solid rgba(0, 40, 87, 0.42);
		border-left-color: transparent;
		border-radius: 45%;
		filter: blur(0.2rem);
		transform: rotate(-20deg);
	}

	.decor-loop {
		position: absolute;
		border: 1.2rem solid rgba(123, 209, 255, 0.6);
		border-right-color: transparent;
		border-radius: 50%;
		box-shadow: inset 0 0 1rem rgba(255, 255, 255, 0.18), 0 0 1.5rem rgba(20, 180, 255, 0.22);
		transform: rotate(-35deg);
	}

	.decor-loop-one {
		bottom: 12%;
		left: 28%;
		width: 9rem;
		height: 5rem;
	}

	.decor-loop-two {
		right: 29%;
		bottom: 24%;
		width: 6rem;
		height: 2.5rem;
		border-width: 0.8rem;
		transform: rotate(15deg);
	}

	.decor-wave {
		position: absolute;
		right: -5rem;
		bottom: -4.5rem;
		width: 28rem;
		height: 10rem;
		border-radius: 50%;
		background: rgba(132, 207, 255, 0.32);
		filter: blur(0.2rem);
		transform: rotate(-8deg);
	}

	.login-card {
		position: relative;
		z-index: 1;
		width: min(100%, 430px);
		max-height: calc(100svh - 2rem);
		padding: 2rem 2.25rem;
		overflow-y: auto;
		border: 1px solid rgba(255, 255, 255, 0.3);
		border-radius: 1.5rem;
		background: linear-gradient(145deg, rgba(255, 255, 255, 0.18), rgba(211, 236, 255, 0.1));
		box-shadow: 0 24px 80px rgba(0, 28, 67, 0.32), inset 0 1px 0 rgba(255, 255, 255, 0.2);
		backdrop-filter: blur(18px);
		-webkit-backdrop-filter: blur(18px);
	}

	.login-header {
		margin-bottom: 1.4rem;
		text-align: center;
	}

	.logo-img {
		display: block;
		width: 68px;
		height: 68px;
		margin: 0 auto 0.75rem;
		padding: 0.35rem;
		border: 1px solid rgba(255, 255, 255, 0.3);
		border-radius: 1.25rem;
		background: rgba(255, 255, 255, 0.92);
		object-fit: contain;
		box-shadow: 0 8px 24px rgba(0, 34, 75, 0.2);
	}

	.login-header h1 {
		margin-bottom: 0.45rem;
		color: #ffffff;
		font-size: 1.65rem;
		font-weight: 700;
		letter-spacing: -0.025em;
	}

	.login-subtitle {
		color: rgba(232, 245, 255, 0.82);
		font-size: 0.92rem;
	}

	.login-form {
		display: flex;
		flex-direction: column;
		gap: 0.9rem;
	}

	.form-group {
		display: flex;
		flex-direction: column;
		gap: 0.5rem;
	}

	.form-label {
		color: rgba(255, 255, 255, 0.95);
		font-size: 0.9rem;
		font-weight: 600;
	}

	.form-input {
		width: 100%;
		padding: 0.72rem 1rem;
		border: 1px solid rgba(215, 235, 255, 0.8);
		border-radius: 0.65rem;
		background: rgba(255, 255, 255, 0.96);
		color: #0f2942;
		font: inherit;
		transition: border-color 0.2s, box-shadow 0.2s;
	}

	.form-input::placeholder {
		color: #8294a5;
	}

	.form-input:focus {
		outline: none;
		border-color: #8fe5ff;
		box-shadow: 0 0 0 4px rgba(108, 220, 255, 0.24);
	}

	.btn-ingresar {
		margin-top: 0.35rem;
		padding: 0.9rem 1rem;
		border: 1px solid rgba(255, 255, 255, 0.18);
		border-radius: 0.65rem;
		background: linear-gradient(100deg, #034d87, #003b6d);
		color: #ffffff;
		font: inherit;
		font-weight: 700;
		cursor: pointer;
		box-shadow: 0 8px 20px rgba(0, 34, 75, 0.2);
		transition: background 0.2s, transform 0.2s, box-shadow 0.2s;
	}

	.btn-ingresar:hover:not(:disabled) {
		background: linear-gradient(100deg, #023d70, #002e59);
		box-shadow: 0 11px 24px rgba(0, 28, 67, 0.28);
		transform: translateY(-1px);
	}

	.btn-ingresar:active:not(:disabled) {
		transform: translateY(0);
	}

	.btn-ingresar:disabled {
		cursor: not-allowed;
		opacity: 0.7;
	}

	.recuperar-enlace {
		display: flex;
		align-items: center;
		justify-content: center;
		margin-top: 1.25rem;
		padding: 0.75rem 1rem;
		border: 1px solid rgba(223, 243, 255, 0.5);
		border-radius: 0.65rem;
		background: rgba(255, 255, 255, 0.08);
		color: #ffffff;
		font-size: 0.92rem;
		font-weight: 600;
		text-decoration: none;
		transition: background 0.2s, border-color 0.2s;
	}

	.recuperar-enlace:hover {
		border-color: rgba(255, 255, 255, 0.85);
		background: rgba(255, 255, 255, 0.17);
	}

	.recuperar-enlace:focus-visible,
	.volver-enlace:focus-visible,
	.btn-ingresar:focus-visible {
		outline: 3px solid #a3ecff;
		outline-offset: 3px;
	}

	.error-alert {
		display: flex;
		align-items: center;
		gap: 0.7rem;
		margin-top: 1rem;
		padding: 0.85rem 1rem;
		border: 1px solid rgba(254, 202, 202, 0.8);
		border-radius: 0.7rem;
		background: rgba(254, 242, 242, 0.96);
	}

	.error-icon {
		color: #dc2626;
		font-size: 1.1rem;
		font-weight: 700;
	}

	.error-message {
		color: #7f1d1d;
		font-size: 0.9rem;
		font-weight: 600;
	}

	.volver-enlace {
		display: block;
		margin-top: 1.35rem;
		color: rgba(238, 248, 255, 0.88);
		font-size: 0.9rem;
		font-weight: 500;
		text-align: center;
		text-decoration: none;
	}

	.volver-enlace:hover {
		color: #ffffff;
		text-decoration: underline;
	}

	@media (max-width: 576px) {
		.login-page {
			padding: 1rem;
		}

		.login-card {
			width: min(100%, 430px);
			padding: 1.5rem 1.25rem;
		}
	}

	@media (prefers-reduced-motion: reduce) {
		.login-page *,
		.login-page *::before,
		.login-page *::after {
			transition-duration: 0.01ms !important;
		}
	}
</style>
