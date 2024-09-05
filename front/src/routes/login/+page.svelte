<script>
    import { goto } from '$app/navigation';
    import { onMount } from 'svelte';
    import { API_BASE_URL } from '../../config';
    import { isLoggedIn } from '../../store';

    let username = '';
    let password = '';

    async function login() {
        try {
            const response = await fetch(`${API_BASE_URL}/login`, {
                method: 'POST',
                body: JSON.stringify({ username, password }),
                credentials: "include"
            });

            if (!response.ok) {
                // 상태 코드에 따른 에러 메시지 표시
                const errorMessage = await response.text();
                console.error('서버 응답 오류:', errorMessage);
                throw new Error('로그인 실패');
            }
            // Access Token을 localStorage에 저장
            localStorage.setItem('accessToken', response.headers.get('Authorization'));

            isLoggedIn.set(true);

            window.history.back();
        } catch (error) {
            console.error('로그인 오류:', error);
            alert('로그인에 실패했습니다. 다시 시도해주세요.');
        }
    }

    onMount(() => {
        if($isLoggedIn) {
            goto('/');
        }
    })
</script>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <h2 class="text-center">Login</h2>
            <form on:submit|preventDefault={login}>
                <div class="form-group">
                    <label for="username">Username</label>
                    <input type="text" id="username" class="form-control" bind:value={username} placeholder="Username" required />
                </div>
                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" class="form-control" bind:value={password} placeholder="Password" required />
                </div>
                <button type="submit" class="btn btn-primary btn-block">Login</button>
            </form>
            <div class="text-center mt-3">
                <p>Don't have an account? <a href='/join'>Sign Up</a></p>
            </div>
        </div>
    </div>
</div>