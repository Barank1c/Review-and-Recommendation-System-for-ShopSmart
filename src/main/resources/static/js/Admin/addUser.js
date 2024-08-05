document.getElementById("addUserForm").addEventListener("submit", function(event) {
    event.preventDefault(); // Formun otomatik olarak gönderilmesini engelle
    const formData = new FormData(this); // Form verilerini al

    addProduct(formData); // Ürünü ekleme işlemini başlat
});

function addProduct(formData) {
    fetch("/api/users", {
        method: "POST",
        body: formData
    })
        .then(response => {
            if (! (response.status === 201)) {
                alert("Invalid Input(s)");
                throw new Error('Network response was not ok');
            }
            goToProductsPage();
        })
        .catch(error => console.error('Error adding user:', error));
}
function goToProductsPage() {
    window.location.href = "../Admin/manageUsers.html"; // Diğer sayfaya git
}