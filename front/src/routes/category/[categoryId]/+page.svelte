<script>
    import { page } from '$app/stores';
    import PostList from '../../../components/PostList.svelte';

    $: categoryId = $page.params.categoryId;
</script>

<PostList {categoryId} />