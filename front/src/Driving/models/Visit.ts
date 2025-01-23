
// Types
export interface Visit {
    visitId?: number;
    address: string;
    firstName: string;
    lastName: string;
    phoneNumber: string;
    maxHour?: number;
    note?: string;
    status: string;
    priority?: number;
    notes?: string;
    dietaryPreferences?: string;
    name?: string;
    constraints?: [string, number][];
    startTime?: string;
    endTime?: string;
    familySize?: number;
  }