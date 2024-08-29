import { API_BASE_URL } from '../config.js';
import { reissue } from './reissue.js';
import { isLoggedIn } from '../store.js';

export async function fetchPosts(categoryId, page) {
    let posts = [];
    let hasMore = true;
    let error = null;

    const fetchOptions = {
        method: 'GET',
        credentials: "include",
        headers: {}
    };

    if (isLoggedIn) {
        fetchOptions.headers.Authorization = localStorage.getItem('accessToken');
    }

    async function makeRequestWithToken() {
        let url = `${API_BASE_URL}/post/info`;
        if (categoryId) {
            url += `/category/${categoryId}`;
        }
        url += `?page=${page}`;

        let response = await fetch(url, fetchOptions);

        if (response.status === 401) {
            await reissue();
            fetchOptions.headers.Authorization = localStorage.getItem('accessToken');  // 새 토큰 설정
            response = await fetch(url, fetchOptions);
        }

        return response;
    }

    try {
        const response = await makeRequestWithToken();

        if (!response.ok) {
            throw new Error(`Error: ${response.statusText}`);
        }

        const data = await response.json();

        if (data.length === 0) {
            hasMore = false;
        } else {
            posts = data;
        }
    } catch (err) {
        error = err.message;
        hasMore = false;
    }

    return { posts, hasMore, error };
}
