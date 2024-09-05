<script>
    import { onMount } from 'svelte';
    import { isLoggedIn } from '../store.js'; // 스토어 import

    let accountInfo;
    let accountHref;

    // 반응형 코드로 상태에 따라 UI를 업데이트
    $: {
        accountInfo = $isLoggedIn ? '로그아웃' : '로그인'; // 스토어 값에 $를 붙여 사용
        accountHref = $isLoggedIn ? '/logout' : '/login'; // 스토어 값에 $를 붙여 사용
    }

    onMount(() => {
        if(localStorage.getItem('accessToken') == null) {
            isLoggedIn.set(false);
        }
        else {
            isLoggedIn.set(true);
        }
    });
</script>

<div class="text-right mr-5 mt-3">
    <a href={accountHref}>{accountInfo}</a>
    {#if $isLoggedIn}
        <a href='/myPage'><i class="fa-solid fa-gear"></i></a>
    {/if}
</div>