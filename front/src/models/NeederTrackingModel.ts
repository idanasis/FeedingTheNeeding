export interface NeederTrackingModel {
    id: number;
    needy:{
        id: number,
        firstName: string,
        lastName: string,
        phoneNumber: string,
        address: string,
        city: string,
        familySize: number
    }
    status: string;
    foodPreference: string;
    notes: string;
}