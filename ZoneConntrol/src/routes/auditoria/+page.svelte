<script lang="ts">
	import HistorialAccesos from '../../lib/components/HistorialAccesos.svelte';
	import ArchivoIntercambio from '../../lib/components/ArchivoIntercambio.svelte';

	let vista = $state<'accesos' | 'intercambio'>('accesos');
</script>

<svelte:head>
	<title>Auditoría y archivos | Zone Control</title>
</svelte:head>

<section class="unified-page">
	<header class="page-header">
		<h1>Auditoría y archivos de intercambio</h1>
		<p>Consulta el historial de accesos o genera archivos de actividad desde una sola pantalla.</p>
	</header>

	<div class="tabs" role="tablist" aria-label="Secciones de auditoría">
		<button
			id="tab-accesos"
			class:active={vista === 'accesos'}
			type="button"
			role="tab"
			aria-selected={vista === 'accesos'}
			aria-controls="panel-auditoria"
			onclick={() => (vista = 'accesos')}
		>
			Historial de accesos
		</button>
		<button
			id="tab-intercambio"
			class:active={vista === 'intercambio'}
			type="button"
			role="tab"
			aria-selected={vista === 'intercambio'}
			aria-controls="panel-auditoria"
			onclick={() => (vista = 'intercambio')}
		>
			Archivos de intercambio
		</button>
	</div>

	<div
		id="panel-auditoria"
		class="tab-panel"
		role="tabpanel"
		aria-labelledby={vista === 'accesos' ? 'tab-accesos' : 'tab-intercambio'}
	>
		{#if vista === 'accesos'}
			<HistorialAccesos />
		{:else}
			<ArchivoIntercambio />
		{/if}
	</div>
</section>

<style>
	.unified-page {
		max-width: 1200px;
		margin: 0 auto;
		padding: 1rem;
	}

	.page-header {
		margin: 0 auto 1.25rem;
		max-width: 1200px;
	}

	.page-header h1 {
		margin: 0 0 0.5rem;
		color: #0f172a;
		font-size: 1.75rem;
	}

	.page-header p {
		margin: 0;
		color: #64748b;
	}

	.tabs {
		display: flex;
		gap: 0.5rem;
		border-bottom: 1px solid #e2e8f0;
		margin: 0 auto;
		max-width: 1200px;
	}

	.tabs button {
		padding: 0.75rem 1rem;
		border: 1px solid transparent;
		border-bottom: 3px solid transparent;
		background: transparent;
		color: #475569;
		font: inherit;
		font-weight: 600;
		cursor: pointer;
	}

	.tabs button:hover {
		color: #1e6f9e;
	}

	.tabs button.active {
		color: #1e6f9e;
		border-bottom-color: #1e6f9e;
	}

	.tabs button:focus-visible {
		outline: 3px solid rgba(30, 111, 158, 0.25);
		outline-offset: 2px;
	}

	.tab-panel :global(.page) {
		max-width: 1200px;
		padding: 1.5rem 0 0;
	}

	.tab-panel :global(.page-header) {
		display: none;
	}

	@media (max-width: 600px) {
		.tabs {
			flex-direction: column;
			gap: 0;
		}

		.tabs button {
			text-align: left;
		}
	}
</style>
