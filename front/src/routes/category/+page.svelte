<script>
    import { onMount } from 'svelte';
    import { API_BASE_URL } from '../../config';
  
    let categories = [];
  
    // Fetch categories from the API
    async function fetchCategories() {
      const response = await fetch(`${API_BASE_URL}/category/info`);
      categories = await response.json();
    }
  
    // Fetch categories on mount
    onMount(fetchCategories);
  
    // Recursive function to render categories
    function renderCategories(categoryList) {
      return `
        <ul style="list-style-type: none; padding-left: 20px;">
          ${categoryList.map(category => `
            <li key="${category.categoryId}" style="margin: 5px 0; font-size: 18px;">
              <div class="category-item" style="display: flex; justify-content: space-between; align-items: center; margin: 10px;">
                <a herf=/category/${category.categoryId}>${category.categoryName}</a>
                <div class="button-group">
                    <button 
                        style="background-color: #28a745; color: white; border: none; padding: 5px 10px; border-radius: 5px; cursor: pointer; font-size: 14px; transition: background-color 0.3s ease;"
                        onclick="addCategory(${category.categoryId})"
                        >
                        Add
                    </button>
                    <button 
                        style="background-color: #007bff; color: white; border: none; padding: 5px 10px; border-radius: 5px; cursor: pointer; font-size: 14px; transition: background-color 0.3s ease;"
                        onclick="editCategory(${category.categoryId})"
                        >
                        Edit
                    </button>
                    <button 
                        style="background-color: #dc3545; color: white; border: none; padding: 5px 10px; border-radius: 5px; cursor: pointer; font-size: 14px; transition: background-color 0.3s ease;"
                        onclick="deleteCategory(${category.categoryId})"
                        >
                        Delete
                    </button>
                </div>
              </div>
              ${category.children.length > 0 ? renderCategories(category.children) : ''}
            </li>
          `).join('')}
        </ul>
      `;
    }
  
    
    function editCategory(categoryId) {
      alert(`Edit category with ID: ${categoryId}`);
      // TODO: 수정 기능
    }
  
    function deleteCategory(categoryId) {
      // TODO: 삭제 기능
    }
  
    function addCategory(categoryId) {
      alert(`Add a new category under ID: ${categoryId}`);
      // TODO: 추가 기능
    }
</script>
  

<div class="container" style="max-width: 600px; margin: 20px auto; padding: 20px; border: 1px solid #ccc; border-radius: 8px; background-color: #f9f9f9; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);">
  <h1 style="font-size: 24px; margin-bottom: 20px; text-align: center;">Category List</h1>
  
  <button 
    style="background-color: #007bff; color: white; border: none; padding: 10px 15px; border-radius: 5px; cursor: pointer; margin-bottom: 20px; font-size: 16px; transition: background-color 0.3s ease;"
    on:click={fetchCategories}>
    Refresh Categories
  </button>
  
  {#if categories.length > 0}
    {@html renderCategories(categories)}
  {:else}
    <p style="text-align: center;">Loading categories...</p>
  {/if}
</div>
