import axios from 'axios';
const backendUrl = import.meta.env.VITE_BACKEND_URL;

const API_BASE_URL = `${backendUrl}`;

export interface CookConstraintsData {
    cookId: number;
    startTime: string;
    endTime: string;
    constraints: Record<string, number>;
    location: string;
    date: string;
}

export const submitConstraints = async (data : CookConstraintsData) : Promise<void> => {
    try{
        const token = localStorage.getItem('token');

        if (!token) {
            throw new Error('Authentication token not found');
        }

        const response = await axios.post(
            `${API_BASE_URL}/cooking/submit/constraints`,
            data,
            {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            }
        );

        console.log('Submitting successful: ', response.data);
        return response;

    } catch (error) {
        console.error('Error during submitting constraints');
        console.log(error)
        throw new Error(error || 'Submitting constraints failed. Please try again later');
    }
}

export const getFoodConstraints = async(date: string): Promise<Record<string, number>> => {
    try{
        console.log('Fetching needed food for date ', date);
        const response = await axios.get(`${API_BASE_URL}/social/getNeededFoodByDate`, {
            params: {
                date: date
            },
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });
        console.log('Submitting fetched the data: ', response.data);
        return response.data;
    } catch (error) {
        console.error('Error during food fetching');
        console.log(error)
        throw new Error(error || 'Submitting constraints failed. Please try again later');
    }
}