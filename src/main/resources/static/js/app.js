const API_BASE = '';

// Auth Page Logic
const loginForm = document.getElementById('loginForm');
if (loginForm) {
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const username = document.getElementById('loginUsername').value;
        const password = document.getElementById('loginPassword').value;

        try {
            const response = await fetch('/accounts/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });

            if (response.ok) {
                window.location.href = `/dashboard/${username}`;
            } else {
                const errorText = await response.text();
                if (errorText.includes("Account not verified")) {
                    alert("Account not verified. Please check your email.");
                } else {
                    alert('Invalid username or password');
                }
            }
        } catch (error) {
            console.error('Login Error:', error);
            alert('An error occurred during login.');
        }
    });
}

const registerForm = document.getElementById('registerForm');
if (registerForm) {
    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const password = document.getElementById('regPassword').value;
        const confirmPassword = document.getElementById('regConfirmPassword').value;

        if (password !== confirmPassword) {
            alert("Passwords do not match!");
            return;
        }

        const data = {
            username: document.getElementById('regUsername').value,
            password: password,
            email: document.getElementById('regEmail').value
        };

        try {
            const response = await fetch('/accounts/', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });

            if (response.ok) {
                window.location.href = '/check-email';
                registerForm.reset();
            } else {
                const errorText = await response.text();
                alert(`Registration failed: ${errorText}`);
            }
        } catch (error) {
            console.error('Error:', error);
            alert('An error occurred.');
        }
    });
}

// Dashboard Logic
async function loadDashboardData(username) {
    if (!document.getElementById('totalSavings')) return;

    // Fetch Stats
    try {
        const statsRes = await fetch(`/statistics/current?accountId=${username}`);
        if (statsRes.ok) {
            const stats = await statsRes.json();
            document.getElementById('totalSavings').textContent = `$${stats.totalAmount.toFixed(2)}`;

            const list = document.getElementById('itemsList');
            list.innerHTML = '';
            stats.items.forEach(item => {
                const li = document.createElement('li');
                li.className = 'bg-gray-50 mb-2 p-4 rounded-lg flex justify-between';
                li.innerHTML = `
                    <span>${item.title} (${item.period})</span>
                    <span class="${item.type === 'INCOME' ? 'text-green-600' : 'text-red-600'} font-bold">
                        ${item.type === 'INCOME' ? '+' : '-'}${item.amount}
                    </span>
                `;
                list.appendChild(li);
            });
        }
    } catch (e) {
        console.error("Failed to load stats", e);
    }

    // Fetch Notifications
    try {
        const notifRes = await fetch(`/notifications/settings?accountName=${username}`);
        if (notifRes.ok) {
            const settings = await notifRes.json();
            const list = document.getElementById('notificationSettings');
            list.innerHTML = '';
            for (const [key, value] of Object.entries(settings.settings)) {
                const li = document.createElement('li');
                li.className = 'mb-1 p-2 bg-gray-50 rounded flex justify-between';
                li.innerHTML = `<span class="font-semibold">${key}</span> <span>${value}</span>`;
                list.appendChild(li);
            }
        }
    } catch (e) {
        console.error("Failed to load notifications", e);
    }
}

const addItemForm = document.getElementById('addItemForm');
if (addItemForm) {
    addItemForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const username = window.location.pathname.split('/').pop();

        const data = {
            title: document.getElementById('itemTitle').value,
            amount: parseFloat(document.getElementById('itemAmount').value),
            currency: document.getElementById('itemCurrency').value,
            period: document.getElementById('itemPeriod').value,
            type: document.getElementById('itemType').value
        };

        try {
            const res = await fetch(`/statistics/items?accountId=${username}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });

            if (res.ok) {
                addItemForm.reset();
                loadDashboardData(username); // Reload data
            } else {
                const errorText = await res.text();
                console.error('Failed to add item:', res.status, errorText);
                alert(`Failed to add item: ${res.status} ${errorText}`);
            }
        } catch (e) {
            console.error('Network/Logic Error:', e);
            alert(`Error: ${e.message}`);
        }
    });
}

const addNotificationForm = document.getElementById('addNotificationForm');
if (addNotificationForm) {
    addNotificationForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const username = window.location.pathname.split('/').pop();
        const key = document.getElementById('notifKey').value;
        const value = document.getElementById('notifValue').value;

        try {
            const res = await fetch(`/notifications/settings?accountName=${username}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ key, value })
            });

            if (res.ok) {
                addNotificationForm.reset();
                loadDashboardData(username); // Reloads the notification list
            } else {
                alert('Failed to add notification setting');
            }
        } catch (e) {
            console.error(e);
            alert('Error adding setting');
        }
    });
}
