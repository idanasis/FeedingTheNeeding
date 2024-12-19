export interface NeederTrackingModel {
    id: number;
    date: string;
    needy:{
        id: number,
        firstName: string,
        lastName: string,
        phoneNumber: string,
        address: string,
        city: string,
        familySize: number
    }
    weekStatus?: string;
    dietaryPreferences?: string;
    additionalNotes?: string;
}