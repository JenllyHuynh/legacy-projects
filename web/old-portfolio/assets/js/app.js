class BlogApp {
    constructor() {
        this.dataService = new DataService();
        this.postService = new PostService();
        this.sectionService = new SectionService();
        
        this.profileRenderer = new ProfileRenderer();
        this.backgroundRenderer = new BackgroundRenderer();
        this.featuredRenderer = new FeaturedRenderer();
        this.recentRenderer = new RecentRenderer();
        
        this.init();
    }

    async init() {
        try {
            // Load configuration and data
            await this.dataService.loadAllData();
            
            // Initialize services with loaded data
            this.postService.setPosts(this.dataService.getPosts());
            
            // Render all sections
            this.renderProfile();
            this.renderBackground();
            this.renderFeaturedPost();
            this.renderRecentPosts();
            
            // Initialize UI interactions
            this.initUI();
            
            console.log('Blog app initialized successfully');
        } catch (error) {
            console.error('Failed to initialize blog app:', error);
        }
    }

    renderProfile() {
        const profileData = this.dataService.getProfile();
        this.profileRenderer.render(profileData);
    }

    renderBackground() {
        const backgroundData = this.dataService.getBackground();
        this.backgroundRenderer.render(backgroundData);
    }

    renderFeaturedPost() {
        const featuredPost = this.postService.getFeaturedPost();
        this.featuredRenderer.render(featuredPost);
    }

    renderRecentPosts() {
        const recentPosts = this.postService.getRecentPosts(3);
        this.recentRenderer.render(recentPosts);
    }

    initUI() {
        // Mobile menu toggle
        this.initMobileMenu();
        
        // Scroll effects
        this.initScrollEffects();
        
        // Set current date
        this.setCurrentDate();
    }

    initMobileMenu() {
        const hamburger = document.getElementById('hamburger');
        const navMenu = document.getElementById('nav-menu');
        
        if (hamburger && navMenu) {
            hamburger.addEventListener('click', () => {
                hamburger.classList.toggle('active');
                navMenu.classList.toggle('active');
            });
            
            // Close menu when clicking on links
            const navLinks = navMenu.querySelectorAll('a');
            navLinks.forEach(link => {
                link.addEventListener('click', () => {
                    hamburger.classList.remove('active');
                    navMenu.classList.remove('active');
                });
            });
        }
    }

    initScrollEffects() {
        window.addEventListener('scroll', () => {
            const header = document.querySelector('header');
            if (header) {
                header.classList.toggle('scrolled', window.scrollY > 50);
            }
        });
    }

    setCurrentDate() {
        const dateElement = document.getElementById('current-date');
        if (dateElement) {
            const formattedDate = DateFormatter.formatDate(new Date());
            dateElement.textContent = formattedDate;
        }
    }
}

// Initialize app when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new BlogApp();
});