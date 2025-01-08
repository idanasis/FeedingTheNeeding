import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080';


export interface CookConstraintsData {
    cookId: number;
    startTime: string;
    endTime: string;
    PlatesNum: number;
    location: string;
    date: string;
}

export const submitConstraints = async (data : CookConstraintsData) : Promise<void> => {
    try{
        console.log('Submitting the data: ', data);
        const response = await axios.post(`${API_BASE_URL}/cooking/submit/constraints`, data);
        console.log('Submitting successful: ', response.data);
        return response;
    } catch (error) {
        console.error('Error during submitting constraints');
        console.log(error)
        throw new Error(error || 'Submitting constraints failed. Please try again later');
    }
}