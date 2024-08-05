document.getElementById("editProductForm").addEventListener("submit", function(event) {
    const url = window.location.href;
    const params = new URLSearchParams(new URL(url).search);
    const productId = params.get('productId');
    event.preventDefault(); // Formun otomatik olarak gönderilmesini engelle
    const formData = new FormData(this); // Form verilerini al
    editProduct(formData,productId); // Ürünü ekleme işlemini başlat
});

document.addEventListener('DOMContentLoaded', function() {
    const url = window.location.href;
    const params = new URLSearchParams(new URL(url).search);
    const productId = params.get('productId');

    if (productId) {
        fetchProductData(productId);
    }
});

function editProduct(formData,productId) {
    fetch(`/api/products/${productId}`, {
        method: "PUT",
        body: formData
    })
        .then(response => {
            if (! (response.status === 201)) {
                throw new Error('Network response was not ok');
            }
            goToProductsPage();
        })
        .catch(error => console.error('Error adding product:', error));
}

function goToProductsPage() {
    window.location.href = "../Merchant/productsOfMerchant.html"; // Diğer sayfaya git
}
function fetchProductData(productId) {
    fetch(`/api/products/${productId}`, {
        method: "GET"
    })
        .then(response => response.json())
        .then(data => populateProductForm(data))
        .catch(error => console.error('Failed to fetch product data:', error));
}

function populateProductForm(productData) {
    fetch('/api/browseFilterP/getAllCategories')
        .then(response => response.json())
        .then(data => {
            const select = document.getElementById('categoryContainer');
            data.forEach(category => {
                const label = document.createElement('label');
                const isChecked = productData.categories.some(productCategory => productCategory.categoryId === category.categoryId);
                console.log(isChecked);
                label.className = 'category-checkbox';
                label.innerHTML = `
                <div class="form-check">
                <input type="checkbox" name="categoryIds" id="category-${category.categoryId}" value="${category.categoryId}">
                <span>${category.categoryName}</span>
                </div>
                `;

                select.appendChild(label);
                if(isChecked){
                    document.getElementById(`category-${category.categoryId}`).checked = true;
                }
            });
        })
        .catch(error => console.error('Error fetching categories:', error));

    document.getElementById('productName').value = productData.productName || '';
    document.getElementById('description').value = productData.description || '';
    document.getElementById('price').value = productData.price || '';
    document.getElementById('currentProductImage').src = productData.productImage || '';

}