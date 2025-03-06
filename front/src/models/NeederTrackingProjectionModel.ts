export interface NeederTrackingProjectionModel{
    id: number;
    needyId: number;
    date: string;
    weekStatus: string;
    dietaryPreferences: string;
    additionalNotes: string;
    needyFirstName: string;
    needyLastName: string;
    needyPhoneNumber: string;
    needyAddress: string;
    needyFamilySize?: number;
    needyStreet: string;
}