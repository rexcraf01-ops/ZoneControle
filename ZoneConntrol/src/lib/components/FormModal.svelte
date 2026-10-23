<script lang="ts">
	import type { Snippet } from 'svelte';

	let {
		title,
		onClose,
		children
	}: {
		title: string;
		onClose: () => void;
		children: Snippet;
	} = $props();

	function handleKeydown(event: KeyboardEvent) {
		if (event.key === 'Escape') onClose();
	}
</script>

<div class="modal-overlay">
	<dialog open aria-modal="true" aria-labelledby="form-modal-title" class="modal" onkeydown={handleKeydown}>
		<header class="modal-header">
			<h2 id="form-modal-title">{title}</h2>
			<button class="close-button" type="button" aria-label="Cerrar ventana" onclick={onClose}>×</button>
		</header>
		<div class="modal-content">
			{@render children()}
		</div>
	</dialog>
</div>

<style>
	.modal-overlay {
		position: fixed;
		inset: 0;
		z-index: 1000;
		display: grid;
		place-items: center;
		padding: 1rem;
		background: rgba(15, 23, 42, 0.58);
		backdrop-filter: blur(4px);
	}

	.modal {
		position: relative;
		inset: auto;
		width: min(100%, 900px);
		max-width: none;
		max-height: min(90vh, 900px);
		margin: 0;
		padding: 0;
		overflow: hidden;
		border: 1px solid var(--lab-border);
		border-radius: var(--lab-radius);
		background: var(--lab-surface);
		color: var(--lab-text);
		box-shadow: var(--lab-shadow);
	}

	.modal-header {
		position: sticky;
		top: 0;
		z-index: 1;
		display: flex;
		align-items: center;
		justify-content: space-between;
		gap: 1rem;
		padding: 1rem 1.5rem;
		border-bottom: 1px solid var(--lab-border);
		background: var(--lab-surface);
	}

	.modal-header h2 {
		margin: 0;
		font-size: 1.2rem;
	}

	.close-button {
		display: grid;
		width: 2.5rem;
		height: 2.5rem;
		place-items: center;
		border: 0;
		border-radius: 0.5rem;
		background: transparent;
		color: var(--lab-text-muted);
		font-size: 1.75rem;
		line-height: 1;
		cursor: pointer;
	}

	.close-button:hover {
		background: var(--lab-bg);
		color: var(--lab-text);
	}

	.close-button:focus-visible {
		outline: 3px solid rgba(30, 111, 158, 0.3);
		outline-offset: 2px;
	}

	.modal-content {
		max-height: calc(min(90vh, 900px) - 4.5rem);
		overflow: auto;
	}
</style>
