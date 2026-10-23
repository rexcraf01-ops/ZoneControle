import { error } from '@sveltejs/kit';
import type { Handle } from '@sveltejs/kit/hooks';

const API_BASE = (import.meta.env.VITE_API_URL || 'http://localhost:8081/api').replace(/\/$/, '');
const PUBLIC_ROUTES = new Set([
	'/',
	'/portal_publico',
	'/iniciar-sesion',
	'/recuperar-cuenta',
	'/restablecer-contrasena'
]);
const STATIC_PREFIXES = ['/_app', '/logo.png', '/favicon'];

const ROLE_ROUTES: Record<string, 'ADMIN' | 'GESTOR' | 'SUPERVISOR'> = {
	'/usuarios-internos': 'ADMIN',
	'/carga-masiva': 'ADMIN',
	'/registrar-personal': 'GESTOR',
	'/buscar-personal': 'GESTOR',
	'/actualizar-personal': 'GESTOR',
	'/lector-credenciales': 'GESTOR',
	'/auditoria': 'SUPERVISOR'
};

function normalizeRole(role: string): 'ADMIN' | 'GESTOR' | 'SUPERVISOR' | 'UNKNOWN' {
	const normalized = role.toUpperCase();
	if (normalized.includes('ADMIN')) return 'ADMIN';
	if (normalized.includes('GESTOR')) return 'GESTOR';
	if (normalized.includes('SUPERVISOR') || normalized.includes('AUDITOR')) return 'SUPERVISOR';
	return 'UNKNOWN';
}

function redirectWithoutCache(location: string): Response {
	return new Response(null, {
		status: 303,
		headers: {
			location,
			'cache-control': 'no-cache, no-store, must-revalidate',
			pragma: 'no-cache',
			expires: '0'
		}
	});
}

export const handle: Handle = async ({ event, resolve }) => {
	const pathname = event.url.pathname;
	const isPublic = PUBLIC_ROUTES.has(pathname);
	if (STATIC_PREFIXES.some((prefix) => pathname.startsWith(prefix))) {
		return resolve(event);
	}
	event.setHeaders({
		'cache-control': 'no-cache, no-store, must-revalidate',
		pragma: 'no-cache',
		expires: '0'
	});

	const token = event.cookies.get('session_token');
	if (!token) {
		if (isPublic) return resolve(event);
		return redirectWithoutCache(
			`/iniciar-sesion?mensaje=${encodeURIComponent('Debe iniciar sesión para acceder al sistema')}`
		);
	}

	let response: Response;
	try {
		const apiUrl = new URL(API_BASE);
		const localHosts = new Set(['localhost', '127.0.0.1']);
		if (localHosts.has(apiUrl.hostname) && localHosts.has(event.url.hostname)) {
			apiUrl.hostname = event.url.hostname;
		}
		response = await event.fetch(`${apiUrl.toString().replace(/\/$/, '')}/auth/me`, {
			headers: { Authorization: `Bearer ${token}` }
		});
	} catch {
		if (isPublic) return resolve(event);
		throw error(503, 'No se pudo conectar con el servicio de autenticación. Intente más tarde.');
	}

	if (!response.ok) {
		event.cookies.delete('session_token', { path: '/' });
		if (isPublic) return resolve(event);
		return redirectWithoutCache(
			`/iniciar-sesion?mensaje=${encodeURIComponent('Su sesión venció. Inicie sesión nuevamente')}`
		);
	}

	const data = await response.json();
	if (!data.success || typeof data.nombre_usuario !== 'string' || typeof data.rol !== 'string') {
		event.cookies.delete('session_token', { path: '/' });
		if (isPublic) return resolve(event);
		return redirectWithoutCache(
			`/iniciar-sesion?mensaje=${encodeURIComponent('Su sesión no es válida. Inicie sesión nuevamente')}`
		);
	}

	event.locals.user = { nombre_usuario: data.nombre_usuario, rol: data.rol };

	const requiredRole = Object.entries(ROLE_ROUTES).find(([route]) => pathname.startsWith(route))?.[1];
	const currentRole = normalizeRole(data.rol);
	if (requiredRole && currentRole !== 'ADMIN' && currentRole !== requiredRole) {
		return redirectWithoutCache(`/panel?error=${encodeURIComponent('La función no corresponde a su rol')}`);
	}

	return resolve(event);
};
