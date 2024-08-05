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


document.addEventListener("DOMContentLoaded", function () {
    const productId = new URLSearchParams(window.location.search).get('productId');
    if (productId) {
        fetchProductDetails(productId);


    } else {
        console.error("No product ID specified in the URL.");
    }
});

function fetchProductDetails(productId) {
    fetch(`/api/browseFilterP/getProductById/${productId}`)
        .then(response => response.json())
        .then(data => {
            if (data) {
                populateProductDetails(data);
            } else {
                console.error('Product not found.');
            }
        })
        .catch(error => {
            console.error('Error fetching product details:', error);
        });
}

function buyProduct(productId){
    fetch("/api/browseFilterP/buyProduct/" + productId, {
        method: 'POST'
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
}


function bookmarkProduct(productId){
    fetch("/api/browseFilterP/bookmarkProduct/" + productId, {
        method: 'POST'
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
}

function populateProductDetails(product) {
    document.getElementById('productDetailsProductImage').src = ` data:image/png;base64,${product.productImage}`;
    document.getElementById('product-name').textContent = product.productName;
    document.getElementById('product-price').textContent = `${product.price} ₺`;
    let temp123 = "";
    console.log(product.categories);
    for (var i = 0; i < product.categories.length; i++) {
        if(i!== product.categories.length-1){
            temp123+= product.categories[i].categoryName +", ";
        }
        else{
            temp123+= product.categories[i].categoryName;
        }
    }
    document.getElementById("productCategories123").innerText = temp123;
    document.getElementById('product-description').textContent = product.description;
    let temp12 = product.rating ?? "0";
    document.getElementById("product-rating").innerText = temp12 + "/5";
    document.getElementById('review-count').textContent = `${product.review_count} reviews`;
    let temp = Number(product.productId);
    document.getElementById("review-count").href = `/html/UnregUser/productReviews.html?productId=${temp}`;
    document.getElementById("qa").href = `/html/UnregUser/qa.html?productId=${temp}`;
    let button = document.getElementById('purchase-button');
    let bookmark = document.getElementById('bookmark-button');

    if(appUser===null || appUser.role === 'ADMIN' || product.user_id ===appUser.userId){
        button.style.display= "none";
        bookmark.style.display = "none";
    }
    else{
        //bookmark default değeri çekme product.isbookmarkedbyuser(dtodan bak)
        if(!product.isProductBookmarkedByUser) {
            document.getElementById("bookmark-button").innerHTML =`
                    <svg xmlns="http://www.w3.org/2000/svg" width="30" height="30" fill="currentColor"
                         className="bi bi-bookmark" viewBox="0 0 16 16">
                        <path
                            d="M2 2a2 2 0 0 1 2-2h8a2 2 0 0 1 2 2v13.5a.5.5 0 0 1-.777.416L8 13.101l-5.223 2.815A.5.5 0 0 1 2 15.5zm2-1a1 1 0 0 0-1 1v12.566l4.723-2.482a.5.5 0 0 1 .554 0L13 14.566V2a1 1 0 0 0-1-1z"/>
                    </svg>`;
        } else {
            document.getElementById("bookmark-button").innerHTML = `
                <svg xmlns="http://www.w3.org/2000/svg" width="30" height="30" fill="currentColor" class="bi bi-bookmark-fill" viewBox="0 0 16 16">
                    <path d="M2 2v13.5a.5.5 0 0 0 .74.439L8 13.069l5.26 2.87A.5.5 0 0 0 14 15.5V2a2 2 0 0 0-2-2H4a2 2 0 0 0-2 2"/>
                </svg>`;
        }
        button.addEventListener('click', function () {
            buyProduct(product.productId);
        });
        bookmark.addEventListener('click', function () {
            bookmarkProduct(product.productId);
            window.location.reload();
        });

    }
}





