import { API_BASE_URL } from '../config.js';
import { reissue } from './reissue.js';
import { isLoggedIn } from '../store.js';

export async function fetchPosts(categoryId, page, searchKeyword) {
    let posts = [];
    let hasMore = true;
    let error = null;
    let categorys = null;

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

    async function postSearch() {
        let postResponse = await fetch(`${API_BASE_URL}/post/search/${searchKeyword}?page=${page}`, fetchOptions);

        if (postResponse.status === 401) {
            await reissue();
            fetchOptions.headers.Authorization = localStorage.getItem('accessToken');
            postResponse = await fetch(url, fetchOptions);
        }
        return postResponse;
    }

    async function categorySearch() {
        let categoryResponse = await fetch(`${API_BASE_URL}/category/search/${searchKeyword}`, fetchOptions);

        if (categoryResponse.status === 401) {
            await reissue();
            fetchOptions.headers.Authorization = localStorage.getItem('accessToken');
            categoryResponse = await fetch(url, fetchOptions);
        }
        categorys = await categoryResponse.json();
    }

    try {
        let response;
        if (searchKeyword) {
            response = await postSearch();
            await categorySearch();
        } else {
            response = await makeRequestWithToken();
        }

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
    return { posts, hasMore, error, categorys };
}
