<script lang="ts">
	import { onMount } from 'svelte';

	let { floating = false }: { floating?: boolean } = $props();
	let temaOscuro = $state(false);

	function aplicarTema(oscuro: boolean) {
		temaOscuro = oscuro;
		document.documentElement.dataset.theme = oscuro ? 'dark' : 'light';
		localStorage.setItem('zonecontrol-theme', oscuro ? 'dark' : 'light');
	}

	function alternarTema() {
		aplicarTema(!temaOscuro);
	}

	onMount(() => {
		const temaGuardado = localStorage.getItem('zonecontrol-theme');
		const temaInicial =
			temaGuardado === 'dark' ||
			(temaGuardado !== 'light' && window.matchMedia('(prefers-color-scheme: dark)').matches);
		aplicarTema(temaInicial);
	});
</script>

<button
	class="theme-toggle"
	class:floating
	type="button"
	onclick={alternarTema}
	aria-label={temaOscuro ? 'Cambiar a modo claro' : 'Cambiar a modo oscuro'}
	title={temaOscuro ? 'Cambiar a modo claro' : 'Cambiar a modo oscuro'}
>
	{#if temaOscuro}
		<svg viewBox="0 0 24 24" aria-hidden="true">
			<circle cx="12" cy="12" r="4" />
			<path d="M12 2v2m0 16v2M4.93 4.93l1.42 1.42m11.3 11.3 1.42 1.42M2 12h2m16 0h2M4.93 19.07l1.42-1.42m11.3-11.3 1.42-1.42" />
		</svg>
		<span>Claro</span>
	{:else}
		<svg viewBox="0 0 24 24" aria-hidden="true">
			<path d="M20.2 15.2A8.5 8.5 0 0 1 8.8 3.8 8.7 8.7 0 1 0 20.2 15.2Z" />
		</svg>
		<span>Oscuro</span>
	{/if}
</button>

<style>
	.theme-toggle {
		display: inline-flex;
		align-items: center;
		justify-content: center;
		gap: 0.45rem;
		min-height: 40px;
		padding: 0.45rem 0.75rem;
		border: 1px solid var(--lab-border);
		border-radius: 999px;
		background: var(--lab-surface);
		color: var(--lab-text);
		font-size: 0.85rem;
		font-weight: 650;
		cursor: pointer;
		transition: background 160ms ease, border-color 160ms ease;
	}

	.theme-toggle:hover {
		background: var(--lab-bg);
	}

	svg {
		width: 18px;
		height: 18px;
		fill: none;
		stroke: currentColor;
		stroke-width: 1.8;
		stroke-linecap: round;
		stroke-linejoin: round;
	}

	.theme-toggle.floating {
		position: fixed;
		top: 1.25rem;
		right: 1.25rem;
		z-index: 1300;
	}

	@media (max-width: 768px) {
		.theme-toggle.floating {
			top: 0.75rem;
			right: 0.75rem;
		}

		.theme-toggle.floating span {
			display: none;
		}
	}
</style>
