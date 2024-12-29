import { Donor } from './Donor';
import { Visit } from './Visit';
export interface Route {
    routeId: number;
    driverId?: number;
    driver?: Donor;
    visit: Visit[];
    submitted: boolean;
  }