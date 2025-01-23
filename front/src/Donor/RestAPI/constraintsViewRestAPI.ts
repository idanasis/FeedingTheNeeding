import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080';

export interface PendingCookDTO {
    id: number;
    name: string;
    startTime: string;
    endTime: string;
    constraints: Record<string, number>;
    address: string;
    date: string;
    status: string;
    phoneNumber: string;
}

export interface DriverConstraints {
    driverId: number;
    date: string;
    start_hour: number;
    end_hour: number;
    startLocation: string;
    requests: string;
}

export interface Visit{
    visitId: number;
    address: string;
    firstName: string;
    lastName: string;
    phoneNumber: string;
    maxHour: string;
    status: string;
    priority: number;
    note: string
}

export interface DriverRoutes{
    routeId: number;
    driverId: number;
    date: string;
    visit: Visit[];
    submitted: boolean;
}


export const getCookConstraints = async (): Promise<PendingCookDTO[]> => {
    try {
        console.log('Trying to retrive constraints for cook ');
        const currentDate = new Date().toISOString().split('T')[0]; // Gets current date in YYYY-MM-DD format

        const token = localStorage.getItem('token');

        if (!token) {
            throw new Error('Authentication token not found');
        }

        const response = await axios.post(
                    `${API_BASE_URL}/cooking/constraints/latest`,
                    { date: currentDate }, // Send as an object
                    {
                        headers: {
                            'Authorization': `Bearer ${token}`,
                            'Content-Type': 'application/json'
                        }
                    }
                );

        console.log('Retrieved Constraints:', response.data);
        return response.data;
    } catch (error) {
        console.error('Error fetching constraints:', error);
        throw new Error('Failed to fetch constraints. Please try again later.');
    }
};

export const getDriverConstraints = async (): Promise<DriverConstraints[]> => {
    try {
            const token = localStorage.getItem('token');
            const idResponse = await axios.get(`${API_BASE_URL}/auth/user-id`, {
                        params: { token: token }, // Send token as query parameter
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    });

            console.log("Succesffully got id: ", idResponse.data);

            const driverId = idResponse.data;

            const response = await axios.get(
                        `${API_BASE_URL}/driving/constraints/driver/futureNotApproved`, {
                            params: { driverId: driverId },
                            headers: {
                                'Authorization': `Bearer ${token}`,
                                'Content-Type': 'application/json'
                            }
                        }
                    );
            console.log('Retrieved drivers constraints:', response.data);
            return response.data;
        } catch (error) {
            console.error('Error fetching driver constraints:', error);
            throw new Error('Failed to fetch drivers requests. Please try again later.');
        }
};

export const getDriverRoutes = async (date: string): Promise<DriverRoutes> => {
    try {
        const token = localStorage.getItem('token');
        if (!token) {
            throw new Error('Authentication token not found');
        }

        const idResponse = await axios.get(`${API_BASE_URL}/auth/user-id`, {
            params: { token: token },
            headers: {
                'Content-Type': 'application/json'
            }
        });

        console.log("Successfully got id: ", idResponse.data);
        const driverId = idResponse.data;

        const response = await axios.get(
            `${API_BASE_URL}/driving/routes`, {
                params: {
                    date: date,
                    driverId: driverId
                },
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            }
        );
        console.log('Retrieved drivers routes:', response.data);
        return response.data;
    } catch (error) {
        console.error('Error fetching driver routes:', error);
        throw new Error('Failed to fetch drivers routes. Please try again later.');
    }
};

