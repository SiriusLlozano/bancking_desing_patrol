import { apiLogin } from './api.js'
import { guardarSesion } from './auth.js'

const $ = (id) => document.getElementById(id)
const mensaje = $('mensaje')

function mostrarMensaje(texto, tipo = 'error') {
  mensaje.textContent = texto
  mensaje.className = `mensaje ${tipo}`
  mensaje.hidden = false
}

$('form-login').addEventListener('submit', async (e) => {
  e.preventDefault()
  mensaje.hidden = true

  try {
    const sesion = await apiLogin(
      $('username').value.trim(),
      $('password').value.trim()
    )
    guardarSesion(sesion)
    window.location.href = 'dashboard.html'
  } catch (err) {
    mostrarMensaje(err.message)
  }
})
