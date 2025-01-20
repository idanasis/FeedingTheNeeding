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
    phoneNumber: string; //not needed
}

interface ConstraintResponse {
    constraintId: number;
    cookId: number;
    startTime: string;
    endTime: string;
    constraints: Record<string, number>;
    address: string;
    date: string;
    status: string;
}

export const getAllRequests = async (date: string): Promise<PendingCookDTO[]> => {
    try {
        const response = await axios.get(`${API_BASE_URL}/cooking/getConstraints/${date}`,
        {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });
        console.log('Retrieved pending requests:', response.data);
        return response.data;
    } catch (error) {
        console.error('Error fetching pending requests:', error);
        throw new Error('Failed to fetch pending requests. Please try again later.');
    }
};

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
        console.log('Successfully fetched food constraints: ', response.data);
        return response.data;
    } catch (error) {
        console.error('Error during fetching food constraints');
        console.log(error)
        throw new Error(error || 'Submitting constraints failed. Please try again later');
    }
}

export const getAcceptedConstraints = async(date: string): Promise<Record<string, number>> => {
    try {
        console.log('Fetching accepted constraints ', date);
        const response = await axios.get<ConstraintResponse[]>(`${API_BASE_URL}/cooking/getAccepted/${date}`,
        {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });
        console.log('Successfully fetched accepted constraints: ', response.data);

        // Create aggregated constraints object
        const summedConstraints: Record<string, number> = {};

        // Loop through all objects and sum up their constraints
        response.data.forEach(item => {
            Object.entries(item.constraints).forEach(([key, value]) => {
                // Initialize key if it doesn't exist
                if (!(key in summedConstraints)) {
                    summedConstraints[key] = 0;
                }
                summedConstraints[key] += value;
            });
        });

        return summedConstraints;
    } catch (error) {
        console.error('Error during fetching accepted constraints');
        console.log(error);
        throw new Error(error?.toString() || 'Fetching constraints failed. Please try again later');
    }
}

export const updateConstraint = async(id: number, constraints: Record<string, number>): Promise<Void> => {
    try {
        console.log("Updating constraint with id:", id, "New constraints:", constraints);

        const params = new URLSearchParams({
                    constraintId: id.toString(),
                    ...Object.fromEntries(
                        Object.entries(constraints).map(([key, value]) => [key, value.toString()])
                    )
                });

        await axios.post(`${API_BASE_URL}/cooking/updateConstraint`, null, {
            params: params
        });
        console.log('Request rejected successfully');
    } catch (error) {
        console.error('Error updating request:', error);
        throw new Error('Failed to reject request. Please try again later.');
    }
}
