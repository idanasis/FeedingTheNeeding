import { Donor } from "../Driving/models/Donor";
import { DriverConstraints } from "../Driving/models/DriverConstraints";
import { Route } from "../Driving/models/Route";
import { NeederTrackingProjectionModel } from "../models/NeederTrackingProjectionModel";
import { socialUrl } from "./socialRestapi";
import axios from 'axios';


const drivingUrl="http://localhost:8080/driving"
const userUrl="http://localhost:8080/user"
export const addRoute = async (date: Date) => {
    const response =  await axios.post(drivingUrl+"/routes/create"+"?date="+date.toISOString().split('T')[0],{},
    {headers: { 'Content-Type': 'application/json',Authorization: 'Bearer ' + localStorage.getItem('token')}});
    return response.data as Route;
}
export const getRoutes=async (date: Date)=>{
    const response =  await axios.get(drivingUrl+"/routes/getRoutes?date="+date.toISOString().split('T')[0],
    {headers: { 'Content-Type': 'application/json',Authorization: 'Bearer ' + localStorage.getItem('token')}});
    const res=response.data as Route[];
    return res;
}
export const setDriverIdToRoute=async (routeId:number,driverId:number)=>{
    const response =  await axios.put(drivingUrl+"/routes/"+routeId+"/driver/"+driverId,{},
    {headers: { 'Content-Type': 'application/json',Authorization: 'Bearer ' + localStorage.getItem('token')}});
    return response.data as Route;
}
export const getDriversConstraints=async (date: Date)=>{
    const response =  await axios.get(drivingUrl+"/constraints/"+date.toISOString().split('T')[0],
    {headers: { 'Content-Type': 'application/json',Authorization: 'Bearer ' + localStorage.getItem('token')}});
    const drivers= response.data as DriverConstraints[];
    return drivers;
}
export const getDonor=async(id:number)=>{
    const response =  await axios.get(userUrl+"/donor/donorId/"+id,
        {headers: { 'Content-Type': 'application/json',Authorization: 'Bearer ' + localStorage.getItem('token')}}); 
        return response.data as Donor;
}

export const getNeedersHere=async (date: Date)=>{
    const response =  await axios.get(socialUrl+"getNeedersHere?date="+date.toISOString().split('T')[0],
    {headers: { 'Content-Type': 'application/json',Authorization: 'Bearer ' + localStorage.getItem('token')}});
    const neederTracking= response.data as NeederTrackingProjectionModel[];
    return neederTracking.map((neederTracking:NeederTrackingProjectionModel)=>transformNeederTrackingProjectionToVisit(neederTracking));
}

const transformNeederTrackingProjectionToVisit=(neederTracking:NeederTrackingProjectionModel)=>{
    console.log(neederTracking);
    return {
        firstName:neederTracking.needyFirstName,
        lastName:neederTracking.needyLastName,
        phoneNumber:neederTracking.needyPhoneNumber,
        address:neederTracking.needyAddress,
        notes:neederTracking.additionalNotes,
        maxHour:0,
        status:"Deliver",
        dietaryPreferences:neederTracking.dietaryPreferences
    }
}
export const updateRoute=async(route:Route)=>{
   await axios.patch(drivingUrl+"/routes/updateRoute",route,
    {headers: { 'Content-Type': 'application/json',Authorization: 'Bearer ' + localStorage.getItem('token')}});
}

export const submitRoute=async(route:Route)=>{
    await axios.post(drivingUrl+"/routes/submit/"+route.routeId,{},
     {headers: { 'Content-Type': 'application/json',Authorization: 'Bearer ' + localStorage.getItem('token')}});
    }

export const submitAllRoutes=async(date:Date)=>{
    await axios.post(drivingUrl+"/routes/submitAll/"+date.toISOString().split('T')[0],{},
     {headers: { 'Content-Type': 'application/json',Authorization: 'Bearer ' + localStorage.getItem('token')}});
}

export const getDonorApproved = async (): Promise<Donor[]> => {
    const response=await axios.get(`${userUrl}/donor/approved`,{headers: { 'Content-Type': 'application/json',Authorization: 'Bearer ' + localStorage.getItem('token')}});
    return await response.data as Donor[];
}

export const addDriverConstraints = async (driverConstraints: DriverConstraints) => {
    const response = await axios.post(drivingUrl+"/constraints", driverConstraints,
    {headers: { 'Content-Type': 'application/json',Authorization: 'Bearer ' + localStorage.getItem('token')}});
    return response.data as DriverConstraints;
}

export const deleteRoute = async (routeId: number) => {
    await axios.delete(drivingUrl+"/routes/"+routeId,
    {headers: { 'Content-Type': 'application/json',Authorization: 'Bearer ' + localStorage.getItem('token')}});
}