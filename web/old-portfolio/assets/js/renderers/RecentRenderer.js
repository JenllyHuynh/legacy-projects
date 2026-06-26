class RecentRenderer {
    constructor() {
        this.container = document.getElementById('recent-posts-container');
    }

    render(posts) {
        if (!this.container || !posts || posts.length === 0) {
            console.warn('Recent posts container not found or no posts to render');
            return;
        }

        const postsHTML = posts.map(post => this.createPostHTML(post)).join('');
        this.container.innerHTML = postsHTML;
        
        // Add event listeners for post interactions
        this.addPostEventListeners();
    }

    createPostHTML(post) {
        return `
            <article class="blog-post" data-post-id="${post.id}">
                <div class="blog-image">
                    <div class="category-tag">${post.category || 'General'}</div>
                </div>
                <div class="blog-content">
                    <h3>${post.title}</h3>
                    <p>${post.excerpt}</p>
                    <div class="blog-meta">
                        <span><i class="far fa-clock"></i> ${post.readTime || 5} min read</span>
                    </div>
                </div>
            </article>
        `;
    }

    addPostEventListeners() {
        const postElements = this.container.querySelectorAll('.blog-post');
        postElements.forEach(postElement => {
            postElement.addEventListener('click', (e) => {
                const postId = postElement.getAttribute('data-post-id');
                this.navigateToPost(postId);
            });
        });
    }

    navigateToPost(postId) {
        // Navigate to post detail page
        window.location.href = `post.html?id=${postId}`;
    }
}