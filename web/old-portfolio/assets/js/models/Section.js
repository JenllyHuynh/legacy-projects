class Section {
    constructor(data) {
        this.id       = data.id;
        this.type     = data.type;
        this.color    = data.color;
        this.position = data.position;
        this.title    = data.title || '';
    }

    getColorClass() {
        return `border-${this.color}`;
    }
}