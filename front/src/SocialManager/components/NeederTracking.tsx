import React,{useEffect, useState} from 'react';
import '../Styles/NeedTracking.css';
import CollapsibleTable from './CollapsibleTable';
import { NeederTrackingModel } from '@/src/commons/models/NeederTrackingModel';
import ResponsiveDatePickers from './ResponsiveDatePickers';
import dayjs from 'dayjs';
import { getAllNeederTracking } from '../../commons/Restapi/socialRestapi';
import { getNearestFriday } from '../../commons/Commons';
import DiveHeader from '../../GoPage/DiveHeader';


const NeederTracking = () => {
    const [currNeeders, setCurrNeeders] = useState<NeederTrackingModel[]>([]);
    useEffect(() => {
        async function fetchNeeders() {
            const data=await getAllNeederTracking(getNearestFriday(dayjs(Date.now())).toDate());;
            if(data===null)
                alert('אין נתונים להצגה')
            else
                setCurrNeeders(data)
        }
        fetchNeeders();
    }, []);
    const handleDateChange = (newDate:dayjs.Dayjs|null ) => {
        const d =newDate===null?dayjs(Date.now()).toDate():newDate.toDate();
        console.log('Selected date:', newDate);
        async function fetchNeeders() {
            const data=await getAllNeederTracking(d as Date);
            if(data===null)
                alert('אין נתונים להצגה')
            else
                setCurrNeeders(data)
        }
        fetchNeeders();
      };

    return (
        <div className='social-need-tracking' style={{}}>
            <DiveHeader/>
            <div style={{paddingRight:"1rem"}}>
            <ResponsiveDatePickers onDateChange={handleDateChange}/>  
            </div>
        <CollapsibleTable data={currNeeders}/> 
        </div>
    );
};

export default NeederTracking;
