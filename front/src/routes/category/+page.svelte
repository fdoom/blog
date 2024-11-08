<!-- src/routes/ParentComponent.svelte -->
<script>
  import { onMount } from 'svelte';
  import { API_BASE_URL } from '../../config';
  import { isLoggedIn } from '../../store';
  import { goto } from '$app/navigation';
  import { reissue } from '../../util/reissue';
  import Category from '../../components/Category.svelte';

  let categories = [];
  let isLoading = true;

  onMount(async () => {
        isLoading = true;
        await fetchCategories();
        isLoading = false;
  });

  async function fetchCategories() {
      const response = await fetch(`${API_BASE_URL}/category/info`);
      categories = await response.json();
  }

  async function responseAlert(response) {
      let alertText = null;
      const statusActions = {
          200: async () => {
              await fetchCategories(); // 200 응답시 카테고리 데이터를 다시 가져옴
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

      (statusActions[response.status] || statusActions[200])?.();

      if (alertText) {
          alert(alertText);
      }
  }

  async function addNewCategory() {
    const categoryName = prompt('Enter the name of the new category:');
    if (categoryName) {
        try {
            const fetchOptions = {
                method: 'POST',
                credentials: "include",
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: localStorage.getItem('accessToken')
                },
                body: JSON.stringify({ categoryName })
            };

            let response = await fetch(`${API_BASE_URL}/category/info`, fetchOptions);

            if (response.status === 401) {
                await reissue();
                fetchOptions.headers.Authorization = localStorage.getItem('accessToken');
                response = await fetch(`${API_BASE_URL}/category/info`, fetchOptions);
            }

            await responseAlert(response);

        } catch (error) {
            console.error('Error adding category:', error);
            alert('에러가 발생했습니다.');
        }
    } else {
        alert('카테고리 입력이 취소되었습니다.');
    }
  }
</script>

<div class="container">
  {#if categories.length > 0}
      <Category {categories} depth={0} refreshCategories={fetchCategories} />

  {/if}

  {#if isLoading}
    <div class="text-center mt-5">
        <div class="spinner-border" role="status">
        <span class="sr-only">Loading...</span>
        </div>
    </div>
  {/if}
</div>

{#if $isLoggedIn}
  <button class="fas fa-plus icon" on:click={addNewCategory}></button>
{/if}

<style>
    .container {
        max-width: 90%;
        margin: 20px auto;
        padding: 20px;
        border: 1px solid #ccc;
        border-radius: 8px;
        background-color: #f9f9f9;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    }

    button {
        position: fixed; 
        bottom: 20px; 
        right: 20px; 
        background-color: #28a745; 
        color: white;
        border: none; 
        padding: 20px 20px; 
        border-radius: 50%; 
        cursor: pointer; 
        font-size: 20px; 
        box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2); 
        transition: background-color 0.3s ease;
    }
</style>