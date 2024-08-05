const appUser = JSON.parse(localStorage.getItem("appUser"));



function banUser(userId){
    fetch(`/api/forum/banUser/${userId}`, {
        method: 'PUT',
    })
        .then(response => {
            if (response.ok) {
                console.log('Kullanıcı başarıyla banlandı.');
                window.location.reload();
            } else if (response.status === 403) {
                console.log('Yetkisiz işlem. Bu işlemi yapmak için gerekli izne sahip değilsiniz.');
            } else if (response.status === 400) {
                console.log('Geçersiz istek. Kullanıcı ID’si bulunamadı.');
            } else {
                console.log('Bir hata oluştu. Status kodu:', response.status);
            }
        })
        .catch(error => {
            console.error('Fetch isteğinde bir hata oluştu:', error);
        });

}

function unbanUser(userId){
    fetch(`/api/forum/unbanUser/${userId}`, {
        method: 'PUT',
    })
        .then(response => {
            if (response.ok) {
                console.log('Kullanıcı başarıyla unbanlandı.');
                window.location.reload();

            } else if (response.status === 403) {
                console.log('Yetkisiz işlem. Bu işlemi yapmak için gerekli izne sahip değilsiniz.');
            } else if (response.status === 400) {
                console.log('Geçersiz istek. Kullanıcı ID’si bulunamadı.');
            } else {
                console.log('Bir hata oluştu. Status kodu:', response.status);
            }
        })
        .catch(error => {
            console.error('Fetch isteğinde bir hata oluştu:', error);
        });

}

document.addEventListener("DOMContentLoaded", function() {
    const apiUrl = '/api/users';
    let currentPage = 1;
    const isRegistered = appUser != null;
    const userSection = document.getElementById('user-section');

    if (isRegistered) {
        userSection.innerHTML = `<div class="flex items-center space-x-5">
                                <a id="manageprofilepage" href="/html/User/profile.html">
                                    <span>${appUser.username}</span>
                                </a>
                                <button class="logout" onclick='logout()'>
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-box-arrow-left" viewBox="0 0 16 16">
                                      <path fill-rule="evenodd" d="M6 12.5a.5.5 0 0 0 .5.5h8a.5.5 0 0 0 .5-.5v-9a.5.5 0 0 0-.5-.5h-8a.5.5 0 0 0-.5.5v2a.5.5 0 0 1-1 0v-2A1.5.5 0 0 1 6.5 2h8A1.5 1.5 0 0 1 16 3.5v9a1.5 1.5 0 0 1-1.5 1.5h-8A1.5 1.5 0 0 1 5 12.5v-2a.5.5 0 0 1 1 0z"/>
                                      <path fill-rule="evenodd" d="M.146 8.354a.5.5 0 0 1 0-.708l3-3a.5.5 0 1 1 .708.708L1.707 7.5H10.5a.5.5 0 0 1 0 1H1.707l2.147 2.146a.5.5 0 0 1-.708.708z"/>
                                    </svg>
                                </button>
                             </div>`;
    } else {
        userSection.innerHTML = '<a href="/html/UnregUser/login.html">Sign in/Register</a>';
    }

    function logout() {
        fetch("/api/auth/logout", {
            method: "POST"
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                localStorage.removeItem("appUser");
                window.location.href = "http://localhost:8080/html/UnregUser/homePage.html";
            })
            .catch(error => console.error('Error', error));

    };

    function openAddUserPage() {
        window.location.href = '/html/Admin/addUser.html';
    }

    function attachAddUserEventListener() {
        const addUserButton = document.getElementById('add-user-button');
        if (addUserButton) {
            addUserButton.addEventListener('click', openAddUserPage);
        } else {
            console.error('Add User button not found');
        }
    }
    function attachProductsOfMerchantEventListener() {
        const addUserButton = document.getElementById('add-user-button');
        if (addUserButton) {
            addUserButton.addEventListener('click', openAddUserPage);
        } else {
            console.error('Add User button not found');
        }
    }

    async function fetchUsers(page = 1) {
        try {
            const response = await fetch(`${apiUrl}/paged?page=${page}`);
            if (!response.ok) {
                throw new Error('Network response was not ok ' + response.statusText);
            }
            const users = await response.json();
            populateTable(users, page);
            updatePagination(page);
        } catch (error) {
            console.error('There has been a problem with your fetch operation:', error);
            const tableBody = document.getElementById('userTableBody');
            tableBody.innerHTML = `<tr><td colspan="7">Failed to load data: ${error.message}</td></tr>`;
        }
    }

    function populateTable(users, currentPage) {
        const tableBody = document.getElementById('userTableBody');
        tableBody.innerHTML = ''; // Clear existing entries

        users.forEach((user, index) => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td class="pl-4">${index + 1}</td>
                <td>
                    <span class="text-muted">${user.userId}</span><br>
                </td>
                <td>
                    <span class="text-muted">${user.name}</span>
                </td>
                
                <td>
                    <span class="text-muted">${user.username}</span><br>
                </td>
                <td>
                    <span class="text-muted">${user.email}</span><br>
                    <span class="text-muted">${user.phoneNumber}</span>
                </td>
                <td>
                    <span class="text-muted">${user.joinTime}</span><br>
                </td>
                <td>
                    <button type="button" class="btn btn-outline-info btn-circle btn-lg btn-circle ml-2 delete-button" data-user-id="${user.userId}">
                        <i class="fa fa-trash"></i>
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-trash" viewBox="0 0 16 16">
                              <path d="M5.5 5.5A.5.5 0 0 1 6 6v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5m2.5 0a.5.5 0 0 1 .5.5v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5m3 .5a.5.5 0 0 0-1 0v6a.5.5 0 0 0 1 0z"/>
                              <path d="M14.5 3a1 1 0 0 1-1 1H13v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V4h-.5a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1H6a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1h3.5a1 1 0 0 1 1 1zM4.118 4 4 4.059V13a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4.059L11.882 4zM2.5 3h11V2h-11z"/>
                            </svg>
                    </button>
                    <button type="button" class="btn btn-outline-info btn-circle btn-lg btn-circle ml-2 edit-button" data-user-id="${user.userId}">
                        <i class="fa fa-edit"></i> 
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-pencil-square" viewBox="0 0 16 16">
                              <path d="M15.502 1.94a.5.5 0 0 1 0 .706L14.459 3.69l-2-2L13.502.646a.5.5 0 0 1 .707 0l1.293 1.293zm-1.75 2.456-2-2L4.939 9.21a.5.5 0 0 0-.121.196l-.805 2.414a.25.25 0 0 0 .316.316l2.414-.805a.5.5 0 0 0 .196-.12l6.813-6.814z"/>
                              <path fill-rule="evenodd" d="M1 13.5A1.5 1.5 0 0 0 2.5 15h11a1.5 1.5 0 0 0 1.5-1.5v-6a.5.5 0 0 0-1 0v6a.5.5 0 0 1-.5.5h-11a.5.5 0 0 1-.5-.5v-11a.5.5 0 0 1 .5-.5H9a.5.5 0 0 0 0-1H2.5A1.5 1.5 0 0 0 1 2.5z"/>
                            </svg>
                    </button>
                    <button type="button" class="btn btn-outline-info btn-circle btn-lg btn-circle ml-2 add-reward-button" data-user-id="${user.userId}">
                    <i class="fa fa-gift"></i>
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-gift" viewBox="0 0 16 16">
                        <path d="M8 2.5A1.5 1.5 0 1 1 9.5 1a1.5 1.5 0 0 1-1.5 1.5zm0-2A.5.5 0 0 0 7.5 1 .5.5 0 0 0 8 1.5zm-2 2A1.5 1.5 0 1 0 4.5 1 1.5 1.5 0 0 0 6 2.5z"/>
                        <path d="M8 0a2 2 0 0 0-2 2v1H3.5A2.5 2.5 0 0 0 1 5.5V6H0v3h1v5.5A1.5 1.5 0 0 0 2.5 16H7V9H2v5.5a.5.5 0 0 0 .5.5H6v-4h4v4h1.5a.5.5 0 0 0 .5-.5V9h-5V4H8V2a2 2 0 0 0 2-2V0h1a2 2 0 0 0-2-2H8z"/>
                        <path d="M15 6v-.5A2.5 2.5 0 0 0 12.5 3H11v1a2 2 0 0 0 2 2h2V6zm-4.5-1A1.5 1.5 0 1 1 9 3.5 1.5 1.5 0 0 1 10.5 5zm0-3A.5.5 0 0 0 10 2.5a.5.5 0 0 0 .5.5zm-4.5 3A1.5 1.5 0 1 0 4.5 3 1.5 1.5 0 0 0 6 4.5zm-2-2A.5.5 0 0 0 3.5 2 .5.5 0 0 0 4 2.5zm5.5 2V6h-4v4h1v6h6a1.5 1.5 0 0 0 1.5-1.5V9h1V6h-1v-.5A1.5 1.5 0 0 0 10.5 4H9z"/>
                    </svg>
                </button>
                ${user.bannedFromForum  ? `<button type="button" class="btn btn-outline-info btn-circle btn-lg btn-circle ml-2 unban-user-button" onclick="unbanUser(${user.userId})" ">
                        <i class="bi bi-ban"></i>
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-ban" viewBox="0 0 16 16">
                          <path d="M15 8a6.97 6.97 0 0 0-1.71-4.584l-9.874 9.875A7 7 0 0 0 15 8M2.71 12.584l9.874-9.875a7 7 0 0 0-9.874 9.874ZM16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0"/>
                        </svg>
                    </button>` : `<button type="button" class="btn btn-outline-info btn-circle btn-lg btn-circle ml-2 ban-user-button" onclick="banUser(${user.userId})" ">
                        <i class="bi bi-check2"></i>
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-check2" viewBox="0 0 16 16">
                          <path d="M13.854 3.646a.5.5 0 0 1 0 .708l-7 7a.5.5 0 0 1-.708 0l-3.5-3.5a.5.5 0 1 1 .708-.708L6.5 10.293l6.646-6.647a.5.5 0 0 1 .708 0"/>
                        </svg>
                  
                    </button>` }
                </td>
            `;
            tableBody.appendChild(row);
        });

        attachDeleteEventListeners();
        attachEditEventListeners();
        attachAddUserEventListener()
        attachAddRewardEventListeners()
    }

    function attachDeleteEventListeners() {
        const deleteButtons = document.querySelectorAll('.delete-button');
        deleteButtons.forEach(button => {
            button.addEventListener('click', function() {
                const userId = this.getAttribute('data-user-id');
                showConfirmationDialog(userId);
            });
        });
    }

    function showConfirmationDialog(userId) {
        if (confirm(`Are you sure you want to delete the user with ID: ${userId}?`)) {
            deleteUser(userId);
        }
    }

    async function deleteUser(userId) {
        try {
            const response = await fetch(`${apiUrl}/${userId}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                }
            });
            if (response.status === 204) {
                alert('User deleted successfully!');
                fetchUsers(currentPage);  // Refresh the list of users
            } else {
                throw new Error(`Failed to delete user, server responded with status: ${response.status}`);
            }
        } catch (error) {
            console.error('Error deleting user:', error);
            alert('Failed to delete the user: ' + error.message);
        }
    }

    function attachEditEventListeners() {
        const editButtons = document.querySelectorAll('.edit-button');
        editButtons.forEach(button => {
            button.addEventListener('click', function() {
                const userId = this.getAttribute('data-user-id');
                window.location.href = `../Admin/editUser.html?userId=${userId}`; // Redirect to the edit user page
            });
        });
    }


    async function updatePagination(currentPage) {
        try {
            const response = await fetch(`${apiUrl}/page-count`);
            if (!response.ok) {
                throw new Error('Failed to fetch page count');
            }
            const pageCount = await response.json();
            createPaginationButtons(pageCount, currentPage);
        } catch (error) {
            console.error('Failed to update pagination:', error);
        }
    }

    function createPaginationButtons(pageCount, currentPage) {
        const paginationButtonsContainer = document.getElementById('paginationButtons');
        paginationButtonsContainer.innerHTML = '';

        for (let i = 1; i <= pageCount; i++) {
            const button = document.createElement('button');
            button.textContent = i;
            button.className = 'page-button';
            if (i === currentPage) {
                button.classList.add('active');
            }
            button.addEventListener('click', function() {
                fetchUsers(i);
                setActiveButton(i);
            });
            paginationButtonsContainer.appendChild(button);
        }
    }

    function setActiveButton(pageNumber) {
        const buttons = document.querySelectorAll('#paginationButtons .page-button');
        buttons.forEach(button => {
            button.classList.remove('active');
        });
        const activeButton = buttons[pageNumber - 1]; // Adjust index since page numbers start at 1
        if (activeButton) {
            activeButton.classList.add('active');
        }
    }
    function attachAddRewardEventListeners() {
        const addRewardButtons = document.querySelectorAll('.add-reward-button');
        addRewardButtons.forEach(button => {
            button.addEventListener('click', function() {
                const userId = this.getAttribute('data-user-id');
                // Functionality for adding reward will go here
                let rewardName = prompt("Enter a reward name:");
                let rewardDesc = prompt("Enter the reward description:");
                const formData1 = new FormData();
                formData1.set("rewardName",rewardName);
                formData1.set("description",rewardDesc);

                fetch("/api/users/addRewardToUser/" + userId, {
                    method: 'POST',
                    body: formData1
                })
                    .then(function(response) {
                        if (response.ok) {
                            return response.text();
                        } else {
                            return response.text().then(function(errorMessage) {
                                throw new Error(errorMessage);
                            });
                        }
                    })
                    .then(function(message) {
                        console.log('Response: ', message);
                    })
                    .catch(function(error) {
                        console.error('Error: ', error.message);
                    });

            });
        });
    }

    fetchUsers(currentPage);
    attachAddUserEventListener()
});


function logout() {
    fetch("/api/auth/logout", {
        method: "POST"
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            localStorage.removeItem("appUser");
            window.location.href="/html/UnregUser/homePage.html";
        })
        .catch(error => console.error('Error', error));

};