import { Donor } from "../models/DonorModel";
import axios from 'axios';
const backendUrl = import.meta.env.VITE_BACKEND_URL;


const apiUrl=`${backendUrl}/user`
export const getPendingDonors = async (): Promise<Donor[]> => {
    console.log('getPendingDonors')
    const response = await fetch(`${apiUrl}/donor/pending`,{headers: { 'Content-Type': 'application/json',Authorization: 'Bearer ' + localStorage.getItem('token')}});
    return await response.json() as Donor[];
}

export const updateDonor = async (donor:Donor): Promise<void> => {
    console.log("updateDonor restapi ")
    console.log(donor)
    await axios.put(`${apiUrl}/donor`,donor,{headers: { 'Content-Type': 'application/json',Authorization: 'Bearer ' + localStorage.getItem('token')}});
}

export const deleteDonor = async (donor:Donor): Promise<void> => {
    await axios.delete(`${apiUrl}/donor/${donor.id}`,{headers: { 'Content-Type': 'application/json',Authorization: 'Bearer ' + localStorage.getItem('token')}});
}

export const getDonorApproved = async (): Promise<Donor[]> => {
    const response=await axios.get(`${apiUrl}/donor/approved`,{headers: { 'Content-Type': 'application/json',Authorization: 'Bearer ' + localStorage.getItem('token')}});
    return await response.data as Donor[];
}