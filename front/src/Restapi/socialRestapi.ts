import { NeederTrackingModel } from "../models/NeederTrackingModel";
import axios from 'axios';
const neederUrl="http://localhost:8080/needer"
export const getAllNeederTracking= async (date: Date) => {
    try{
        const response =  await axios.get(neederUrl+"/needy-tracking?date="+date.toISOString().split('T')[0],
    {headers: { 'Content-Type': 'application/json',Authorization: 'Bearer ' + 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkZGRkZGRkZGRkQGRqZGQiLCJpYXQiOjE3MzQ2MTg4MDIsImV4cCI6MTczNDcwNTIwMn0.cryDc3ElVLx0g10gAGP_PgHEn-TWWkywKTBa82kJ37c'}});
        return response.data as NeederTrackingModel[];
    }catch(err){
        console.error(err);
        return null;
    }
}
export const updateNeederTracking= async (id:number,neederTrackingModel:NeederTrackingModel) => {
    neederTrackingModel.weekStatus=neederTrackingModel.weekStatus==="זמין"?"Here":"NotHere";
    console.log(neederTrackingModel.weekStatus);
    console.log(id);
    const response =  await axios.put("http://localhost:8080/social/"+id,neederTrackingModel,
    {headers: { 'Content-Type': 'application/json',Authorization: 'Bearer ' + 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkZGRkZGRkZGRkQGRqZGQiLCJpYXQiOjE3MzQ2MTg4MDIsImV4cCI6MTczNDcwNTIwMn0.cryDc3ElVLx0g10gAGP_PgHEn-TWWkywKTBa82kJ37c'}});
    return response;
}