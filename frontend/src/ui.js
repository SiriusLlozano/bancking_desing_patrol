/** Renderiza la tabla de cuentas recibida desde el backend. */
export function pintarCuentas(cuentas, cuerpoTabla) {
  if (!cuentas || cuentas.length === 0) {
    cuerpoTabla.innerHTML = '<tr><td colspan="4" class="vacio">Aún no tienes cuentas. Apertura una cuenta para empezar.</td></tr>'
    return
  }

  const etiquetas = {
    AHORROS: 'Ahorros',
    CORRIENTE: 'Corriente',
    EMPRESARIAL: 'Empresarial'
  }

  cuerpoTabla.innerHTML = cuentas
    .map(
      (c) => `
      <tr>
        <td>${escapar(c.numero)}</td>
        <td><span class="etiqueta ${c.tipo.toLowerCase()}">${etiquetas[c.tipo] || c.tipo}</span></td>
        <td>${c.titularidad === 'MANCOMUNADA' ? 'Mancomunada' : 'Individual'}${c.requiereAprobacion ? ' · requiere aprobación' : ''}</td>
        <td class="derecha">${formatearDinero(c.saldo)}</td>
      </tr>`
    )
    .join('')
}

function escapar(valor) {
  return String(valor).replace(/[&<>"']/g, (caracter) => ({
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#39;'
  })[caracter])
}

function formatearDinero(valor) {
  return Number(valor).toLocaleString('es', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}
