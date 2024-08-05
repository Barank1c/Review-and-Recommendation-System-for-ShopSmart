const url = window.location.href;
const params = new URLSearchParams(new URL(url).search);
const wishListId = params.get('wishlistId');
fetchProductsAndDisplayForms(wishListId);

function fetchProductsAndDisplayForms(wishListId) {
    fetch(`/api/wishlists/aptwl/${wishListId}`)
        .then(response => response.json())
        .then(products => {
            const form = document.getElementById('productsContainer');
            products.forEach(product => {
                const checkbox = createProductCheckbox(product);
                form.appendChild(checkbox);
            });

            const submitButton = document.createElement('button');
            submitButton.type = 'submit';
            submitButton.textContent = 'Add Selected Products to Wishlist';
            submitButton.style.display = 'inline-flex';
            submitButton.style.justifyContent = 'center';
            submitButton.style.alignItems = 'center'; // Örneğin, dikey hizalamayı ortalamak için
            form.appendChild(submitButton);

            form.onsubmit = handleFormSubmit;

        })
        .catch(error => {
            console.error('Error fetching products:', error);
        });
}

function createProductCheckbox(product) {
    const label = document.createElement('label');
    label.className = 'product-checkbox';
    label.innerHTML = `
        <img src="data:image/png;base64,${product.productImage}" alt="${product.productName}">
        <input type="checkbox" name="productIds" value="${product.productId}">
        <span>${product.productName} - ${product.price}</span>
        
    `;
    return label;
}

function handleFormSubmit(event) {
    event.preventDefault();
    const formData = new FormData(event.target);
    const selectedProductIds = formData.getAll('productIds');
    let lastProducts = []
    selectedProductIds.forEach(products =>{
        lastProducts.push(parseInt(products));
    })

    fetch(`/api/wishlists/aptwl/${wishListId}`, {
        method: "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(lastProducts)
    })
        .then(response => {
            location.reload();
            if (! (response.status === 201)) {
                throw new Error('Network response was not ok');
            }

        })
        .catch(error => console.error('Error adding product:', error));
}

function addToWishList(productIds) {
    console.log('Adding products to wishlist:', productIds);
}