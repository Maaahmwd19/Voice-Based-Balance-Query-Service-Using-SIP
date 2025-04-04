document.addEventListener("DOMContentLoaded", () => {
    const userList = document.getElementById("userList");

    // Delete Modal Elements
    const deleteModal = document.getElementById("deleteModal");
    const deleteMessage = document.getElementById("deleteMessage");
    const confirmDelete = document.getElementById("confirmDelete");
    const cancelDelete = document.getElementById("cancelDelete");

    // Update Modal Elements
    const updateModal = document.getElementById("updateModal");
    const newBalanceInput = document.getElementById("newBalance");
    const saveBalance = document.getElementById("saveBalance");

    // Add User Modal Elements
    const addUserModal = document.getElementById("addUserModal");
    const addUserBtn = document.getElementById("addUserBtn");
    const saveUserBtn = document.getElementById("saveUser");
    const cancelAddUserBtn = document.getElementById("cancelUser");
    const phoneInput = document.getElementById("newPhone");
    const balanceInput = document.getElementById("newUserBalance");

    let selectedUserIndex = null;
    let users = [];

    // Fetch users from servlet
    async function fetchUsers() {
        try {
            const response = await fetch('/service/AdminBalance');
            users = await response.json();
            renderUsers();
        } catch (error) {
            console.error('Error fetching users:', error);
        }
    }

    function renderUsers() {
        userList.innerHTML = "";
        users.forEach((user, index) => {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${user.msisdn}</td>
                <td>$${user.balance}</td>
                <td>
                    <button class="update-btn" data-index="${index}">Update</button>
                    <button class="delete-btn" data-index="${index}">Delete</button>
                </td>
            `;
            userList.appendChild(row);
        });

        document.querySelectorAll(".delete-btn").forEach(btn => 
            btn.addEventListener("click", event => openDeleteModal(event.target.dataset.index))
        );

        document.querySelectorAll(".update-btn").forEach(btn => 
            btn.addEventListener("click", event => openUpdateModal(event.target.dataset.index))
        );
    }

    function openDeleteModal(index) {
        selectedUserIndex = index;
        deleteMessage.innerText = `Are you sure you want to delete user ${users[index].msisdn}?`;
        deleteModal.style.display = "flex";
    }

    function closeDeleteModal() {
        deleteModal.style.display = "none";
        selectedUserIndex = null;
    }

    confirmDelete.onclick = async () => {
        if (selectedUserIndex !== null) {
            try {
                const response = await fetch('/service/AdminBalance', {
                    method: 'DELETE',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        id: users[selectedUserIndex].id,
                        msisdn: users[selectedUserIndex].msisdn,
                        balance: users[selectedUserIndex].balance
                    })
                });

                const result = await response.json();
                if (result.success) {
                    await fetchUsers(); // Refresh user list from server
                    closeDeleteModal();
                } else {
                    alert('Failed to delete user. Please try again.');
                }
            } catch (error) {
                console.error('Error deleting user:', error);
                alert('Failed to delete user. Please try again.');
            }
        }
    };

    cancelDelete.onclick = closeDeleteModal;

    function openUpdateModal(index) {
        selectedUserIndex = index;
        newBalanceInput.value = users[index].balance; 
        updateModal.style.display = "flex";
    }
    function closeUpdateModal() {
        updateModal.style.display = "none";
        selectedUserIndex = null;
    }

    saveBalance.onclick = async () => {
        if (selectedUserIndex !== null) {
            const newBalance = parseFloat(newBalanceInput.value);
            if (!isNaN(newBalance) && newBalance >= 0) {
                try {
                    const response = await fetch('/service/AdminBalance', {
                        method: 'PUT',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify({
                            id: users[selectedUserIndex].id,
                            msisdn: users[selectedUserIndex].msisdn,
                            balance: newBalance
                        })
                    });

                    const result = await response.json();
                    if (result.success) {
                        await fetchUsers(); // Refresh user list from server
                        closeUpdateModal();
                    } else {
                        alert('Failed to update balance. Please try again.');
                    }
                } catch (error) {
                    console.error('Error updating balance:', error);
                    alert('Failed to update balance. Please try again.');
                }
            } else {
                alert("Please enter a valid balance.");
            }
        }
    };

    // Open Add User Modal
    addUserBtn.onclick = () => {
        addUserModal.style.display = "flex";
    };

    // Close Add User Modal
    cancelAddUserBtn.onclick = () => {
        addUserModal.style.display = "none";
        clearAddUserForm();
    };

    // Save New User
    saveUserBtn.onclick = async () => {
        const msisdn = phoneInput.value.trim();
        const balance = parseFloat(balanceInput.value);

        if (!msisdn || isNaN(balance) || balance < 0) {
            alert("Please enter a valid phone number and balance.");
            return;
        }

        try {
            const response = await fetch('/service/AdminBalance', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ msisdn, balance })
            });

            const result = await response.json();
            if (result.success) {
                await fetchUsers(); // Refresh the user list
                addUserModal.style.display = "none";
                clearAddUserForm();
            }
        } catch (error) {
            console.error('Error adding user:', error);
            alert('Failed to add user. Please try again.');
        }
    };

    function clearAddUserForm() {
        phoneInput.value = "";
        balanceInput.value = "";
    }

    // Close modals when clicking outside
    window.onclick = (event) => {
        if (event.target === deleteModal) closeDeleteModal();
        if (event.target === updateModal) closeUpdateModal();
        if (event.target === addUserModal) addUserModal.style.display = "none";
    };

    // Ensure modals don't open automatically on reload
    deleteModal.style.display = "none";
    updateModal.style.display = "none";
    addUserModal.style.display = "none";

    // Initialize the page
    fetchUsers();
});
