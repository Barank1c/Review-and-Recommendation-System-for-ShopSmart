
document.addEventListener("DOMContentLoaded", function() {
    fetch('/api/browseFilterP/getAllCategories')
        .then(response => response.json())
        .then(data => {
            const select = document.getElementById('categoryContainer');
            data.forEach(category => {
                const label = document.createElement('label');
                label.className = 'category-checkbox';
                label.innerHTML = `
                <div class="form-check">
                <input type="checkbox" name="categoryIds" value="${category.categoryId}">
                <span>${category.categoryName}</span>
                </div>
                `;
                select.appendChild(label);
            });
        })
        .catch(error => console.error('Error fetching categories:', error));
});


document.getElementById("addProductForm").addEventListener("submit", function(event) {
    event.preventDefault(); // Prevent the default form submission
    const form = event.target;
    const formData = new FormData(form);
    console.log(formData);

    addProduct(formData); // Ürünü ekleme işlemini başlat
});

function addProduct(formData) {
    fetch("/api/products", {
        method: "POST",
        body: formData
    })
        .then(response => {
            if (response.status !== 201) {
                throw new Error('Network response was not ok');
            }
            goToProductsPage();
        })
        .catch(error => console.error('Error adding product:', error));
}

function goToProductsPage() {
    window.location.href = "../Merchant/productsOfMerchant.html"; // Diğer sayfaya git
}
