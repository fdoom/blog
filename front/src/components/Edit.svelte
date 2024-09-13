<script>
    import { onMount, afterUpdate } from 'svelte';
    import { marked } from 'marked';
    import hljs from 'highlight.js';
    import 'highlight.js/styles/github.css';
    import { reissue } from '../util/reissue';
    import { goto } from '$app/navigation';
    import { API_BASE_URL } from '../config';

    let postInfo = {
        postTitle: '',
        postContent: '',
        shareStatus: '',
        categoryId: ''
    };
    
    let textareaRef;
    let previewRef;
    let activeButton = '';
    let textareaWidth = '50%';
    let previewWidth = '50%';
    let categories = [];
    export let postId = null;

    onMount(async () => {
            
        try {
            let response = null;
            if(postId) {
                response = await fetch(`${API_BASE_URL}/post/info/${postId}`, {
                    method: 'GET',
                    credentials: 'include',
                    headers: {
                        Authorization: localStorage.getItem('accessToken')
                    }
                })

                if(response.status == 401) {
                    await reissue();

                    response = await fetch(`${API_BASE_URL}/post/info/${postId}`, {
                        method: 'GET',
                        credentials: 'include',
                        headers: {
                            Authorization: localStorage.getItem('accessToken')
                        }
                    });
                }

                if(response.ok) {
                    postInfo = await response.json();
                } else if(response.status == 403) {
                    throw Error('권한이 없습니다.');
                } else {
                    throw Error('해당 페이지를 불러오는데 오류가 발생했습니다.');
                }

                response = await fetch(`${API_BASE_URL}/category/info/post/${postId}`, {
                    method: 'GET'
                })

                if(response.ok) {
                    const categoryInfo = await response.json();
                    postInfo.categoryId = categoryInfo.categoryId;
                } else if(response.status == 404) {
                    throw Error('존재하지 않습니다.');
                } else {
                    throw Error('카테고리 정보를 불러오는데 오류가 발생했습니다.');
                }
            }
            response = await fetch(`${API_BASE_URL}/category/info/list`, {
                method: 'GET',
                credentials: 'include',
                headers: {
                    Authorization: localStorage.getItem('accessToken')
                }
            });

            if (response.ok) {
                categories = await response.json();
            } else if (response.status === 401) {
                await reissue();
                const retryResponse = await fetch(`${API_BASE_URL}/category/info/list`, {
                    method: 'GET',
                    credentials: 'include',
                    headers: {
                        Authorization: localStorage.getItem('accessToken')
                    }
                });
                if (retryResponse.ok) {
                    categories = await retryResponse.json();
                }
            } else {
                throw new Error('카테고리 목록을 불러오지 못했습니다.');
            }
        } catch (error) {
            alert(error.message);
        }
    });

    afterUpdate(async () => {
        hljs.highlightAll();
    })

    const handleInput = (event) => {
        postInfo.postContent = event.target.value;
        syncScroll();
        adjustTextareaHeight();
    };

    const adjustTextareaHeight = () => {
        if (!textareaRef) return;
        textareaRef.style.height = 'auto';
        textareaRef.style.height = `${textareaRef.scrollHeight}px`;
    };

    const syncScroll = () => {
        if (!previewRef) return;
        const textareaScrollTop = textareaRef.scrollTop;
        const textareaScrollHeight = textareaRef.scrollHeight - textareaRef.clientHeight;
        const previewScrollHeight = previewRef.scrollHeight - previewRef.clientHeight;
        const scrollPercentage = textareaScrollTop / textareaScrollHeight;
        previewRef.scrollTop = scrollPercentage * previewScrollHeight;
    };

    async function handlePost() {
        // 입력값 검증
        if (!postInfo.postTitle || !postInfo.postContent || !postInfo.shareStatus || !postInfo.categoryId) {
            alert('모든 필드를 입력해주세요.'); // 필드가 비어있을 경우 경고 메시지
            return; // 요청을 보내지 않고 함수 종료
        }

        try {
            let response;
            if(postId) {
                response = await fetch(`${API_BASE_URL}/post/info/${postId}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': localStorage.getItem('accessToken')
                    },
                    body: JSON.stringify(postInfo)
                });

                if (response.status == 401) {
                    await reissue();
                    response = await fetch(`${API_BASE_URL}/post/info/${postId}`, {
                        method: 'PUT',
                        headers: {
                            'Content-Type': 'application/json',
                            'Authorization': localStorage.getItem('accessToken')
                        },
                        body: JSON.stringify(postInfo)
                    });
                }
            } else {
                response = await fetch(`${API_BASE_URL}/post/info`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': localStorage.getItem('accessToken')
                    },
                    body: JSON.stringify(postInfo)
                });

                if (response.status == 401) {
                    await reissue();
                    response = await fetch(`${API_BASE_URL}/post/info`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                            'Authorization': localStorage.getItem('accessToken')
                        },
                        body: JSON.stringify(postInfo)
                    });
                }
            }

            if (response.ok) {
                alert('게시물이 성공적으로 등록되었습니다.');
                goto('/');
            } else if (response.status == 403) {
                throw new Error('권한이 없습니다.');
            } else {
                const errorData = await response.json();
                throw new Error(errorData.message || '게시물 등록에 실패했습니다.');
            }
        } catch (error) {
            alert(error.message);
            window.history.back();
        }
    }

    const handleCancel = () => {
        window.history.back();
    };

    const handleKeyDown = (event) => {
        if (event.key === 'Tab') {
            event.preventDefault();
            const start = textareaRef.selectionStart;
            const end = textareaRef.selectionEnd;

            postInfo.postContent = postInfo.postContent.substring(0, start) + '\t' + postInfo.postContent.substring(end);
            textareaRef.value = postInfo.postContent;
            textareaRef.selectionStart = textareaRef.selectionEnd = start + 1;
            adjustTextareaHeight();
        }
    };

    const toggleView = (view) => {
        if (activeButton === view) {
            activeButton = '';
            textareaWidth = '50%';
            previewWidth = '50%';
        } else {
            activeButton = view;
            if (view === 'textarea') {
                textareaWidth = '100%';
                previewWidth = '0%';
                adjustTextareaHeight();
            } else if (view === 'preview') {
                previewWidth = '100%';
                textareaWidth = '0%';
            }
        }
    };

    async function uploadImage(file) {
        const formData = new FormData();
        formData.append("file", file);

        try {
            let response = await fetch(`${API_BASE_URL}/image/upload`, {
                method: 'POST',
                credentials: "include",
                headers: {
                    Authorization: localStorage.getItem('accessToken')
                },
                body: formData
            });

            if (response.status == 401) {
                await reissue();
                response = await fetch(`${API_BASE_URL}/image/upload`, {
                    method: 'POST',
                    credentials: "include",
                    headers: {
                        Authorization: localStorage.getItem('accessToken')
                    },
                    body: formData
                });
            }
            if (response.ok) {
                const data = await response.json();
                return data.url;
            } else if (response.status == 403) {
                throw new Error("권한이 없습니다.");
            } else {
                throw new Error("이미지 업로드 실패");
            }
        } catch (error) {
            alert(error.message);
            return null;
        }
    }

    async function handlePaste(event) {
        const clipboardData = event.clipboardData;

        if (clipboardData && clipboardData.items) {
            for (let i = 0; i < clipboardData.items.length; i++) {
                const item = clipboardData.items[i];

                if (item.type.indexOf("image") !== -1) {
                    const file = item.getAsFile();
                    event.preventDefault();

                    const imageUrl = await uploadImage(file);
                    if (imageUrl) {
                        const markdownImage = `![image](${imageUrl})`;
                        const start = textareaRef.selectionStart;
                        postInfo.postContent = postInfo.postContent.substring(0, start) + markdownImage + postInfo.postContent.substring(start);
                        textareaRef.value = postInfo.postContent;
                        textareaRef.selectionStart = textareaRef.selectionEnd = start + markdownImage.length;
                        handleInput(event);
                    }
                    break;
                }
            }
        }
    }
</script>

<div class="input-container">
    <input type="text" bind:value={postInfo.postTitle} placeholder="제목을 입력하세요..."/>
</div>

<div class="container">
    {#if activeButton === 'textarea' || activeButton === ''}
        <textarea 
            bind:this={textareaRef} 
            bind:value={postInfo.postContent} 
            on:input={handleInput} 
            on:keydown={handleKeyDown} 
            on:paste={handlePaste}
            style="width: {textareaWidth};"
            placeholder="마크다운을 입력하세요..."></textarea>
    {/if}

    {#if activeButton === 'preview' || activeButton === ''}
        <div class="preview" bind:this={previewRef} style="width: {previewWidth};">
            {@html postInfo.postContent ? marked(postInfo.postContent.replace(/!\[(.*?)\]\((.*?)\)/g, '<div style="text-align: center"><img src="$2" alt="$1" style="max-width: 100%; height: auto;"/></div>')) : 'Post content not found.'}
        </div>
    {/if}
</div>

<!-- 버튼 및 카테고리 컨테이너 -->
<div class="controls">
    <div class="button-container">
        <button 
            class="toggleBtn {activeButton === 'textarea' ? 'active' : ''}" 
            on:click={() => toggleView('textarea')}>
            텍스트 에디터
        </button>
        <button 
            class="toggleBtn {activeButton === 'preview' ? 'active' : ''}" 
            on:click={() => toggleView('preview')}>
            미리보기
        </button>
    </div>

    <div class="shareStatus-container">
        <select bind:value={postInfo.shareStatus}>
            <option value="" disabled>공유 여부를 선택하세요</option>
            <option value='PUBLIC'>PUBLIC</option>
            <option value='PROTECTED'>PROTECTED</option>
            <option value='PRIVATE'>PRIVATE</option>
        </select>
    </div>

    <div class="category-container">
        <select bind:value={postInfo.categoryId}>
            <option value="" disabled>카테고리를 선택하세요</option>
            {#each categories as category}
                <option value={category.categoryId}>{category.categoryName}</option>
            {/each}
        </select>
    </div>

    <div class="buttons">
        <button class="postBtn" on:click={handlePost}>
            <i class="fas fa-paper-plane"></i> 게시
        </button>
        <button class="cancelBtn" on:click={handleCancel}>
            <i class="fas fa-times"></i> 취소
        </button>
    </div>
</div>

<style>
    .input-container {
        display: flex;
        justify-content: center;
        margin-bottom: 20px;
    }

    input[type="text"] {
        width: 90%;
        padding: 10px 15px;
        font-size: 18px;
        border: 1px solid #ccc;
        border-radius: 5px;
        box-sizing: border-box;
        text-align: center;
        margin-top: 20px;
    }

    .container {
        display: flex;
        height: 100%;
        width: 100%;
        padding: 0;
        position: relative;
        margin-bottom: 80px;
    }

    textarea {
        padding: 20px;
        font-size: 16px;
        resize: none;
        overflow: hidden;
        box-sizing: border-box;
        border-radius: 20px;
        height: auto;
        
    }

    .preview {
        padding: 20px;
        overflow-y: auto;
        box-sizing: border-box;
        border-left: 1px solid #ccc;
        background-color: #f7f7f7;
        border-radius: 20px;
    }

    .controls {
        position: fixed;
        bottom: 20px;
        left: 20px;
        right: 20px;
        display: flex;
        flex-direction: row;
        justify-content: space-between;
        gap: 10px;
        z-index: 1000;
    }

    .button-container,
    .buttons,
    .category-container,
    .shareStatus-container {
        display: flex;
        gap: 10px;
    }

    button {
        padding: 10px 15px;
        border: none;
        border-radius: 5px;
        cursor: pointer;
        background-color: #007bff;
        color: white;
    }

    button:hover {
        background-color: #0056b3;
    }

    select {
        padding: 10px 15px;
        border: 1px solid #ccc;
        border-radius: 5px;
    }

    /* 모바일 스타일 */
    @media (max-width: 600px) {
        .controls {
            flex-direction: column;
            align-items: center;
        }

        .button-container,
        .buttons,
        .category-container {
            width: 100%; /* 전체 너비 사용 */
            justify-content: center; /* 가운데 정렬 */
        }

        .buttons {
            margin-top: 10px; /* 섹션 간의 여백 추가 */
        }
    }
</style>
