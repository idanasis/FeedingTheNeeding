import { Donor } from "../Driving/models/Donor";
import { DriverConstraints } from "../Driving/models/DriverConstraints";
import { Route } from "../Driving/models/Route";
import { Visit } from "../Driving/models/Visit";
import { NeederTrackingProjectionModel } from "../models/NeederTrackingProjectionModel";
import { socialUrl } from "./socialRestapi";
import axios from 'axios';


const drivingUrl="http://localhost:8080/driving"
const userUrl="http://localhost:8080/user"
const cookingUrl="http://localhost:8080/cooking"
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
    console.log(response)
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
        startTime:"0",
        maxHour:0,
        status:"Deliver",
        additionalNotes :neederTracking.needyFamilySize +" "+neederTracking.dietaryPreferences
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

export const getPickupVisits = async (date: Date) => {
    const response = await axios.get(cookingUrl+"/getAccepted/"+date.toISOString().split('T')[0],
    {headers: { 'Content-Type': 'application/json',Authorization: 'Bearer ' + localStorage.getItem('token')}});
    let res=response.data as Visit[];
    console.log(res);
    res=res.map((visit:Visit)=>transformCookingConstraintProjectionToVisit(visit));
    return res;
}

const transformCookingConstraintProjectionToVisit=(visit:Visit)=>{
    const constraintsArray = Array.isArray(visit.constraints)
        ? visit.constraints
        : Object.entries(visit.constraints || {}); // Convert object to array of [key, value]

    // Create a string in "key value key value..." format
    const constraintsString = constraintsArray
        .map(([key, value]) => `${value} ${key}`)
        .join(' ');
    return {
        firstName:visit.name!.split(' ')[0],
        lastName:visit.name!.split(' ')[1],
        phoneNumber:visit.phoneNumber,
        address:visit.address,
        additionalNotes:constraintsString,
        notes: "אין",
        startTime:visit.startTime,
        maxHour:Number.parseInt(visit.endTime!),
        status:"Pickup",
    }
}