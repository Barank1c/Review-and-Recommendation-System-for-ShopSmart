document.addEventListener('DOMContentLoaded', () => {
    const blogTitle = document.getElementById('blog-title');
    const blogContent = document.getElementById('blog-content');
    const editBtn = document.getElementById('edit-btn');
    const deleteBtn = document.getElementById('delete-btn');
    const editBlogBox = document.getElementById('edit-blog-box');
    const editBlogTextarea = document.getElementById('edit-blog-textarea');
    const cancelEditBtn = document.getElementById('cancel-edit-btn');
    const saveEditBtn = document.getElementById('save-edit-btn');
    const homeBtn = document.getElementById('home-btn');
    let currentUser = { username: 'User1', role: 'admin' }; // Example user data
    let blogOwner = 'User1'; // Example blog owner data

    // Populate blog details (example data)
    blogTitle.innerText = 'Blog Topic Title';
    blogContent.innerText = 'This is the content of the blog...KHSAVDKHAVDBJSADBHKSADBKSABDKSABDJSABDJHASBDLKSANDHKSABDJSADNLJDVBAFKHDBNSAJBDKJSAB';

    // Edit button event listener
    editBtn.addEventListener('click', () => {
        if (currentUser.role === 'admin' || currentUser.role === 'moderator' || currentUser.username === blogOwner) {
            editBlogTextarea.value = blogContent.innerText;
            editBlogBox.classList.remove('hidden');
        } else {
            alert('You do not have permission to edit this blog.');
        }
    });

    // Save edit button event listener
    saveEditBtn.addEventListener('click', () => {
        const newContent = editBlogTextarea.value.trim();
        if (newContent) {
            blogContent.innerText = newContent;
            editBlogBox.classList.add('hidden');
        }
    });

    // Cancel edit button event listener
    cancelEditBtn.addEventListener('click', () => {
        editBlogBox.classList.add('hidden');
    });

    // Delete button event listener
    deleteBtn.addEventListener('click', () => {
        if (currentUser.role === 'admin' || currentUser.role === 'moderator' || currentUser.username === blogOwner) {
            if (confirm('Are you sure you want to delete this blog?')) {
                // Perform delete action (example: redirect to home)
                alert('Blog deleted successfully.');
                window.location.href = 'forum.html';
            }
        } else {
            alert('You do not have permission to delete this blog.');
        }
    });

    // Navigate to home
    homeBtn.addEventListener('click', () => {
        window.location.href = '../../html/UnregUser/forum.html';
    });
});
