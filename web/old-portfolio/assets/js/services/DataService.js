class DataService {
    constructor() {
        this.profile = null;
        this.background = null;
        this.posts = [];
        this.sections = null;
    }

    async loadAllData() {
        try {
            const [profile, background, posts, sections] = await Promise.all([
                this.loadJSON(AppConfig.DATA_PATHS.PROFILE),
                this.loadJSON(AppConfig.DATA_PATHS.BACKGROUND),
                this.loadJSON(AppConfig.DATA_PATHS.POSTS),
                this.loadJSON(AppConfig.DATA_PATHS.SECTIONS)
            ]);
            
            this.profile = profile;
            this.background = background;
            this.posts = posts;
            this.sections = sections;
            
            return true;
        } catch (error) {
            console.error('Error loading data:', error);
            throw error;
        }
    }

    async loadJSON(path) {
        try {
            const response = await fetch(path);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return await response.json();
        } catch (error) {
            console.error(`Error loading ${path}:`, error);
            // Return fallback data
            return this.getFallbackData(path);
        }
    }

    getFallbackData(path) {
        // Provide fallback data if files can't be loaded
        const fallbacks = {
            'data/profile.json': {
                name: "Jenlly Huỳnh",
                title: "Full-Stack Developer & Designer",
                bio: "Passionate developer creating amazing web experiences",
                socialLinks: [
                    { platform: "github", url: "#", icon: "fab fa-github" },
                    { platform: "linkedin", url: "#", icon: "fab fa-linkedin" },
                    { platform: "twitter", url: "#", icon: "fab fa-twitter" }
                ]
            },
            'data/background.json': {
                title: "My Background",
                description: "Passionate developer with expertise in modern web technologies.",
                skills: ["JavaScript", "React", "Node.js", "CSS3"],
                lastUpdated: new Date().toISOString()
            },
            'data/posts.json': [
                {
                    id: "featured",
                    title: "Building Modern Web Applications",
                    excerpt: "Exploring the latest trends and techniques in web development for 2025.",
                    content: "Full content here...",
                    featured: true,
                    readTime: 10,
                    publishDate: "2025-11-06",
                    category: "Development"
                },
                {
                    id: "responsive-design",
                    title: "Responsive Design Mastery",
                    excerpt: "Learn how to create websites that look great on all devices.",
                    content: "Full content here...",
                    featured: false,
                    readTime: 7,
                    publishDate: "2025-10-15",
                    category: "Tutorial"
                }
            ],
            'data/sections-config.json': {
                layout: {
                    profile: { order: 1, visible: true },
                    background: { order: 2, visible: true },
                    featured: { order: 3, visible: true },
                    recent: { order: 4, visible: true }
                }
            }
        };
        
        return fallbacks[path] || {};
    }

    getProfile() {
        return this.profile;
    }

    getBackground() {
        return this.background;
    }

    getPosts() {
        return this.posts;
    }

    getSections() {
        return this.sections;
    }
}