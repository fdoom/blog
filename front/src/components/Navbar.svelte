<script>
    import { isLoggedIn } from "../store";

</script>

<nav class="navbar navbar-expand-lg navbar-light bg-primary rounded-bottom">
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>
    
    <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav mr-auto">
            <li class="nav-item"><a class="nav-link text-white" href="/">홈</a></li>
            <li class="nav-item"><a class="nav-link text-white" href="/category">카테고리</a></li>

            {#if $isLoggedIn}
                <li class="nav-item"><a class="nav-link text-white" href="/post/edit">글 작성</a></li>
            {/if}
        </ul>
        <form class="form-inline ml-auto">
            <div class="input-group">
                <input class="form-control" type="search" placeholder="검색..." aria-label="Search">
                <div class="input-group-append">
                    <button class="btn btn-outline-light" type="submit">
                        <i class="fas fa-search"></i>
                    </button>
                </div>
            </div>
        </form>
    </div>
</nav>

<style>
    nav {
        margin: 10px;
    }

    .navbar {
        box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
        border-radius: 30px !important; /* 네비게이션 바의 하단 모서리를 둥글게 */
    }

    .input-group {
        width: 100%; /* 검색 바가 input-group의 너비를 가득 채우도록 설정 */
    }

    .nav-item .nav-link {
        transition: color 0.4s; /* 글자 색의 변화를 부드럽게 전환 */
    }

    .nav-item .nav-link:hover {
        color: #000000 !important; /* 호버 시 글자 색을 흰색으로 변경 */
    }
</style>