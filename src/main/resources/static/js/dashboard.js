// --- 1. Planned Distribution (Doughnut) ---
if (document.getElementById('plannedDistributionChart')) {
    const ctx1 = document.getElementById('plannedDistributionChart').getContext('2d');
    new Chart(ctx1, {
        type: 'doughnut',
        data: {
            labels: expenseRules.map(r => r.category),
            datasets: [{
                data: expenseRules.map(r => r.amount),
                backgroundColor: ['#ef4444', '#f97316', '#eab308', '#22c55e', '#3b82f6', '#a855f7', '#ec4899', '#64748b'],
                borderWidth: 0
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { position: 'right', labels: { boxWidth: 10 } } }
        }
    });
}

// --- 2. Daily Spending (Bar - Last 7 Days) ---
if (document.getElementById('dailySpendingChart')) {
    const ctx2 = document.getElementById('dailySpendingChart').getContext('2d');
    new Chart(ctx2, {
        type: 'bar',
        data: {
            labels: dailyKeys,
            datasets: [{
                label: 'Daily Spending',
                data: dailyValues,
                backgroundColor: '#3b82f6',
                borderRadius: 4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: { y: { beginAtZero: true, grid: { display: false } }, x: { grid: { display: false } } },
            plugins: { legend: { display: false } }
        }
    });
}

// --- 3. Plan vs Actual (Grouped Bar) ---
if (document.getElementById('planVsActualChart')) {
    try {
        console.log('Initializing Plan vs Actual Chart');
        console.log('Categories:', chartCategories);
        console.log('Budget:', budgetValues);
        console.log('Actual:', actualValues);

        const ctx3 = document.getElementById('planVsActualChart').getContext('2d');
        new Chart(ctx3, {
            type: 'bar',
            data: {
                labels: chartCategories && chartCategories.length > 0 ? chartCategories : ['No Data'],
                datasets: [
                    {
                        label: 'Planned',
                        data: budgetValues || [],
                        backgroundColor: '#94a3b8',
                        borderRadius: 4
                    },
                    {
                        label: 'Actual',
                        data: actualValues || [],
                        backgroundColor: (actualValues || []).map((v, i) => {
                            const planned = (budgetValues && budgetValues[i]) || 0;
                            return v > planned ? '#ef4444' : '#22c55e';
                        }),
                        borderRadius: 4
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: { y: { beginAtZero: true } }
            }
        });
    } catch (err) {
        console.error('Error initializing Plan vs Actual Chart:', err);
    }
}

// --- 4. Actual Expense Breakdown (Pie) ---
if (document.getElementById('expenseBreakdownChart')) {
    const expensesOnly = varianceData.filter(v => v.type === 'EXPENSE' && v.actual > 0);
    const ctx4 = document.getElementById('expenseBreakdownChart').getContext('2d');

    new Chart(ctx4, {
        type: 'pie',
        data: {
            labels: expensesOnly.map(v => v.category),
            datasets: [{
                data: expensesOnly.map(v => v.actual),
                backgroundColor: ['#ef4444', '#f97316', '#eab308', '#22c55e', '#3b82f6', '#a855f7', '#ec4899', '#64748b'],
                borderWidth: 0
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { position: 'right' } }
        }
    });
}

// Init logic
document.addEventListener('DOMContentLoaded', () => {
    const params = new URLSearchParams(window.location.search);
    const tab = params.get('tab') || 'plan';
    switchTab(tab);
});

function switchTab(tabName) {
    // Update URL
    const url = new URL(window.location);
    url.searchParams.set('tab', tabName);
    history.pushState(null, '', url);

    // Hide all contents
    document.querySelectorAll('.tab-content').forEach(el => el.classList.remove('active'));

    // Reset all nav buttons
    document.querySelectorAll('.tab-btn-nav').forEach(el => {
        el.classList.remove('text-blue-400', 'bg-slate-800');
        el.classList.add('text-slate-400');
    });

    // Show selected content
    document.getElementById('tab-' + tabName).classList.add('active');

    // Highlight selected nav button
    const btn = document.getElementById('btn-' + tabName);
    if (btn) {
        btn.classList.remove('text-slate-400');
        btn.classList.add('text-blue-400', 'bg-slate-800');
    }

    // Update Headers
    const titles = { 'plan': 'My Plan', 'tracker': 'Tracker', 'analysis': 'Analysis' };
    if (titles[tabName]) {
        document.getElementById('pageTitle').innerText = titles[tabName];
    }
}

// Init Form Listeners
document.getElementById('addRuleForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const data = {
        title: document.getElementById('ruleTitle').value,
        amount: document.getElementById('ruleAmount').value,
        period: document.getElementById('rulePeriod').value,
        type: document.getElementById('ruleType').value,
        category: document.getElementById('ruleCategory').value,
        currency: 'INR'
    };

    try {
        const response = await fetch('/api/budget/rules?accountId=' + accountId, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        if (response.ok) {
            window.location.reload();
        } else {
            alert('Failed to add rule');
        }
    } catch (err) {
        console.error(err);
        alert('Error adding rule');
    }
});

document.getElementById('addTxForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const data = {
        description: document.getElementById('txDesc').value,
        amount: document.getElementById('txAmount').value,
        category: document.getElementById('txCategory').value
    };

    try {
        const response = await fetch('/api/tracking/transactions?accountId=' + accountId, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        if (response.ok) {
            window.location.reload();
        } else {
            alert('Failed to add transaction');
        }
    } catch (err) {
        console.error(err);
        alert('Error adding transaction');
    }
});

// --- Notification Logic ---
const notifBtn = document.getElementById('notificationBtn');
const notifPanel = document.getElementById('notificationPanel');
const closeNotifBtn = document.getElementById('closeNotificationBtn');

// Form Elements
const dailyToggle = document.getElementById('dailySummaryToggle');
const dailyTime = document.getElementById('dailySummaryTime');
const budgetToggle = document.getElementById('budgetAlertToggle');
const budgetThreshold = document.getElementById('budgetThreshold');
const saveBtn = document.getElementById('saveSettingsBtn');

function toggleNotifications() {
    const isHidden = notifPanel.classList.contains('hidden');
    if (isHidden) {
        notifPanel.classList.remove('hidden');
        loadSettings();
    } else {
        notifPanel.classList.add('hidden');
    }
}

notifBtn.addEventListener('click', (e) => {
    e.stopPropagation();
    toggleNotifications();
});

closeNotifBtn.addEventListener('click', (e) => {
    e.stopPropagation();
    notifPanel.classList.add('hidden');
});

// Toggle Logic
dailyToggle.addEventListener('change', () => {
    dailyTime.disabled = !dailyToggle.checked;
    if (!dailyToggle.checked) dailyTime.value = '';
});

budgetToggle.addEventListener('change', () => {
    budgetThreshold.disabled = !budgetToggle.checked;
    if (!budgetToggle.checked) budgetThreshold.value = '';
});

// Close on click outside
document.addEventListener('click', (e) => {
    if (!notifPanel.contains(e.target) && !notifBtn.contains(e.target)) {
        notifPanel.classList.add('hidden');
    }
});

// Stop propagation inside panel to prevent closing when clicking inputs
notifPanel.addEventListener('click', (e) => {
    e.stopPropagation();
});

async function loadSettings() {
    try {
        const res = await fetch(`/notifications/settings?accountName=${accountId}`);
        if (res.ok) {
            const data = await res.json();

            dailyToggle.checked = data.dailySummaryEnabled || false;
            if (data.dailySummaryTime) {
                dailyTime.value = data.dailySummaryTime.substring(0, 5); // HH:mm
            }
            dailyTime.disabled = !dailyToggle.checked;

            budgetToggle.checked = data.budgetAlertEnabled || false;
            budgetThreshold.value = data.budgetThreshold || 90;
            budgetThreshold.disabled = !budgetToggle.checked;

        } else {
            console.error('Failed to load settings');
        }
    } catch (e) {
        console.error(e);
    }
}

saveBtn.addEventListener('click', async () => {
    const data = {
        dailySummaryEnabled: dailyToggle.checked,
        dailySummaryTime: dailyToggle.checked && dailyTime.value ? dailyTime.value + ":00" : null,
        budgetAlertEnabled: budgetToggle.checked,
        budgetThreshold: budgetToggle.checked ? parseInt(budgetThreshold.value) : null,
        emailAddress: null // Not updating email here
    };

    const originalText = saveBtn.innerText;
    saveBtn.innerText = 'Saving...';
    saveBtn.disabled = true;

    try {
        const res = await fetch(`/notifications/settings?accountName=${accountId}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        if (res.ok) {
            saveBtn.innerText = 'Saved!';
            setTimeout(() => {
                saveBtn.innerText = originalText;
                saveBtn.disabled = false;
                notifPanel.classList.add('hidden');
            }, 1000);
        } else {
            saveBtn.innerText = 'Failed';
            setTimeout(() => {
                saveBtn.innerText = originalText;
                saveBtn.disabled = false;
            }, 2000);
        }
    } catch (e) {
        console.error(e);
        saveBtn.innerText = 'Error';
        setTimeout(() => {
            saveBtn.innerText = originalText;
            saveBtn.disabled = false;
        }, 2000);
    }
});
