const getCsrfHeaders = () => {
    const tokenMeta = document.querySelector('meta[name="_csrf"]');
    const headerMeta = document.querySelector('meta[name="_csrf_header"]');
    return tokenMeta && headerMeta ? { [headerMeta.content]: tokenMeta.content } : {};
};

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


