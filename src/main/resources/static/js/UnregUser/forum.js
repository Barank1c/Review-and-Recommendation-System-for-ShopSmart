const currentUser = JSON.parse(localStorage.getItem("appUser")); // Mevcut kullanıcıyı al

// Konuları ve blogları listeleyecek olan elementler
const topicsListElement = document.getElementById('topics-list');

// Konuların ve blogların sayfalama kontrol elemanları
const topicsPaginationElement = document.getElementById('topics-pagination');

// Edit Topic/Blog Box elements
const editBoxElement = document.getElementById('edit-topic-blog-box');
const editTitleElement = document.getElementById('edit-topic-title');
const editTextareaElement = document.getElementById('edit-topic-blog-textarea');
const cancelEditButton = document.getElementById('cancel-edit-topic-blog-btn');
const saveEditButton = document.getElementById('save-edit-topic-blog-btn');

// Create New Topic Box elements
const createBoxElement = document.getElementById('create-topic-box');
const newTopicTitleElement = document.getElementById('new-topic-title');
const newTopicContentElement = document.getElementById('new-topic-content');
const cancelCreateButton = document.getElementById('cancel-create-topic-btn');
const saveCreateButton = document.getElementById('save-create-topic-btn');

let currentEditItem = null; // To keep track of the item being edited

// Konuları getiren fonksiyon
const fetchTopics = async (page = 1) => {
    try {
        const response = await fetch(`/api/forum/viewTopics?page=${page}`);
        if (response.status === 403) {
            alert('You do not have permission to perform this action.');
            return;
        }
        if (response.ok) {
            const topics = await response.json();
            topicsListElement.innerHTML = generateTopicCards(topics);
        }
    } catch (error) {
        console.error('Error fetching topics:', error);
    }
};



// Konular için sayfalama kontrollerini ayarlayan fonksiyon
const setupPaginationForum = async () => {
    try {
        const totalPagesResponse = await fetch(`/api/forum/pageCount`);
        if (totalPagesResponse.status === 403) {
            alert('You do not have permission to perform this action.');
            return;
        }
        if (totalPagesResponse.ok) {
            const totalPages = await totalPagesResponse.json();
            topicsPaginationElement.innerHTML = generatePaginationControls(totalPages, "forum");
        }
    } catch (error) {
        console.error('Error setting up forum pagination:', error);
    }
};


// Sayfalama kontrol elemanlarını oluşturan fonksiyon
const generatePaginationControls = (totalPages, type) => {
    let controls = '';
    for (let i = 1; i <= totalPages; i++) {
        controls += `<button class="pagination-btn" data-page="${i}" data-type="${type}">${i}</button>`;
    }
    return controls;
};

// Sayfadaki herhangi bir tıklamayı dinleyen ana event listener
document.addEventListener('click', async (event) => {
    if (event.target.classList.contains('pagination-btn')) {
        const page = event.target.getAttribute('data-page');
        const type = event.target.getAttribute('data-type');
        if (type === 'forum') {
            await fetchTopics(page);
        }
    }

    if (event.target.closest('.edit-btn')) {
        const itemElement = event.target.closest('.topic-item') || event.target.closest('.blog-item');
        const itemId = Number(itemElement.getAttribute('data-index'));
        const isTopic = itemElement.classList.contains('topic-item');

        currentEditItem = { id: itemId, isTopic: isTopic };

        // Populate the edit box with the item's current content
        if (isTopic) {
            const response = await fetch(`/api/forum/viewTopic/${itemId}`);
            if (response.status === 403) {
                alert('You do not have permission to perform this action.');
                return;
            }
            if (response.ok) {
                const topic = await response.json();
                editTitleElement.value = topic.title;
                editTextareaElement.value = topic.content;
            }
        } else {
            // Populate for blog (if necessary)
        }

        // Show the edit box
        editBoxElement.classList.remove('hidden');
        editBoxElement.classList.add('active');
    }

    if (event.target.closest('.delete-btn')) {
        const itemElement = event.target.closest('.topic-item') || event.target.closest('.blog-item');
        const itemId = Number(itemElement.getAttribute('data-index'));
        const isTopic = itemElement.classList.contains('topic-item');

        if (isTopic) {
            await deleteTopic(itemId);
            window.location.reload();
        } else {
            // Blog silme işlemi burada gerçekleştirilebilir.
        }
    }

    if (event.target.id === 'create-topic-btn') {
        // Show the create topic box
        createBoxElement.classList.remove('hidden');
        createBoxElement.classList.add('active');
    }
});

// Cancel edit button event listener
cancelEditButton.addEventListener('click', () => {
    editBoxElement.classList.remove('active');
    editBoxElement.classList.add('hidden');
});

// Cancel create button event listener
cancelCreateButton.addEventListener('click', () => {
    createBoxElement.classList.remove('active');
    createBoxElement.classList.add('hidden');
});

// Save edit button event listener
saveEditButton.addEventListener('click', async () => {
    const newTitle = editTitleElement.value;
    const newContent = editTextareaElement.value;
    if (currentEditItem) {
        if (currentEditItem.isTopic) {
            await updateTopic(currentEditItem.id, newTitle, newContent);
        } else {
            // Update blog işlemi burada gerçekleştirilebilir.
        }
        window.location.reload(); // Refresh the page to show updated content
    }
});

// Save create button event listener
saveCreateButton.addEventListener('click', async () => {
    const newTitle = newTopicTitleElement.value;
    const newContent = newTopicContentElement.value;
    await createNewTopic(newTitle, newContent);
    window.location.reload(); // Refresh the page to show the new topic
});

// Konu kartları oluşturan fonksiyon
const generateTopicCards = (topicList) => {
    return topicList.map((topic, index) => `
        <div class="topic-item" data-index="${topic.cpId}" style="display: flex; justify-content: space-between; align-items: center; padding: 10px; border: 1px solid #ccc; margin-bottom: 5px;">
            <span style="flex-grow: 1;" onclick="window.location.href='forumTopic.html?topicId=${topic.cpId}'">${topic.title}</span>
            ${(currentUser.role === 'ADMIN' || currentUser.role === 'COMMUNITY_MODERATOR' || currentUser.username === topic.username) ? `
            <div class="action-buttons" style="display: flex;">
                <button class="edit-btn btn-edit" style="margin-left: 10px;">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" class="bi bi-pen" viewBox="0 0 16 16">
                        <path d="m13.498.795.149-.149a1.207 1.207 0 1 1 1.707 1.708l-.149.148a.5.5 0 0 1-.059 2.059L4.854 14.854a.5.5 0 0 1-.233.131l-4 1a.5.5 0 0 1-.606-.606l1-4a.5.5 0 0 1 .131-.232l9.642-9.642a.5.5 0 0 0-.642.056L6.854 4.854a.5.5 0 1 1-.708-.708L9.44.854A1.5 1.5 0 0 1 11.5.796a1.5 1.5 0 0 1 1.998-.001m-.644.766a.5.5 0 0 0-.707 0L1.95 11.756l-.764 3.057 3.057-.764L14.44 3.854a.5.5 0 0 0 0-.708z"/>
                    </svg>
                </button>
                <button class="delete-btn btn-delete" style="margin-left: 10px;">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" class="bi bi-x-circle" viewBox="0 0 16 16">
                        <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14m0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16zM4.646 4.646a.5.5 0 0 1 .708 0L8 7.293l2.646-2.647a.5.5 0 0 1 .708.708L8.707 8l2.647 2.646a.5.5 0 0 1-.708.708L8 8.707l-2.646 2.647a.5.5 0 0 1-.708-.708L7.293 8 4.646 5.354a.5.5 0 0 1 0-.708z"/>
                    </svg>
                </button>
            </div>
            ` : ''}
        </div>
    `).join('');
};

// Silme işlemi için topic silme fonksiyonu
const deleteTopic = async (topicId) => {
    try {
        const response = await fetch(`/api/forum/deleteTopic/${topicId}`, {
            method: 'DELETE'
        });
        if (response.status === 403) {
            alert('You do not have permission to perform this action.');
            return;
        }
        if (response.ok) {
            const data = await response.json();
            console.log('Topic deleted:', data);
            // Silme işlemi başarılıysa sayfayı yeniden yükle veya silinen öğeyi kaldır
        } else {
            console.error('Failed to delete topic:', response.status);
        }
    } catch (error) {
        console.error('Error deleting topic:', error);
    }
};

// Topic güncelleme fonksiyonu
const updateTopic = async (topicId, newTitle, newContent) => {
    try {
        const formData = new FormData();
        formData.append('title', newTitle);
        formData.append('content', newContent);

        const response = await fetch(`/api/forum/editTopic/${topicId}`, {
            method: 'PUT',
            body: formData
        });
        if (response.status === 403) {
            alert('You do not have permission to perform this action.');
            return;
        }
        if (response.ok) {
            const data = await response.json();
            console.log('Topic updated:', data);
        } else {
            console.error('Failed to update topic:', response.status);
        }
    } catch (error) {
        console.error('Error updating topic:', error);
    }
};

// Yeni konu oluşturma fonksiyonu
const createNewTopic = async (title, content) => {
    try {
        const formData = new FormData();
        formData.append('title', title);
        formData.append('content', content);

        const response = await fetch(`/api/forum/addNewTopic`, {
            method: 'POST',
            body: formData
        });
        if (response.status === 403) {
            alert('You do not have permission to perform this action.');
            return;
        }
        if (response.ok) {
            const data = await response.json();
            console.log('New topic created:', data);
        } else {
            console.error('Failed to create new topic:', response.status);
        }
    } catch (error) {
        console.error('Error creating new topic:', error);
    }
};




// Sayfa yüklendiğinde konuları ve blogları yükle
window.addEventListener('DOMContentLoaded', async () => {
    await fetchTopics();
    await setupPaginationForum();
});
