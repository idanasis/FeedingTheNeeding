import { Donor } from "../Driving/models/Donor";
import { DriverConstraints } from "../Driving/models/DriverConstraints";
import { Route } from "../Driving/models/Route";
import { NeederTrackingModel } from "../models/NeederTrackingModel";
import { NeederTrackingProjectionModel } from "../models/NeederTrackingProjectionModel";
import { socialUrl } from "./socialRestapi";
import axios from 'axios';


const drivingUrl="http://localhost:8080/driving"
const userUrl="http://localhost:8080/user"
export const addRoute = async (date: Date) => {
    const response =  await axios.post(drivingUrl+"/routes/create"+"?date="+date.toISOString().split('T')[0],{},
    {headers: { 'Content-Type': 'application/json',Authorization: 'Bearer ' + localStorage.getItem('token')}});
    console.log(response);
    return response.data as Route;
}
export const getRoutes=async (date: Date)=>{
    const response =  await axios.get(drivingUrl+"/routes/getRoutes?date="+date.toISOString().split('T')[0],
    {headers: { 'Content-Type': 'application/json',Authorization: 'Bearer ' + localStorage.getItem('token')}});
    return response.data as Route[];
}
export const setDriverIdToRoute=async (routeId:number,driverId:number)=>{
    const response =  await axios.put(drivingUrl+"/routes/"+routeId+"/driver/"+driverId,{},
    {headers: { 'Content-Type': 'application/json',Authorization: 'Bearer ' + localStorage.getItem('token')}});
    return response.data as Route;
}
export const getDriversConstraints=async (date: Date)=>{
    const response =  await axios.get(drivingUrl+"/constraints/"+date.toISOString().split('T')[0],
    {headers: { 'Content-Type': 'application/json',Authorization: 'Bearer ' + localStorage.getItem('token')}});
    const driverId= response.data as DriverConstraints[];
    let drivers=[];
    for (let i = 0; i < driverId.length; i++) {
        let driver= await getDonor(driverId[i].driverId);
        driver.requests=driverId[i].requests;
        drivers.push(driver);
    }
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
    return {
        firstName:neederTracking.needyFirstName,
        lastName:neederTracking.needyLastName,
        phoneNumber:neederTracking.needyPhoneNumber,
        address:neederTracking.needyAddress,
        notes:neederTracking.additionalNotes,
        maxHour:0,
        status:"Pickup"
    }
}
export const updateRoute=async(route:Route)=>{
   await axios.patch(drivingUrl+"/routes/updateRoute",route,
    {headers: { 'Content-Type': 'application/json',Authorization: 'Bearer ' + localStorage.getItem('token')}});
}
