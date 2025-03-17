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

document.addEventListener("DOMContentLoaded", function () {
    const suggestionsContainer = document.getElementById("suggestions-container");
    const csrfHeaders = getCsrfHeaders();

    // Função para buscar as sugestões de hashtags
    const fetchHashtagsSuggestions = () => {
        const input = document.getElementById("hashtag-input").value;

        // Se o input estiver vazio, não faz nada
        if (input.trim() === "") {
            suggestionsContainer.style.display = 'none';
            return;
        }

        // Faz uma requisição para o backend para buscar as hashtags
        fetch(`/hashtag/suggest?query=${input}`)
            .then(response => response.json())
            .then(data => {
                suggestionsContainer.innerHTML = ""; // Limpa as sugestões anteriores
                if (data.length > 0) {
                    suggestionsContainer.style.display = 'block';
                    data.forEach(hashtag => {
                        let suggestionItem = document.createElement("div");
                        suggestionItem.innerText = "#" + hashtag.text;
                        suggestionItem.classList.add("suggestion-item");
                        suggestionItem.onclick = () => addHashtagToPhoto(hashtag.text);
                        suggestionsContainer.appendChild(suggestionItem);
                    });
                } else {
                    suggestionsContainer.style.display = 'none';
                }
            })
            .catch(error => {
                console.error("Erro ao buscar hashtags:", error);
            });
    };

    // Função para adicionar a hashtag à foto
    const addHashtagToPhoto = (hashtagText) => {
        const photoId = document.getElementById("photo-id").value;

        fetch(`/hashtag/add?photoId=${photoId}&hashtagText=${hashtagText}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                ...csrfHeaders
            },
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert("Hashtag adicionada com sucesso!");

                    // Agora, vamos adicionar a hashtag diretamente na lista de hashtags
                    let newHashtagDiv = document.createElement("div");
                    newHashtagDiv.innerHTML = `<span>#${hashtagText}</span>`;
                    document.getElementById("hashtag-list").appendChild(newHashtagDiv);
                } else {
                    alert("Erro ao adicionar hashtag.");
                }
            })
            .catch(error => {
                console.error("Erro ao adicionar hashtag:", error);
            });
    };

    // Função que verifica quando a tecla Enter é pressionada
    const checkEnterKey = (event) => {
        if (event.key === "Enter") {
            const input = document.getElementById("hashtag-input").value;
            if (input.trim() !== "") {
                // Se a hashtag não for sugerida, cria uma nova
                addHashtagToPhoto(input);
            }
        }
    };

    // Conectando os eventos de input e tecla pressionada
    document.getElementById("hashtag-input").addEventListener("input", fetchHashtagsSuggestions);
    document.getElementById("hashtag-input").addEventListener("keydown", checkEnterKey);
});
