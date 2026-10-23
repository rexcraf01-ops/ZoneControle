<script lang="ts">
	import type { Snippet } from 'svelte';

	let {
		children,
		className = ''
	}: {
		children: Snippet;
		className?: string;
	} = $props();
</script>

<section class={`data-card ${className}`}>
	{@render children()}
</section>

<style>
	.data-card {
		background: var(--lab-surface);
		border: 1px solid var(--lab-border);
		border-radius: var(--lab-radius);
		padding: 1.5rem;
		box-shadow: var(--lab-shadow);
	}

	:global(.data-card.card h2) {
		margin-top: 0;
		font-size: 1.1rem;
		font-weight: 600;
		text-align: center;
		color: var(--lab-text);
	}
</style>
