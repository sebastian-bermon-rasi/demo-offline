// app.js (MVP sin frameworks)
// Trabaja contra el BRANCH (mismo origen). Si sirves el HTML desde el branch, usa rutas relativas.
const API = '/api/pacientes';

const els = {
    form: document.getElementById('formPaciente'),
    tipoDoc: document.getElementById('tipoDoc'),
    numDoc: document.getElementById('numDoc'),
    nombre: document.getElementById('nombre'),
    apellidos: document.getElementById('apellidos'),
    // birthDate: document.getElementById('birthDate'),
    tbody: document.querySelector('#tblPacientes tbody'),
    netStatus: document.getElementById('netStatus'),
    refreshBtn: document.getElementById('btnRefresh') || null
};

// ---- Utilidades UI ----
function setStatus(msg, type = 'info') {
    // Puedes estilizar por data-attr o clases. Aquí solo texto.
    if (els.netStatus) {
        els.netStatus.textContent = msg;
        els.netStatus.dataset.type = type; // para CSS [data-type="ok"|"warn"|"err"]
    }
}

function fmt(dateStr) {
    if (!dateStr) return '';
    try { return new Date(dateStr).toLocaleDateString(); } catch { return dateStr; }
}

function rowTemplate(p) {
    const deletedBadge = p.deleted ? ' <span class="badge badge-danger">eliminado</span>' : '';
    return `
    <tr data-id="${p.publicId}">
      <td>${p.tipoDoc || ''}</td>
      <td>${p.numDoc || ''}</td>
      <td>${p.nombre || ''}</td>
      <td>${p.apellidos || ''}</td>
      // <td>
      //   <button class="btn-del" ${p.deleted ? 'disabled' : ''} title="Eliminar">🗑️</button>
      // </td>
    </tr>
  `;
}

// ---- Estado de red (solo referencia del navegador, tu sync real es backend→central) ----
function updateOnlineStatus() {
    if (navigator.onLine) setStatus('Online (la sede está operando localmente)', 'ok');
    else setStatus('Offline (sigues trabajando en sede; el backend enviará cambios cuando vuelva la red)', 'warn');
}
window.addEventListener('online', updateOnlineStatus);
window.addEventListener('offline', updateOnlineStatus);

// ---- CRUD Pacientes (contra el branch) ----
async function listPacientes() {
    try {
        const res = await fetch(API, { headers: { 'Accept': 'application/json' } });
        if (!res.ok) throw new Error(`Error listando pacientes: ${res.status}`);
        const data = await res.json();
        els.tbody.innerHTML = data.map(rowTemplate).join('');
    } catch (err) {
        console.error(err);
        setStatus('No se pudo listar pacientes', 'err');
    }
}

async function createPaciente(payload) {
    const res = await fetch(API, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
        body: JSON.stringify(payload)
    });
    if (!res.ok) {
        const txt = await res.text().catch(() => '');
        throw new Error(`Error creando paciente: ${res.status} ${txt}`);
    }
    return res.json();
}

async function deletePaciente(id) {
    const res = await fetch(`${API}/${encodeURIComponent(id)}`, { method: 'DELETE' });
    if (!res.ok && res.status !== 204) {
        const txt = await res.text().catch(() => '');
        throw new Error(`Error eliminando paciente: ${res.status} ${txt}`);
    }
}

// ---- Handlers UI ----
els.form?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const payload = {
        numDoc: els.numDoc.value.trim(),
        nombre: els.nombre.value.trim(),
        apellidos: els.apellidos.value.trim()
        // El service en backend completa: publicId, version, branchId, updatedBy, deleted=false
    };
    if (!payload.numDoc || !payload.nombre) {
        setStatus('Num. doc y Nombre son obligatorios', 'warn');
        return;
    }
    try {
        await createPaciente(payload);
        els.form.reset();
        setStatus('Paciente creado. Se enviará al central cuando el scheduler ejecute.', 'ok');
        await listPacientes();
    } catch (err) {
        console.error(err);
        setStatus('Error creando paciente', 'err');
    }
});

els.tbody?.addEventListener('click', async (e) => {
    const btn = e.target.closest('.btn-del');
    if (!btn) return;
    const tr = btn.closest('tr');
    const id = tr?.dataset?.id;
    if (!id) return;
    if (!confirm('¿Eliminar (borrado lógico) este paciente?')) return;

    try {
        await deletePaciente(id);
        setStatus('Paciente marcado como eliminado. El evento se publicará al central.', 'ok');
        await listPacientes();
    } catch (err) {
        console.error(err);
        setStatus('Error eliminando paciente', 'err');
    }
});

els.refreshBtn?.addEventListener('click', listPacientes);

// ---- Init ----
(async function init() {
    updateOnlineStatus();
    await listPacientes();
    // (Opcional) auto-refresco cada 15s para ver cambios que vengan de otro cliente de la sede
    // setInterval(listPacientes, 15000);
})();
