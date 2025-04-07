import axios from 'axios';
const backendUrl = import.meta.env.VITE_BACKEND_URL;

const API_BASE_URL = `${backendUrl}/auth`;



export const getUserRoleFromJWT = async (token: string): Promise<string> => {
    try {
        const response = await axios.get(`${API_BASE_URL}/user-role`, {
            params: { token }
        });
        console.log('User role retrieved successfully:', response.data);
        return response.data as string;
    } catch (error: any) {
        console.error('Error retrieving user role:', error.response?.data || error.message);
        throw new Error(error.response?.data || 'Failed to retrieve user role. Please try again later.');
    }
};