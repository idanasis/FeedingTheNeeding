import { Needy } from "./NeedyModel";

export interface NeederTrackingModel {
    id: number;
    date: string;
    needy:Needy
    weekStatus?: string;
    dietaryPreferences?: string;
    additionalNotes?: string;
}