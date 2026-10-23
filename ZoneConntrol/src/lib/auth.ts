export const API_BASE = (import.meta.env.VITE_API_URL || 'http://localhost:8081/api').replace(/\/$/, '');

function getApiBase(): string {
	if (typeof window === 'undefined') return API_BASE;

	const apiUrl = new URL(API_BASE);
	const localHosts = new Set(['localhost', '127.0.0.1']);
	if (localHosts.has(apiUrl.hostname) && localHosts.has(window.location.hostname)) {
		apiUrl.hostname = window.location.hostname;
	}
	return apiUrl.toString().replace(/\/$/, '');
}

export interface UsuarioSesion {
	nombre_usuario: string;
	rol: string;
}

interface ApiErrorResponse {
	mensaje?: string;
	message?: string;
	detail?: string;
}

const CONNECTION_ERROR_MESSAGE =
	'No se pudo conectar con el servidor. Verifique que el backend esté disponible.';

async function invalidarSesionInconsistente(mensaje: string) {
	try {
		await cerrarSesion();
		return { ok: false as const, mensaje };
	} catch (cause) {
		const detalle = cause instanceof Error ? cause.message : 'Error desconocido';
		return { ok: false as const, mensaje: `${mensaje} No se pudo cerrar la sesión recibida: ${detalle}` };
	}
}

export async function iniciarSesion(
	nombreUsuario: string,
	contrasena: string
): Promise<{ ok: boolean; mensaje?: string; user?: UsuarioSesion }> {
	try {
		await cerrarSesion();
	} catch (cause) {
		return {
			ok: false,
			mensaje: cause instanceof Error ? cause.message : 'No se pudo limpiar la sesión anterior.'
		};
	}

	let response: Response;
	try {
		response = await fetch(`${getApiBase()}/auth/login`, {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			credentials: 'include',
			body: JSON.stringify({ nombre_usuario: nombreUsuario.trim(), contrasena })
		});
	} catch (cause) {
		if (cause instanceof TypeError) return { ok: false, mensaje: CONNECTION_ERROR_MESSAGE };
		throw cause;
	}

	let body: {
		success?: boolean;
		mensaje?: string;
		nombre_usuario?: string;
		rol?: string;
	};
	try {
		body = await response.json();
	} catch {
		return { ok: false, mensaje: 'El servidor devolvió una respuesta no válida.' };
	}

	if (!response.ok || !body.success) {
		return { ok: false, mensaje: body.mensaje ?? 'Usuario o contraseña inválidos' };
	}

	if (typeof body.nombre_usuario !== 'string' || typeof body.rol !== 'string' || !body.rol) {
		return invalidarSesionInconsistente('El servidor devolvió una sesión sin usuario o rol válido.');
	}

	let sessionResponse: Response;
	try {
		sessionResponse = await apiFetch('/auth/me');
	} catch (cause) {
		if (cause instanceof TypeError) {
			return { ok: false, mensaje: CONNECTION_ERROR_MESSAGE };
		}
		throw cause;
	}
	if (!sessionResponse.ok) {
		return invalidarSesionInconsistente(
			'El inicio de sesión respondió correctamente, pero no se pudo validar la nueva sesión.'
		);
	}

	let session: {
		success?: boolean;
		nombre_usuario?: string;
		rol?: string;
	};
	try {
		session = await sessionResponse.json();
	} catch {
		return invalidarSesionInconsistente('El servidor devolvió una sesión no válida.');
	}

	if (
		!session.success ||
		typeof session.nombre_usuario !== 'string' ||
		typeof session.rol !== 'string' ||
		session.nombre_usuario !== body.nombre_usuario ||
		normalizarRol(session.rol) !== normalizarRol(body.rol)
	) {
		return invalidarSesionInconsistente(
			'La sesión activa no coincide con el usuario autenticado. Verifica el dominio y vuelve a intentarlo.'
		);
	}

	if (normalizarRol(session.rol) === 'UNKNOWN') {
		return invalidarSesionInconsistente(
			'El usuario autenticado no tiene un rol habilitado para acceder al sistema.'
		);
	}

	return { ok: true, user: { nombre_usuario: session.nombre_usuario, rol: session.rol } };
}

export async function cerrarSesion(): Promise<void> {
	const response = await apiFetch('/auth/logout', { method: 'POST' });
	if (!response.ok) {
		throw new Error(await getApiError(response));
	}
}

export async function solicitarRecuperacion(email: string): Promise<void> {
	await apiJson<void>('/auth/password/forgot', {
		method: 'POST',
		body: JSON.stringify({ email: email.trim() })
	});
}

export async function restablecerContrasena(token: string, nuevaContrasena: string): Promise<void> {
	await apiJson<void>('/auth/password/reset', {
		method: 'POST',
		body: JSON.stringify({ token, nueva_contrasena: nuevaContrasena })
	});
}

export async function cambiarContrasena(
	contrasenaActual: string,
	nuevaContrasena: string
): Promise<void> {
	const response = await apiFetch('/auth/change-password', {
		method: 'POST',
		body: JSON.stringify({
			contrasena_actual: contrasenaActual,
			nueva_contrasena: nuevaContrasena
		})
	});
	if (!response.ok) throw new Error(await getApiError(response));
}

export async function verificarSesion(): Promise<boolean> {
	try {
		const response = await apiFetch('/auth/me');
		if (!response.ok) return false;

		const data = await response.json();
		if (!data.success) return false;

		return typeof data.nombre_usuario === 'string' && typeof data.rol === 'string';
	} catch {
		return false;
	}
}

export function normalizarRol(rol: string | undefined): 'ADMIN' | 'GESTOR' | 'SUPERVISOR' | 'UNKNOWN' {
	if (!rol) return 'UNKNOWN';
	const value = rol.toUpperCase();
	if (value.includes('ADMIN')) return 'ADMIN';
	if (value.includes('GESTOR')) return 'GESTOR';
	if (value.includes('SUPERVISOR') || value.includes('AUDITOR')) return 'SUPERVISOR';
	return 'UNKNOWN';
}

export function rutaInicialPorRol(rol: string | undefined): string {
	switch (normalizarRol(rol)) {
		case 'ADMIN':
			return '/usuarios-internos';
		case 'GESTOR':
			return '/buscar-personal';
		case 'SUPERVISOR':
			return '/auditoria';
		default:
			return '/portal_publico';
	}
}

export async function apiFetch(path: string, options: RequestInit = {}): Promise<Response> {
	const headers = new Headers(options.headers);
	if (options.body && !(options.body instanceof FormData) && !headers.has('Content-Type')) {
		headers.set('Content-Type', 'application/json');
	}

	try {
		return await fetch(`${getApiBase()}${path.startsWith('/') ? path : `/${path}`}`, {
			...options,
			headers,
			credentials: 'include'
		});
	} catch (cause) {
		if (cause instanceof TypeError) throw new Error(CONNECTION_ERROR_MESSAGE);
		throw cause;
	}
}

export async function apiJson<T>(path: string, options: RequestInit = {}): Promise<T> {
	const response = await apiFetch(path, options);
	if (!response.ok) {
		throw new Error(await getApiError(response));
	}
	return (await response.json()) as T;
}

export async function getApiError(response: Response): Promise<string> {
	try {
		const body = (await response.json()) as ApiErrorResponse;
		return body.mensaje ?? body.message ?? body.detail ?? `La solicitud falló (${response.status})`;
	} catch {
		return `La solicitud falló (${response.status})`;
	}
}
