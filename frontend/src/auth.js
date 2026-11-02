// Utilidades de sesión compartidas por las 3 páginas.

export function obtenerSesion() {
  try {
    return JSON.parse(localStorage.getItem('sesion'))
  } catch {
    return null
  }
}

export function guardarSesion(datos) {
  localStorage.setItem('sesion', JSON.stringify(datos))
}

export function cerrarSesion() {
  localStorage.removeItem('sesion')
}

/**
 * Protege una página: si no hay sesión, redirige al login.
 * Devuelve la sesión o null.
 */
export function exigirSesion() {
  const sesion = obtenerSesion()
  if (!sesion) {
    window.location.replace('index.html')
    return null
  }
  return sesion
}
