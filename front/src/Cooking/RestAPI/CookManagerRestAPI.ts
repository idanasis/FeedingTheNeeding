import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080';

export interface PendingCookDTO {
    name: number; //change to string later
    start_hour: string;
    end_hour: string;
    meal_amount: number;
    address: string;
    date: string;
    status: number; //not needed
}

export enum Status {
    Pending = 'Pending',
    Approved = 'Approved',
    Rejected = 'Rejected'
}

export interface PendingConstraint {
    constraintId: number;
    name: string;
    start_hour: string;
    end_hour: string;
    meal_amount: number;
    address: string;
    date: string; // LocalDate will be received as a string in JSON
    status: Status;
}

export const getPendingRequests = async (date: string): Promise<PendingCookDTO[]> => {
    try {
        const response = await axios.get(`${API_BASE_URL}/cooking/getPending/${date}`);
        console.log('Retrieved pending requests:', response.data);
        let duplicatedData: PendingCookDTO[] = [];
                for(let i = 0; i < 10; i++) {
                    // For each copy, create new objects with slightly modified data
                    const newData = response.data.map((item: PendingCookDTO, index: number) => ({
                        ...item,
                        name: `${item.cookId} ${i + 1}`,  // Add number to name to distinguish entries
                        meal_amount: item.platesNum + i  // Slightly modify meal amount
                    }));
                    duplicatedData = [...duplicatedData, ...newData];
                }
        return duplicatedData;
    } catch (error) {
        console.error('Error fetching pending requests:', error);
        throw new Error('Failed to fetch pending requests. Please try again later.');
    }
};

//TODO: change the signature so requestId will be long and not string
export const approveCookRequest = async (constraintId: number): Promise<void> => {
    try {
        console.log("Wanting to approve request with id: ", constraintId)
        await axios.post(`${API_BASE_URL}/cooking/acceptConstraint/${constraintId}`);
        console.log('Request approved successfully');
    } catch (error) {
        console.error('Error approving request:', error);
        throw new Error('Failed to approve request. Please try again later.');
    }
};

export const rejectCookRequest = async (constraintId: number): Promise<void> => {
    try {
        await axios.post(`${API_BASE_URL}/cooking/rejectConstraint/${constraintId}`);
        console.log('Request rejected successfully');
    } catch (error) {
        console.error('Error rejecting request:', error);
        throw new Error('Failed to reject request. Please try again later.');
    }
};