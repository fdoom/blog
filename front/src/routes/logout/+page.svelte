<script>
    import { onMount } from 'svelte';
    import { API_BASE_URL } from '../../config';
    import { goto } from '$app/navigation';
    import { isLoggedIn } from '../../store';

    async function logout() {
        try {
            const response = await fetch(`${API_BASE_URL}/logout`, {
                method: 'POST',
                credentials: "include"
            });

            if (!response.ok) {
                // 상태 코드에 따른 에러 메시지 표시
                const errorMessage = await response.text();
                console.error('서버 응답 오류:', errorMessage);
                throw new Error('로그아웃에 실패');
            }

            localStorage.removeItem('accessToken');
            isLoggedIn.set(false);
        } catch (error) {
            console.error('로그아웃 오류:', error);
        }
    }

    onMount(() => {
        logout();
        goto('/')
    })
    
</script>
