import axios from 'axios';


const API = 'http://localhost:8080/orchids'
 export const getAllList= async () => {
    const respone =await axios.get(API);
    return respone.data;

 }
 export const deleteOrchid =async (id) => {
    const response = await axios.delete(`${API}/${id}`);
    return response.data;
 }