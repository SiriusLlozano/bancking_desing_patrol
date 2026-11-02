import {
  apiAprobarRetiro,
  apiCerrarSesion,
  apiCrearCuenta,
  apiGenerarCodigoRetiro,
  apiListarCuentas,
  apiListarSolicitudesRetiro,
  apiListarTitulares,
  apiRegistrarMovimiento,
  apiTransferir
} from './api.js'
import { pintarCuentas } from './ui.js'
import { exigirSesion, cerrarSesion } from './auth.js'

const $ = (id) => document.getElementById(id)
const modal = $('modal-cuenta')
const formulario = $('form-crear-cuenta')
const resultado = $('resultado-cuenta')
const mensajeModal = $('mensaje-modal')
const modalOperacion = $('modal-operacion')
const formularioOperacion = $('form-operacion')
const resultadoOperacion = $('resultado-operacion')
const sesion = exigirSesion()
let tipoOperacion = null

if (sesion) {
  $('sesion-usuario').textContent = `Hola, ${sesion.nombre || sesion.username}`
  refrescarCuentas()
}

async function refrescarCuentas() {
  const cuerpo = $('tabla-cuerpo')
  try {
    const cuentas = await apiListarCuentas()
    pintarCuentas(cuentas, cuerpo)
    return cuentas
  } catch (error) {
    cuerpo.replaceChildren()
    const fila = document.createElement('tr')
    const celda = document.createElement('td')
    celda.colSpan = 4
    celda.className = 'vacio error'
    celda.textContent = error.message
    fila.append(celda)
    cuerpo.append(fila)
    return []
  }
}

function formatearDinero(valor) {
  return `S/ ${Number(valor).toLocaleString('es-PE', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`
}

async function cambiarVista(vista) {
  const vistas = ['cuentas', 'operaciones', 'solicitudes']
  for (const nombre of vistas) {
    $(`vista-${nombre}`).hidden = nombre !== vista
    $(`tab-${nombre}`).classList.toggle('activa', nombre === vista)
    $(`tab-${nombre}`).setAttribute('aria-selected', String(nombre === vista))
  }
  if (vista === 'solicitudes') await refrescarSolicitudes()
}

async function cargarCuentasEnSelector() {
  const cuentas = await refrescarCuentas()
  const selector = $('cuenta-operacion')
  selector.replaceChildren()
  for (const cuenta of cuentas) {
    const opcion = document.createElement('option')
    opcion.value = cuenta.numero
    opcion.textContent = `${cuenta.numero} · ${cuenta.tipo} · ${formatearDinero(cuenta.saldo)}`
    selector.append(opcion)
  }
  if (cuentas.length === 0) {
    const opcion = document.createElement('option')
    opcion.value = ''
    opcion.textContent = 'Apertura una cuenta antes de continuar'
    selector.append(opcion)
  }
  return cuentas
}

function abrirOperacion(tipo) {
  tipoOperacion = tipo
  const titulos = { DEPOSITO: 'Depositar', RETIRO: 'Retirar', TRANSFERENCIA: 'Transferir' }
  $('titulo-operacion').textContent = titulos[tipo]
  const ayudas = {
    DEPOSITO: 'Elige la cuenta donde deseas ingresar dinero.',
    RETIRO: 'El retiro podría requerir la aprobación de los otros titulares.',
    TRANSFERENCIA: 'Elige una cuenta propia e ingresa la cuenta destino.'
  }
  $('ayuda-operacion').textContent = ayudas[tipo]
  $('campo-cuenta-destino').hidden = tipo !== 'TRANSFERENCIA'
  $('cuenta-destino').required = tipo === 'TRANSFERENCIA'
  $('datos-codigo-retiro').hidden = true
  $('btn-confirmar-operacion').disabled = true
  cargarCuentasEnSelector().then((cuentas) => {
    $('btn-confirmar-operacion').disabled = cuentas.length === 0
  })
  modalOperacion.showModal()
}

function cerrarOperacion() {
  modalOperacion.close()
}

async function cargarTitulares() {
  const lista = $('lista-titulares')
  const ayuda = $('ayuda-titulares')
  lista.replaceChildren()
  ayuda.textContent = 'Cargando usuarios disponibles...'
  try {
    const titulares = await apiListarTitulares()
    if (titulares.length === 0) {
      ayuda.textContent = 'No hay otros usuarios registrados. Registra otra cuenta de usuario para compartir la cuenta.'
      return
    }

    for (const titular of titulares) {
      const etiqueta = document.createElement('label')
      etiqueta.className = 'opcion-titular'
      const casilla = document.createElement('input')
      casilla.type = 'checkbox'
      casilla.name = 'titularIds'
      casilla.value = titular.id
      const nombre = document.createElement('span')
      nombre.textContent = `${titular.nombre} (${titular.username})`
      etiqueta.append(casilla, nombre)
      lista.append(etiqueta)
    }
    ayuda.textContent = 'Marca al menos otro usuario. El titular de la sesión se agrega automáticamente.'
  } catch (error) {
    ayuda.textContent = `No se pudieron cargar los usuarios: ${error.message}`
  }
}

function pintarSolicitudes(solicitudes) {
  const lista = $('lista-solicitudes')
  lista.replaceChildren()
  if (!solicitudes.length) {
    const vacio = document.createElement('p')
    vacio.className = 'vacio'
    vacio.textContent = 'No tienes solicitudes pendientes ni retiros recientes.'
    lista.append(vacio)
    return
  }

  for (const solicitud of solicitudes) {
    const tarjeta = document.createElement('article')
    tarjeta.className = 'tarjeta-solicitud'
    const titulo = document.createElement('h2')
    titulo.textContent = `Retiro de ${formatearDinero(solicitud.monto)}`
    const cuenta = document.createElement('p')
    cuenta.textContent = `Cuenta ${solicitud.numeroCuenta} · Solicitó: ${solicitud.nombreSolicitante}`
    const estado = document.createElement('p')
    estado.className = 'estado-solicitud'
    const etiquetasEstado = {
      PENDIENTE: 'Pendiente de aprobación',
      AUTORIZADO: 'Aprobado',
      EJECUTADO: 'Retiro ejecutado'
    }
    estado.textContent = etiquetasEstado[solicitud.estado] || solicitud.estado
    tarjeta.append(titulo, cuenta, estado)

    if (solicitud.estado === 'PENDIENTE') {
      const progreso = document.createElement('p')
      progreso.textContent = `Aprobaciones: ${solicitud.aprobacionesRecibidas} de ${solicitud.aprobacionesRequeridas}`
      tarjeta.append(progreso)
    }

    if (solicitud.codigo) {
      const codigo = document.createElement('p')
      codigo.className = 'codigo-solicitud'
      codigo.textContent = `Código para el cajero: ${solicitud.codigo}`
      tarjeta.append(codigo)
      if (solicitud.venceEn) {
        const vence = document.createElement('small')
        vence.textContent = `Vence: ${new Date(solicitud.venceEn).toLocaleString('es-PE')}`
        tarjeta.append(vence)
      }
    }

    if (solicitud.puedeAprobar) {
      const aprobar = document.createElement('button')
      aprobar.className = 'btn btn-primario'
      aprobar.type = 'button'
      aprobar.dataset.aprobarId = solicitud.id
      aprobar.textContent = 'Aprobar retiro'
      tarjeta.append(aprobar)
    }
    lista.append(tarjeta)
  }
}

async function refrescarSolicitudes() {
  const lista = $('lista-solicitudes')
  try {
    pintarSolicitudes(await apiListarSolicitudesRetiro())
  } catch (error) {
    lista.replaceChildren()
    const mensaje = document.createElement('p')
    mensaje.className = 'mensaje error'
    mensaje.textContent = error.message
    lista.append(mensaje)
  }
}

$('tab-cuentas').addEventListener('click', () => cambiarVista('cuentas'))
$('tab-operaciones').addEventListener('click', () => cambiarVista('operaciones'))
$('tab-solicitudes').addEventListener('click', () => cambiarVista('solicitudes'))
$('vista-operaciones').addEventListener('click', (event) => {
  const boton = event.target.closest('[data-operation]')
  if (boton) abrirOperacion(boton.dataset.operation)
})
$('lista-solicitudes').addEventListener('click', async (event) => {
  const boton = event.target.closest('[data-aprobar-id]')
  if (!boton) return
  boton.disabled = true
  try {
    const resultadoAprobacion = await apiAprobarRetiro(boton.dataset.aprobarId)
    await refrescarSolicitudes()
    await refrescarCuentas()
    if (resultadoAprobacion.estado === 'AUTORIZADO') {
      $('lista-solicitudes').prepend(Object.assign(document.createElement('p'), {
        className: 'mensaje ok',
        textContent: 'Retiro aprobado por todos los titulares. El titular solicitante ya puede consultar el código.'
      }))
    }
  } catch (error) {
    boton.disabled = false
    const mensaje = document.createElement('p')
    mensaje.className = 'mensaje error'
    mensaje.textContent = error.message
    $('lista-solicitudes').prepend(mensaje)
  }
})

$('btn-cerrar-operacion').addEventListener('click', cerrarOperacion)
$('btn-cancelar-operacion').addEventListener('click', cerrarOperacion)
$('btn-listo-operacion').addEventListener('click', cerrarOperacion)

modalOperacion.addEventListener('click', (event) => {
  if (event.target === modalOperacion) cerrarOperacion()
})
modalOperacion.addEventListener('close', () => {
  formularioOperacion.reset()
  formularioOperacion.hidden = false
  $('encabezado-operacion').hidden = false
  $('campo-cuenta-destino').hidden = true
  resultadoOperacion.hidden = true
  $('error-operacion').hidden = true
  tipoOperacion = null
})

formularioOperacion.addEventListener('submit', async (event) => {
  event.preventDefault()
  const boton = $('btn-confirmar-operacion')
  const error = $('error-operacion')
  error.hidden = true
  boton.disabled = true
  boton.textContent = 'Procesando...'
  const monto = parseFloat($('monto-operacion').value)
  const numeroOrigen = $('cuenta-operacion').value

  try {
    let detalle
    let tituloResultado
    if (tipoOperacion === 'RETIRO') {
      const retiro = await apiGenerarCodigoRetiro(numeroOrigen, monto)
      if (retiro.estado === 'PENDIENTE') {
        tituloResultado = 'Retiro pendiente de aprobación'
        detalle = `Se solicitó retirar ${formatearDinero(monto)}. Los demás titulares deben aprobarlo antes de generar el código.`
      } else {
        const vencimiento = new Date(retiro.venceEn).toLocaleTimeString('es-PE', {
          hour: '2-digit',
          minute: '2-digit'
        })
        tituloResultado = 'Código de retiro generado'
        detalle = `Monto ${formatearDinero(monto)}. Código válido hasta las ${vencimiento}. El saldo se descontará al validarlo.`
        $('codigo-retiro').textContent = retiro.codigo
        $('datos-codigo-retiro').hidden = false
      }
      await refrescarSolicitudes()
    } else if (tipoOperacion === 'TRANSFERENCIA') {
      const transferencia = await apiTransferir({
        numeroOrigen,
        numeroDestino: $('cuenta-destino').value.trim(),
        monto
      })
      tituloResultado = 'Transferencia completada'
      detalle = `${formatearDinero(monto)} enviados a ${transferencia.numeroDestino}. Saldo disponible: ${formatearDinero(transferencia.saldoOrigen)}.`
    } else {
      const movimiento = await apiRegistrarMovimiento(numeroOrigen, tipoOperacion, monto)
      tituloResultado = 'Depósito realizado'
      detalle = `Nuevo saldo disponible: ${formatearDinero(movimiento.saldoResultante)}.`
    }
    $('texto-operacion').textContent = tituloResultado
    $('detalle-operacion').textContent = detalle
    $('encabezado-operacion').hidden = true
    formularioOperacion.hidden = true
    resultadoOperacion.hidden = false
    await refrescarCuentas()
  } catch (err) {
    error.textContent = err.message
    error.hidden = false
  } finally {
    boton.disabled = false
    boton.textContent = 'Confirmar operación'
  }
})

$('btn-abrir-cuenta').addEventListener('click', () => {
  modal.showModal()
  cargarTitulares()
})
$('btn-cerrar-modal').addEventListener('click', () => modal.close())
$('btn-cancelar-cuenta').addEventListener('click', () => modal.close())
$('btn-listo').addEventListener('click', () => modal.close())

for (const radio of formulario.elements.titularidad) {
  radio.addEventListener('change', () => {
    const mancomunada = formulario.elements.titularidad.value === 'MANCOMUNADA'
    $('campos-mancomunada').hidden = !mancomunada
  })
}

modal.addEventListener('click', (event) => {
  if (event.target === modal) modal.close()
})

modal.addEventListener('close', () => {
  formulario.reset()
  $('campos-mancomunada').hidden = true
  $('lista-titulares').replaceChildren()
  $('ayuda-titulares').textContent = 'Cargando usuarios disponibles...'
  $('encabezado-modal-cuenta').hidden = false
  formulario.hidden = false
  resultado.hidden = true
  mensajeModal.hidden = true
})

formulario.addEventListener('submit', async (event) => {
  event.preventDefault()
  mensajeModal.hidden = true
  const botonConfirmar = $('btn-confirmar-apertura')
  botonConfirmar.disabled = true
  botonConfirmar.textContent = 'Aperturando...'

  try {
    const titularidad = formulario.elements.titularidad.value
    const titularesIds = [...$('lista-titulares').querySelectorAll('input[name="titularIds"]:checked')]
      .map((casilla) => Number(casilla.value))
    if (titularidad === 'MANCOMUNADA' && titularesIds.length === 0) {
      throw new Error('Selecciona al menos otro titular para la cuenta mancomunada.')
    }
    const creada = await apiCrearCuenta({
      tipo: formulario.elements.tipo.value,
      saldoInicial: parseFloat($('cuenta-saldo').value),
      titularidad,
      titularesIds: titularidad === 'MANCOMUNADA' ? titularesIds : [],
      requiereAprobacion: titularidad === 'MANCOMUNADA' && $('requiere-aprobacion').checked
    })
    $('numero-asignado').textContent = creada.numero
    $('detalle-cuenta-creada').textContent = creada.titularidad === 'MANCOMUNADA'
      ? `Cuenta mancomunada con ${creada.titulares.length} titulares${creada.requiereAprobacion ? '; los retiros requieren aprobación de todos los demás titulares.' : '.'}`
      : 'Cuenta individual aperturada correctamente.'
    $('encabezado-modal-cuenta').hidden = true
    formulario.hidden = true
    resultado.hidden = false
    await refrescarCuentas()
  } catch (error) {
    mensajeModal.textContent = error.message
    mensajeModal.hidden = false
  } finally {
    botonConfirmar.disabled = false
    botonConfirmar.textContent = 'Confirmar apertura'
  }
})

$('btn-refrescar').addEventListener('click', refrescarCuentas)
$('btn-refrescar-solicitudes').addEventListener('click', refrescarSolicitudes)

$('btn-logout').addEventListener('click', () => {
  apiCerrarSesion()
    .catch(() => {})
    .finally(() => {
      cerrarSesion()
      window.location.href = 'index.html'
    })
})
