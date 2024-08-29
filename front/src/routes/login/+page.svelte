<script>
    import { goto } from '$app/navigation';
    import { API_BASE_URL } from '../../config';

    let username = '';
    let password = '';

    async function login() {
        try {
            console.log('로그인 시도 중...');

            const response = await fetch(`${API_BASE_URL}/login`, {
                method: 'POST',
                credentials: "include",
                body: JSON.stringify({ username, password }),
            });

            if (!response.ok) {
                // 상태 코드에 따른 에러 메시지 표시
                const errorMessage = await response.text();
                console.error('서버 응답 오류:', errorMessage);
                throw new Error('로그인 실패');
            }
            // Access Token을 localStorage에 저장
            localStorage.setItem('accessToken', response.headers.accessToken);
            
            console.log('메인 페이지로 리다이렉션 중...');
            // 메인 페이지로 리다이렉션
            goto('/');
        } catch (error) {
            console.error('로그인 오류:', error);
            alert('로그인에 실패했습니다. 다시 시도해주세요.');
        }
    }
</script>

<form on:submit|preventDefault={login}>
    <input type="text" bind:value={username} placeholder="Username" required />
    <input type="password" bind:value={password} placeholder="Password" required />
    <button type="submit">Login</button>
</form>
