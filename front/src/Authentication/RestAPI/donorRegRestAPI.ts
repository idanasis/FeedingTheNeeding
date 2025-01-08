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
        if(error.response?.status === 409)
            throw new Error("User already exists");
        throw new Error(error.response?.data || 'Registration failed. Please try again later.');
    }
};

export const verifyDonor = async (phoneNumber: string, verificationCode: string): Promise<void> => {
    try {
        const response = await axios.post(`${API_BASE_URL}/auth/verify-donor`, {phoneNumber, verificationCode});
        console.log('Verification successful:', response.data);
    }catch (error: any){
        console.error('Error during verification:', error.response?.data || error.message);
        throw new Error(error.response?.data || 'Verification failed. Please try again later.');
    }
}

export const resendVerificationSMSCode = async (phoneNumber: string): Promise<void> => {
    try {
        const response = await axios.post(`${API_BASE_URL}/auth/resend-sms?phoneNumber=${phoneNumber}`);
        console.log('Resend code successful:', response.data);
    } catch (error: any) {
        console.error('Error resending verification code:', error.response?.data || error.message);
        throw new Error(error.response?.data || 'Failed to resend code. Please try again.');
    }
};
