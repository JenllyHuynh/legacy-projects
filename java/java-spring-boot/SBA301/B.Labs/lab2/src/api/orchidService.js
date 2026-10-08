const orchidService = {
    async getOrchids() {
        const response = await fetch('/orchids.json', { headers: { Accept: 'application/json' } });
        if (!response.ok) throw new Error(`HTTP ${response.status}: Không thể tải orchids.json`);
        return response.json();
    }
};
export default orchidService;