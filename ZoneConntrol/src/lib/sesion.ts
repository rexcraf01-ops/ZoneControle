// src/lib/sesion.ts
// Esta sesión del cliente ha sido deshabilitada.
// La autenticación ahora se maneja en el servidor Java Spring Boot mediante cookies HttpOnly y JWT.
// Consulta: backend/src/main/java/com/lab/zonecontrol/security/ y src/hooks.server.ts

export const sesion = null as unknown as { subscribe: () => void };

export function iniciarSesion() {
	throw new Error('La sesión se maneja en el servidor Java. No use localStorage.');
}

export function cerrarSesion() {
	throw new Error('La sesión se maneja en el servidor Java. No use localStorage.');
}

export function restaurarSesion() {
	// No-op: la sesión se restaura desde la cookie en +layout.server.ts
}