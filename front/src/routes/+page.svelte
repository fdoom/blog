<script>
    import { onMount } from 'svelte';
    import { API_BASE_URL } from '../config.js';
    import { marked } from 'marked';
    import { isLoggedIn } from '../store.js';
    import { reissue } from '../common.js'

    let posts = [];
    let error = null;
    let page = 0;
    let isLoading = false;
    let hasMore = true;

    async function fetchPosts() {
        if (isLoading || !hasMore) return;
        isLoading = true;

        const fetchOptions = {
            method: 'GET',
            credentials: "include",
            headers: {}
        };

        if (isLoggedIn) {
            fetchOptions.headers.Authorization = localStorage.getItem('accessToken');
        }

        try {
            const response = await makeRequestWithToken(fetchOptions);
            
            if (!response.ok) {
                throw new Error(`Error: ${response.statusText}`);
            }

            const data = await response.json();

            if (data.length === 0) {
                hasMore = false;
            } else {
                posts = [...posts, ...data];
                page += 1;
            }
        } catch (err) {
            error = err.message;
        } finally {
            isLoading = false;
        }
    }

    async function makeRequestWithToken(fetchOptions) {
        let response = await fetch(`${API_BASE_URL}/post/info?page=${page}`, fetchOptions);

        if (response.status === 401) {
            await reissue();
            fetchOptions.headers.Authorization = localStorage.getItem('accessToken');  // 새 토큰 설정
            response = await fetch(`${API_BASE_URL}/post/info?page=${page}`, fetchOptions);
        }

        return response;
    }

    onMount(() => {
        fetchPosts();

        const observer = new IntersectionObserver(
            ([entry]) => {
                if (entry.isIntersecting) {
                    fetchPosts();
                }
            },
            { threshold: 1.0 }
        );

        const sentinel = document.getElementById('sentinel');
        if (sentinel) {
            observer.observe(sentinel);
        }
    });
</script>

<main class="container my-4">
    <h1 class="text-center my-4">블로그 글 목록</h1>
    
    {#if error}
      <p class="alert alert-danger text-center">오류: {error}</p>
    {:else if posts.length === 0}
      <p class="text-center">게시물이 없습니다.</p>
    {:else}
      <div class="row">
        {#each posts as post}
          <div class="col-md-4 mb-4">
            <div class="card shadow-sm">
                <a href="/post/{post.postId}">
                    <div class="card-body">
                        <h5 class="card-title">{post.postTitle}</h5>
                        <p class="card-text"><small class="text-muted">작성일: {new Date(post.createdAt).toLocaleString()}</small></p>
                        <p class="content">{@html marked(post.postContent)}</p>
                        <p class="share-status font-weight-bold text-primary">{post.shareStatus}</p>
                      </div>
                </a>
            </div>
          </div>
        {/each}
      </div>
    {/if}
    
    {#if isLoading}
        <div class="text-center mt-5">
            <div class="spinner-border" role="status">
            <span class="sr-only">Loading...</span>
            </div>
        </div>
    {/if}

    <!-- 스크롤 감지를 위한 sentinel 요소 -->
    <div id="sentinel" style="height: 1px;"></div>
</main>

<style>
    .content {
        height: 60px;
        overflow: hidden;
        text-overflow: ellipsis; /* 생략 기호 */
    }

    .card {
        transition: transform 0.3s, box-shadow 0.3s;
        border-radius: 30px;
    }

    .card:hover {
        transform: scale(1.05); /* 호버 시 크기 증가 */
        box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2); /* 호버 시 그림자 효과 */
    }

    a {
        text-decoration: none;
    }
</style>
