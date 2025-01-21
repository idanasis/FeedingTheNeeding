import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080';

export interface DriverConstraintsData {
    driverId: number;
    date: string;
    startHour: number;
    endHour: number;
    startLocation: string;
    requests: string;
}

export const submitDriverConstraints = async (data: DriverConstraintsData): Promise<void> => {
    try {
        console.log('Submitting driver data: ', data);

        const token = localStorage.getItem('token');

        if (!token) {
            throw new Error('Authentication token not found');
        }

        console.log("Getting driver id");
        const idResponse = await axios.get(`${API_BASE_URL}/auth/user-id`, {
                                params: { token: token }, // Send token as query parameter
                                headers: {
                                    'Content-Type': 'application/json'
                                }
                            });

        console.log("Succesffully got id: ", idResponse.data);

        const driverId = idResponse.data;
        data.driverId = idResponse.data;


        const response = await axios.post(`${API_BASE_URL}/driving/constraints`, data,{
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        console.log('Submission successful: ', response.data);
        return response.data;
    } catch (error) {
        console.error('Error during submitting driver constraints');
        console.log(error);
        throw new Error('Submitting driver constraints failed. Please try again later');
    }
};