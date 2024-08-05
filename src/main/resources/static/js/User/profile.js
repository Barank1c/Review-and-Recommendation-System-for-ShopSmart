let appUser = JSON.parse(localStorage.getItem("appUser"));


document.addEventListener('DOMContentLoaded', function() {

    let currentPage = 1;

    function setActiveSidebarItem(itemId) {
        const sidebarItems = document.querySelectorAll("#side-bar-of-user .nav-link");
        sidebarItems.forEach(item => {
            item.classList.remove("active-item");
        });
        document.getElementById(itemId).classList.add("active-item");
    }

    document.getElementById("side-bar-of-user").innerHTML = "<li id='profile-details-id' class=\"nav-item mb-2\">\n" +
        "                    <a class=\"nav-link\" id='edit-profile-id'>Edit Profile</a>\n" +
        "                </li>\n" +
        "                <li id='change-password-id' class=\"nav-item mb-2\">\n" +
        "                    <a class=\"nav-link\" id='change-password-id'>Change Password</a>\n" +
        "                </li>";

    if (appUser.role !== 'ADMIN'){
        document.getElementById("side-bar-of-user").innerHTML = document.getElementById("side-bar-of-user").innerHTML+
            "                <li class=\"nav-item mb-2\">\n" +
            "                    <a class=\"nav-link active\"  id='rewards-id'>Rewards</a>\n" +
            "                </li>\n" +
            "                <li class=\"nav-item mb-2\">\n" +
            "                    <a class=\"nav-link active\" id='tickets-id'>Support Tickets</a>\n" +
            "                </li> \n" +
            "                <li class=\"nav-item mb-2\">\n" +
            "                    <a class=\"nav-link active\" id='purchased-products-id'>Purchased Products</a>\n" +
            "                </li>";
        document.getElementById(`edit-profile-id`).onclick = function() {
            editProfileAdder();
        };
        document.getElementById(`change-password-id`).onclick = function() {
            changePassword();
        };
        document.getElementById(`rewards-id`).onclick = function() {
            seeRewards();
        };
        document.getElementById(`tickets-id`).onclick = function() {
            UserSupportTickets();

        };
        document.getElementById(`purchased-products-id`).onclick = function() {
            seeProducts();
        };
    }

    if (appUser.role === 'ADMIN'){
        document.getElementById("side-bar-of-user").innerHTML = document.getElementById("side-bar-of-user").innerHTML +
            "                <li class=\"nav-item mb-2\">\n" +
            "                    <a class=\"nav-link active\" id='manage-users-id' href='/html/Admin/manageUsers.html'>Manage Users</a>\n" +
            "                </li> \n" +
            "                <li class=\"nav-item mb-2\">\n" +
            "                    <a class=\"nav-link active\" id='manage-st-id'>Manage Support Tickets</a>\n" +
            "                </li>" +
            "                <li class=\"nav-item mb-2\">\n" +
            "                    <a class=\"nav-link active\" id='manage-applyings-id'>Manage Applications</a>\n" +
            "                </li>";
        document.getElementById(`edit-profile-id`).onclick = function() {
            editProfileAdder();
        };
        document.getElementById(`change-password-id`).onclick = function() {
            changePassword();
        };

        document.getElementById('manage-st-id').onclick = function() {
            manageSupportTickets();

        };
        document.getElementById(`manage-applyings-id`).onclick = function() {
            manageApplyings();
        };

    }

    if (appUser.role === 'MERCHANT'){
        document.getElementById("side-bar-of-user").innerHTML = document.getElementById("side-bar-of-user").innerHTML +
            "                <li class=\"nav-item mb-2\">\n" +
            "                    <a class=\"nav-link active\" id='manage-product-id' href='/html/Merchant/productsOfMerchant.html'>Manage Products</a>\n" +
            "                </li>";
        document.getElementById(`edit-profile-id`).onclick = function() {
            editProfileAdder();
        };
        document.getElementById(`change-password-id`).onclick = function() {
            changePassword();
        };
        document.getElementById(`rewards-id`).onclick = function() {
            seeRewards();
        };
        document.getElementById(`tickets-id`).onclick = function() {
            UserSupportTickets();

        };

        document.getElementById(`purchased-products-id`).onclick = function() {
            seeProducts();
        };
    }

    function manageSupportTickets() {
        document.getElementById("main-content").innerHTML = `
       
        <h1>Ticket Management</h1>
        <div id="ticket-list" class="ticket-management-container"></div>
        <div id="pagination" class="ticket-management-container"></div>
    `;

        let currentPage = 1;

        loadTickets(currentPage);
        loadTicketPageCount();

    }

    function answerTicket(ticketId) {
        const answer = prompt("Enter answer:");

        const formData = new FormData();
        formData.append('answer', answer);

        fetch(`/api/users/addUpdateAnswerToTicket/${ticketId}`, {
            method: 'POST',
            body: formData
        })
            .then(response => {
                if (!response.ok) {
                    return response.text().then(text => { throw new Error(text) });
                }
                return response.text();
            })
            .then(message => {
                alert(message);
                loadTickets(currentPage);
            })
            .catch(error => console.error('Error answering ticket:', error));
    }

    function loadTickets(page) {
        fetch(`/api/users/getAllSupportTickets/paged?page=${page}`)
            .then(response => response.json())
            .then(tickets => {
                const ticketsContainer = document.getElementById("ticket-list");

                ticketsContainer.innerHTML = tickets.map(ticket => `
                <div class="ticket-item">
                    <p><strong>Title:</strong> ${ticket.title}</p>
                    <p><strong>Text:</strong> ${ticket.text}</p>
                    <p><strong>Answer:</strong> ${ticket.answer}</p>
                    <button class="answer-btn" value="${ticket.ticketId}">Answer</button>
                </div>
                
            `).join('');

                document.querySelectorAll(".answer-btn").forEach(button => {
                    button.addEventListener("click", function() {
                        answerTicket(this.value);
                    });
                });


            })
            .catch(error => console.error('Error loading tickets:', error));
    }

    function loadTicketPageCount() {
        fetch(`/api/users/getAllSupportTickets/page-count`)
            .then(response => response.json())
            .then(pageCount => {
                const paginationContainer = document.getElementById("pagination");
                paginationContainer.innerHTML = '';
                for (let i = 1; i <= pageCount; i++) {
                    const pageButton = document.createElement('button');
                    pageButton.className = 'pagination-button'; // Add this line to set the class
                    pageButton.innerText = i;

                    if (i === currentPage) {
                        pageButton.classList.add('active');
                    }
                    pageButton.addEventListener('click', () => {
                        currentPage = i;
                        loadTickets(currentPage);
                        updatePaginationButtons(paginationContainer, i);
                    });
                    paginationContainer.appendChild(pageButton);
                }
            })
            .catch(error => console.error('Error loading page count:', error));
    }


    function UserSupportTickets() {
        document.getElementById("main-content").innerHTML = `
        
        <h1>Manage Support Tickets</h1>
        <button id="add-ticket-btn" class="btn btn-secondary">Add Support Ticket</button>
        <div id="support-tickets" class="support-container"></div>
        <div id="pagination" class="support-container"></div>
    `;

        currentPage = 1;

        fetchSupportTickets(currentPage);
        fetchSupportTicketsPageCount();
        document.getElementById("add-ticket-btn").addEventListener("click", addSupportTicket);
    }

    function addSupportTicket() {
        const title = prompt("Enter ticket title:");
        const text = prompt("Enter ticket text:");

        fetch(`/api/profile/addSupportTicket?title=${encodeURIComponent(title)}&text=${encodeURIComponent(text)}`, {
            method: 'POST'
        })
            .then(response => response.text())
            .then(message => {
                alert(message);
                fetchSupportTickets(currentPage);
                fetchSupportTicketsPageCount();
            })
            .catch(error => console.error('Error adding ticket:', error));
    }

    function fetchSupportTickets(page) {
        fetch(`/api/profile/getUserSupportTickets/paged?page=${page}`)
            .then(response => response.json())
            .then(tickets => {
                const ticketsContainer = document.getElementById("support-tickets");
                ticketsContainer.innerHTML = tickets.map(ticket => `
            <div class="ticket">
                <p><strong>Title:</strong> ${ticket.title}</p>
                <p><strong>Text:</strong> ${ticket.text}</p>
                <p><strong>Answer:</strong> ${ticket.answer}</p>
                <button class="btn btn-primary update-btn" value="${ticket.ticketId}">Update</button>
                <button class="btn btn-danger remove-btn" value="${ticket.ticketId}">Remove</button>
            </div>
        `).join('');

                // Add event listeners to buttons
                document.querySelectorAll(".btn.btn-primary").forEach(button => {
                    button.addEventListener("click", function() {
                        updateTicket(this.value);
                    });
                });

                document.querySelectorAll(".btn.btn-danger").forEach(button => {
                    button.addEventListener("click", function() {
                        removeTicket(this.value);
                    });
                });
            })
            .catch(error => console.error('Error fetching tickets:', error));
    }

    function fetchSupportTicketsPageCount() {
        fetch(`/api/profile/getUserSupportTickets/page-count`)
            .then(response => response.json())
            .then(pageCount => {
                const paginationContainer = document.getElementById("pagination");
                paginationContainer.innerHTML = '';
                for (let i = 1; i <= pageCount; i++) {
                    const pageButton = document.createElement('button');
                    pageButton.className = 'pagination-button'; // Add this line to set the class
                    pageButton.innerText = i;

                    // Set the active class
                    if (i === currentPage) {
                        pageButton.classList.add('active');
                    }
                    pageButton.addEventListener('click', () => {
                        currentPage = i;
                        fetchSupportTickets(currentPage);
                        updatePaginationButtons(paginationContainer, i);
                    });
                    paginationContainer.appendChild(pageButton);
                }
            })
            .catch(error => console.error('Error fetching page count:', error));
    }

    function updateTicket(ticketId) {
        const title = prompt("Enter new title:");
        const text = prompt("Enter new text:");

        fetch(`/api/profile/updateSupportTicket/${ticketId}?title=${encodeURIComponent(title)}&text=${encodeURIComponent(text)}`, {
            method: 'PUT'
        })
            .then(response => response.text())
            .then(message => {
                alert(message);
                fetchSupportTickets(currentPage);
            })
            .catch(error => console.error('Error updating ticket:', error));
    }

    function removeTicket(ticketId) {
        fetch(`/api/profile/removeTicket/${ticketId}`, {
            method: 'DELETE'
        })
            .then(response => response.text())
            .then(message => {
                alert(message);
                fetchSupportTickets(currentPage);
                fetchSupportTicketsPageCount();
            })
            .catch(error => console.error('Error removing ticket:', error));
    }

    function manageApplyings() {
        document.getElementById("main-content").innerHTML = `
        <h1>Manage Role Applications</h1>
        <div id="role-applications"></div>
        <div id="pagination" class="support-container"></div>
    `;

        currentPage = 1;

        fetchApplications(currentPage);
        fetchApplicationsPageCount();
    }

    function fetchApplications(page) {
        fetch(`/api/users/getApplyRolesByPage/paged?page=${page}`)
            .then(response => response.json())
            .then(applications => {
                const applicationsContainer = document.getElementById("role-applications");
                applicationsContainer.innerHTML = applications.map(app => `
                <div class="application">
                    <p><strong>User:</strong> ${app.username}</p>
                    <p><strong>Role Applied:</strong> ${app.role}</p>
                    <button class="approve-btn" value="${app.arId}">Approve</button>
                    <button class="reject-btn" value="${app.arId}">Reject</button>
                </div>
            `).join('');

                // Add event listeners to buttons
                document.querySelectorAll(".approve-btn").forEach(button => {
                    button.addEventListener("click", function() {
                        approveRole(this.value);
                    });
                });

                document.querySelectorAll(".reject-btn").forEach(button => {
                    button.addEventListener("click", function() {
                        rejectRole(this.value);
                    });
                });
            })
            .catch(error => console.error('Error fetching applications:', error));
    }

    function fetchApplicationsPageCount() {
        fetch(`/api/users/getApplyRolesByPage/page-count`)
            .then(response => response.json())
            .then(pageCount => {
                const paginationContainer = document.getElementById("pagination");
                paginationContainer.innerHTML = '';
                for (let i = 1; i <= pageCount; i++) {
                    const pageButton = document.createElement('button');
                    pageButton.className = 'pagination-button'; // Add this line to set the class
                    pageButton.innerText = i;

                    if (i === currentPage) {
                        pageButton.classList.add('active');
                    }
                    pageButton.addEventListener('click', () => {
                        currentPage = i;
                        fetchApplications(currentPage);
                        updatePaginationButtons(paginationContainer, i);
                    });
                    paginationContainer.appendChild(pageButton);
                }
            })
            .catch(error => console.error('Error fetching page count:', error));
    }



    function approveRole(appId) {
        fetch(`/api/users/changeRoleOfUser?arId=${appId}`, {
            method: 'POST'
        })
            .then(response => response.text())
            .then(message => {
                alert(message);
                fetchApplications(currentPage);
                fetchApplicationsPageCount();
            })
            .catch(error => console.error('Error approving role:', error));
    }

    function rejectRole(appId) {
        fetch(`/api/users/rejectRoleChangeRequest?arId=${appId}`, {
            method: 'DELETE'
        })
            .then(response => response.text())
            .then(message => {
                alert(message);
                fetchApplications(currentPage);
                fetchApplicationsPageCount();
            })
            .catch(error => console.error('Error rejecting role:', error));
    }

    function editProfileAdder() {
        document.getElementById("main-content").innerHTML = `
        <h1>Edit Profile</h1>
        <form id="editUserForm">
            <div class="form-group">
                <label for="name">Name Surname:</label>
                <input type="text" id="name" name="name" required>
            </div>
            <div class="form-group">
                <label for="email">Email:</label>
                <input type="text" id="email" name="email" required>
            </div>
            <div class="form-group">
                <label for="phoneNumber">Phone Number (+90xxxxxxxxxx):</label>
                <input type="text" id="phoneNumber" name="phoneNumber" required>
            </div>
            <div class="form-group">
                <label for="gender">Gender:</label>
                <select id="gender" name="gender" required>
                    <option value="male">Male</option>
                    <option value="female">Female</option>
                </select>
            </div>
            <div class="form-group">
                <label for="dateOfBirth">Birth Date (YYYY-MM-DD):</label>
                <input type="text" id="dateOfBirth" name="dateOfBirth" required>
            </div>
            <div class="form-group">
                <label for="theme">Theme (dark/light):</label>
                <select id="theme" name="theme" required>
                    <option value="light">Light</option>
                    <option value="dark">Dark</select>
            </div>
            <div class="form-group">
                <label for="notificationEnabled">Notifications Enabled?:</label>
                <select id="notificationEnabled" name="notificationEnabled" required>
                    <option value="false">No</option>
                    <option value="true">Yes</option>
                </select>
            </div>
            <div class="form-group action-buttons" style="display: flex; justify-content: space-between;">
                <button type="submit">Edit User</button>
       
            </div>
            <div id="role-buttons" class="form-group" style="margin-top: 20px;">
                <!-- Role application buttons will be injected here -->
            </div>
        </form>
    `;

        document.getElementById("user-name").innerText = `${appUser.username}`;
        document.getElementById("name").value = appUser.name;
        document.getElementById("email").value = appUser.email;
        document.getElementById("phoneNumber").value = appUser.phoneNumber;
        document.getElementById("gender").value = appUser.gender;
        document.getElementById("dateOfBirth").value = appUser.dateOfBirth;
        document.getElementById("theme").value = appUser.theme;
        document.getElementById("notificationEnabled").value = appUser.notificationEnabled.toString();

        // Kullanıcı USER rolünde ise rol başvuru butonlarını ekleyin
        if (appUser.role === 'USER') {
            document.getElementById("role-buttons").innerHTML = `
            <button type="button" id="apply-merchant" class="btn btn-secondary">Apply for Merchant Role</button>
            <button type="button" id="apply-comMod" class="btn btn-secondary">Apply for Community Moderator Role</button>
        `;

            // Merchant rolüne başvuru
            document.getElementById("apply-merchant").addEventListener("click", function() {
                applyForRole('MERCHANT');
            });

            // Admin rolüne başvuru
            document.getElementById("apply-comMod").addEventListener("click", function() {
                applyForRole('COMMUNITY_MODERATOR');
            });
        }

        document.getElementById("editUserForm").addEventListener("submit", function(event) {
            event.preventDefault();
            const formData = new FormData(this);
            fetch("/api/profile/updateProfile", {
                method: "PUT",
                body: formData
            })
                .then(response => {
                    if (!response.ok) {
                        alert("Invalid Input(s)");
                        throw new Error('Network response was not ok');
                    }
                    fetch('/api/auth/getCurrentUser', {
                        method: 'GET',
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    })
                        .then(response => {
                            if (!response.ok) {
                                throw new Error('Network response was not ok ' + response.statusText);
                            }
                            return response.json();
                        })
                        .then(data => {
                            localStorage.setItem("appUser", JSON.stringify(data));
                        })
                        .catch(error => {
                            console.error('Error:', error);
                        });
                    appUser = JSON.parse(localStorage.getItem("appUser"));
                    window.location.reload();
                })
                .catch(error => console.error('Error editing user:', error));
        });
    }

    function applyForRole(role) {
        fetch(`/api/profile/applyRole?role=${role}`, {
            method: "POST"
        })
            .then(response => response.text())
            .then(message => {
                alert(message);
            })
            .catch(error => console.error('Error applying for role:', error));
    }

    function changePassword(){
        document.getElementById("main-content").innerHTML = `
        <h1>Change Password</h1>
        <form id="changePassword">
            <div class="form-group">
                <label for="oldPassword">Current Password:</label>
                <input type="password" id="oldPassword" name="oldPassword" required>
            </div>
            <div class="form-group">
                <label for="newPassword">New Password:</label>
                <input type="password" id="newPassword" name="newPassword" required>
            </div>
            <div class="form-group">
                <label for="rnewPassword">New Password Again</label>
                <input type="password" id="rnewPassword" name="rnewPassword" required>
            </div>
            <div class="form-group action-buttons" style="display: flex; justify-content: space-between;">
                <button type="submit">Change Password</button>
                <button type="button" class="returnbut">
                    <a>Go to Users Page</a>
                </button>
            </div>
        </form>
    `;

        document.getElementById("changePassword").addEventListener("submit", function(event) {
            event.preventDefault();

            const oldPassword = document.getElementById("oldPassword").value;
            const newPassword = document.getElementById("newPassword").value;
            const rnewPassword = document.getElementById("rnewPassword").value;

            // Yeni şifrelerin eşleştiğini kontrol edin
            if (newPassword !== rnewPassword) {
                alert("New passwords do not match.");
                return;
            }

            // Verileri URLSearchParams kullanarak URL parametresi formatında oluşturun
            const params = new URLSearchParams();
            params.append("oldPassword", oldPassword);
            params.append("newPassword", newPassword);

            fetch("/api/auth/changePassword", {
                method: "PUT",
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: params
            })
                .then(response => {
                    if (!response.ok) {
                        return response.text().then(text => { throw new Error(text) });
                    }
                    return response.text();
                })
                .then(message => {
                    alert(message);
                    window.location.reload();
                })
                .catch(error => alert('Error changing password: ' + error.message));
        });
    }


    function managePromotion() {
        document.getElementById("main-content").innerHTML = `
       
        <h1>Promotion Management</h1>
        <button id="add-promotion-btn" class="promotion-button">Add Promotion</button>
        <div id="promotion-list" class="promotion-management-container"></div>
        <div id="pagination" class="promotion-management-container"></div>
    `;

        let currentPage = 1;

        fetchPromotions(currentPage);
        fetchPromotionsPageCount();
        document.getElementById("add-promotion-btn").addEventListener("click", addPromotion);
    }

    function addPromotion() {
        const productId = prompt("Enter product ID:");
        const discount = prompt("Enter discount percentage:");

        fetch(`/api/products/addMerchantPromotion/${productId}?discount=${encodeURIComponent(discount)}`, {
            method: 'POST'
        })
            .then(response => response.text())
            .then(message => {
                alert(message);
                fetchPromotions(currentPage);
            })
            .catch(error => console.error('Error adding promotion:', error));
    }

    function fetchPromotions(page) {
        fetch(`/api/products/merchantPromotions/paged?page=${page}`)
            .then(response => response.json())
            .then(promotions => {
                const promotionsContainer = document.getElementById("promotion-list");
                promotionsContainer.innerHTML = promotions.map(promotion => `
                <div class="promotion-item">
                    <div>
                        <h3>${promotion.productName}</h3>
                        <p>Discount: ${promotion.discount}%</p>
                        <p>Original Price: ${promotion.originalPrice}</p>
                        <p>Current Price: ${Math.floor(promotion.originalPrice * ((100 - promotion.discount) / 100))}</p>
                    </div>
                    <div class="btn-group">
                        <button class="update-btn" value="${promotion.mpId}">Update</button>
                        <button class="remove-btn" value="${promotion.mpId}">Remove</button>
                    </div>
                </div>
            `).join('');

                document.querySelectorAll(".update-btn").forEach(button => {
                    button.addEventListener("click", function() {
                        updatePromotion(this.value);
                    });
                });

                document.querySelectorAll(".remove-btn").forEach(button => {
                    button.addEventListener("click", function() {
                        removePromotion(this.value);
                    });
                });
            })
            .catch(error => console.error('Error fetching promotions:', error));
    }

    function fetchPromotionsPageCount() {
        fetch(`/api/products/merchantPromotions/page-count`)
            .then(response => response.json())
            .then(pageCount => {
                const paginationContainer = document.getElementById("pagination");
                paginationContainer.innerHTML = '';
                for (let i = 1; i <= pageCount; i++) {
                    const pageButton = document.createElement('button');
                    pageButton.className = 'pagination-button'; // Add this line to set the class
                    pageButton.innerText = i;
                    pageButton.addEventListener('click', () => {
                        currentPage = i;
                        fetchPromotions(currentPage);
                        updatePaginationButtons(paginationContainer, i);
                    });
                    paginationContainer.appendChild(pageButton);
                }
            })
            .catch(error => console.error('Error fetching page count:', error));
    }



    function updatePromotion(mp_id) {
        const discount = prompt("Enter new discount percentage:");
        const temp = new FormData();
        temp.set("discount",Number(discount))

        fetch(`/api/products/updateMerchantPromotion/${mp_id}`, {
            method: 'POST',
            body: temp
        })
            .then(response => response.text())
            .then(message => {
                alert(message);
                fetchPromotions(currentPage);
            })
            .catch(error => console.error('Error updating promotion:', error));
    }

    function removePromotion(mp_id) {
        fetch(`/api/products/removeMerchantPromotion/${mp_id}`, {
            method: 'DELETE'
        })
            .then(response => response.text())
            .then(message => {
                alert(message);
                fetchPromotions(currentPage);
            })
            .catch(error => console.error('Error removing promotion:', error));
    }

    function seeRewards(){
        document.getElementById("main-content").innerHTML = `
       
        <h1>Ticket Management</h1>
        <div id="reward-list" class="ticket-management-container"></div>
        <div id="pagination" class="ticket-management-container"></div>
    `;
        let currentPage = 1;

        loadRewards(currentPage);

        // Create page buttons
        loadRewardPageCount("pagination", currentPage, loadRewards);
    }

    function loadRewards(page) {
        fetch(`/api/profile/userRewards/paged?page=${page}`)
            .then(response => response.json())
            .then(rewards => {
                const rewardsContainer = document.getElementById("reward-list");

                rewardsContainer.innerHTML = rewards.map(reward => `
                <div class="ticket-item">
                    <h3>Reward: ${reward.rewardName}</h3>
                    <p>Description: ${reward.description}</p>
                </div>
            `).join('');
            })
            .catch(error => console.error('Error loading rewards:', error));
    }

    function loadRewardPageCount(containerId, currentPage, loadFunction){
        fetch(`/api/profile/userRewards/page-count`)
            .then(response => response.json())
            .then(pageCount => {
                const paginationContainer = document.getElementById(containerId);
                paginationContainer.innerHTML = ''; // Clear existing buttons
                for (let i = 1; i <= pageCount; i++) {
                    const pageButton = document.createElement('button');
                    pageButton.className = 'pagination-button'; // Add this line to set the class
                    pageButton.innerText = i;

                    // Set the active class
                    if (i === currentPage) {
                        pageButton.classList.add('active');
                    }
                    pageButton.addEventListener('click', () => {
                        currentPage = i;
                        loadFunction(currentPage);
                        updatePaginationButtons(paginationContainer, i);
                    });
                    paginationContainer.appendChild(pageButton);
                }
            })
            .catch(error => console.error('Error loading page count:', error));
    }



    function seeProducts() {
        document.getElementById("main-content").innerHTML = `
       
        <h1>Purchased Products</h1>
        <div id="productsContainer" class="product-management-container"></div>
        <div id="pagination" class="product-management-container"></div>
    `;

        let currentPage = 1;

        loadProducts(currentPage);

        // Create page buttons
        loadPageButtons("pagination", currentPage, loadProducts);
    }

    function loadProducts(page) {
        fetch(`/api/profile/userProducts/paged?page=${page}`)
            .then(response => response.json())
            .then(products => {
                const productsContainer = document.getElementById("productsContainer");

                productsContainer.innerHTML = ''; // Clear
                products.forEach(product => {
                    // Create card for each product
                    const productCard = document.createElement('div');
                    productCard.className = 'product-item'; // Adjust columns as needed
                    productCard.innerHTML = `
                    <div>
                        <h3>${product.productName}</h3>
                        <p>${product.description}</p>
                        <p>Price: ${product.price}</p>
                    </div>
                `;
                    productsContainer.appendChild(productCard);
                });
            })
            .catch(error => console.error('Error fetching products:', error));
    }

    function loadPageButtons(containerId, currentPage, loadFunction) {
        fetch(`/api/profile/userProducts/page-count`)
            .then(response => response.json())
            .then(pageCount => {
                const paginationContainer = document.getElementById(containerId);
                paginationContainer.innerHTML = ''; // Clear existing buttons
                for (let i = 1; i <= pageCount; i++) {
                    const pageButton = document.createElement('button');
                    pageButton.className = 'pagination-button btn btn-secondary'; // Add this line to set the class
                    pageButton.innerText = i;

                    // Set the active class
                    if (i === currentPage) {
                        pageButton.classList.add('active');
                    }

                    pageButton.addEventListener('click', () => {
                        currentPage = i;
                        loadFunction(currentPage);
                        loadPageButtons(containerId, currentPage, loadFunction); // Update buttons
                    });

                    // Add space between buttons
                    pageButton.style.marginRight = '5px';

                    paginationContainer.appendChild(pageButton);
                }
            })
            .catch(error => console.error('Error loading page count:', error));
    }



    function updatePaginationButtons(paginationContainer, currentPage) {
        const buttons = paginationContainer.getElementsByClassName('pagination-button');
        for (let button of buttons) {
            button.classList.remove('active');
        }
        buttons[currentPage - 1].classList.add('active');
    }

    editProfileAdder();
});
