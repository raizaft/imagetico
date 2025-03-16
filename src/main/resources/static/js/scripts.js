const getCsrfHeaders = () => {
    const tokenMeta = document.querySelector('meta[name="_csrf"]');
    const headerMeta = document.querySelector('meta[name="_csrf_header"]');
    return tokenMeta && headerMeta ? { [headerMeta.content]: tokenMeta.content } : {};
};

// função para curtir a foto
const likePhoto = (photoId, photographerId) => {
    const csrfHeaders = getCsrfHeaders();
    fetch('/photo/likePhoto', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            ...csrfHeaders
        },
        body: JSON.stringify({ photographerId, photoId })
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                document.getElementById('likes-count').innerText = data.likesCount;
            } else {
                alert("Erro ao curtir: " + data.error);
            }
        })
        .catch(error => {
            console.error("Erro ao curtir foto:", error);
            alert("Erro ao curtir foto.");
        });
};

// Função para editar o comentário
const editComment = (commentId, commentText) => {
    const csrfHeaders = getCsrfHeaders();
    fetch('/photo/editComment', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            ...csrfHeaders
        },
        body: JSON.stringify({ commentId, commentText, deleteFlag: false })
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                document.querySelector(`#comment-text-${commentId}`).innerText = commentText;
            } else {
                alert("Erro ao editar comentário: " + data.error);
            }
        })
        .catch(error => {
            console.error("Erro ao editar comentário:", error);
            alert("Erro ao editar comentário.");
        });
};

// Função para excluir o comentário
const deleteComment = (commentId) => {
    const csrfHeaders = getCsrfHeaders();
    fetch('/photo/delete/' + commentId, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            ...csrfHeaders
        }
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                document.getElementById(`comment-${commentId}`).remove();
            } else {
                alert("Erro ao excluir comentário: " + data.error);
            }
        })
        .catch(error => {
            console.error("Erro ao excluir comentário:", error);
            alert("Erro ao excluir comentário.");
        });
};

