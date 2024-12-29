import { Donor } from './Donor';
import { Visit } from './Visit';
export interface Route {
    id: number;
    driverId?: number;
    driver?: Donor;
    visit: Visit[];
    status: string;
  }