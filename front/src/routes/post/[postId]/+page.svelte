<script>
    import { page } from '$app/stores';
    import { onMount } from 'svelte';
    import { API_BASE_URL } from '../../../config';
    import { marked } from 'marked';
    import { isLoggedIn } from '../../../store';
    import { reissue } from '../../../util/reissue';

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
            fetchOptions.headers.Authorization = localStorage.getItem('accessToken');  // 새 토큰 설정
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
                <p class="card-text">{@html postInfo.postContent ? marked(postInfo.postContent) : 'Post content not found.'}</p>
                <hr />
                <div class="meta">
                    <p class="text-muted">Created At: {new Date(postInfo.createdAt).toLocaleString()}</p>
                    <p class="text-muted">Updated At: {new Date(postInfo.updatedAt).toLocaleString()}</p>
                    <span class="badge {postInfo.shareStatus === 'PUBLIC' ? 'badge-success' : 'badge-danger'}">
                        Share Status: {postInfo.shareStatus}
                    </span>
                </div>
            </div>
        </div>
    </div>
{/if}

<style>
    /* You can add custom styles here if needed */
    .meta {
        margin-top: 20px;
    }
</style>
