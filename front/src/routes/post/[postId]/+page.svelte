<script>
    import { page } from '$app/stores';
    import { onMount, afterUpdate } from 'svelte';
    import { API_BASE_URL } from '../../../config';
    import { marked } from 'marked';
    import { isLoggedIn } from '../../../store';
    import { reissue } from '../../../util/reissue';
    import { goto } from '$app/navigation';
    import hljs from 'highlight.js';
    import Comment from '../../../components/Comment.svelte';

    let postId;
    let postInfo = {};
    let comments = [];
    let parentCommentId = null;
    let commentContent = "";

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
            await getComments();
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
        goto(`/post/edit/${postId}`);
    }

    async function deletePost() {
        if(prompt('정말로 해당 게시물을 삭제하시겠습니까?\n게시물의 제목을 입력하면 삭제됩니다.') == postInfo.postTitle) {
            try {
                let response = await fetch(`${API_BASE_URL}/post/info/${postId}`, {
                    method: 'DELETE',
                    credentials: 'include',
                    headers: {
                        Authorization: localStorage.getItem('accessToken')
                    },
                });

                if(response.status == 401) {
                    await reissue();
                    response = await fetch(`${API_BASE_URL}/post/info/${postId}`, {
                        method: 'DELETE',
                        credentials: 'include',
                        headers: {
                            Authorization: localStorage.getItem('accessToken')
                        },
                    });
                }

                if(response.ok) {
                    goto('/');
                } else if(response.status == 403) {
                    throw Error('권한이 없습니다.');
                } else if(response.status == 404) {
                    throw Error('삭제 가능한 게시물이 존재하지 않습니다.');
                } else {
                    throw Error('삭제하는데 실패했습니다.');
                }
            } catch(error) {
                alert(error.message)
            }
        }
    }

    async function getComments() {
        const response = await fetch(`${API_BASE_URL}/comment/info/post/${postId}`, {
            method: 'GET',
            credentials: 'include'
        })

        if(response.ok) {
            comments = await response.json();
        }
    }

    async function createComment() {
        const accessToken = localStorage.getItem('accessToken')
        if(accessToken) {
            let response = await fetch(`${API_BASE_URL}/comment/info`, {
                method: 'POSt',
                credentials: 'include',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: accessToken,
                },

                body: JSON.stringify({
                        postId,
                        parentCommentId,
                        commentContent
                })
            })

            if(response.status == 401) {
                await reissue();
                response = await fetch(`${API_BASE_URL}/comment/info`, {
                    method: 'POSt',
                    credentials: 'include',
                    headers: {
                        'Content-Type': 'application/json',
                        Authorization: localStorage.getItem('accessToken'),
                    },

                    body: JSON.stringify({
                            postId,
                            parentCommentId,
                            commentContent
                    })
                })
            }

            if(response.ok) {
                getComments();
                alert('댓글 작성이 완료되었습니다.')
                commentContent = '';
            } else {
                const responseBody = await response.json();
                alert(responseBody.message);
            }
        } else {
            alert('로그인이 필요합니다.');
        }

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
                    {@html postInfo.postContent ? marked(postInfo.postContent.replace(/!\[(.*?)\]\((.*?)\)/g, '<div style="text-align: center"><img src="$2" alt="$1" style="max-width: 100%; height: auto;"/></div>')) : 'Post content not found.'}
                </p>
                <hr />
                <div class="meta">
                    <p class="text-muted">작성일: {new Date(postInfo.createdAt).toLocaleString()}</p>
                    {#if postInfo.createdAt != postInfo.updatedAt}
                        <p class="text-muted">수정일: {new Date(postInfo.updatedAt).toLocaleString()}</p>
                    {/if}
                    <span class="badge {postInfo.shareStatus === 'PUBLIC' ? 'badge-success' : (postInfo.shareStatus === 'PROTECTED' ? 'badge badge-warning' : 'badge-danger')}">
                        {postInfo.shareStatus}
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

                <div class="comment-input mt-4">
                    <textarea placeholder="댓글을 입력하세요" bind:value={commentContent}></textarea>
                    <button class="btn btn-success mt-2" on:click={createComment}>댓글 작성</button>
                </div>
            </div>
            <div class="mt-5 p-2">
                {#each comments as comment}
                    <Comment comment={comment} depth={0} postId={postId} getComments={getComments}/>
                {/each}
            </div>
        </div>
    </div>
{/if}

<style>
    .meta {
        margin-top: 20px;
    }

    .meta > p {
        margin: 2px;
    }

    .actions {
        float: right;
        margin: 10px;
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

    .comment-input textarea {
        width: 100%;
        height: 80px;
        padding: 10px;
        border: 1px solid #ccc;
        border-radius: 4px;
    }

    .comment-input button {
        float: right;
    }
</style>
