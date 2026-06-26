class AppConfig {
    static get API_BASE_URL() {
        return ''; // Empty for local files, or your API endpoint
    }
    
    static get DATA_PATHS() {
        return {
            PROFILE: 'data/profile.json',
            BACKGROUND: 'data/background.json',
            POSTS: 'data/posts.json',
            SECTIONS: 'data/sections-config.json'
        };
    }
    
    static get UI_CONFIG() {
        return {
            RECENT_POSTS_LIMIT: 3,
            FEATURED_POST_ID: 'featured', // ID of featured post
            ANIMATION_DURATION: 300
        };
    }
}