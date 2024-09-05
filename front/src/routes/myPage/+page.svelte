<script>
    import { goto } from '$app/navigation';
    import { onMount } from 'svelte';
    import { API_BASE_URL } from '../../config';
    import { reissue } from '../../util/reissue';
    import { isLoggedIn } from '../../store';

    let password = '';
    let newPassword = '';

    async function alter() {
        if (password === newPassword) {
            alert('Passwords do match');
            return;
        }

        let response = await fetch(`${API_BASE_URL}/account/alter`, {
            method: 'POST',
            body: JSON.stringify({ password, newPassword }),
            headers: {
                Authorization: localStorage.getItem('accessToken'),
                'Content-type': 'application/json'
            },
            credentials: 'include'
        });

        if(response.status == 401) {
            await reissue();
            response = await fetch(`${API_BASE_URL}/account/alter`, {
                method: 'POST',
                body: JSON.stringify({ password, newPassword }),
                headers: {
                    Authorization: localStorage.getItem('accessToken'),
                    'Content-type': 'application/json'
                },
                credentials: 'include'
            });
        }

        if(response.ok) {
            alert('비밀번호가 변경되었습니다.');
            goto('/logout');
        }
        if (!response.ok) {
            const errorMessage = await response.json();
            alert(errorMessage.message);
        }
    }

    async function deleteInfo() {
        let response = await fetch(`${API_BASE_URL}/account/info`, {
            method: 'DELETE',
            headers: {
                Authorization: localStorage.getItem('accessToken')
            },
            credentials: 'include'
        });

        if(response.status == 401) {
            await reissue();
            response = await fetch(`${API_BASE_URL}/account/info`, {
                method: 'DELETE',
                headers: {
                    Authorization: localStorage.getItem('accessToken')
                },
                credentials: 'include'
            });
        }

        if(response.ok) {
            alert('탈퇴하였습니다.');
            goto('/logout');
        }
        if (!response.ok) {
            const errorMessage = await response.json();
            alert(errorMessage.message);
        }
    }

    onMount(() => {
        if(!$isLoggedIn) {
            goto('/login')
        }
    })
</script>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <h2 class="text-center">My Page</h2>
            <form on:submit|preventDefault={alter}>
                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" class="form-control" bind:value={password} placeholder="Password" required />
                </div>
                <div class="form-group">
                    <label for="newPassword">New Password</label>
                    <input type="password" id="newPassword" class="form-control" bind:value={newPassword} placeholder="New Password" required />
                </div>
                <button type="submit" class="btn btn-primary btn-block">Password Change</button>
            </form>
            <button type="submit" class="btn btn-danger btn-block mt-3" on:click={deleteInfo}>Delete This Account</button>
        </div>
    </div>
</div>
