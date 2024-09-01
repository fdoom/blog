<script>
    import { page } from '$app/stores';
    import { onMount, afterUpdate } from 'svelte';
    import { API_BASE_URL } from '../../../config';
    import { marked } from 'marked';
    import { isLoggedIn } from '../../../store';
    import { reissue } from '../../../util/reissue';
    import { goto } from '$app/navigation';
    import hljs from 'highlight.js';

    let postId;
    let postInfo = {};

    $: postId = $page.params.postId;

    let isLoading = true;

    const fetchOptions = {
        method: 'GET',
        credentials: "include",
        headers: {}
    };

    async function makeRequestWithToken(fetchOptions) {
        let response = await fetch(`${API_BASE_URL}/post/info/${postId}`, fetchOptions);

        if (response.status === 401) {
            await reissue();
            fetchOptions.headers.Authorization = localStorage.getItem('accessToken');
            response = await fetch(`${API_BASE_URL}/post/info/${postId}`, fetchOptions);
        }

        return response;
    }

    onMount(async () => {
        if (isLoggedIn) {
            fetchOptions.headers.Authorization = localStorage.getItem('accessToken');
        }

        try {
            const response = await makeRequestWithToken(fetchOptions);
            if (response.status != 400 && response.status != 404 && !response.ok) {
                throw new Error('Network response was not ok');
            }
            postInfo = await response.json();
        } catch (error) {
            console.error('Error fetching post info:', error);
        } finally {
            isLoading = false;
        }
    });

    afterUpdate(async () => {
        hljs.highlightAll();
    })

    function editPost() {
        //게시물 편집 페이지로 이동
        goto(`/post/edit/${postId}`); // postId를 포함하여 경로 변경
    }

    function deletePost() {
        // TODO: 게시물 삭제 로직 구현
        console.log('Delete post');
    }
</script>

{#if isLoading}
    <div class="text-center mt-5">
        <div class="spinner-border" role="status">
          <span class="sr-only">Loading...</span>
        </div>
    </div>
{:else}
    <div class="container mt-5">
        <div class="card shadow">
            <div class="card-body">
                <h1 class="card-title">{postInfo.postTitle}</h1>
                <p class="card-text">
                    {@html postInfo.postContent ? marked(postInfo.postContent.replace(/!\[(.*?)\]\((.*?)\)/g, '<img src="$2" alt="$1" style="max-width: 100%; height: auto;" />')) : 'Post content not found.'}
                </p>
                <hr />
                <div class="meta">
                    <p class="text-muted">Created At: {new Date(postInfo.createdAt).toLocaleString()}</p>
                    <p class="text-muted">Updated At: {new Date(postInfo.updatedAt).toLocaleString()}</p>
                    <span class="badge {postInfo.shareStatus === 'PUBLIC' ? 'badge-success' : 'badge-danger'}">
                        Share Status: {postInfo.shareStatus}
                    </span>
                </div>

                {#if $isLoggedIn}
                    <div class="actions">
                        <button class="btn btn-primary" on:click={editPost}>
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-danger" on:click={deletePost}>
                            <i class="fas fa-trash-alt"></i>
                        </button>
                    </div>
                {/if}
            </div>
        </div>
    </div>
{/if}

<style>
    .meta {
        margin-top: 20px;
    }

    .actions {
        position: absolute;
        bottom: 20px;
        right: 20px;
    }

    .actions button {
        width: 40px;
        height: 40px;
        display: inline-flex;
        align-items: center;
        justify-content: center;
        font-size: 18px;
        margin-left: 10px;
        border-radius: 50%;
    }
</style>
