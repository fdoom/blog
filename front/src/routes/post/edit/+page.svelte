<script>
    import { marked } from 'marked';
    import hljs from 'highlight.js';
    import 'highlight.js/styles/github.css';

    marked.setOptions({
        highlight: (code, lang) => {
            const language = lang && hljs.getLanguage(lang) ? lang : 'plaintext';
            return hljs.highlight(code, { language }).value;
        }
    });

    let markdownContent = '';
    let textareaRef;
    let previewRef;
    let activeButton = '';
    let textareaWidth = '50%';
    let previewWidth = '50%';

    const handleInput = (event) => {
        markdownContent = event.target.value;
        syncScroll(); 
        adjustTextareaHeight();
    };

    const adjustTextareaHeight = () => {
        if (!textareaRef) return;
        textareaRef.style.height = 'auto';
        textareaRef.style.height = `${textareaRef.scrollHeight}px`;
    };

    const syncScroll = () => {
        if (!previewRef) return;
        const textareaScrollTop = textareaRef.scrollTop;
        const textareaScrollHeight = textareaRef.scrollHeight - textareaRef.clientHeight;
        const previewScrollHeight = previewRef.scrollHeight - previewRef.clientHeight;
        const scrollPercentage = textareaScrollTop / textareaScrollHeight;
        previewRef.scrollTop = scrollPercentage * previewScrollHeight;
    };

    const handlePost = () => {
        alert("게시 완료!");
    };

    const handleCancel = () => {
        markdownContent = '';
        adjustTextareaHeight();
    };
    
    const handleKeyDown = (event) => {
        if (event.key === 'Tab') {
            event.preventDefault();
            const start = textareaRef.selectionStart;
            const end = textareaRef.selectionEnd;

            markdownContent = markdownContent.substring(0, start) + '\t' + markdownContent.substring(end);
            textareaRef.value = markdownContent;
            textareaRef.selectionStart = textareaRef.selectionEnd = start + 1;
            adjustTextareaHeight();
        }
    };

    const toggleView = (view) => {
        if (activeButton === view) {
            activeButton = '';
            textareaWidth = '50%';
            previewWidth = '50%';
        } else {
            activeButton = view;
            if (view === 'textarea') {
                textareaWidth = '100%';
                previewWidth = '0%';
                adjustTextareaHeight();
            } else if (view === 'preview') {
                previewWidth = '100%';
                textareaWidth = '0%';
            }
        }
    };
</script>

<div class="container">
    {#if activeButton === 'textarea' || activeButton === ''}
        <textarea 
            bind:this={textareaRef} 
            bind:value={markdownContent} 
            on:input={handleInput} 
            on:keydown={handleKeyDown} 
            style="width: {textareaWidth};"
            placeholder="Write your markdown here..."></textarea>
    {/if}
    
    {#if activeButton === 'preview' || activeButton === ''}
        <div class="preview" bind:this={previewRef} style="width: {previewWidth};">
            {@html marked(markdownContent)}
        </div>
    {/if}
</div>

<div class="button-container">
    <button 
        class="toggleBtn {activeButton === 'textarea' ? 'active' : ''}" 
        on:click={() => toggleView('textarea')}>
        Textarea
    </button>
    <button 
        class="toggleBtn {activeButton === 'preview' ? 'active' : ''}" 
        on:click={() => toggleView('preview')}>
        Preview
    </button>
</div>

<div class="buttons">
    <button class="postBtn" on:click={handlePost}>
        <i class="fas fa-paper-plane"></i> 게시
    </button>
    <button class="cancelBtn" on:click={handleCancel}>
        <i class="fas fa-times"></i> 취소
    </button>
</div>

<style>
    .container {
        display: flex;
        height: calc(100% - 60px);
        width: 100%;
        padding: 0;
        position: relative;
    }
    textarea {
        padding: 20px;
        font-size: 16px;
        resize: none;
        overflow: hidden;
        box-sizing: border-box;
        border-radius: 20px;
        height: auto;
    }
    .preview {
        padding: 20px;
        overflow-y: auto;
        box-sizing: border-box;
        border-left: 1px solid #ccc;
        background-color: #f7f7f7;
        border-radius: 20px;
    }
    .preview :global(pre) {
        background: #f5f5f5;
        padding: 10px;
        border-radius: 5px;
        overflow-x: auto;
    }
    .preview :global(code) {
        background: #eee;
        padding: 2px 4px;
        border-radius: 3px;
    }
    .button-container {
        position: fixed;
        bottom: 20px;
        left: 20px;
        display: flex;
        gap: 10px;
        z-index: 1000;
    }
    .toggleBtn {
        padding: 10px 15px;
        font-size: 16px;
        cursor: pointer;
        border: 1px solid #ccc;
        border-radius: 5px;
        background-color: white;
        transition: background-color 0.3s;
    }
    .toggleBtn.active {
        background-color: #007bff;
        color: white;
    }
    .buttons {
        position: fixed;
        bottom: 20px;
        right: 20px;
        display: flex;
        gap: 10px;
        z-index: 1000;
    }
    .buttons button {
        padding: 10px 15px;
        font-size: 16px;
        cursor: pointer;
        border: none;
        border-radius: 5px;
        color: white;
        transition: background-color 0.3s;
    }
    .buttons button i {
        margin-right: 5px;
    }
    .postBtn {
        background-color: #007bff;
    }
    .postBtn:hover {
        background-color: #0056b3;
    }
    .cancelBtn {
        background-color: #ff0000;
    }
    .cancelBtn:hover {
        background-color: #b30000;
    }
</style>
