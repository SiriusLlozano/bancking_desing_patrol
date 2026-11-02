import { apiRegistro } from './api.js'

const $ = (id) => document.getElementById(id)
const mensaje = $('mensaje')

$('form-registro').addEventListener('submit', async (e) => {
  e.preventDefault()
  mensaje.hidden = true

  try {
    await apiRegistro(
      $('nombre').value.trim(),
      $('username').value.trim(),
      $('password').value.trim()
    )
    mensaje.textContent = 'Usuario registrado correctamente. Redirigiendo al login…'
    mensaje.className = 'mensaje ok'
    mensaje.hidden = false
    setTimeout(() => {
      window.location.href = 'index.html'
    }, 1200)
  } catch (err) {
    mensaje.textContent = err.message
    mensaje.className = 'mensaje error'
    mensaje.hidden = false
  }
})
