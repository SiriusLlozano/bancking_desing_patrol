const API = 'http://localhost:8080/api'

/**
 * Envía una petición al backend Spring Boot.
 * Si hay sesión activa, agrega el token demo en la cabecera Authorization.
 */
async function pedir(ruta, opciones = {}) {
  const cabeceras = {
    'Content-Type': 'application/json',
    ...(opciones.headers || {})
  }

  const sesion = JSON.parse(localStorage.getItem('sesion') || 'null')
  if (sesion && sesion.token) {
    cabeceras['Authorization'] = `Bearer ${sesion.token}`
  }

  let respuesta
  try {
    respuesta = await fetch(`${API}${ruta}`, { ...opciones, headers: cabeceras })
  } catch {
    throw new Error('No se pudo conectar con el backend. ¿Está corriendo en el puerto 8080?')
  }

  const texto = await respuesta.text()
  const datos = texto ? JSON.parse(texto) : null

  if (!respuesta.ok) {
    throw new Error(datos?.message || datos?.error || `Error HTTP ${respuesta.status}`)
  }
  return datos
}

export function apiLogin(username, password) {
  return pedir('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username, password })
  })
}

export function apiCerrarSesion() {
  return pedir('/auth/logout', { method: 'POST' })
}

export function apiRegistro(nombre, username, password) {
  return pedir('/auth/register', {
    method: 'POST',
    body: JSON.stringify({ username, password, nombre })
  })
}

export function apiCrearCuenta(cuenta) {
  return pedir('/cuentas', {
    method: 'POST',
    body: JSON.stringify(cuenta)
  })
}

export function apiListarCuentas() {
  return pedir('/cuentas')
}

export function apiListarTitulares() {
  return pedir('/usuarios/titulares')
}

export function apiListarSolicitudesRetiro() {
  return pedir('/retiros/solicitudes')
}

export function apiAprobarRetiro(retiroId) {
  return pedir(`/retiros/${retiroId}/aprobar`, { method: 'POST' })
}

export function apiRegistrarMovimiento(numeroCuenta, tipo, monto) {
  return pedir(`/cuentas/${encodeURIComponent(numeroCuenta)}/movimientos`, {
    method: 'POST',
    body: JSON.stringify({ tipo, monto })
  })
}

export function apiListarMovimientos(numeroCuenta) {
  return pedir(`/cuentas/${encodeURIComponent(numeroCuenta)}/movimientos`)
}

export function apiTransferir(transferencia) {
  return pedir('/transferencias', {
    method: 'POST',
    body: JSON.stringify(transferencia)
  })
}

export function apiGenerarCodigoRetiro(numeroCuenta, monto) {
  return pedir(`/cuentas/${encodeURIComponent(numeroCuenta)}/retiros`, {
    method: 'POST',
    body: JSON.stringify({ monto })
  })
}
