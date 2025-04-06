
// Types
export interface Visit {
    visitId?: number;
    address: string;
    firstName: string;
    lastName: string;
    phoneNumber: string;
    note?: string;
    status: string;
    priority?: number;
    notes?: string;
    dietaryPreferences?: string;
    name?: string;
    constraints?: [string, number][];
    startTime?: string;
    endTime?: string;
    startHour?: string;
    endHour?: string;
    familySize?: number;
    additionalNotes?: string;
    constraintId?: number;
    street: string;
  }