import { Needy } from "../models/NeedyModel";
import axios from 'axios';
const backendUrl = import.meta.env.VITE_BACKEND_URL;

const apiUrl=`${backendUrl}/needer`

export const updateNeedy = async (needy: Needy): Promise<void> => {
    await axios.patch(apiUrl+"/"+needy.id,needy,{headers: { 'Content-Type': 'application/json',Authorization: 'Bearer ' + localStorage.getItem('token')}});
}

export const getNeedyList = async (): Promise<Needy[]> => {
    const response=await axios.get(apiUrl+"/accepted",{headers: { 'Content-Type': 'application/json',Authorization: 'Bearer ' + localStorage.getItem('token')}});
    return await response.data as Needy[];
}

export const deleteNeedy = async (needy: Needy): Promise<void> => {
    await axios.delete(apiUrl+"/"+needy.id,{headers: { 'Content-Type': 'application/json',Authorization: 'Bearer ' + localStorage.getItem('token')}});
}

export const registerNeedy = async (needy: Needy): Promise<void> => {
    await axios.post(apiUrl,needy,{headers: { 'Content-Type': 'application/json',Authorization: 'Bearer ' + localStorage.getItem('token')}});
}