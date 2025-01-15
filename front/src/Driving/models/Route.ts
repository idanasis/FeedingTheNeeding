import { Donor } from './Donor';
import { DriverConstraints } from './DriverConstraints';
import { Visit } from './Visit';
export interface Route {
    routeId: number;
    driverId?: number;
    driver?: DriverConstraints;
    visit: Visit[];
    submitted: boolean;
  }