export interface Donor {
    id:number;
    firstName: string;
    lastName: string;
    phoneNumber: string;
    address: string;
    city: string;
    RegistrationStatus: string;
    email?: string;
    status?: string;
    lastDonationDate?: Date;
    role?: string;
}