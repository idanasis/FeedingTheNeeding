import { Donor } from "../Driving/models/Donor";
import { DriverConstraints } from "../Driving/models/DriverConstraints";
import { Route } from "../Driving/models/Route";
import axios from 'axios';

const drivingUrl="http://localhost:8080/driving"
const userUrl="http://localhost:8080/user"
export const addRoute = async (date: Date) => {
    const response =  await axios.post(drivingUrl+"/routes/create"+"?date="+date.toISOString().split('T')[0],{},
    {headers: { 'Content-Type': 'application/json',Authorization: 'Bearer ' + localStorage.getItem('token')}});
    return response.data as Route;
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
