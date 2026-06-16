const API = 'http://localhost:8080/api/expenses';

let barChartInstance = null;
let pieChartInstance = null;

// ══════════════════════════════════════════════════════════════════
//  AUTH — Check login & Logout
// ══════════════════════════════════════════════════════════════════
async function checkAuth() {
  try {
    const res = await fetch('http://localhost:8080/api/auth/me', {
      method: 'GET',
      credentials: 'include'
    });
    if (!res.ok) {
      window.location.replace('login.html');
      return;
    }
    const data = await res.json();
    document.getElementById('welcomeUser').textContent = '👤 ' + data.username;
  } catch (e) {
    window.location.replace('login.html');
  }
}

async function doLogout() {
  try {
    await fetch('http://localhost:8080/api/auth/logout', {
      method: 'POST',
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' }
    });
  } catch (e) {
    // even if request fails, redirect to login
  } finally {
    window.location.replace('login.html');
  }
}

// ══════════════════════════════════════════════════════════════════
//  SHOW / HIDE SECTIONS
// ══════════════════════════════════════════════════════════════════
function showSection(id) {
  document.querySelectorAll('.section').forEach(s => s.classList.remove('active'));
  document.getElementById(id).classList.add('active');
  if (id === 'entries') loadAllEntries();
  if (id === 'dashboard') loadDashboard();
}

// ══════════════════════════════════════════════════════════════════
//  TOAST NOTIFICATION
// ══════════════════════════════════════════════════════════════════
function showToast(msg, color = '#333') {
  const t = document.getElementById('toast');
  t.textContent = msg;
  t.style.background = color;
  t.style.display = 'block';
  setTimeout(() => t.style.display = 'none', 2500);
}

// ══════════════════════════════════════════════════════════════════
//  DASHBOARD
// ══════════════════════════════════════════════════════════════════
async function loadDashboard() {
  const year  = document.getElementById('filterYear').value;
  const month = document.getElementById('filterMonth').value;

  try {
    const res  = await fetch(`${API}/summary?year=${year}&month=${month}`, {
      credentials: 'include'
    });

    if (res.status === 401) { window.location.replace('login.html'); return; }

    const data = await res.json();

    document.getElementById('totalIncome').textContent  = '₹' + data.totalIncome.toFixed(2);
    document.getElementById('totalExpense').textContent = '₹' + data.totalExpense.toFixed(2);

    const bal   = data.balance;
    const balEl = document.getElementById('balance');
    balEl.textContent = '₹' + bal.toFixed(2);
    balEl.style.color = bal >= 0 ? '#34a853' : '#ea4335';

    drawBarChart(data.totalIncome, data.totalExpense);
    drawPieChart(data.categoryBreakdown);
  } catch (err) {
    showToast('Error loading dashboard', '#ea4335');
  }
}

function drawBarChart(income, expense) {
  if (barChartInstance) barChartInstance.destroy();
  const ctx = document.getElementById('barChart').getContext('2d');
  barChartInstance = new Chart(ctx, {
    type: 'bar',
    data: {
      labels: ['Income', 'Expense'],
      datasets: [{
        label: 'Amount (₹)',
        data: [income, expense],
        backgroundColor: ['rgba(52,168,83,0.7)', 'rgba(234,67,53,0.7)'],
        borderColor:     ['#34a853', '#ea4335'],
        borderWidth: 2,
        borderRadius: 6,
      }]
    },
    options: {
      responsive: true,
      plugins: { legend: { display: false } },
      scales: { y: { beginAtZero: true } }
    }
  });
}

function drawPieChart(breakdown) {
  if (pieChartInstance) pieChartInstance.destroy();
  const labels = Object.keys(breakdown);
  const values = Object.values(breakdown);
  if (labels.length === 0) return;

  const colors = ['#1a73e8','#ea4335','#fbbc04','#34a853','#9c27b0',
                  '#ff5722','#00bcd4','#8bc34a','#ff9800','#795548'];

  const ctx = document.getElementById('pieChart').getContext('2d');
  pieChartInstance = new Chart(ctx, {
    type: 'pie',
    data: {
      labels,
      datasets: [{
        data: values,
        backgroundColor: colors.slice(0, labels.length),
        borderWidth: 2,
      }]
    },
    options: {
      responsive: true,
      plugins: { legend: { position: 'bottom' } }
    }
  });
}

// ══════════════════════════════════════════════════════════════════
//  ALL ENTRIES TABLE
// ══════════════════════════════════════════════════════════════════
async function loadAllEntries() {
  try {
    const res = await fetch(API, { credentials: 'include' });

    if (res.status === 401) { window.location.replace('login.html'); return; }

    const entries = await res.json();
    const tbody   = document.getElementById('entriesBody');
    tbody.innerHTML = '';

    if (entries.length === 0) {
      tbody.innerHTML = `<tr><td colspan="8" style="text-align:center;padding:20px;color:#888;">No entries found. Add some!</td></tr>`;
      return;
    }

    entries.sort((a, b) => new Date(b.date) - new Date(a.date));

    entries.forEach(e => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${e.id}</td>
        <td>${e.title}</td>
        <td>₹${e.amount.toFixed(2)}</td>
        <td><span class="badge ${e.type.toLowerCase()}">${e.type}</span></td>
        <td>${e.category}</td>
        <td>${e.date}</td>
        <td>${e.note || '-'}</td>
        <td>
          <button class="btn-edit"   onclick="editEntry(${e.id})">✏ Edit</button>
          <button class="btn-delete" onclick="deleteEntry(${e.id})">🗑 Delete</button>
        </td>`;
      tbody.appendChild(tr);
    });
  } catch (err) {
    showToast('Error loading entries', '#ea4335');
  }
}

// ══════════════════════════════════════════════════════════════════
//  ADD / EDIT FORM
// ══════════════════════════════════════════════════════════════════
async function submitForm(e) {
  e.preventDefault();

  const id      = document.getElementById('editId').value;
  const payload = {
    title:    document.getElementById('title').value,
    amount:   parseFloat(document.getElementById('amount').value),
    type:     document.getElementById('type').value,
    category: document.getElementById('category').value,
    date:     document.getElementById('date').value,
    note:     document.getElementById('note').value,
  };

  try {
    if (id) {
      await fetch(`${API}/${id}`, {
        method: 'PUT',
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });
      showToast('Entry updated!', '#34a853');
    } else {
      await fetch(API, {
        method: 'POST',
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });
      showToast('Entry added!', '#34a853');
    }
    resetForm();
    showSection('entries');
  } catch (err) {
    showToast('Error saving entry', '#ea4335');
  }
}

async function editEntry(id) {
  try {
    const res = await fetch(`${API}/${id}`, { credentials: 'include' });

    if (res.status === 401) { window.location.replace('login.html'); return; }

    const e = await res.json();

    document.getElementById('editId').value   = e.id;
    document.getElementById('title').value    = e.title;
    document.getElementById('amount').value   = e.amount;
    document.getElementById('type').value     = e.type;
    document.getElementById('category').value = e.category;
    document.getElementById('date').value     = e.date;
    document.getElementById('note').value     = e.note || '';

    document.getElementById('formTitle').textContent = 'Edit Entry';
    showSection('add');
  } catch (err) {
    showToast('Error loading entry', '#ea4335');
  }
}

async function deleteEntry(id) {
  if (!confirm('Delete this entry?')) return;
  try {
    await fetch(`${API}/${id}`, {
      method: 'DELETE',
      credentials: 'include'
    });
    showToast('Deleted!', '#ea4335');
    loadAllEntries();
  } catch (err) {
    showToast('Error deleting entry', '#ea4335');
  }
}

function resetForm() {
  document.getElementById('editId').value   = '';
  document.getElementById('title').value    = '';
  document.getElementById('amount').value   = '';
  document.getElementById('type').value     = '';
  document.getElementById('category').value = '';
  document.getElementById('date').value     = '';
  document.getElementById('note').value     = '';
  document.getElementById('formTitle').textContent = 'Add New Entry';
}

// ══════════════════════════════════════════════════════════════════
//  ON PAGE LOAD
// ══════════════════════════════════════════════════════════════════
window.onload = async () => {
  await checkAuth();

  const today = new Date().toISOString().split('T')[0];
  document.getElementById('date').value = today;

  const now = new Date();
  document.getElementById('filterYear').value  = now.getFullYear();
  document.getElementById('filterMonth').value = now.getMonth() + 1;

  loadDashboard();
};