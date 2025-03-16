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

// Função para exibir as sugestões de hashtags
function showHashtagSuggestions(input) {
    const inputValue = input.value.trim();
    const suggestionsList = document.getElementById('hashtag-suggestions');

    if (inputValue === '') {
        suggestionsList.style.display = 'none';
        return;
    }

    fetch(`/hashtag/search?hashtagText=${inputValue}`)
        .then(response => response.json())
        .then(suggestions => {
            suggestionsList.innerHTML = ''; // Limpa as sugestões anteriores
            if (suggestions.length > 0) {
                suggestions.forEach(suggestion => {
                    const li = document.createElement('li');
                    li.classList.add('list-group-item');
                    li.textContent = suggestion.text;
                    li.onclick = function() {
                        addHashtagToPhoto(suggestion.text);  // Função que adiciona a hashtag à foto
                        input.value = suggestion.text;  // Preenche o input com a hashtag
                        suggestionsList.style.display = 'none';  // Esconde a lista de sugestões
                    };
                    suggestionsList.appendChild(li);
                });
                suggestionsList.style.display = 'block';
            } else {
                suggestionsList.style.display = 'none';
            }
        });
}

// Função para adicionar a hashtag à foto
function addHashtagToPhoto(hashtagText) {
    const csrfHeaders = getCsrfHeaders();
    const photoId = document.getElementById('photo-id').value;  // Certifique-se de ter o id da foto
    fetch(`/hashtag/add?photoId=${photoId}&hashtagText=${encodeURIComponent(hashtagText)}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            ...csrfHeaders
        },
    })
        .then(response => {
            if (response.ok) {
                console.log('Hashtag adicionada com sucesso!');
            } else {
                console.error('Erro ao adicionar a hashtag');
            }
        });
}

// Função para criar uma nova hashtag quando ENTER é pressionado
document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('hashtag-input').addEventListener('keydown', function(event) {
        if (event.key === 'Enter') {
            const input = event.target;
            let hashtagText = input.value.trim();
            if (hashtagText !== '') {
                addHashtagToPhoto(hashtagText);  // Adiciona a hashtag à foto
                input.value = '';  // Limpa o campo de input
                event.preventDefault();  // Impede o envio do formulário
            }
        }
    });
});

