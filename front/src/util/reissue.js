import { API_BASE_URL } from "../config";
import { goto } from '$app/navigation';
import { isLoggedIn } from '../store'

async function reissue() {
    const response = await fetch(`${API_BASE_URL}/reissue`, {
        method: 'POST',
        credentials: "include"
    });

    if (!response.ok) {
        localStorage.removeItem('accessToken');
        isLoggedIn.set(false);
        goto('/login');
        return;
    }

    const newToken = response.headers.get('Authorization');
    if (newToken) {
        isLoggedIn.set(true);
        localStorage.setItem('accessToken', newToken);
    }
}

export { reissue };