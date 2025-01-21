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

//need to add history for spesific driver also
export const getDriverConstraints = async (driverId: number): Promise<DriverConstraints[]> => {
    try {
            const response = await axios.get(`${API_BASE_URL}/driving/constraints/driver/futureNotApproved${driverId}`);
            console.log('Retrieved pending requests:', response.data);
            return response.data;
        } catch (error) {
            console.error('Error fetching pending requests:', error);
            throw new Error('Failed to fetch pending requests. Please try again later.');
        }
};


