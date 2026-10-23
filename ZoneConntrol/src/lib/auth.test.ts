import { afterEach, describe, expect, it, vi } from 'vitest';
import {
	cambiarContrasena,
	iniciarSesion,
	normalizarRol,
	rutaInicialPorRol,
	restablecerContrasena,
	solicitarRecuperacion
} from './auth';

afterEach(() => {
	vi.unstubAllGlobals();
});

describe('autenticación', () => {
	it('envía las credenciales con cookies y devuelve el usuario autenticado', async () => {
		const loginBody = JSON.stringify({
			success: true,
			nombre_usuario: 'admin',
			rol: 'Administrador del sistema'
		});
		const fetchMock = vi
			.fn()
			.mockResolvedValueOnce(
				new Response(JSON.stringify({ success: true }), {
					status: 200,
					headers: { 'Content-Type': 'application/json' }
				})
			)
			.mockResolvedValueOnce(
				new Response(loginBody, { status: 200, headers: { 'Content-Type': 'application/json' } })
			)
			.mockResolvedValueOnce(
				new Response(loginBody, { status: 200, headers: { 'Content-Type': 'application/json' } })
			);
		vi.stubGlobal('fetch', fetchMock);

		await expect(iniciarSesion('admin', '1234')).resolves.toEqual({
			ok: true,
			user: { nombre_usuario: 'admin', rol: 'Administrador del sistema' }
		});
		expect(fetchMock).toHaveBeenNthCalledWith(
			1,
			expect.stringMatching(/\/auth\/logout$/),
			expect.objectContaining({ method: 'POST', credentials: 'include' })
		);
		expect(fetchMock).toHaveBeenNthCalledWith(
			2,
			expect.stringMatching(/\/auth\/login$/),
			expect.objectContaining({
				credentials: 'include',
				body: JSON.stringify({ nombre_usuario: 'admin', contrasena: '1234' })
			})
		);
		expect(fetchMock).toHaveBeenNthCalledWith(
			3,
			expect.stringMatching(/\/auth\/me$/),
			expect.objectContaining({ credentials: 'include' })
		);
	});

	it('rechaza una cookie que mantiene abierta una sesión de otro usuario', async () => {
		const fetchMock = vi
			.fn()
			.mockResolvedValueOnce(
				new Response(JSON.stringify({ success: true }), {
					status: 200,
					headers: { 'Content-Type': 'application/json' }
				})
			)
			.mockResolvedValueOnce(
				new Response(
					JSON.stringify({
						success: true,
						nombre_usuario: 'admin',
						rol: 'Administrador del sistema'
					}),
					{ status: 200, headers: { 'Content-Type': 'application/json' } }
				)
			)
			.mockResolvedValueOnce(
				new Response(
					JSON.stringify({
						success: true,
						nombre_usuario: 'gestor',
						rol: 'Gestor de personal'
					}),
					{ status: 200, headers: { 'Content-Type': 'application/json' } }
				)
			)
			.mockResolvedValueOnce(
				new Response(JSON.stringify({ success: true }), {
					status: 200,
					headers: { 'Content-Type': 'application/json' }
				})
			);
		vi.stubGlobal('fetch', fetchMock);

		await expect(iniciarSesion('admin', '1234')).resolves.toEqual({
			ok: false,
			mensaje:
				'La sesión activa no coincide con el usuario autenticado. Verifica el dominio y vuelve a intentarlo.'
		});
		expect(fetchMock).toHaveBeenNthCalledWith(
			4,
			expect.stringMatching(/\/auth\/logout$/),
			expect.objectContaining({ method: 'POST', credentials: 'include' })
		);
	});

	it('cierra la sesión si el backend devuelve un rol sin una vista autorizada', async () => {
		const unknownRole = JSON.stringify({
			success: true,
			nombre_usuario: 'operador',
			rol: 'Operador'
		});
		const fetchMock = vi
			.fn()
			.mockResolvedValueOnce(new Response('{}', { status: 200 }))
			.mockResolvedValueOnce(new Response(unknownRole, { status: 200 }))
			.mockResolvedValueOnce(new Response(unknownRole, { status: 200 }))
			.mockResolvedValueOnce(new Response('{}', { status: 200 }));
		vi.stubGlobal('fetch', fetchMock);

		await expect(iniciarSesion('operador', '1234')).resolves.toEqual({
			ok: false,
			mensaje: 'El usuario autenticado no tiene un rol habilitado para acceder al sistema.'
		});
		expect(fetchMock).toHaveBeenNthCalledWith(
			4,
			expect.stringMatching(/\/auth\/logout$/),
			expect.objectContaining({ method: 'POST', credentials: 'include' })
		);
	});

	it('normaliza los nombres de roles usados por el backend', () => {
		expect(normalizarRol('Administrador del sistema')).toBe('ADMIN');
		expect(normalizarRol('Gestor de personal')).toBe('GESTOR');
		expect(normalizarRol('Supervisor/Auditor')).toBe('SUPERVISOR');
		expect(normalizarRol(undefined)).toBe('UNKNOWN');
	});

	it('redirige cada rol a su vista operativa principal', () => {
		expect(rutaInicialPorRol('Administrador del sistema')).toBe('/usuarios-internos');
		expect(rutaInicialPorRol('Gestor de personal')).toBe('/buscar-personal');
		expect(rutaInicialPorRol('Supervisor/Auditor')).toBe('/auditoria');
		expect(rutaInicialPorRol(undefined)).toBe('/portal_publico');
	});

	it('envía las solicitudes de recuperación y cambio de contraseña con cookies incluidas', async () => {
		const fetchMock = vi.fn().mockImplementation(() =>
			Promise.resolve(
				new Response(JSON.stringify({ success: true, mensaje: 'ok' }), {
				status: 200,
				headers: { 'Content-Type': 'application/json' }
				})
			)
		);
		vi.stubGlobal('fetch', fetchMock);

		await solicitarRecuperacion(' usuario@example.com ');
		await restablecerContrasena('token-seguro', 'nueva-clave-segura');
		await cambiarContrasena('clave-anterior', 'nueva-clave-segura');

		expect(fetchMock).toHaveBeenNthCalledWith(
			1,
			expect.stringMatching(/\/auth\/password\/forgot$/),
			expect.objectContaining({
				method: 'POST',
				credentials: 'include',
				body: JSON.stringify({ email: 'usuario@example.com' })
			})
		);
		expect(fetchMock).toHaveBeenNthCalledWith(
			2,
			expect.stringMatching(/\/auth\/password\/reset$/),
			expect.objectContaining({
				method: 'POST',
				credentials: 'include',
				body: JSON.stringify({ token: 'token-seguro', nueva_contrasena: 'nueva-clave-segura' })
			})
		);
		expect(fetchMock).toHaveBeenNthCalledWith(
			3,
			expect.stringMatching(/\/auth\/change-password$/),
			expect.objectContaining({
				method: 'POST',
				credentials: 'include',
				body: JSON.stringify({
					contrasena_actual: 'clave-anterior',
					nueva_contrasena: 'nueva-clave-segura'
				})
			})
		);
	});
});
