import { apiClient } from './apiClient';
export async function getOrchidsByAxios() {
    const response = await apiClient.get('/orchids.json');
    return response.data;
}