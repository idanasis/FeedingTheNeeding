import { Route } from "../Driving/models/Route";
import axios from 'axios';

const drivingUrl="http://localhost:8080/driving"
export const addRoute = async (date: Date) => {
    const response =  await axios.post(drivingUrl+"/routes/create",{date: date},
    {headers: { 'Content-Type': 'application/json',Authorization: 'Bearer ' + localStorage.getItem('token')}});
    return response.data as Route;
}
export const setDriverIdToRoute=async (routeId:number,driverId:number)=>{
    const response =  await axios.put(drivingUrl+"/routes/"+routeId+"/driver/"+driverId,
    {headers: { 'Content-Type': 'application/json',Authorization: 'Bearer ' + localStorage.getItem('token')}});
    return response.data as Route;
}
export const getDrivers=async (date: Date)=>{
    const response =  await axios.get(drivingUrl+"/constraints"+date.toISOString(),
    {headers: { 'Content-Type': 'application/json',Authorization: 'Bearer ' + localStorage.getItem('token')}});
    return response.data as number[];
}