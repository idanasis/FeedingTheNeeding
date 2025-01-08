import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080';

export interface DriverConstraintsData {
    driverId: number;
    date: string;
    startHour: string;
    endHour: string;
    startLocation: string;
    requests: string;
}

export const submitDriverConstraints = async (data: DriverConstraintsData): Promise<void> => {
    try {
        console.log('Submitting driver data: ', data);
        const response = await axios.post(`${API_BASE_URL}/driving/constraints`, data);
        console.log('Submission successful: ', response.data);
        return response.data;
    } catch (error) {
        console.error('Error during submitting driver constraints');
        console.log(error);
        throw new Error('Submitting driver constraints failed. Please try again later');
    }
};