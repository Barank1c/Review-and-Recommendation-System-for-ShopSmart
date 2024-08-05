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
    const isRegistered = appUser!=null;
    const userSection = document.getElementById('user-section');
    const recommendedSection = document.getElementById('recommended-section');
    const popularSection = document.getElementById('popular-section');
    const allProductsSection = document.getElementById('all-products-section');
    const paginationControls = document.querySelector('#all-products-section .flex.justify-center');

    let currentPage = 1;

    // Toggle user section
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
        recommendedSection.style.display = 'block';
    } else {
        userSection.innerHTML = '<a href="/html/UnregUser/login.html">Sign in/Register</a>';
        recommendedSection.style.display = 'none';
    }



    // Function to fetch all products by page number
    const fetchAllProducts = async (page) => {
        try {
            const response = await fetch(`/api/browseFilterP/all/paged?page=${page}`);
            if (!response.ok) {
                throw new Error('Error fetching all products');
            }
            const data = await response.json();
            return data;
        } catch (error) {
            console.error('Error fetching all products:', error);
            throw error;
        }
    };

    // Function to display all products based on the current page
    const displayAllProducts = async (page) => {
        try {
            const productList = await fetchAllProducts(page);
            allProductsSection.querySelector('.grid').innerHTML = generateProductCards(productList);
            await updatePaginationControls(page);
        } catch (error) {
            console.error('Error displaying all products:', error);
        }
    };

    // Function to generate product cards
    const generateProductCards = (productList) => {
        return productList.map(product => `
        <div class="bg-white p-4 rounded shadow">
             <a href="/html/UnregUser/productDetails.html?productId=${product.productId}" class="block">
                <img src="data:image/png;base64,${product.productImage}" class="w-full h-40 object-cover mb-4" alt="${product.productName}">
            </a>
            <a href="/html/UnregUser/productDetails.html?productId=${product.productId}" class="block">
                <h3 class="text-lg font-semibold">${product.productName}</h3>
            </a>
            <p class="text-gray-600">Price: ${product.price}</p>
            ${product.rating !== null ? `<p><span class="text-yellow-500">★</span> <span class="text-black">${product.rating}</span></p>` : ''}
            <p class="text-sm text-gray-500">${product.review_count} reviews</p>
        </div>
    `).join('');
    };







    // Function to update pagination controls
    const updatePaginationControls = async (page) => {
        const totalPages =  await (await fetch('/api/browseFilterP/all/page-count')).json();
        let paginationHTML = '';
        for (let i = 1; i <= totalPages; i++) {
            paginationHTML += `<button class="px-3 py-1 rounded ${i === page ? 'bg-yellow-800 text-white' : 'bg-white text-purple-800'}" data-page="${i}">${i}</button>`;
        }
        paginationControls.innerHTML = paginationHTML;
    };

    // Initial display of all products
    displayAllProducts(currentPage);

    // Pagination controls click event
    paginationControls.addEventListener('click', (e) => {
        if (e.target.tagName === 'BUTTON') {
            currentPage = parseInt(e.target.getAttribute('data-page'));
            displayAllProducts(currentPage);
        }
    });

    // Function to fetch recommended products
    const fetchRecommendedProducts = async () => {
        try {
            const response = await fetch('/api/browseFilterP/recommendedProducts');
            if (!response.ok) {
                throw new Error('Error fetching recommended products');
            }
            const data = await response.json();
            return data;
        } catch (error) {
            console.error('Error fetching recommended products:', error);
            throw error;
        }
    };

    // Function to display recommended products
    const displayRecommendedProducts = async () => {
        try {
            const productList = await fetchRecommendedProducts();
            if(productList!=null && productList.length !== 0){
                recommendedSection.querySelector('.grid').innerHTML = generateProductCards(productList);
            }else{
                recommendedSection.style.display = 'none';
            }
        } catch (error) {
            console.error('Error displaying recommended products:', error);
            recommendedSection.style.display = 'none';
        }
    };

    // Initial display of recommended products
    if(appUser!=null){
        displayRecommendedProducts();
    }


    // Function to fetch currently popular products
    const fetchCurrentlyPopularProducts = async () => {
        try {
            const response = await fetch('/api/browseFilterP/currently-popular');
            if (!response.ok) {
                throw new Error('Error fetching currently popular products');
            }
            const data = await response.json();
            return data;
        } catch (error) {
            console.error('Error fetching currently popular products:', error);
            throw error;
        }
    };

    // Function to display currently popular products
    const displayCurrentlyPopularProducts = async () => {
        try {
            const productList = await fetchCurrentlyPopularProducts();
            if(productList!=null && productList.length!==0){
                popularSection.querySelector('.grid').innerHTML = generateProductCards(productList);
            }else{
                popularSection.style.display="none";
            }
        } catch (error) {
            console.error('Error displaying currently popular products:', error);
        }
    };

    displayCurrentlyPopularProducts();
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