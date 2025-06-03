import { NeederTrackingModel } from "../models/NeederTrackingModel";
import axios from 'axios';
import { Needy } from "../models/NeedyModel";
const backendUrl = import.meta.env.VITE_BACKEND_URL;

const neederUrl=`${backendUrl}/needer`
export const socialUrl=`${backendUrl}/social/`
export const getAllNeederTracking= async (date: Date) => {
    try{
        const response =  await axios.get(neederUrl+"/needy-tracking?date="+date.toISOString().split('T')[0],
    {headers: { 'Content-Type': 'application/json',Authorization: 'Bearer ' + localStorage.getItem('token')}});
        return response.data as NeederTrackingModel[];
    }catch(err){
        console.error(err);
        return null;
    }
}
export const updateNeederTracking= async (id:number,neederTrackingModel:NeederTrackingModel) => {
    neederTrackingModel.weekStatus=neederTrackingModel.weekStatus==="זמין"?"Here":"NotHere";
    const response =  await axios.put(socialUrl+id,neederTrackingModel,
    {headers: { 'Content-Type': 'application/json',Authorization: 'Bearer ' +localStorage.getItem('token')}});
    return response;
}
export const getNeedyPending= async () => {
    const response =  await axios.get(neederUrl+"/pending",
    {headers: { 'Content-Type': 'application/json',Authorization:'Bearer ' + localStorage.getItem('token')}});
    return response.data as Needy[];
}

export const updateNeedy= async (needy:Needy) => {
   // console.log(needy);
    await axios.post(neederUrl,needy,
    {headers: { 'Content-Type': 'application/json',Authorization:'Bearer ' + localStorage.getItem('token')}});
}

export const deleteNeedy= async (needy:Needy) => {
    await axios.delete(neederUrl+"/"+needy.id,
    {headers: { 'Content-Type': 'application/json',Authorization:'Bearer ' + localStorage.getItem('token')}});
}

export const acceptNeedy = async (id: number) => {
    try {
        const response = await axios.post(`${neederUrl}/${id}`, {}, {
            headers: {
                'Content-Type': 'application/json',
                Authorization: 'Bearer ' + localStorage.getItem('token')
            }
        });
        return response.data as Needy;
    } catch (err) {
        console.error('Error accepting needy:', err);
        return null;
    }
};
