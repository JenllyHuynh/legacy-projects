import axios from 'axios';

const API = 'http://localhost:8080/orchids';

export const getAllList = async () => {
    const response = await axios.get(API);
    return response.data;
};

export const searchOrchidByName = async (name) => {
    const response = await axios.get(`${API}/search`, { params: { name } });
    return response.data;
};

export const addOrchid = async (orchid) => {
    const response = await axios.post(API, orchid);
    return response.data;
};

export const updateOrchid = async (id, orchid) => {
    const response = await axios.put(`${API}/${id}`, orchid);
    return response.data;
};

export const deleteOrchid = async (id) => {
    const response = await axios.delete(`${API}/${id}`);
    return response.data;
};
