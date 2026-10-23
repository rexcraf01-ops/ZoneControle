<script lang="ts">
	interface Props {
		id: string;
		label: string;
		value?: string;
		type?: string;
		name?: string;
		placeholder?: string;
		required?: boolean;
		maxlength?: number;
	}

	let {
		id,
		label,
		value = $bindable(''),
		type = 'text',
		name = id,
		placeholder = '',
		required = false,
		maxlength
	}: Props = $props();
</script>

<div class="form-group">
	<label for={id} class="form-label">{label}</label>
	<input
		{id}
		{name}
		{type}
		{placeholder}
		{required}
		{maxlength}
		bind:value
		class="form-input"
	/>
</div>

<style>
	.form-group {
		display: flex;
		flex-direction: column;
		gap: 0.5rem;
	}

	.form-label {
		font-size: 0.9rem;
		font-weight: 600;
		color: #0f172a;
	}

	.form-input {
		padding: 0.75rem 1rem;
		border: 1px solid #e2e8f0;
		border-radius: 0.5rem;
		font-size: 0.95rem;
		font-family: inherit;
		background: #f8fafc;
	}

	.form-input:focus {
		outline: none;
		border-color: #1e6f9e;
		box-shadow: 0 0 0 3px rgba(30, 111, 158, 0.15);
	}
</style>
