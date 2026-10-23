<script lang="ts">
	import '../app.css';
	import { afterNavigate, goto } from '$app/navigation';
	import { page } from '$app/state';
	import { onMount } from 'svelte';
	import favicon from '#lib/assets/favicon.svg';
	import type { LayoutProps } from './$types';
	import { apiFetch, getApiError, normalizarRol } from '../lib/auth';
	import ThemeToggle from '../lib/components/ThemeToggle.svelte';

	let { data, children }: LayoutProps = $props();
	let sesion = $derived(data?.user ?? null);
	let verificandoSesion = $state(false);
	let secuenciaVerificacion = 0;

	const rutasPublicas = new Set([
		'/',
		'/portal_publico',
		'/iniciar-sesion',
		'/recuperar-cuenta',
		'/restablecer-contrasena'
	]);

	function esRutaPrivada(pathname: string) {
		return (
			!rutasPublicas.has(pathname) &&
			!pathname.startsWith('/_app') &&
			!pathname.startsWith('/favicon') &&
			pathname !== '/logo.png'
		);
	}

	async function verificarRutaPrivada(pathname: string) {
		if (!esRutaPrivada(pathname)) {
			verificandoSesion = false;
			return;
		}

		const secuencia = ++secuenciaVerificacion;
		verificandoSesion = true;
		try {
			const response = await apiFetch('/auth/me');
			if (secuencia !== secuenciaVerificacion) return;

			if (!response.ok) {
				const mensaje =
					response.status === 401
						? 'La sesión ya no está activa. Inicie sesión nuevamente.'
						: await getApiError(response);
				await goto(`/iniciar-sesion?mensaje=${encodeURIComponent(mensaje)}`, {
					replaceState: true,
					invalidateAll: true
				});
				return;
			}

			const sesionActual: { success?: boolean; nombre_usuario?: string; rol?: string } =
				await response.json();
			if (
				!sesionActual.success ||
				typeof sesionActual.nombre_usuario !== 'string' ||
				typeof sesionActual.rol !== 'string'
			) {
				await goto(
					`/iniciar-sesion?mensaje=${encodeURIComponent('La sesión ya no está activa. Inicie sesión nuevamente.')}`,
					{ replaceState: true, invalidateAll: true }
				);
				return;
			}

			verificandoSesion = false;
		} catch (cause) {
			if (secuencia !== secuenciaVerificacion) return;
			const mensaje =
				cause instanceof Error
					? `No se pudo verificar la sesión: ${cause.message}`
					: 'No se pudo verificar la sesión. Inicie sesión nuevamente.';
			await goto(`/iniciar-sesion?mensaje=${encodeURIComponent(mensaje)}`, {
				replaceState: true,
				invalidateAll: true
			});
		}
	}

	afterNavigate(({ to }) => {
		void verificarRutaPrivada(to?.url.pathname ?? page.url.pathname);
	});

	onMount(() => {
		const protegerPaginaRestaurada = (event: PageTransitionEvent) => {
			if (event.persisted) void verificarRutaPrivada(window.location.pathname);
		};
		const ocultarRutaPrivadaAlRetroceder = () => {
			if (esRutaPrivada(window.location.pathname)) verificandoSesion = true;
		};

		window.addEventListener('pageshow', protegerPaginaRestaurada);
		window.addEventListener('popstate', ocultarRutaPrivadaAlRetroceder);
		return () => {
			window.removeEventListener('pageshow', protegerPaginaRestaurada);
			window.removeEventListener('popstate', ocultarRutaPrivadaAlRetroceder);
		};
	});

	const sidebarItems = [
		{ label: 'Usuarios internos', href: '/usuarios-internos', role: 'ADMIN', icon: 'users' },
		{ label: 'Buscar personal', href: '/buscar-personal', role: 'GESTOR', icon: 'search' },
		{ label: 'Lector de credenciales', href: '/lector-credenciales', role: 'GESTOR', icon: 'badge' },
		{ label: 'Auditoría y archivos', href: '/auditoria', role: 'SUPERVISOR', icon: 'chart' }
	];
	let currentRole = $derived(normalizarRol(sesion?.rol));
	let visibleSidebarItems = $derived(
		sidebarItems.filter((item) => item.role === 'ALL' || item.role === currentRole || currentRole === 'ADMIN')
	);
</script>

<svelte:head>
	<link rel="icon" href="/logo.png" />
	<title>Zone Control - Laboratorio XYZ</title>
</svelte:head>

<div class="app" class:checking-session={verificandoSesion}>
	<details class="account-menu">
		<summary aria-label="Abrir menú de usuario" title="Menú de usuario">
			<svg viewBox="0 0 24 24" aria-hidden="true">
				<path d="M4 6h16M4 12h16M4 18h16" />
			</svg>
			{#if sesion}<span class="menu-user-name">{sesion.nombre_usuario}</span>{/if}
		</summary>
		<div class="account-menu-panel">
			{#if sesion}
				<div class="menu-user-info">
					<strong>{sesion.nombre_usuario}</strong>
					<span>Rol: {sesion.rol}</span>
				</div>
			{/if}
			<ThemeToggle />
			{#if sesion}
				<a href="/logout" class="menu-logout">Cerrar sesión</a>
			{:else}
				<a href="/iniciar-sesion" class="menu-login">Iniciar sesión</a>
			{/if}
		</div>
	</details>
	<header class="lab-header">
		<div class="lab-container">
			<div class="lab-brand">
				<a href="/portal_publico" class="brand-link">
					<img src="/logo.png" alt="Laboratorio XYZ" class="brand-img" />
				</a>
				<h2>Zone Control</h2>
			</div>
		</div>
	</header>

	<div class="lab-body">
		{#if sesion}
			<aside class="lab-sidebar">
				<nav class="sidebar-nav">
					{#each visibleSidebarItems as item}
						<a
							href={item.href}
							class="sidebar-link"
							class:active={page.url.pathname.startsWith(item.href)}
							aria-current={page.url.pathname.startsWith(item.href) ? 'page' : undefined}
							aria-label={item.label}
							title={item.label}
						>
							<svg viewBox="0 0 24 24" aria-hidden="true">
								{#if item.icon === 'users'}
									<path d="M16 20v-1.5a3.5 3.5 0 0 0-3.5-3.5h-5A3.5 3.5 0 0 0 4 18.5V20m6-8a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7Zm6-6.7a3.5 3.5 0 0 1 0 6.7m2 3h.5a3.5 3.5 0 0 1 3.5 3.5V20" />
								{:else if item.icon === 'search'}
									<circle cx="10.8" cy="10.8" r="6.8" />
									<path d="m16 16 4.5 4.5" />
								{:else if item.icon === 'badge'}
									<rect x="3" y="5" width="18" height="14" rx="2" />
									<circle cx="9" cy="11" r="2" />
									<path d="M6 16a3.5 3.5 0 0 1 6 0m3-6h3m-3 4h3" />
								{:else}
									<path d="M4 19V5m0 14h17M7 15l4-4 3 2 6-7" />
								{/if}
							</svg>
							<span>{item.label}</span>
						</a>
					{/each}
				</nav>
			</aside>
		{/if}

		<main class="lab-main">{@render children()}</main>
	</div>

	<footer class="portal-footer">
		<div class="footer-main section-wrap">
			<div class="footer-brand">
				<img src="/logo.png" alt="" />
				<div><strong>Laboratorio XYZ</strong><span>Zone Control</span></div>
			</div>
			<div class="footer-location">
				<span class="footer-label">Ubicación</span>
				<p>Austin, Texas, Estados Unidos</p>
			</div>
			<nav class="footer-links" aria-label="Enlaces institucionales">
				<a href="#perfil">Nosotros</a>
				<a href="#capacidades">Capacidades</a>
				<a href="#calidad">Calidad</a>
				<a href="/iniciar-sesion">Acceder al sistema</a>
			</nav>
		</div>
		<div class="footer-bottom section-wrap">
			<p>© 2026 Laboratorio XYZ. Todos los derechos reservados.</p>
			<p>Excelencia analítica para la investigación científica.</p>
		</div>
	</footer>
</div>

<style>
	* { margin: 0; padding: 0; box-sizing: border-box; }

	:root {
		--lab-radius: 16px;
	}

	.brand-img {
		width: 96px;
		height: 96px;
		object-fit: contain;
	}

	:global(body) {
		font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu,
			Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
		background-color: var(--lab-bg);
		color: var(--lab-text);
		line-height: 1.6;
	}

	.lab-container { max-width: 1200px; margin: 0 auto; padding: 0 1.5rem; }
	.app { display: flex; flex-direction: column; min-height: 100vh; }

	.lab-header {
		background: var(--lab-surface);
		border-bottom: 1px solid var(--lab-border);
		position: sticky;
		top: 0;
		z-index: 100;
	}

	.lab-header .lab-container {
		display: flex;
		align-items: center;
		justify-content: space-between;
		min-height: 108px;
	}

	.lab-brand { display: flex; align-items: center; gap: 0.9rem; }
	.brand-link { display: flex; flex: 0 0 auto; align-items: center; }
	.brand-img { width: 80px; height: 80px; object-fit: contain; }
	.lab-brand h2 {
		font-size: clamp(1.6rem, 2.5vw, 2rem);
		font-weight: 800;
		color: var(--lab-text);
		letter-spacing: -0.035em;
	}

	.account-menu {
		position: fixed;
		top: 1.25rem;
		right: 1.5rem;
		z-index: 1300;
	}

	.account-menu summary {
		display: flex;
		min-width: 48px;
		min-height: 48px;
		align-items: center;
		justify-content: center;
		gap: 0.65rem;
		padding: 0.55rem 0.85rem;
		border: 1px solid var(--lab-border);
		border-radius: 999px;
		background: var(--lab-surface);
		color: var(--lab-text);
		box-shadow: var(--lab-shadow);
		cursor: pointer;
		list-style: none;
	}

	.account-menu summary::-webkit-details-marker { display: none; }
	.account-menu summary:hover { background: var(--lab-surface-raised); }
	.account-menu summary:focus-visible { outline: none; box-shadow: var(--lab-focus); }
	.account-menu summary svg {
		width: 22px;
		height: 22px;
		fill: none;
		stroke: currentColor;
		stroke-width: 1.8;
		stroke-linecap: round;
	}
	.menu-user-name { max-width: 150px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; font-size: 0.9rem; font-weight: 650; }

	.account-menu-panel {
		position: absolute;
		top: calc(100% + 0.65rem);
		right: 0;
		display: grid;
		width: 230px;
		gap: 0.65rem;
		padding: 0.85rem;
		border: 1px solid var(--lab-border);
		border-radius: 16px;
		background: var(--lab-surface);
		box-shadow: var(--lab-shadow);
	}

	.menu-user-info { display: grid; gap: 0.15rem; padding: 0.25rem 0.3rem 0.65rem; border-bottom: 1px solid var(--lab-border); }
	.menu-user-info strong { color: var(--lab-text); font-size: 0.95rem; }
	.menu-user-info span { color: var(--lab-text-muted); font-size: 0.82rem; }
	.account-menu-panel :global(.theme-toggle) { width: 100%; justify-content: flex-start; border-radius: 10px; }
	.menu-logout, .menu-login {
		display: block;
		padding: 0.55rem 0.7rem;
		border-radius: 10px;
		background: var(--lab-primary);
		color: #fff;
		font-size: 0.9rem;
		font-weight: 650;
		text-align: center;
		text-decoration: none;
	}
	.menu-logout:hover, .menu-login:hover { background: var(--lab-primary-dark); }

	.lab-body { display: flex; flex: 1; overflow: hidden; }

	.lab-sidebar {
		width: 78px;
		flex: 0 0 auto;
		background: var(--lab-surface);
		border-right: 1px solid var(--lab-border);
		display: flex;
		flex-direction: column;
		overflow: hidden;
		transition: width 260ms cubic-bezier(0.2, 0.75, 0.25, 1);
	}

	.lab-sidebar:hover,
	.lab-sidebar:focus-within {
		width: 250px;
	}

	.sidebar-nav { display: flex; flex-direction: column; gap: 0.35rem; padding: 1.25rem 0.75rem; }
	.sidebar-link {
		display: flex;
		align-items: center;
		gap: 0.75rem;
		min-height: 54px;
		padding: 0.7rem 0.85rem;
		color: var(--lab-text-muted);
		text-decoration: none;
		border-radius: 0.8rem;
		font-size: 0.9rem;
		font-weight: 600;
		transition: background 150ms ease, color 150ms ease, padding 260ms ease;
	}
	.sidebar-link span {
		max-width: 0;
		overflow: hidden;
		opacity: 0;
		white-space: nowrap;
		transition: max-width 260ms ease, opacity 180ms ease;
	}
	.lab-sidebar:hover .sidebar-link,
	.lab-sidebar:focus-within .sidebar-link {
		padding-right: 0.8rem;
		padding-left: 0.8rem;
	}
	.lab-sidebar:hover .sidebar-link span,
	.lab-sidebar:focus-within .sidebar-link span {
		max-width: 190px;
		opacity: 1;
	}
	.sidebar-link:hover,
	.sidebar-link.active {
		background: #eff6ff;
		color: var(--lab-primary);
	}
	.sidebar-link svg { width: 27px; height: 27px; flex: 0 0 auto; fill: none; stroke: currentColor; stroke-width: 1.7; stroke-linecap: round; stroke-linejoin: round; }

	.lab-main { flex: 1; min-width: 0; overflow-y: auto; padding: 1.75rem; background: var(--lab-bg); }
	.checking-session .lab-body { visibility: hidden; }

	.checking-session::after {
		position: fixed;
		inset: 0;
		z-index: 200;
		display: grid;
		place-items: center;
		background: var(--lab-bg);
		color: var(--lab-text-muted);
		content: 'Verificando sesión...';
		font-weight: 600;
	}

	.portal-footer { border-top: 1px solid var(--portal-line); background: var(--portal-card); }
	.footer-main { display: grid; grid-template-columns: 1fr 1fr 1.2fr; align-items: center; gap: 2rem; padding-block: 2rem; }
	.footer-brand { display: flex; align-items: center; gap: 0.75rem; }
	.footer-brand img { width: 48px; height: 48px; border-radius: 12px; background: #fff; object-fit: contain; }
	.footer-brand div { display: grid; gap: 0.1rem; }
	.footer-brand strong { color: var(--portal-ink); font-size: 0.95rem; }
	.footer-brand span, .footer-location p { color: var(--portal-muted); font-size: 0.82rem; }
	.footer-label { color: var(--portal-accent-strong); font-size: 0.68rem; font-weight: 800; letter-spacing: 0.1em; text-transform: uppercase; }
	.footer-location p { margin: 0.3rem 0 0; }
	.footer-links { display: flex; flex-wrap: wrap; justify-content: flex-end; gap: 0.8rem 1rem; }
	.footer-links a { color: var(--portal-muted); font-size: 0.8rem; font-weight: 650; text-decoration: none; }
	.footer-links a:hover { color: var(--portal-accent-strong); }
	.footer-bottom { display: flex; justify-content: space-between; gap: 1rem; padding-block: 1rem; border-top: 1px solid var(--portal-line); }
	.footer-bottom p { margin: 0; color: var(--portal-muted); font-size: 0.74rem; }

	@media (max-width: 850px) {
		.footer-main { grid-template-columns: 1fr 1fr; }
		.footer-links { grid-column: 1 / -1; justify-content: flex-start; }
	}

	@media (max-width: 650px) {
		.footer-main { grid-template-columns: 1fr; gap: 1.3rem; }
		.footer-links { grid-column: auto; }
		.footer-bottom { flex-direction: column; }
	}

	@media (max-width: 768px) {
		.lab-header .lab-container { min-height: 68px; }
		.brand-img { width: 66px; height: 66px; }
		.lab-brand { gap: 0.55rem; }
		.lab-brand h2 { font-size: 1.3rem; }
		.account-menu { top: 0.65rem; right: 0.7rem; }
		.menu-user-name { display: none; }
		.lab-body { flex-direction: column; }
		.lab-sidebar,
		.lab-sidebar:hover,
		.lab-sidebar:focus-within {
			width: 100%;
			border-right: none;
			border-bottom: 1px solid var(--lab-border);
			overflow: visible;
		}
		.sidebar-nav { flex-direction: row; overflow-x: auto; padding: 0.6rem; }
		.sidebar-link { min-height: 46px; flex: 0 0 auto; }
		.sidebar-link span,
		.lab-sidebar:hover .sidebar-link span,
		.lab-sidebar:focus-within .sidebar-link span { max-width: none; opacity: 1; }
		.sidebar-link svg { width: 22px; height: 22px; }
		.lab-main { padding: 1rem; }
		.lab-header .lab-container { padding: 0 0.8rem; }
	}

	@media (prefers-reduced-motion: reduce) {
		.lab-sidebar,
		.sidebar-link,
		.sidebar-link span { transition-duration: 0.01ms; }
	}
</style>