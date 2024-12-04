<script>
    import { API_BASE_URL } from "../config";
    import { goto } from '$app/navigation';
    import { isLoggedIn } from "../store";
    import { reissue } from "../util/reissue";

    export let categories = [];
    export let depth = 0;
    export let refreshCategories; // 부모 컴포넌트에서 카테고리 새로 고침 함수 전달

    const fetchOptions = {
        method: '',
        credentials: "include",
        headers: {
            'Content-Type': 'application/json',
            Authorization: ''
        },
        body: null
    };

    async function responseAlert(response) {
        let alertText = null;
        const statusActions = {
            200: async () => {
                await refreshCategories(); // 200 응답시 카테고리 데이터를 다시 가져옴
            },
            403: () => alertText = '권한이 없습니다.',
            401: () => {
                alertText = '로그인이 필요합니다.';
                goto('login');
            },
            409: () => alertText = '카테고리 이름이 중복되었습니다.',
            400: () => alertText = '등록 불가능한 이름입니다.',
            default: () => alertText = '해당 작업을 실패했습니다.'
        };

        (statusActions[response.status] || statusActions[200])();

        if (alertText) {
            alert(alertText);
        }
    }

    async function addCategory(categoryId) {
        const categoryName = prompt('Enter the name of the new category:');

        if(categoryName) {
            fetchOptions.method = 'POST';
            fetchOptions.headers.Authorization = localStorage.getItem('accessToken');
            const parentCategoryId = categoryId;
            fetchOptions.body = JSON.stringify({categoryName, parentCategoryId})

            let response = await fetch(`${API_BASE_URL}/category/info`, fetchOptions);

            if (response.status === 401) {
                await reissue();
                fetchOptions.headers.Authorization = localStorage.getItem('accessToken');
                response = await fetch(`${API_BASE_URL}/category/info`, fetchOptions);
            }

            await responseAlert(response);
        }
    }

    async function editCategory(categoryId) {
        const categoryToEdit = categories.find(cat => cat.categoryId === categoryId);
        const categoryName = prompt("Edit category name:", categoryToEdit.categoryName);
        const inputId = Number(prompt("parentCategoryId:"));
        const parentCategoryId = inputId > 0 ? inputId : null;

        if (categoryName) {
            fetchOptions.method = 'PUT'; // Change to PUT for updating
            fetchOptions.headers.Authorization = localStorage.getItem('accessToken');
            fetchOptions.body = JSON.stringify({ categoryName, parentCategoryId});

            let response = await fetch(`${API_BASE_URL}/category/info/${categoryId}`, fetchOptions);

            if (response.status === 401) {
                await reissue();
                fetchOptions.headers.Authorization = localStorage.getItem('accessToken');
                response = await fetch(`${API_BASE_URL}/category/info/${categoryId}`, fetchOptions);
            }

            await responseAlert(response);
        }
    }

    async function deleteCategory(categoryId) {
        const result = confirm("해당 카테고리 및 하위 카테고리 정보와 관련 글들이 전부 사라집니다.\n정말로 삭제하시겠습니까?");
        if(result) {
            fetchOptions.method = 'DELETE';
            fetchOptions.headers.Authorization = localStorage.getItem('accessToken');
            fetchOptions.body = null;

            let response = await fetch(`${API_BASE_URL}/category/info/${categoryId}`, fetchOptions);

            if (response.status === 401) {
                await reissue();
                fetchOptions.headers.Authorization = localStorage.getItem('accessToken');
                response = await fetch(`${API_BASE_URL}/category/info/${categoryId}`, fetchOptions);
            }
            
            await responseAlert(response);
        }
    }
</script>

<ul style="list-style-type: none; padding-left: {depth + 10}px; margin: 0;">
    {#each categories as category}
        <li key={category.categoryId}>
            <div class="category-item">
                <span>
                    {#if depth > 0}
                        └
                    {/if}
                    <a href="/category/{category.categoryId}" style="text-decoration: none;">{category.categoryName}</a>
                </span>
                {#if $isLoggedIn}
                    <div class="button-group">
                        <button
                            class="fas fa-plus icon addBtn"
                            on:click={() => addCategory(category.categoryId)}
                        >
                        </button>
                        <button
                            class="fas fa-edit icon editBtn"
                            on:click={() => editCategory(category.categoryId)}
                        >
                        </button>
                        <button
                            class="fas fa-trash icon deleteBtn"
                            on:click={() => deleteCategory(category.categoryId)}
                        >
                        </button>
                    </div>
                {/if}
            </div>
            {#if category.children && category.children.length > 0}
                <svelte:self categories={category.children} depth={depth + 1} refreshCategories={refreshCategories} />
            {/if}
        </li>
    {/each}
</ul>


<style>
    li {
        margin: 5px 0;
        font-size: 18px;
    }

    .category-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin: 5px 0;
    }

    .category-item:hover {
        background-color: rgba(128, 128, 128, 0.321);
        transition-duration: 0.5s;
        border-radius: 10px;
    }

    span {
        display: inline-block;
        width: 100%;
    }

    .button-group {
        display: flex;
        gap: 5px;
    }

    button {
        color: white;
        border: none;
        padding: 13px;
        border-radius: 5px;
        cursor: pointer;
        font-size: 14px;
        transition: background-color 0.3s ease;
    }

    .addBtn {
        background-color: #28a745;
    }

    .editBtn {
        background-color: #007bff;
    }

    .deleteBtn {
        background-color: #dc3545;
    }
</style>