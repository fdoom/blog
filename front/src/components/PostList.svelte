<script>
    import { onMount } from 'svelte';
    import { marked } from 'marked';
    import { fetchPosts } from '../util/fetchPosts';

    export let categoryId = null;
    export let searchKeyword = null;
    let posts = [];
    let error = null;
    let page = 0;
    let isLoading = false;
    let hasMore = true;
    let categorys = [];
    let showCategories = false; // 카테고리 목록의 표시 여부를 관리하는 변수

    async function loadPosts() {
        if (isLoading || !hasMore) return;
        isLoading = true;

        const result = await fetchPosts(categoryId, page, searchKeyword);

        if (result.error) {
            error = result.error;
        } else {
            posts = [...posts, ...result.posts];
            hasMore = result.hasMore;

            if (result.categorys && result.categorys.length > 0) {
                categorys = result.categorys;
            }
            
            if (result.hasMore) {
                page += 1;
            }
        }

        isLoading = false;
    }

    onMount(() => {
        loadPosts();

        const observer = new IntersectionObserver(
            ([entry]) => {
                if (entry.isIntersecting) {
                    loadPosts();
                }
            },
            { threshold: 1.0 }
        );

        const sentinel = document.getElementById('sentinel');
        if (sentinel) {
            observer.observe(sentinel);
        }
    });

    $: if (searchKeyword !== null) {
        if (typeof window !== 'undefined') {
            posts = [];
            page = 0;
            hasMore = true;
            categorys = [];
            loadPosts();
        }
    }

    function toggleCategories() {
        showCategories = !showCategories;
    }
</script>

<main class="container my-4">
    <!-- 카테고리 목록 -->
    {#if categorys.length > 0}
        <!-- 카테고리 목록 버튼 -->
        <button 
            class="btn btn-link text-center my-4" 
            on:click={toggleCategories} 
            aria-expanded={showCategories}
            aria-controls="category-list">
            카테고리 목록 {showCategories ? '▲' : '▼'}
        </button>
        {#if showCategories}
            <div id="category-list" class="category-list">
                <ul class="list-group">
                    {#each categorys as category}
                        <li class="list-group-item category-item">
                            <a href={`/category/${category.categoryId}`} class="category-link">
                                {category.categoryName}
                            </a>
                        </li>
                    {/each}
                </ul>
            </div>
        {/if}
    {/if}
    
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
                                <p class="content">{@html post.postContent ? marked(post.postContent.replace(/!\[(.*?)\]\((.*?)\)/g, '<div style="text-align: center"><img src="$2" alt="$1" style="max-width: 100%; height: auto;"/></div>')) : 'Post content not found.'}</p>
                                <span class="badge {post.shareStatus === 'PUBLIC' ? 'badge-success' : (post.shareStatus === 'PROTECTED' ? 'badge badge-warning' : 'badge-danger')}">
                                    {post.shareStatus}
                                </span>
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

    .category-list {
        margin-top: 20px;
    }

    .list-group-item {
        border: none;
        border-radius: 10px;
        transition: background-color 0.3s;
    }

    .category-item:hover {
        background-color: #f8f9fa;
    }

    .category-link {
        display: block;
        padding: 10px 20px;
        color: #007bff;
        font-weight: bold;
    }

    .category-link:hover {
        color: #0056b3;
        text-decoration: underline;
    }

    .btn {
        text-decoration: none;
    }
</style>
