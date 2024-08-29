<script>
  import { onMount } from 'svelte';
  import { API_BASE_URL } from '../../config';
  import { isLoggedIn } from '../../store';

  let categories = [];

  async function fetchCategories() {
    const response = await fetch(`${API_BASE_URL}/category/info`);
    categories = await response.json();
  }

  onMount(fetchCategories);

  function renderCategories(categoryList, depth = 0) {
    return `
      <ul style="list-style-type: none; padding-left: ${depth * 20}px; margin: 0;">
        ${categoryList.map(category => `
          <li key="${category.categoryId}" style="margin: 5px 0; font-size: 18px;">
            <div class="category-item" style="display: flex; justify-content: space-between; align-items: center; margin: 5px 0;">
              <span style="display: inline-block; width: 100%;">
                ${depth > 0 ? '└─' : ''} <a href="/category/${category.categoryId}" style="text-decoration: none;">${category.categoryName}</a>
              </span>
              ${$isLoggedIn ? `
              <div class="button-group" style="display: flex; gap: 5px;">
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
              ` : ''}
            </div>
            ${category.children.length > 0 ? renderCategories(category.children, depth + 1) : ''}
          </li>
        `).join('')}
      </ul>
    `;
  }

  function editCategory(categoryId) {
    alert(`Edit category with ID: ${categoryId}`);
    // TODO: Edit functionality
  }

  function deleteCategory(categoryId) {
    alert(`Delete category with ID: ${categoryId}`);
    // TODO: Delete functionality
  }

  function addCategory(categoryId) {
    alert(`Add a new category under ID: ${categoryId}`);
    // TODO: Add functionality
  }
</script>


<div class="container" style="max-width: 600px; margin: 20px auto; padding: 20px; border: 1px solid #ccc; border-radius: 8px; background-color: #f9f9f9; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);">
<h1 style="font-size: 24px; margin-bottom: 20px; text-align: center;">Category List</h1>

{#if categories.length > 0}
  {@html renderCategories(categories)}
{:else}
  <p style="text-align: center;">Loading categories...</p>
{/if}
</div>
