<script lang="ts">
	let {
		action,
		onAction,
		disabled = false
	}: {
		action: 'edit' | 'delete';
		onAction: () => void;
		disabled?: boolean;
	} = $props();

	const label = $derived(action === 'edit' ? 'Editar' : 'Eliminar');
</script>

<button
	class="icon-button"
	class:delete-action={action === 'delete'}
	type="button"
	aria-label={label}
	title={label}
	{disabled}
	onclick={onAction}
>
	{#if action === 'edit'}
		<svg viewBox="0 0 24 24" aria-hidden="true">
			<rect x="3" y="3" width="18" height="18" rx="4" fill="#cfeaff" stroke="#202124" stroke-width="2" />
			<path d="m8 15.8 1.1-3.7 7.1-7.1a1.4 1.4 0 0 1 2 2l-7.1 7.1L8 15.8Z" fill="#159be8" stroke="#202124" stroke-linejoin="round" stroke-width="1.6" />
			<path d="m14.9 6.2 2.9 2.9" stroke="#202124" stroke-width="1.5" />
		</svg>
	{:else}
		<svg viewBox="0 0 24 24" aria-hidden="true">
			<path d="M9 3h6l1 2h4v2H4V5h4l1-2Zm-3 6h12l-1 12H7L6 9Zm3 2v8h2v-8H9Zm4 0v8h2v-8h-2Z" fill="#f00000" />
		</svg>
	{/if}
</button>

<style>
	.icon-button {
		display: inline-flex;
		width: 30px;
		height: 30px;
		align-items: center;
		justify-content: center;
		padding: 4px;
		border: 0;
		border-radius: 6px;
		background: transparent;
		cursor: pointer;
		vertical-align: middle;
	}

	.icon-button:hover {
		background: #eff6ff;
	}

	.icon-button.delete-action:hover {
		background: #fef2f2;
	}

	.icon-button:focus-visible {
		outline: 2px solid #1e6f9e;
		outline-offset: 2px;
	}

	.icon-button:disabled {
		cursor: not-allowed;
		opacity: 0.5;
	}

	svg {
		display: block;
		width: 20px;
		height: 20px;
	}
</style>
