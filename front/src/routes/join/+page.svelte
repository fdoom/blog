<script>
    import { goto } from '$app/navigation';
    import { onMount } from 'svelte';
    import { API_BASE_URL } from '../../config';
    import { isLoggedIn } from '../../store';

    let username = '';
    let password = '';
    let confirmPassword = '';

    async function register() {
        if (password !== confirmPassword) {
            alert('Passwords do not match');
            return;
        }

        const response = await fetch(`${API_BASE_URL}/account/join`, {
            method: 'POST',
            credentials: 'include',
            body: JSON.stringify({ username, password }),
            headers: {
                'Content-type': 'application/json'
            }
        });

        if(response.ok) {
            alert('회원가입이 완료되었습니다! 이제 로그인해주세요.');
            goto('/login');
        }
        if (!response.ok) {
            const errorMessage = await response.json();
            alert(errorMessage.message);
        }
    }

    onMount(() => {
        if($isLoggedIn) {
            goto('/')
        }
    })
</script>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <h2 class="text-center">Sign Up</h2>
            <form on:submit|preventDefault={register}>
                <div class="form-group">
                    <label for="username">Username</label>
                    <input type="text" id="username" class="form-control" bind:value={username} placeholder="Username" required />
                </div>
                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" class="form-control" bind:value={password} placeholder="Password" required />
                </div>
                <div class="form-group">
                    <label for="confirmPassword">Confirm Password</label>
                    <input type="password" id="confirmPassword" class="form-control" bind:value={confirmPassword} placeholder="Confirm Password" required />
                </div>
                <button type="submit" class="btn btn-primary btn-block">Sign Up</button>
            </form>
            <div class="text-center mt-3">
                <p>Already have an account? <a href='/login'>Login</a></p>
            </div>
        </div>
    </div>
</div>
