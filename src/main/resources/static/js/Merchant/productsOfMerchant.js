const appUser = JSON.parse(localStorage.getItem("appUser"));

document.getElementById("UserNameOfMerchant").innerText = appUser.username;

document.addEventListener('DOMContentLoaded', function() {
    fetch('/api/products/page-count')
        .then(response => response.json())
        .then(pageCount => {
            createPaginationButtons(pageCount);
            getProductsByPage(1); // Load the first page of products
        })
        .catch(error => console.error('Error fetching page count:', error));
});

function getProductsByPage(page) {
    fetch(`/api/products/paged?page=${page}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(products => {
            const productsContainer = document.getElementById('productsContainer');
            productsContainer.innerHTML = ''; // Clear existing products
            products.forEach(product => {
                const productCard = document.createElement('div');
                productCard.className = 'col-md-3'; // Adjust columns as needed
                productCard.style.paddingLeft = "0px";
                productCard.innerHTML = `
                    <div class="card">
                        <img src="data:image/png;base64,${product.productImage}" class="card-img-top" alt="${product.productName}">
                        <div class="card-body">
                            <h5 class="card-title">${product.productName}</h5>
                            <p class="card-text">${product.description}</p>
                            <p class="card-text">Price: ${product.price}</p>
                            <div class="btn-group" role="group" aria-label="Product actions">
                                <button onclick="goToEditProductPage(${product.productId})" class="btn btn-primary btn-edit">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-pencil-square" viewBox="0 0 16 16">
                                      <path d="M15.502 1.94a.5.5 0 0 1 0 .706L14.459 3.69l-2-2L13.502.646a.5.5 0 0 1 .707 0l1.293 1.293zm-1.75 2.456-2-2L4.939 9.21a.5.5 0 0 0-.121.196l-.805 2.414a.25.25 0 0 0 .316.316l2.414-.805a.5.5 0 0 0 .196-.12l6.813-6.814z"/>
                                      <path fill-rule="evenodd" d="M1 13.5A1.5 1.5 0 0 0 2.5 15h11a1.5 1.5 0 0 0 1.5-1.5v-6a.5.5 0 0 0-1 0v6a.5.5 0 0 1-.5.5h-11a.5.5 0 0 1-.5-.5v-11a.5.5 0 0 1 .5-.5H9a.5.5 0 0 0 0-1H2.5A1.5 1.5 0 0 0 1 2.5z"/>
                                    </svg>
                                    Edit
                                </button>
                                <button onclick="removeProduct(${product.productId})" class="btn btn-danger btn-remove">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-trash" viewBox="0 0 16 16">
                                      <path d="M5.5 5.5A.5.5 0 0 1 6 6v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5m2.5 0a.5.5 0 0 1 .5.5v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5m3 .5a.5.5 0 0 0-1 0v6a.5.5 0 0 0 1 0z"/>
                                      <path d="M14.5 3a1 1 0 0 1-1 1H13v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V4h-.5a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1H6a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1h3.5a1 1 0 0 1 1 1zM4.118 4 4 4.059V13a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4.059L11.882 4zM2.5 3h11V2h-11z"/>
                                    </svg>
                                    Remove
                                </button>
                            </div>
                            ${product.isProductHasPromotion == null ? `
                                <div class="d-grid gap-2 d-md-flex justify-content-start mt-2">
                                    <button onclick="addPromotion(${product.productId})" class="btn btn-primary btn-promotion" style="width: 250px; background-color: darkolivegreen; border-color: transparent;" onmouseover="this.style.backgroundColor='#2d3818'; this.style.borderColor='#2d3818';"
                                         onmouseout="this.style.backgroundColor='darkolivegreen'; this.style.borderColor='transparent';">
                                        Add New Promotion
                                    </button>
                                </div>
                            ` : `
                                <div class="btn-group" role="group" aria-label="Product actions">
                                    <button onclick="editPromotion(${product.isProductHasPromotion})" class="btn btn-primary btn-edit">
                                        Edit Promotion
                                    </button>
                                    <button onclick="removePromotion(${product.isProductHasPromotion})" class="btn btn-danger btn-remove">
                                        Delete Promotion
                                    </button>
                                </div>
                            `}
                        </div>
                    </div>
                `;
                productsContainer.appendChild(productCard);
            });
        })
        .catch(error => console.error('Error fetching products:', error));
}

function addPromotion(productId) {
    const discount = prompt("Enter discount percentage:");
    if(discount){
        fetch(`/api/products/addMerchantPromotion/${productId}?discount=${encodeURIComponent(discount)}`, {
            method: 'POST'
        }).then(response => response.text())
            .then(message => {
                location.reload(); // Reload the page
            })
            .catch(error => console.error('Error adding promotion:', error));
    }
}

function editPromotion(mp_id) {
    const discount = prompt("Enter new discount percentage:");
    if(discount){
        const temp = new FormData();
        temp.set("discount", Number(discount));
        fetch(`/api/products/updateMerchantPromotion/${mp_id}`, {
            method: 'POST',
            body: temp
        }).then(response => response.text())
            .then(message => {
                location.reload(); // Reload the page
            })
            .catch(error => console.error('Error updating promotion:', error));

    }

}

function removePromotion(mp_id) {
    fetch(`/api/products/removeMerchantPromotion/${mp_id}`, {
        method: 'DELETE'
    }).then(response => response.text())
        .then(message => {
            location.reload(); // Reload the page
        })
        .catch(error => console.error('Error removing promotion:', error));
}

function removeProduct(productId) {
    fetch(`/api/products/${productId}`, {
        method: 'DELETE',
    }).then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        location.reload(); // Reload the page
    }).catch(error => console.error('Error removing product:', error));
}

function goToEditProductPage(productId) {
    window.location.href = `../Merchant/editProduct.html?productId=${productId}`;
}

function goToAddProductPage() {
    window.location.href = "../Merchant/addProduct.html";
}

function createPaginationButtons(pageCount) {
    const paginationButtonsContainer = document.getElementById('paginationButtons');
    paginationButtonsContainer.innerHTML = ''; // Clear existing buttons

    for (let i = 1; i <= pageCount; i++) {
        const button = document.createElement('button');
        button.textContent = i;
        button.className = 'page-button'; // Add a class for styling
        button.addEventListener('click', function() {
            setActiveButton(i); // Set this button as active
            getProductsByPage(i); // Load products for this page
        });
        paginationButtonsContainer.appendChild(button);

        // Automatically set the first button as active
        if (i === 1) {
            button.classList.add('active');
        }
    }
}

function setActiveButton(pageNumber) {
    document.querySelectorAll('.nav-button button').forEach(btn => {
        btn.classList.remove('active');
    });

    const activeButton = [...document.querySelectorAll('.nav-button button')]
        .find(btn => btn.textContent === pageNumber.toString());
    if (activeButton) {
        activeButton.classList.add('active');
    }
}
