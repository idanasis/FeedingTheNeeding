import axios from 'axios';

export const API_BASE_URL = 'http://localhost:8080';

export interface LoginResponse {
    token: string;
    expirationTime: number;
}

export const login = async (phoneNumber: string, password: string): Promise<LoginResponse> => {
    const response = await axios.post<LoginResponse>(`${API_BASE_URL}/auth/login`, {
        phoneNumber,
        password,
    });
    return response.data;
};

export const resetPassword = async (phoneNumber: string, newPassword: string, verificationCode: string): Promise<void> => {
    try {
        await axios.post(`${API_BASE_URL}/auth/confirm-reset-password`, null, {
            params: {
                phoneNumber,
                newPassword,
                verificationCode,
            },
        });
    } catch (error: any) {
        console.error('Reset password error:', error.response?.data || error.message);
        throw new Error(error.response?.data || 'Failed to reset password');
    }
};

export const requestPasswordReset = async (phoneNumber: string): Promise<void> => {
    try {
        await axios.post(`${API_BASE_URL}/auth/request-reset-password`, null, {
            params: {
                phoneNumber,
            },
        });
    } catch (error: any) {
        console.error('Request password reset error:', error.response?.data || error.message);
        throw new Error(error.response?.data || 'Failed to request password reset');
    }
};
