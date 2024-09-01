<script>
    import Edit from "../../../../components/Edit.svelte";
    import { page } from '$app/stores';

    $: postId = $page.params.postId;
</script>

<Edit postId={postId}/>