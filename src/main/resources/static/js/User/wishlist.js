
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



document.addEventListener('DOMContentLoaded', function() {
    const appUser = JSON.parse(localStorage.getItem("appUser"));
    let menu_icon_box = document.querySelector(".menu_icon_box");
    let box = document.querySelector(".sidebar");

    if (appUser == null || appUser.role === "ADMIN") {
        document.getElementById("wishListIdOfOfTemp").style.display = "none";
    }

    menu_icon_box.onclick = function() {
        menu_icon_box.classList.toggle("active");
        box.classList.toggle("active_sidebar");
    }

    document.addEventListener('DOMContentLoaded', () => {
        const isRegistered = appUser != null;
        const userSection = document.getElementById('user-section');
        userSection.innerHTML = '<a href="/html/UnregUser/login.html">Sign in/Register</a>';
        if (isRegistered) {
            userSection.innerHTML = `<div class="flex items-center space-x-5">
                                <span>${appUser.username}</span>
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


    document.getElementById('searchButtonOfSearch').addEventListener('click', function() {
        const searchInput = document.getElementById('searchInputOfSearch').value;
        window.location.href = `/html/UnregUser/searchedPage.html?search=${encodeURIComponent(searchInput)}`;
    });

    function fetchWishLists() {
        fetch('/api/wishlists')
            .then(response => response.json())
            .then(data => displayWishLists(data))
            .catch(error => console.error('Error fetching wish lists:', error));
    }

    function convertToJSON(dtoStr) {
        const cleanStr = dtoStr.replace(/^[a-zA-Z]+Dto\(|\)$/g, '');
        const keyValuePairs = cleanStr.split(', ');

        const jsonPairs = keyValuePairs.map(pair => {
            const [key, value] = pair.split('=');
            let formattedValue = value;
            if (!value.match(/^\d+$/) && value !== 'null') {
                formattedValue = `"${value}"`;
            }
            return `"${key.trim()}": ${formattedValue}`;
        });

        return `{ ${jsonPairs.join(', ')} }`;
    }

    function displayWishLists(wishLists) {
        const container = document.getElementById('wishListContainer');
        container.innerHTML = ''; // Clear previous contents
        let favouritesSection = null;
        for (const [key, products] of Object.entries(wishLists)) {
            const wishListDetails = JSON.parse(convertToJSON(key));
            const section = document.createElement('section');
            section.className = 'wish-list-section';

            const headerDiv = document.createElement('div');
            headerDiv.className = 'wishlist-header';

            const wishListName = document.createElement('h2');
            wishListName.textContent = wishListDetails.wishListName;
            headerDiv.appendChild(wishListName);

            if (!(wishListDetails.wishListName === "Favourites")) {
                const deleteButton = document.createElement('button');
                deleteButton.innerText = 'Delete Wishlist';
                deleteButton.id = wishListDetails.wishListId;
                deleteButton.className = 'delete-wishlist-button';
                deleteButton.onclick = function () {
                    deleteWishList(wishListDetails.wishListId);
                };
                headerDiv.appendChild(deleteButton);
            }

            const addButton = document.createElement('button');
            addButton.innerText = 'Add Item';
            addButton.className = 'add-product-button';
            addButton.onclick = function() { redirectToItemToWishlist(wishListDetails.wishListId); };
            headerDiv.appendChild(addButton);

            if (!(wishListDetails.wishListName === "Favourites")) {
                const changeNameButton = document.createElement('span');
                changeNameButton.innerHTML = 'Change Name';
                changeNameButton.className = 'change-name-button';
                changeNameButton.onclick = () => changeNameWishlist(wishListDetails.wishListId);
                headerDiv.appendChild(changeNameButton);
            }

            section.appendChild(headerDiv);

            const productListDiv = document.createElement('div');
            productListDiv.className = 'products-container';

            if (products.length > 0) {
                products.forEach(product => {
                    const productDiv = document.createElement('div');
                    productDiv.className = 'product-item';
                    productDiv.id = `product-${product.id}`;

                    const productNameAndRemoveBtn = document.createElement('div');
                    productNameAndRemoveBtn.className = 'product-name-remove';

                    const productName = document.createElement('span');
                    productName.textContent = product.productName;
                    productName.className = 'product-name';

                    productNameAndRemoveBtn.appendChild(productName);

                    const removeButton = document.createElement('span');
                    removeButton.innerHTML = '×';
                    removeButton.className = 'remove-product-button';
                    removeButton.onclick = () => deleteProductFromWishlist(wishListDetails.wishListId, product.productId);
                    productNameAndRemoveBtn.appendChild(removeButton);

                    const productImage = document.createElement('img');
                    productImage.src = `data:image/png;base64,${product.productImage}`;
                    productImage.alt = `${product.productName}`;
                    productImage.className = 'product-image';

                    const productPrice = document.createElement('p');
                    productPrice.textContent = `Price: ${product.price}`;
                    productPrice.className = 'product-price';

                    productDiv.appendChild(productNameAndRemoveBtn);
                    productDiv.appendChild(productImage);
                    productDiv.appendChild(productPrice);

                    productListDiv.appendChild(productDiv);
                });
            } else {
                const noProductsMessage = document.createElement('p');
                noProductsMessage.textContent = 'No products in this wish list.';
                productListDiv.appendChild(noProductsMessage);
            }

            section.appendChild(productListDiv);
            container.appendChild(section);
            if (wishListDetails.wishListName.toLowerCase() === "favourites") {
                favouritesSection = section;
            } else {
                container.appendChild(section);
            }
        }
        if (favouritesSection) {
            container.prepend(favouritesSection);
        }
    }

    function createWishList(wishListName) {
        const formData = new FormData();
        formData.append("wishListName", wishListName);

        fetch("/api/wishlists", {
            method: "POST",
            body: formData
        })
            .then(response => {
                if (!(response.status === 201)) {
                    throw new Error('Network response was not ok');
                }
                fetchWishLists();
            })
            .catch(error => console.error('Error in changing wishlist name!', error));
    }

    document.getElementById('addWishlistButton').addEventListener('click', function() {
        const wishListName = prompt('Enter the name of the new wishlist:');
        if (!wishListName || !wishListName.trim()) {
            alert('Wishlist name cannot be empty.');
        } else {
            createWishList(wishListName);
        }
    });

    function deleteWishList(wishListId) {
        if (!confirm('Are you sure you want to delete this wish list?')) {
            return;
        }

        fetch(`/api/wishlists/${wishListId}`, {
            method: 'DELETE'
        })
            .then(response => {
                if (!(response.status === 201)) {
                    throw new Error('Network response was not ok');
                }
                location.reload();
            })
            .catch(error => console.error('Error removing wishlist:', error));
    }

    function deleteProductFromWishlist(wishListId, productId) {
        if (!confirm('Are you sure you want to delete this item from wishlist?')) {
            return;
        }

        const formData = new FormData();
        formData.append("productId", productId);

        fetch(`/api/wishlists/aptwl/${wishListId}`, {
            method: 'DELETE',
            body: formData
        })
            .then(response => {
                if (!(response.status === 201)) {
                    throw new Error('Network response was not ok');
                }
                location.reload();
            })
            .catch(error => console.error('Error removing product from wishlist:', error));
    }

    function changeNameWishlist(wishListId) {
        const newName = prompt("Please enter the new name for the wishlist:", "New Wishlist Name");
        if (newName === null || newName.trim() === "") {
            console.log("No new name provided. Operation cancelled.");
            return;
        }

        const formData = new FormData();
        formData.append("wishListName", newName);

        fetch(`/api/wishlists/cwln/${wishListId}`, {
            method: 'PUT',
            body: formData
        })
            .then(response => {
                if (!(response.status === 201)) {
                    throw new Error('Network response was not ok');
                }
                location.reload();
            })
            .catch(error => console.error('Error in changing wishlist name!', error));
    }

    function redirectToItemToWishlist(wishListId) {
        const queryString = `?wishlistId=${encodeURIComponent(wishListId)}`;
        window.location.href = `itemtowishlist.html${queryString}`;
    }

    fetchWishLists();
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