import { API_BASE_URL } from "./config";
import { goto } from '$app/navigation';

async function reissue() {
    const response = await fetch(`${API_BASE_URL}/reissue`, {
        method: 'POST',
        credentials: "include"
    });

    if (!response.ok) {
        goto('/login');
        return;
    }

    const newToken = response.headers.get('Authorization');
    if (newToken) {
        localStorage.setItem('accessToken', newToken);
    }
}

export { reissue };