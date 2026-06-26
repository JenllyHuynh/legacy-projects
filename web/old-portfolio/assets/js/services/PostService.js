class PostService {
    constructor() {
        this.posts = [];
    }

    setPosts(posts) {
        this.posts = posts || [];
    }

    getFeaturedPost() {
        return this.posts.find(post => post.featured) || this.posts[0];
    }

    getRecentPosts(limit = 3) {
        return this.posts
            .filter(post => !post.featured)
            .sort((a, b) => new Date(b.publishDate) - new Date(a.publishDate))
            .slice(0, limit);
    }

    getPostById(id) {
        return this.posts.find(post => post.id === id);
    }

    getPostsByCategory(category) {
        return this.posts.filter(post => post.category === category);
    }
}
