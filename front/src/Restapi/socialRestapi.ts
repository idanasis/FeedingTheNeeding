import { NeederTrackingModel } from "../models/NeederTrackingModel";
import axios from 'axios';
const neederUrl="http://localhost:8080/needer"
export const getAllNeederTracking= async (date: Date) => {
    try{
        const response =  await axios.get("http://localhost:8080/needer/needy-tracking?date=2024-12-15",
    {headers: { 'Content-Type': 'application/json',Authorization: 'Bearer ' + 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkZGRkZGRkZGRkQGRqZGQiLCJpYXQiOjE3MzQyODU2NDYsImV4cCI6MTczNDM3MjA0Nn0._T2ggG5dp4tARC5xzoXp9zLbGBLbcDc0Qcks8ta05ac'}});
        return response.data as NeederTrackingModel[];
    }catch(err){
        console.error(err);
        return null;
    }
}