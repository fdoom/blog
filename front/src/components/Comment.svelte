<script>
    import { marked } from 'marked';
    import { API_BASE_URL } from '../config';
    import { reissue } from '../util/reissue';
    import { isLoggedIn } from '../store';
    export let postId;
    export let comment;
    export let depth = 0;
    export let getComments;

    let parentCommentId = null;
    let commentContent = null;
    let isReply = false;
    let isEdit = false;
    let editCommentContent = comment.commentContent;

    async function addComment() {
        isReply = !isReply;
    }

    async function addCommentSave(commentId) {
        console.log(commentId)
        const accessToken = localStorage.getItem('accessToken');
        parentCommentId = commentId;
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
                await getComments();
            } else {
                const responseBody = await response.json();
                alert(responseBody.message);
            }
        } else {
            alert('로그인이 필요합니다.');
        }
        await replayCancel()
    }

    async function replayCancel() {
        isReply = false;
        commentContent = null;
        parentCommentId = null;
    }

    // 댓글 수정 및 삭제 함수
    async function editComment() {
        isEdit = !isEdit;
        if(!isEdit) {
            await editCancel();
        }
    }

    async function editCancel() {
        isEdit = false;
        editCommentContent = comment.commentContent;
    }
    
    async function editCommentSave(commentId) {
        if(localStorage.getItem('accessToken')) {
            let response = await fetch(`${API_BASE_URL}/comment/info/${commentId}`, {
                method: 'PUT',
                credentials: 'include',
                headers: {
                    Authorization: localStorage.getItem('accessToken'),
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                        'commentContent': editCommentContent
                })
            })

            if(response.status == 401) {
                await reissue();
                response = await fetch(`${API_BASE_URL}/comment/info/${commentId}`, {
                    method: 'PUT',
                    credentials: 'include',
                    headers: {
                        Authorization: localStorage.getItem('accessToken')
                    },
                    body: JSON.stringify({
                        'commentContent': editCommentContent
                    })
                });
            }

            if(response.ok) {
                await getComments();
            } else {
                const responseBody = await response.json();
                alert(responseBody.message);
            }
        } else {
            alert('로그인이 필요합니다.');
        }
        editCancel();
    }

    async function deleteComment(commentId) {
        // 삭제 로직 작성
        if (confirm('정말로 이 댓글을 삭제하시겠습니까?')) {
            let response = await fetch(`${API_BASE_URL}/comment/info/${commentId}`, {
                method: 'DELETE',
                credentials: 'include',
                headers: {
                    Authorization: localStorage.getItem('accessToken')
                }
            })

            if(response.status == 401) {
                await reissue();
                response = await fetch(`${API_BASE_URL}/comment/info/${commentId}`, {
                    method: 'DELETE',
                    credentials: 'include',
                    headers: {
                        Authorization: localStorage.getItem('accessToken')
                    }
                })
            }

            if(response.ok) {
                await getComments();
            } else {
                const responseBody = await response.json();
                alert(responseBody.message);
            }
        }
    }
</script>

<div style="margin-left: {depth + 20}px; border-left: 1px solid #ddd; padding-left: 10px; margin-top: 10px;">
    {#if isEdit}
        <textarea bind:value={editCommentContent} rows="3" style="width: 100%;"></textarea>
        <div style="float: right">
            <button on:click={editCommentSave(comment.commentId)}>
                <i class="fas fa-save"></i>
            </button>
            <button on:click={editCancel}>
                <i class="fas fa-cancel"></i>
            </button>
        </div>
    {:else}
        {@html marked(comment.commentContent)}
    {/if}
    
    <p style="font-size: 12px; color: #888;  margin: 2px;">{comment.username}</p>
    <p style="font-size: 12px; color: #888; margin: 2px;">작성일: {new Date(comment.createdAt).toLocaleString()}</p>
    
    {#if comment.createdAt != comment.updatedAt}
        <p style="font-size: 12px; color: #888; margin: 2px;">수정일: {new Date(comment.updatedAt).toLocaleString()}</p>
    {/if}
    
    {#if $isLoggedIn}
        <button on:click={addComment} style="font-size: 12px;">
            <i class="fas fa-reply"></i>
        </button>
        <button on:click={editComment} style="font-size: 12px;">
            <i class="fas fa-edit"></i>
        </button>
        <button on:click={deleteComment(comment.commentId)} style="font-size: 12px;">
            <i class="fas fa-trash"></i>
        </button>
    {/if}


    {#if isReply}
        <textarea class="mt-3" bind:value={commentContent} rows="3" style="width: 100%;"></textarea>
        <div style="float: right">
            <button on:click={addCommentSave(comment.commentId)}>
                <i class="fas fa-save"></i>
            </button>
            <button on:click={replayCancel}>
                <i class="fas fa-cancel"></i>
            </button>
        </div>
    {/if}

    {#if comment.childComments && comment.childComments.length > 0}
        {#each comment.childComments as childComment}
            <svelte:self comment={childComment} depth={depth + 1} postId={postId} getComments={getComments}/>
        {/each}
    {/if}
</div>


<style>
    button {
        background-color: #f1f1f1;
        border: 1px solid #ccc;
        border-radius: 3px;
        font-size: 12px;
        padding: 3px 6px;
        margin-right: 5px;
        cursor: pointer;
        color: #555;
        transition: background-color 0.3s ease, color 0.3s ease;
    }

    button:hover {
        background-color: #ddd;
        color: #333;
    }

    button:active {
        background-color: #ccc;
    }
</style>