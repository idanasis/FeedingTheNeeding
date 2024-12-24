import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080';

export interface DonorRegistrationData {
    email?: string;
    password: string;
    confirmPassword: string;
    firstName: string;
    lastName: string;
    phoneNumber: string;
    address: string;
    city: string;
}

export const validateEmail = (email: string): boolean => {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
};

export const validatePhone = (phoneNumber: string): boolean => {
    return /^05\d{8}$/.test(phoneNumber);
};

export const registerDonor = async (data: DonorRegistrationData): Promise<void> => {
    try {
        console.log('Sending data:', data); // Log the payload
        const response = await axios.post(`${API_BASE_URL}/auth/register/donor`, data);
        console.log('Registration successful:', response.data);
    } catch (error: any) {
        console.error('Error during registration:', error.response?.data || error.message);
        throw new Error(error.response?.data || 'Registration failed. Please try again later.');
    }
};

