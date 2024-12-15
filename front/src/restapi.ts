import { NeederTrackingModel } from "./models/NeederTrackingModel";

export const getAllNeederTracking= async (date: Date) => {
    if(date.getMonth()===11){
    const myData:NeederTrackingModel[] = [
            {id:1,neederTrackingId:1, firstName: 'יוסי', lastName: 'כהן', address: 'רגר 21', city: 'באר שבע', familySize: 4, status: 'זמין', foodPreference: 'צמחוני', notes: 'אאאאאאאאאאאאאא אאאאאאאאאאאאאאאאא אאאאאאאאאאאאאאאאאאאאאאאאאאאין', phoneNumber: '052-3072999' },
            {id:2,neederTrackingId:2, firstName: 'סמי', lastName: 'סומו', address: 'רגר 21', city: 'באר שבע', familySize: 3, status: 'זמין', foodPreference: 'כשר', notes: 'אין', phoneNumber: '123-456-7890' },
            {id:3,neederTrackingId:3, firstName: 'עידן', lastName: 'עסיס', address: 'רגר 21', city: 'באר שבע', familySize: 5, status: 'זמין', foodPreference: 'ללא בוטנים', notes: 'אין', phoneNumber: '123-456-7890' },
            {id:4,neederTrackingId:4, firstName: 'שביט', lastName: 'מור', address: 'רגר 21', city: 'באר שבע', familySize: 2, status: 'לא זמין', foodPreference: 'אין', notes: 'להתקשר בהגעה', phoneNumber: '123-456-7890' },
        ];
    return myData;
    }else{
        return [];
    }
}