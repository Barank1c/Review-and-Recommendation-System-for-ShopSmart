document.addEventListener('DOMContentLoaded', () => {
    const addForumTopicForm = document.getElementById('addForumTopicForm');

    addForumTopicForm.addEventListener('submit', (event) => {
        event.preventDefault();
        const formData = new FormData(addForumTopicForm);

        // Add form validation if necessary

        // Submit the form data to the server (example using fetch) USERID gerekiyor
        fetch('/api/blog/addContent/', {
            method: 'POST',
            body: formData
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('Topic added successfully!');
                    window.location.href = 'forum.html';
                } else {
                    alert('Failed to add topic. Please try again.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('An error occurred. Please try again.');
            });
    });

    // Navigate to home
    const homeBtn = document.getElementById('home-btn');
    homeBtn.addEventListener('click', () => {
        window.location.href = 'forum.html';
    });
});
