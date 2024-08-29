<script>
    export let category;

    function editCategory(categoryId) {
        console.log(`Editing category with ID: ${categoryId}`);
        // Edit functionality implementation
    }

    async function deleteCategory(categoryId) {
        const response = await fetch(`http://localhost:8080/category/${categoryId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            // Propagate the delete event to parent
            dispatch('deleted', categoryId);
            console.log(`Deleted category with ID: ${categoryId}`);
        } else {
            console.error('Failed to delete category');
        }
    }

    import { createEventDispatcher } from 'svelte';
    const dispatch = createEventDispatcher();
</script>

<li>
    <div class="category-item">
        <span>{category.categoryName}</span>
        <button class="edit-btn" on:click={() => editCategory(category.categoryId)}>Edit</button>
        <button class="delete-btn" on:click={() => deleteCategory(category.categoryId)}>Delete</button>
    </div>
    
    {#if category.children.length > 0}
        <ul>
            {#each category.children as childCategory (childCategory.categoryId)}
                <svelte:self {childCategory} />
            {/each}
        </ul>
    {/if}
</li>

<style>
    ul {
        list-style-type: none;
        padding-left: 20px;
    }
    li {
        margin: 10px 0;
    }
    .category-item {
        display: flex;
        align-items: center;
    }
    .category-item span {
        flex-grow: 1;
    }
    .edit-btn, .delete-btn {
        margin: 0 5px;
        padding: 5px 10px;
        cursor: pointer;
        border: none;
        border-radius: 5px;
    }
    .edit-btn {
        background-color: #4CAF50;
        color: white;
    }
    .delete-btn {
        background-color: #f44336;
        color: white;
    }
    .edit-btn:hover, .delete-btn:hover {
        opacity: 0.8;
    }
</style>