const appUser = JSON.parse(localStorage.getItem("appUser"));

let menu_icon_box = document.querySelector(".menu_icon_box");
let box = document.querySelector(".sidebar");

if(appUser == null || appUser.role === "ADMIN"){
    document.getElementById("wishListIdOfOfTemp").style.display = "none";
}

menu_icon_box.onclick = function(){
    menu_icon_box.classList.toggle("active");
    box.classList.toggle("active_sidebar");
}


document.getElementById('searchButtonOfSearch').addEventListener('click', function() {
    const searchInput = document.getElementById('searchInputOfSearch').value;
    window.location.href = `/html/UnregUser/searchedPage.html?search=${encodeURIComponent(searchInput)}`;
});

document.addEventListener("DOMContentLoaded", function() {
    fetch('/api/browseFilterP/getAllCategories')
        .then(response => response.json())
        .then(data => {
            const select = document.getElementById('categoriesOfSystem');
            data.forEach(category => {
                const label = document.createElement('label');
                label.className = 'category-side-bar';
                label.innerHTML = `
                <li><a href="/html/UnregUser/categoryPage.html?category=${encodeURIComponent(category.categoryName)}">${category.categoryName}</a></li>
                `;
                select.appendChild(label);
            });
        })
        .catch(error => console.error('Error fetching categories:', error));
});

document.addEventListener('DOMContentLoaded', () => {
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
            window.location.reload();
        })
        .catch(error => console.error('Error', error));

};


async function fetchProduct(productId) {
    const response = await fetch(`/api/browseFilterP/getProductById/${productId}`);
    return response.json();
}
function formatTimestamp(timestamp) {
    const date = new Date(timestamp);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');

    return `${year}-${month}-${day}&nbsp;&nbsp;&nbsp;&nbsp;${hours}:${minutes}:${seconds}`;
}

document.addEventListener('DOMContentLoaded', function() {
    const reviewsList = document.getElementById('reviews-list');
    const prevPageButton = document.getElementById('prev-page');
    const nextPageButton = document.getElementById('next-page');
    const pageInfo = document.getElementById('page-info');
    const addResponseForm = document.getElementById('addResponseForm');
    const responseForm = document.getElementById('responseForm');
    let currentPage = 1;
    let totalPages = 1;
    let productId;

    async function fetchProduct(productId) {
        const response = await fetch(`/api/browseFilterP/getProductById/${productId}`);
        return response.json();
    }

    async function fetchReviews(productId, page) {
        const response = await fetch(`/api/viewPQa/viewPQ/${productId}?page=${page}`);
        return response.json();
    }

    async function addReview(text, productId) {
        const formData = new FormData();
        formData.set("text", text);
        const response = await fetch(`/api/addRR/addQ/${productId}`, {
            method: 'POST',
            body: formData
        });
        return response.json();
    }

    async function updateReview(reviewId) {
        const text = prompt("Enter updated review text:");
        let answer = "";
        if(appUser.role === 'ADMIN'){
            answer = prompt("Enter updated review answer:");
        }
        if (text) {
            const formData = new FormData();
            formData.set("text", text);
            formData.set("answer",answer);
            const response = await fetch(`/api/addRR/updateQ/${reviewId}`, {
                method: 'PUT',
                body: formData
            });

            if (response.ok) {
                const responseText = await response.text();
                try {
                    return JSON.parse(responseText);
                } catch (error) {
                    console.error("Failed to parse response as JSON:", responseText);
                    return {}; // Return an empty object or handle the error as needed
                }
            } else {
                console.error(`Failed to update review with status: ${response.status}`);
                throw new Error(`Failed to update review: ${response.statusText}`);
            }
        }
    }

    async function updateAnswer(reviewId) {
        const text = null;
        let answer = prompt("Enter updated review answer:");
        if (answer) {
            const formData = new FormData();
            formData.set("text", text);
            formData.set("answer",answer);
            const response = await fetch(`/api/addRR/updateQ/${reviewId}`, {
                method: 'PUT',
                body: formData
            });
            if (response.ok) {
                const responseText = await response.text();
                try {
                    return JSON.parse(responseText);
                } catch (error) {
                    console.error("Failed to parse response as JSON:", responseText);
                    return {}; // Return an empty object or handle the error as needed
                }
            } else {
                console.error(`Failed to update review with status: ${response.status}`);
                throw new Error(`Failed to update review: ${response.statusText}`);
            }
        }
    }

    async function deleteReview(reviewId) {
        if (confirm("Are you sure you want to delete this review?")) {
            const response = await fetch(`/api/addRR/removeQ/${reviewId}`, {
                method: 'DELETE'
            });

            if (response.ok) {
                // Check if the response has a body and parse it only if it does
                const responseText = await response.text();
                if (responseText) {
                    return JSON.parse(responseText);
                }
                return {}; // Return an empty object if there's no body
            } else {
                // Handle non-2xx HTTP responses
                console.error(`Failed to delete review with status: ${response.status}`);
                throw new Error(`Failed to delete review: ${response.statusText}`);
            }
        }
    }

    async function deleteResponse(reviewId) {
        if (confirm("Are you sure you want to delete this answer?")) {
            const response = await fetch(`/api/addRR/removeAnswer/${reviewId}`, {
                method: 'POST'
            });

            if (response.ok) {
                // Check if the response has a body and parse it only if it does
                const responseText = await response.text();
                if (responseText) {
                    return JSON.parse(responseText);
                }
                return {}; // Return an empty object if there's no body
            } else {
                // Handle non-2xx HTTP responses
                console.error(`Failed to delete review with status: ${response.status}`);
                throw new Error(`Failed to delete review: ${response.statusText}`);
            }
        }
    }

    async function upvoteReview(reviewId) {
        const response = await fetch(`/api/addRR/upvoteQ/${reviewId}`, {
            method: 'PUT'
        });
    }

    async function fetchPageCount(productId) {
        const response = await fetch(`/api/viewPQa/viewPQ/page-count/${productId}`);
        const data = await response.json();
        return data;
    }

    function renderReview(review, product) {
        const reviewItem = document.createElement('div');
        reviewItem.classList.add('review-item');
        console.log(review)
        const formattedTime = formatTimestamp(review.qtime);
        reviewItem.innerHTML = `
            <p class="review-user">${review.username}: ${review.text}
            <button class="upvote-review" data-review-id="${review.pqId}"><i class="far fa-thumbs-up"></i>${review.pqUpvote || 0}</button>
            <span class="reviewTime">${formattedTime}</span>
            </p>
            ${appUser != null ? `
                <div class="review-actions">
                    ${review.userId === appUser.userId || appUser.role === 'ADMIN' ? `
                        <button class="update-review small-button" data-review-id="${review.pqId}">Update</button>
                        <button class="delete-review small-button" data-review-id="${review.pqId}">Delete</button>
                    ` : ''}
                    ${review.answer != null ? `
                        <p class="response-user">MERCHANT: ${review.answer}` : ''}
                    
                    ${appUser.userId === product.user_id ? `
                        <div class="add-update-actions">
                        <button class="update-response small-button" data-response-id="${review.pqId}">Add-Update Answer</button>
                        ${review.answer != null ? `
                            <button class="delete-response small-button" data-response-id="${review.pqId}">Delete Answer</button>
                        ` : ''}
                        </div>
                        
                    ` : ''}
                </div>
            ` : ''}
 
             
            `;
        return reviewItem;
    }

    async function loadReviews(page) {
        const url = window.location.href;
        const params = new URLSearchParams(new URL(url).search);
        const product = await fetchProduct(params.get('productId'));
        productId = product.productId;

        if (appUser == null || appUser.userId === product.user_id || appUser.role==="ADMIN") {
            document.getElementById("add-review-button").style.display = 'none';
        }

        if (appUser == null || product.user_id === appUser.userId) {
            document.getElementById("add-review-button").style.display = 'none';
        }
        document.getElementById('productDetailsProductImage').src = ` data:image/png;base64,${product.productImage}`;
        document.getElementById("productNameOfReviews").innerText = product.productName;
        document.getElementById("productDescriptionOfReviews").innerText = product.description;
        let temp123 = product.rating ?? "0";
        document.getElementById("productRatingOfReviews").innerText = temp123 + "/5";
        let temp12 = product.review_count ?? "0";
        document.getElementById("productReviewsNumOfReviews").innerText = temp12 + " Reviews";
        document.getElementById("productPriceOfReviews").innerText = product.price + "₺";

        reviewsList.innerHTML = '';
        const reviews = await fetchReviews(productId, page);
        console.log(reviews); // Log the reviews object to inspect its structure
        reviews.forEach(review => {
            const reviewItem = renderReview(review, product);
            reviewsList.appendChild(reviewItem);
        });
        updatePagination();
    }

    async function updatePagination() {
        totalPages = await fetchPageCount(productId);
        const pagination = document.getElementById('pagination');
        pagination.innerHTML = '';

        for (let i = 1; i <= totalPages; i++) {
            const button = createPaginationButton(i);
            pagination.appendChild(button);
        }
    }

    function createPaginationButton(page) {
        const button = document.createElement('button');
        button.textContent = page;
        if (page === currentPage) {
            button.classList.add('active');
            button.disabled = true;
        }

        button.addEventListener('click', () => {
            currentPage = page;
            loadReviews(currentPage);
        });

        return button;
    }

    document.getElementById('reviewForm').addEventListener('submit', async function(event) {
        event.preventDefault();
        const url = window.location.href;
        const params = new URLSearchParams(new URL(url).search);
        const productId = params.get('productId');

        const review = document.getElementById('review').value;
        await addReview(review, productId);
        location.reload();
    });

    document.querySelector('.reviewForm form').classList.toggle('hidden');
    document.querySelector('.add-review-button').addEventListener('click', function() {
        document.querySelector('.reviewForm form').classList.toggle('hidden');
        window.scrollTo(0, document.body.scrollHeight);
    });

    document.addEventListener('click', async function(event) {
        const target = event.target;

        if (target.classList.contains('update-review')) {
            const reviewId = target.getAttribute('data-review-id');
            await updateReview(reviewId);
            await loadReviews(currentPage);
        } else if (target.classList.contains('delete-review')) {
            const reviewId = target.getAttribute('data-review-id');
            await deleteReview(reviewId);
            await loadReviews(currentPage);
        } else if (target.classList.contains('upvote-review')) {
            const reviewId = target.getAttribute('data-review-id');
            if(appUser !== null && appUser.role !== 'ADMIN') {
                await upvoteReview(reviewId);
                await loadReviews(currentPage);
            }
        } else if (target.classList.contains('update-response')) {
            await updateAnswer(target.getAttribute('data-response-id'));
            await loadReviews(currentPage);
        } else if (target.classList.contains('delete-response')) {
            const reviewId = target.getAttribute('data-response-id');
            await deleteResponse(reviewId);
            await loadReviews(currentPage);
        }
    });

    loadReviews(currentPage);
});
