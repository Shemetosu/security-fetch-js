document.addEventListener('DOMContentLoaded', () => {
    loadCurrentUser();
    loadUsers();
    loadRoles();

    const newTab = document.getElementById('new-tab');
    if (newTab) {
        newTab.addEventListener('shown.bs.tab', loadRoles);
    }
});

async function loadRoles() {
    const res = await fetch('/api/users/roles');
    const roles = await res.json();

    const selects = [
        document.getElementById('edit-roles'),
        document.getElementById('newRoles')
    ];

    selects.forEach(select => {
        if (!select) return;
        select.innerHTML = '';
        roles.forEach(role => {
            const opt = document.createElement('option');
            opt.value = role.id;
            opt.textContent = role.name.replace("ROLE_", "");
            select.appendChild(opt);
        });
    });
}

async function loadCurrentUser() {
    try {
        const res = await fetch('/api/users/current');
        if (!res.ok) throw new Error('Error getting current User');

        const user = await res.json();
        const roles = user.roles.map(r => r.name.replace("ROLE_", "")).join(", ");
        document.getElementById('userInfo').textContent = `${user.username} with roles: ${roles}`;
    } catch (error) {
        console.error('Error download current User:', error);
        document.getElementById('userInfo').textContent = 'Error download current User';
    }
}

async function loadUsers() {
    const response = await fetch('/api/users');
    const users = await response.json();

    const tbody = document.querySelector('#usersTable');
    tbody.innerHTML = '';

    users.forEach(user => {
        const roles = user.roles.map(r => r.name.replace("ROLE_", "")).join(", ");
        const row = `
            <tr>
                <td>${user.id}</td>
                <td>${user.firstname}</td>
                <td>${user.lastname}</td>
                <td>${user.username}</td>
                <td>${roles}</td>
                <td>
                    <button class="btn btn-sm btn-primary" onclick="openEditModal(${user.id})">Edit</button>
                </td>
                <td>
                    <button class="btn btn-sm btn-danger" onclick="openDeleteModal(${user.id})">Delete</button>
                </td>
            </tr>`;
        tbody.insertAdjacentHTML('beforeend', row);
    });
}

async function createUser(event) {
    event.preventDefault();

    const rolesSelect = document.getElementById('newRoles');
    const selectedRoles = Array.from(rolesSelect.selectedOptions)
        .map(opt => parseInt(opt.value))
        .filter(id => !isNaN(id))
        .map(opt => ({ id }));

    const user = {
        firstname: document.getElementById('newFirstname').value,
        lastname: document.getElementById('newLastname').value,
        username: document.getElementById('newUsername').value,
        password: document.getElementById('newPassword').value,
        roles: selectedRoles
    };

    const res = await fetch('/api/users/add', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(user)
    });

    if (res.ok) {
        loadUsers();
        document.getElementById('createUserForm').reset();
        document.querySelector('#users-tab').click();
    } else {
        alert("Error when create New User");
    }
}

async function openEditModal(id) {
    const res = await fetch(`/api/users/get?userId=${id}`);
    const user = await res.json();

    document.getElementById('edit-id').value = user.id;
    document.getElementById('edit-firstname').value = user.firstname;
    document.getElementById('edit-lastname').value = user.lastname;
    document.getElementById('edit-username').value = user.username;
    document.getElementById('edit-password').value = '';

    const rolesRes = await fetch('/api/users/roles');
    const allRoles = await rolesRes.json();
    const select = document.getElementById('edit-roles');
    select.innerHTML = '';
    allRoles.forEach(role => {
        const opt = document.createElement('option');
        opt.value = role.id;
        opt.textContent = role.name.replace("ROLE_", "");
        if (user.roles.some(r => r.id === role.id)) opt.selected = true;
        select.appendChild(opt);
    });

    const modal = new bootstrap.Modal(document.getElementById('editUserModal'));
    modal.show();
}

async function openDeleteModal(id) {
    const res = await fetch(`/api/users/get?userId=${id}`);
    const user = await res.json();

    document.getElementById('delete-id').value = user.id;
    document.getElementById('delete-id-hidden').value = user.id;
    document.getElementById('delete-firstname').value = user.firstname;
    document.getElementById('delete-lastname').value = user.lastname;
    document.getElementById('delete-username').value = user.username;

    const rolesSelect = document.getElementById('delete-roles');
    const rolesRes = await fetch('/api/users/roles');
    const allRoles = await rolesRes.json();
    rolesSelect.innerHTML = '';
    allRoles.forEach(role => {
        const opt = document.createElement('option');
        opt.value = role.id;
        opt.textContent = role.name.replace("ROLE_", "");
        opt.disabled = true;
        if (user.roles.some(r => r.id === role.id)) opt.selected = true;
        rolesSelect.appendChild(opt);
    });

    const modal = new bootstrap.Modal(document.getElementById('deleteUserModal'));
    modal.show();
}

async function submitEditUser(event) {
    event.preventDefault();

    const id = parseInt(document.getElementById('edit-id').value);
    const firstname = document.getElementById('edit-firstname').value;
    const lastname = document.getElementById('edit-lastname').value;
    const username = document.getElementById('edit-username').value;
    const password = document.getElementById('edit-password').value;

    const roles = Array.from(document.getElementById('edit-roles').selectedOptions)
        .map(opt => parseInt(opt.value))
        .filter(id => !isNaN(id))
        .map(id => ({ id }));

    const user = { id, firstname, lastname, username, roles };
    if (password) user.password = password;

    console.log("Sent User: ", user);

    const res = await fetch('/api/users/update', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(user)
    });

    if (res.ok) {
        bootstrap.Modal.getInstance(document.getElementById('editUserModal')).hide();
        loadUsers();
    } else {
        alert('Error when editing User');
    }
}

async function submitDeleteUser(event) {
    event.preventDefault();
    const id = document.getElementById('delete-id-hidden').value;

    const res = await fetch(`/api/users/delete?userId=${id}`, {
        method: 'GET'
    });

    if (res.ok) {
        bootstrap.Modal.getInstance(document.getElementById('deleteUserModal')).hide();
        loadUsers();
    } else {
        alert('Error when deleting User');
    }
}