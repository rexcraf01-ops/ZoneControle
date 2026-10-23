import { describe, expect, it, vi } from 'vitest';
import type { RequestEvent } from '@sveltejs/kit';
import { handle } from './hooks.server';

function mockEvent(path: string, token?: string, role?: string) {
	const event = {
		url: new URL(path, 'http://localhost'),
		cookies: {
			get: vi.fn(() => token),
			delete: vi.fn()
		},
		setHeaders: vi.fn(),
		fetch: vi.fn().mockResolvedValue(
			new Response(
				JSON.stringify({
					success: true,
					nombre_usuario: 'test-user',
					rol: role ?? 'Gestor de personal'
				}),
				{ status: 200, headers: { 'Content-Type': 'application/json' } }
			)
		),
		locals: {}
	};
	return event as unknown as RequestEvent;
}

describe('protección de rutas', () => {
	it('redirige al login cuando no hay sesión', async () => {
		const event = mockEvent('http://localhost/panel');
		const response = await handle({ event, resolve: vi.fn() });
		expect(response.status).toBe(303);
		expect(response.headers.get('location')).toContain('/iniciar-sesion');
		expect(response.headers.get('cache-control')).toBe('no-cache, no-store, must-revalidate');
		expect(response.headers.get('pragma')).toBe('no-cache');
		expect(response.headers.get('expires')).toBe('0');
		expect(event.setHeaders).toHaveBeenCalledWith({
			'cache-control': 'no-cache, no-store, must-revalidate',
			pragma: 'no-cache',
			expires: '0'
		});
	});

	it('redirige al panel cuando el rol no corresponde a la ruta', async () => {
		const event = mockEvent('http://localhost/usuarios-internos', 'session-token', 'Gestor de personal');
		const response = await handle({ event, resolve: vi.fn() });
		expect(response.status).toBe(303);
		expect(response.headers.get('location')).toContain(
			encodeURIComponent('La función no corresponde a su rol')
		);
		expect(response.headers.get('cache-control')).toBe('no-cache, no-store, must-revalidate');
	});

	it.each([
		['/usuarios-internos', 'Administrador del sistema'],
		['/carga-masiva', 'Administrador del sistema'],
		['/buscar-personal', 'Gestor de personal'],
		['/lector-credenciales', 'Gestor de personal'],
		['/auditoria', 'Supervisor/Auditor']
	])('permite %s al rol validado por el backend', async (path, role) => {
		const event = mockEvent(`http://localhost${path}`, 'session-token', role);
		const resolve = vi.fn().mockResolvedValue(new Response('ok'));

		await expect(handle({ event, resolve })).resolves.toBeInstanceOf(Response);
		expect(resolve).toHaveBeenCalledOnce();
		expect(event.locals.user).toEqual({ nombre_usuario: 'test-user', rol: role });
	});

	it('no permite gestión de usuarios a otros roles', async () => {
		const event = mockEvent(
			'http://localhost/usuarios-internos',
			'session-token',
			'Gestor de personal'
		);
		const response = await handle({ event, resolve: vi.fn() });
		expect(response.status).toBe(303);
		expect(response.headers.get('location')).toContain(
			encodeURIComponent('La función no corresponde a su rol')
		);
	});

	it('deniega carga masiva al gestor', async () => {
		const event = mockEvent('http://localhost/carga-masiva', 'session-token', 'Gestor de personal');
		const response = await handle({ event, resolve: vi.fn() });
		expect(response.status).toBe(303);
		expect(response.headers.get('location')).toContain(
			encodeURIComponent('La función no corresponde a su rol')
		);
	});

	it('requiere sesión para el lector de credenciales', async () => {
		const event = mockEvent('http://localhost/lector-credenciales');
		const resolve = vi.fn();
		const response = await handle({ event, resolve });

		expect(response.status).toBe(303);
		expect(response.headers.get('location')).toContain('/iniciar-sesion');
		expect(resolve).not.toHaveBeenCalled();
	});

	it('permite al administrador las rutas destinadas a otros roles', async () => {
		const routes = ['/carga-masiva', '/buscar-personal', '/lector-credenciales', '/auditoria'];
		for (const path of routes) {
			const event = mockEvent(path, 'session-token', 'Administrador del sistema');
			const resolve = vi.fn().mockResolvedValue(new Response('ok'));
			await expect(handle({ event, resolve })).resolves.toBeInstanceOf(Response);
		}
	});
});
