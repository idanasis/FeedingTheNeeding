import React,{useEffect, useState} from 'react';
import './SocialCss/NeedTracking.css';
import CollapsibleTable from './CollapsibleTable';
import { NeederTrackingModel } from '@/src/models/NeederTrackingModel';
import ResponsiveDatePickers from './ResponsiveDatePickers';
import dayjs from 'dayjs';
import { getAllNeederTracking } from '../../restapi';

const NeederTracking = () => {
    const [currNeeders, setCurrNeeders] = useState<NeederTrackingModel[]>([]);
    useEffect(() => {
        async function fetchNeeders() {
            const data=await getAllNeederTracking(new Date());
            setCurrNeeders(data)
        }
        fetchNeeders();
    }, []);
    const handleDateChange = (newDate:dayjs.Dayjs|null ) => {
        const d =newDate===null?dayjs(Date.now()).toDate():newDate.toDate();
        console.log('Selected date:', newDate);
        async function fetchNeeders() {
            const data=await getAllNeederTracking(d as Date);
            setCurrNeeders(data)
        }
        fetchNeeders();
      };

    return (
        <div >
            <h1 className='text-black md:text-4xl text-3xl font-bold'>טבלת מעקב נזקקים</h1>
            <ResponsiveDatePickers onDateChange={handleDateChange}/>  
        <CollapsibleTable data={currNeeders} /> 
         
        </div>
    );
};

export default NeederTracking;
