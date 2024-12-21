import { NeederTrackingModel } from "../models/NeederTrackingModel";
import axios from 'axios';
import { Needy } from "../models/NeedyModel";
const neederUrl="http://localhost:8080/needer"
const socialUrl="http://localhost:8080/social/"
export const getAllNeederTracking= async (date: Date) => {
    try{
        const response =  await axios.get(neederUrl+"/needy-tracking?date="+date.toISOString().split('T')[0],
    {headers: { 'Content-Type': 'application/json',Authorization: 'Bearer ' + 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkZGRkZGRkZGRkQGRqZGQiLCJpYXQiOjE3MzQ3OTgzNzMsImV4cCI6MTczNDg4NDc3M30.J8pYSo52M9-btPzNc6U4yL-5ua-Rbikr2UZAdNpbhL0'}});
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
    const response =  await axios.put(socialUrl+id,neederTrackingModel,
    {headers: { 'Content-Type': 'application/json',Authorization: 'Bearer ' + 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkZGRkZGRkZGRkQGRqZGQiLCJpYXQiOjE3MzQ3OTgzNzMsImV4cCI6MTczNDg4NDc3M30.J8pYSo52M9-btPzNc6U4yL-5ua-Rbikr2UZAdNpbhL0'}});
    return response;
}
export const getNeedyPending= async () => {
    const response =  await axios.get(neederUrl+"/pending",
    {headers: { 'Content-Type': 'application/json',Authorization:'Bearer ' + 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkZGRkZGRkZGRkQGRqZGQiLCJpYXQiOjE3MzQ3OTgzNzMsImV4cCI6MTczNDg4NDc3M30.J8pYSo52M9-btPzNc6U4yL-5ua-Rbikr2UZAdNpbhL0'}});
    return response.data as Needy[];
}

export const updateNeedy= async (needy:Needy) => {
    console.log(needy);
    await axios.post(neederUrl,needy,
    {headers: { 'Content-Type': 'application/json',Authorization:'Bearer ' + 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkZGRkZGRkZGRkQGRqZGQiLCJpYXQiOjE3MzQ3OTgzNzMsImV4cCI6MTczNDg4NDc3M30.J8pYSo52M9-btPzNc6U4yL-5ua-Rbikr2UZAdNpbhL0'}});
}

export const deleteNeedy= async (needy:Needy) => {
    await axios.delete(neederUrl+"/"+needy.id,
    {headers: { 'Content-Type': 'application/json',Authorization:'Bearer ' + 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkZGRkZGRkZGRkQGRqZGQiLCJpYXQiOjE3MzQ3OTgzNzMsImV4cCI6MTczNDg4NDc3M30.J8pYSo52M9-btPzNc6U4yL-5ua-Rbikr2UZAdNpbhL0'}});
}