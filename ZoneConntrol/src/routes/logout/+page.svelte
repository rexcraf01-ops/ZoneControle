<script lang="ts">
	import { onMount } from 'svelte';
	import { cerrarSesion } from '../../lib/auth';

	let error = $state('');
	let cargando = $state(true);

	async function salir() {
		cargando = true;
		error = '';
		try {
			await cerrarSesion();
			window.location.assign('/portal_publico');
		} catch (cause) {
			error = cause instanceof Error ? cause.message : 'No se pudo cerrar la sesión.';
		} finally {
			cargando = false;
		}
	}

	onMount(() => {
		void salir();
	});
</script>

<div style="text-align:center; padding: 2rem;">
	{#if cargando}
		<p>Cerrando sesión...</p>
	{:else if error}
		<p role="alert">{error}</p>
		<button type="button" onclick={salir}>Reintentar</button>
	{/if}
</div>
