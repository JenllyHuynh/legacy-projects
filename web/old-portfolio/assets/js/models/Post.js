class Post {
    constructor(data) {
        this.id         = data.id;
        this.title      = data.title;
        this.subtitle   = data.subtitle || '';
        this.excerpt    = data.excerpt  || '';
        this.content    = data.content;
        this.author     = data.author;
        this.date       = new Date(data.date);
        this.thumbnail  = data.thumbnail;
        this.isFeatured = data.isFeatured || false;
        this.category   = data.category   || 'general';
    }

    getFormattedDate() {
        const options = { year: 'numeric', month: 'long', day: 'numeric' };
        return this.date.toLocaleDateString('en-US', options).toUpperCase();
    }

    getShortExcerpt(maxLength = 100) {
        if (this.excerpt.length <= maxLength) return this.excerpt;
        return this.excerpt.substring(0, maxLength) + '...';
    }
}
