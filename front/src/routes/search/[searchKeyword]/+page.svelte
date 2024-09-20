<script>
    import { page } from '$app/stores';
    import PostList from '../../../components/PostList.svelte';

    $: searchKeyword = $page.params.searchKeyword;
</script>

<PostList searchKeyword={encodeURIComponent(searchKeyword)}/>