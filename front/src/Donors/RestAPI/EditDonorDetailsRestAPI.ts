// donorEditRestAPI.ts

import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080';

export interface Donor {
    id: number;
    firstName: string;
    lastName: string;
    phoneNumber: string;
    address: string;
    email?: string;
    timeOfDonation: number;
    status: string;
    volunteeredDuringLastMonth: boolean;
}

export const getAuthenticatedDonor = async (): Promise<Donor> => {
    try {
        const token = localStorage.getItem('token'); // Adjust based on how you store the token
        const response = await axios.get(`${API_BASE_URL}/user/authDonor`, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
        return response.data as Donor;
    } catch (error: any) {
        console.error('Error fetching donor data:', error.response?.data || error.message);
        throw new Error(error.response?.data || 'Failed to fetch donor data.');
    }
};

export const updateDonor = async (donor: Donor): Promise<void> => {
    try {
        const token = localStorage.getItem('token');
        await axios.put(`${API_BASE_URL}/user/donor`, donor, {
            headers: {
                Authorization: `Bearer ${token}`,
                'Content-Type': 'application/json',
            },
        });
    } catch (error: any) {
        console.error('Error updating donor data:', error.response?.data || error.message);
        throw new Error(error.response?.data || 'Failed to update donor data.');
    }
};
