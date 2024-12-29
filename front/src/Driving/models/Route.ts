import { Visit } from './Visit';
export interface Route {
    id: number;
    driverId?: number;
    visits: Visit[];
    status: string;
  }