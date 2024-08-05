document.addEventListener('DOMContentLoaded', () => {
    const topicDetails = document.getElementById('topic-details');
    const commentsSection = document.getElementById('comments-section');
    const paginationControls = document.querySelector('.pagination');
    const commentTextarea = document.getElementById('comment-textarea');
    const editCommentModal = document.getElementById('edit-comment-modal');
    const editCommentTextarea = document.getElementById('edit-comment-textarea');
    const cancelEditBtn = document.getElementById('cancel-edit-btn');
    const saveEditBtn = document.getElementById('save-edit-btn');
    let currentPage = 1;
    let totalPages = 0;
    let currentEditCommentId = null;

    // LocalStorage'dan currentUser'ı almak
    const currentUser = JSON.parse(localStorage.getItem("appUser"));

    // Topic ID'yi URL'den almak
    const urlParams = new URLSearchParams(window.location.search);
    const topicId = urlParams.get('topicId');

    // API Endpoint'leri
    const topicApiUrl = `/api/forum/viewTopic/${topicId}`;
    const commentsApiUrl = `/api/forum/viewComments/${topicId}`;
    const commentsPageCountApiUrl = `/api/forum/commentPageCount/${topicId}`;

    // API'den topic verisini çekme
    const fetchTopic = async () => {
        try {
            const response = await fetch(topicApiUrl);
            if (response.status === 403) {
                alert('You do not have permission to perform this action.');
                return;
            }
            if (!response.ok) {
                throw new Error('Topic verisi çekilemedi');
            }
            const topicData = await response.json();
            displayTopicDetails(topicData);
        } catch (error) {
            console.error(error);
        }
    };

    // API'den yorum verisini çekme
    const fetchComments = async (page) => {
        try {
            const response = await fetch(`${commentsApiUrl}?page=${page}`);
            if (response.status === 403) {
                alert('You do not have permission to perform this action.');
                return;
            }
            if (!response.ok) {
                throw new Error('Yorum verisi çekilemedi');
            }
            const comments = await response.json();
            displayComments(comments);
        } catch (error) {
            console.error(error);
        }
    };

    // Toplam sayfa sayısını API'den çekme
    const fetchTotalPages = async () => {
        try {
            const response = await fetch(commentsPageCountApiUrl);
            if (response.status === 403) {
                alert('You do not have permission to perform this action.');
                return;
            }
            if (!response.ok) {
                throw new Error('Toplam sayfa sayısı alınamadı');
            }
            totalPages = await response.json();
            updatePaginationControls();
        } catch (error) {
            console.error(error);
        }
    };

    // Topic detaylarını gösterme
    const displayTopicDetails = (topic) => {
        const topicDate = new Date(topic.cpTime).toLocaleString();
        topicDetails.innerHTML = `
            <h2 class="text-2xl font-semibold mb-2">${topic.title}</h2>
            <p class="mb-2">${topic.content}</p>
            <p class="text-sm text-gray-500">Posted by ${topic.username} on ${topicDate}</p>
        `;
    };

    // Yorum kartlarını oluşturma
    const generateCommentCards = (commentList) => {
        return commentList.map(comment => {
            const commentDate = new Date(comment.cpcTime).toLocaleString();
            let actionButtons = '';

            // Kullanıcı yetkisini kontrol etme
            if (currentUser && (currentUser.role === 'ADMIN' || currentUser.role === 'COMMUNITY_MODERATOR' || currentUser.username === comment.username)) {
                actionButtons = `
                    <button class="edit-comment-btn px-2 py-1 bg-primary text-white rounded btn-edit" data-id="${comment.cpcId}">Edit</button>
                    <button class="delete-comment-btn px-2 py-1 bg-red-500 text-white rounded" data-id="${comment.cpcId}">Delete</button>
                `;
            }

            return `
                <div class="comment-card" data-id="${comment.cpcId}">
                    <div>
                        <p class="font-semibold">${comment.username}</p>
                        <p class="text-sm">${commentDate}</p>
                        <p>${comment.comment}</p>
                    </div>
                    <div class="action-buttons">
                        ${actionButtons}
                    </div>
                </div>
            `;
        }).join('');
    };

    // Yorumları gösterme
    const displayComments = (comments) => {
        commentsSection.innerHTML = generateCommentCards(comments);
    };

    // Sayfa kontrol butonlarını güncelleme
    const updatePaginationControls = () => {
        let paginationHTML = '';
        for (let i = 1; i <= totalPages; i++) {
            paginationHTML += `<button class="px-3 py-1 rounded ${i === currentPage ? 'bg-primary text-white' : 'bg-white text-primary'}" data-page="${i}">${i}</button>`;
        }
        paginationControls.innerHTML = paginationHTML;
    };

    // Yeni yorum ekleme
    const addComment = async (text) => {
        const formData = new FormData();
        formData.append('comment', text);

        // API'ye yeni yorumu gönderme
        try {
            const response = await fetch(`/api/forum/addComment/${topicId}`, {
                method: 'POST',
                body: formData
            });

            if (response.status === 403) {
                alert('You do not have permission to perform this action.');
                return;
            }

            if (!response.ok) {
                throw new Error('Yorum eklenemedi');
            }

            // Yorum ekleme başarılı olduğunda yorumları yeniden çekme
            await fetchTotalPages();
            fetchTopic();
            fetchTotalPages();
            fetchComments(currentPage);


        } catch (error) {
            console.error(error);
        }
    };

    // Yorum düzenleme kutusunu açma
    const openEditModal = (commentId, commentText) => {
        currentEditCommentId = commentId;
        editCommentTextarea.value = commentText;
        editCommentModal.classList.remove('hidden');
    };

    // Yorum düzenleme kutusunu kapama
    const closeEditModal = () => {
        editCommentModal.classList.add('hidden');
        currentEditCommentId = null;
        editCommentTextarea.value = '';
    };

    // Yorum düzenleme
    const editComment = async () => {
        const newComment = editCommentTextarea.value.trim();
        if (!newComment) return;

        try {
            const response = await fetch(`/api/forum/editComment/${topicId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: `commentId=${currentEditCommentId}&newComment=${encodeURIComponent(newComment)}`
            });

            if (response.status === 403) {
                alert('You do not have permission to perform this action.');
                return;
            }

            if (!response.ok) {
                throw new Error('Yorum düzenlenemedi');
            }

            // Yorum düzenleme başarılı olduğunda yorumları yeniden çekme
            fetchComments(currentPage);
            closeEditModal();
        } catch (error) {
            console.error(error);
        }
    };

    // Yorum silme
    const deleteComment = async (commentId) => {
        try {
            const response = await fetch(`/api/forum/deleteComment/${topicId}?commentId=${commentId}`, {
                method: 'DELETE'
            });

            if (response.status === 403) {
                alert('You do not have permission to perform this action.');
                return;
            }

            if (!response.ok) {
                throw new Error('Yorum silinemedi');
            }

            // Yorum silme başarılı olduğunda yorumları yeniden çekme
            fetchTopic();
            fetchTotalPages();
            fetchComments(1);

        } catch (error) {
            console.error(error);
        }
    };

    // Sayfa yüklenince API'den verileri çekme
    fetchTopic();
    fetchTotalPages();
    fetchComments(currentPage);

    // Sayfa kontrol butonları tıklama olayı
    paginationControls.addEventListener('click', (e) => {
        if (e.target.tagName === 'BUTTON') {
            currentPage = parseInt(e.target.getAttribute('data-page'));
            fetchComments(currentPage);
        }
    });

    // Enter tuşuna basıldığında yeni yorum ekleme
    commentTextarea.addEventListener('keypress', (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            const commentText = commentTextarea.value.trim();
            if (commentText) {
                addComment(commentText);
                commentTextarea.value = '';
            }
        }
    });

    // Edit ve delete butonlarına tıklama olayları
    commentsSection.addEventListener('click', (e) => {
        if (e.target.classList.contains('edit-comment-btn')) {
            const commentId = e.target.getAttribute('data-id');
            const commentCard = e.target.closest('.comment-card');
            const commentText = commentCard.querySelector('p:last-child').textContent;
            openEditModal(commentId, commentText);
        } else if (e.target.classList.contains('delete-comment-btn')) {
            const commentId = e.target.getAttribute('data-id');
            deleteComment(commentId);
        }
    });

    // Modal butonlarına tıklama olayları
    cancelEditBtn.addEventListener('click', closeEditModal);
    saveEditBtn.addEventListener('click', editComment);
});
