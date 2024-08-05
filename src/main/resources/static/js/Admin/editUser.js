document.addEventListener('DOMContentLoaded', function() {
    const url = window.location.href;
    const params = new URLSearchParams(new URL(url).search);
    const userId = params.get('userId');

    if (userId) {
        fetchUserData(userId);
    }

    document.getElementById('editUserForm').addEventListener('submit', function(event) {
        event.preventDefault();
        const formData = new FormData(this);
        editUser(formData, userId);
    });
});


function fetchUserData(userId) {
    fetch(`/api/users/${userId}`, {
        method: "GET"
    })
        .then(response => response.json())
        .then(data => populateForm(data))
        .catch(error => console.error('Failed to fetch user data:', error));
}

function populateForm(userData) {
    document.getElementById('name').value = userData.name || '';
    document.getElementById('username').value = userData.username || '';
    document.getElementById('email').value = userData.email || '';
    document.getElementById('hashedPassword').value = userData.hashedPassword || '';
    document.getElementById('phoneNumber').value = userData.phoneNumber || '';
    document.getElementById('gender').value = userData.gender || 'male'; // Default to 'male' if undefined
    document.getElementById('dateOfBirth').value = userData.dateOfBirth ? new Date(userData.dateOfBirth).toISOString().substring(0, 10) : '';
    // For 'theme'
    document.getElementById('theme',).value = userData.theme || 'light';

    setSelectOption('role', userData.role);
    setSelectOption('notificationEnabled', userData.notificationEnabled ? 'true' : 'false');
}

function setSelectOption(selectId, value) {
    let selectElement = document.getElementById(selectId);
    for (let option of selectElement.options) {
        if (option.value === value.toString()) {
            option.selected = true;
            break;
        }
    }
}

function editUser(formData,userId) {
    fetch(`/api/users/${userId}`, {
        method: "PUT",
        body: formData
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            goToUsersPage();
        })
        .catch(error => console.error('Error editing user:', error));
}
function showModal(message) {
    document.getElementById('modal-text').textContent = message;
    document.getElementById('modal').style.display = "block";
}


var span = document.getElementsByClassName("close")[0];


span.onclick = function() {
    document.getElementById('modal').style.display = "none";
}


window.onclick = function(event) {
    if (event.target == document.getElementById('modal')) {
        document.getElementById('modal').style.display = "none";
    }
}


function goToUsersPage() {
    window.location.href = "../Admin/manageUsers.html";
}
