async function loadCurrentUser() {
    const res = await fetch('/api/users/current');
    if (res.ok) {
        const user = await res.json();
        const roles = user.roles.map(r => r.name.replace("ROLE_", "")).join(" ");
        document.getElementById('userInfo').textContent = `${user.username} with roles: ${roles}`;

        const hasAdminRole = user.roles.some(role => role.name === 'ROLE_ADMIN');
        if (!hasAdminRole) {
            const adminNavItem = document.getElementById('adminNavItem');
            if (adminNavItem) {
                adminNavItem.style.display = 'none';
            }
        }

        const row = `
            <tr>
                <td>${user.id}</td>
                <td>${user.firstname}</td>
                <td>${user.lastname}</td>
                <td>${user.username}</td>
                <td>${roles}</td>
            </tr>
        `;
        document.getElementById('userTableBody').innerHTML = row;
    } else {
        document.getElementById('userInfo').textContent = 'Failed to load user';
    }
}

document.addEventListener('DOMContentLoaded', loadCurrentUser);