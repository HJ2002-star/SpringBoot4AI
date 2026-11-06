window.addEventListener('DOMContentLoaded', () => {
    const imageUrl = document.getElementById('image-url')?.value;
    if (imageUrl) {
        displayImagePreview(imageUrl);
    }
});

// 이미지 업로드 버튼
const imageUpload = document.getElementById('image-upload');
if (imageUpload) {
    imageUpload.addEventListener('change', async (event) => {
        const file = event.target.files[0];
        if (!file) return;

        if (!file.type.startsWith('image/')) {
            alert('이미지 파일만 업로드 가능합니다.');
            return;
        }

        const formData = new FormData();
        formData.append('file', file);

        fetch('/api/upload', {
            method: 'POST',
            body: formData,
            headers: {
                Authorization: 'Bearer ' + localStorage.getItem('access_token'),
            },
        }).then((response) => {
            if (!response.ok) {
                alert('이미지 업로드에 실패했습니다.');
                throw new Error();
            }
            return response.json();
        })
            .then((data) => {
                document.getElementById('image-url').value = data.imageUrl;
                displayImagePreview(data.imageUrl);
            })
            .catch((e) => console.error(e));
    });
}

function displayImagePreview(imageUrl) {
    const preview = document.getElementById('image-preview');
    const previewImg = document.getElementById('preview-img');

    if (preview && previewImg && imageUrl) {
        previewImg.src = imageUrl;
        preview.style.display = 'block';
    }
}

// 이미지 제거 버튼
const removeImageButton = document.getElementById('remove-image-btn');
if (removeImageButton) {
    removeImageButton.addEventListener('click', () => {
        document.getElementById('image-url').value = '';
        document.getElementById('image-upload').value = '';
        document.getElementById('image-preview').style.display = 'none';
    });
}

// AI 썸네일 생성 버튼
const aiThumbnailButton = document.getElementById('ai-thumbnail-btn');
if (aiThumbnailButton) {
    aiThumbnailButton.addEventListener('click', async () => {
        const title = document.getElementById('title').value;
        const content = document.getElementById('content').value;

        if (!title.trim() && !content.trim()) {
            alert('제목이나 내용을 먼저 입력해주세요.');
            return;
        }

        const loadingDiv = document.getElementById('ai-thumbnail-loading');
        loadingDiv.style.display = 'block';
        aiThumbnailButton.disabled = true;

        fetch('/api/ai-thumbnails', {
            method: 'POST',
            body: JSON.stringify({
                title: title,
                content: content
            }),
            headers: {
                Authorization: 'Bearer ' + localStorage.getItem('access_token'),
                'Content-Type': 'application/json',
            },
        }).then((response) => {
            if (!response.ok) {
                alert('썸네일 생성에 실패했습니다.');
                throw new Error();
            }
            return response.json();
        }).then((data) => {
            document.getElementById('image-url').value = data.imageUrl;
            displayImagePreview(data.imageUrl);
        }).finally(() => {
            loadingDiv.style.display = 'none';
            aiThumbnailButton.disabled = false;
        });
    });
}

// AI 도움 받기 버튼
const aiAssistButton = document.getElementById('ai-assist-btn');

if (aiAssistButton) {
    aiAssistButton.addEventListener('click', event => {
        $('#aiAssistModal').modal('show');
        document.getElementById('ai-suggestion').style.display = 'none';
        document.getElementById('ai-question').value = '';
    });
}

const getSuggestionButton = document.getElementById('get-suggestion-btn');

if (getSuggestionButton) {
    getSuggestionButton.addEventListener('click', event => {
        const title = document.getElementById('title').value;
        const content = document.getElementById('content').value;
        const question = document.getElementById('ai-question').value;

        if (!question.trim()) {
            alert('고민되는 내용을 입력해주세요.');
            return;
        }

        document.getElementById('ai-loading').style.display = 'block';
        document.getElementById('ai-suggestion').style.display = 'none';

        const body = JSON.stringify({
            title: title,
            content: content,
            question: question
        });

        fetch('/api/ai-suggestions', {
            method: 'POST',
            headers: {
                Authorization: 'Bearer ' + localStorage.getItem('access_token'),
                'Content-Type': 'application/json',
            },
            body: body
        })
            .then(response => {
                return response.json();
            })
            .then(data => {
                document.getElementById('ai-loading').style.display = 'none';

                const suggestionContent = document.getElementById('ai-suggestion-content');

                let html = '';
                if (data.suggestions && data.suggestions.length > 0) {
                    html += '<ul class="list-group">';
                    data.suggestions.forEach((suggestion, index) => {
                        html += `<li class="list-group-item suggestion-item" style="cursor: pointer;" data-suggestion="${suggestion.replace(/"/g, '&quot;')}" title="클릭하면 본문에 추가됩니다">
                                ${suggestion}
                                <small class="text-muted float-right">클릭하여 추가</small>
                             </li>`;
                    });
                    html += '</ul>';
                }

                suggestionContent.innerHTML = html;
                document.getElementById('ai-suggestion').style.display = 'block';
            })
    });
}

const suggestionContent = document.getElementById('ai-suggestion-content');
if (suggestionContent) {
    suggestionContent.addEventListener('click', function(e) {
        const suggestionItem = e.target.closest('.suggestion-item');
        if (suggestionItem) {
            const suggestion = suggestionItem.getAttribute('data-suggestion');
            const contentTextarea = document.getElementById('content');

            const currentContent = contentTextarea.value;
            const separator = currentContent && !currentContent.endsWith('\n') ? '\n\n' : '';
            contentTextarea.value = currentContent + separator + suggestion;

            $('#aiAssistModal').modal('hide');

            contentTextarea.focus();
        }
    });
}

// 삭제 기능
const deleteButton = document.getElementById('delete-btn');

if (deleteButton) {
    deleteButton.addEventListener('click', event => {
        let id = document.getElementById('article-id').value;
        function success() {
            alert('삭제가 완료되었습니다.');
            location.replace('/articles');
        }

        function fail() {
            alert('삭제 실패했습니다.');
            location.replace('/articles');
        }

        httpRequest('DELETE',`/api/articles/${id}`, null, success, fail);
    });
}

// 수정 기능
const modifyButton = document.getElementById('modify-btn');

if (modifyButton) {
    modifyButton.addEventListener('click', event => {
        let params = new URLSearchParams(location.search);
        let id = params.get('id');

        body = JSON.stringify({
            title: document.getElementById('title').value,
            imageUrl: document.getElementById('image-url').value,
            content: document.getElementById('content').value
        })

        function success() {
            alert('수정 완료되었습니다.');
            location.replace(`/articles/${id}`);
        }

        function fail() {
            alert('수정 실패했습니다.');
            location.replace(`/articles/${id}`);
        }

        httpRequest('PUT',`/api/articles/${id}`, body, success, fail);
    });
}

// 생성 기능
const createButton = document.getElementById('create-btn');

if (createButton) {
    createButton.addEventListener('click', event => {
        body = JSON.stringify({
            title: document.getElementById('title').value,
            imageUrl: document.getElementById('image-url').value,
            content: document.getElementById('content').value
        });
        function success() {
            alert('등록 완료되었습니다.');
            location.replace('/articles');
        };
        function fail() {
            alert('등록 실패했습니다.');
            location.replace('/articles');
        };

        httpRequest('POST','/api/articles', body, success, fail)
    });
}

// 로그아웃 기능
const logoutButton = document.getElementById('logout-btn');

if (logoutButton) {
    logoutButton.addEventListener('click', event => {
        function success() {
            localStorage.removeItem('access_token');
            deleteCookie('refresh_token');
            location.replace('/login');
        }
        function fail() {
            alert('로그아웃 실패했습니다.');
        }

        httpRequest('DELETE','/api/refresh-token', null, success, fail);
    });
}

// 쿠키를 가져오는 함수
function getCookie(key) {
    var result = null;
    var cookie = document.cookie.split(';');
    cookie.some(function (item) {
        item = item.replace(' ', '');

        var dic = item.split('=');

        if (key === dic[0]) {
            result = dic[1];
            return true;
        }
    });

    return result;
}

// 쿠키를 삭제하는 함수
function deleteCookie(name) {
    document.cookie = name + '=; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
}

// HTTP 요청을 보내는 함수
function httpRequest(method, url, body, success, fail) {
    fetch(url, {
        method: method,
        headers: {
            Authorization: 'Bearer ' + localStorage.getItem('access_token'),
            'Content-Type': 'application/json',
        },
        body: body,
    }).then(response => {
        if (response.status === 200 || response.status === 201) {
            return success();
        }
        const refresh_token = getCookie('refresh_token');
        if (response.status === 401 && refresh_token) {
            fetch('/api/token', {
                method: 'POST',
                headers: {
                    Authorization: 'Bearer ' + localStorage.getItem('access_token'),
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    refreshToken: getCookie('refresh_token'),
                }),
            })
                .then(res => {
                    if (res.ok) {
                        return res.json();
                    }
                })
                .then(result => {
                    localStorage.setItem('access_token', result.accessToken);
                    httpRequest(method, url, body, success, fail);
                })
                .catch(error => fail());
        } else {
            return fail();
        }
    });
}
