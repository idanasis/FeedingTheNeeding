import axios from 'axios';
import { Needy } from '../models/NeedyModel'; // Import the Needy interface
const backendUrl = import.meta.env.VITE_BACKEND_URL;

const API_BASE_URL = `${backendUrl}/auth/register/needy`;

/**
 * Create or update a Needy.
 * @param needy - The Needy object to be created or updated.
 * @returns A Promise resolving to the created or updated Needy object.
 */
export const createOrUpdateNeedy = async (needy: Partial<Needy>): Promise<Needy> => {
  try {
    const response = await axios.post<Needy>(`${API_BASE_URL}`, needy, {
      headers: {
        'Content-Type': 'application/json',
      },
    });
    return response.data;
  } catch (error) {
    console.error('Error creating/updating needy:', error);
    throw error;
  }
};
