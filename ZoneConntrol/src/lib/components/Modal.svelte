<script lang="ts">
	interface Props {
		open: boolean;
		title: string;
		message: string;
		confirmText?: string;
		cancelText?: string;
		onConfirm: () => void;
		onCancel: () => void;
	}

	let {
		open,
		title,
		message,
		confirmText = 'Confirmar',
		cancelText = 'Cancelar',
		onConfirm,
		onCancel
	}: Props = $props();
</script>

{#if open}
	<div class="modal-backdrop">
		<dialog open class="modal" aria-labelledby="modal-title">
			<h2 id="modal-title">{title}</h2>
			<p>{message}</p>
			<div class="modal-actions">
				<button type="button" class="cancel" onclick={onCancel}>{cancelText}</button>
				<button type="button" class="confirm" onclick={onConfirm}>{confirmText}</button>
			</div>
		</dialog>
	</div>
{/if}

<style>
	.modal-backdrop {
		position: fixed;
		inset: 0;
		z-index: 1000;
		display: grid;
		place-items: center;
		padding: 1rem;
		background: rgb(15 23 42 / 55%);
	}

	.modal {
		position: relative;
		margin: 0;
		width: min(100%, 28rem);
		padding: 1.5rem;
		border: 1px solid var(--lab-border);
		border-radius: var(--lab-radius);
		background: var(--lab-surface);
		box-shadow: 0 20px 25px -5px rgb(15 23 42 / 20%);
	}

	.modal h2 {
		margin: 0 0 0.75rem;
		font-size: 1.2rem;
		color: var(--lab-text);
	}

	.modal p {
		margin: 0;
		color: var(--lab-text-muted);
	}

	.modal-actions {
		display: flex;
		justify-content: flex-end;
		gap: 0.75rem;
		margin-top: 1.5rem;
	}

	.modal-actions button {
		padding: 0.65rem 1rem;
		border: 1px solid var(--lab-border);
		border-radius: 0.5rem;
		background: var(--lab-surface);
		color: var(--lab-text);
		font: inherit;
		cursor: pointer;
	}

	.modal-actions .confirm {
		border-color: #b91c1c;
		background: #b91c1c;
		color: #ffffff;
	}
</style>
