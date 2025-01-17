import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080';

export interface PendingCookDTO {
    cookId: number; //change to string later - this is cook id?
    startTime: string;
    endTime: string;
    platesNum: number;
    location: string;
    date: string;
    status: number; //not needed
}

export interface DriverConstraints {
    driverId: number;
    date: string;
    start_hour: number;
    end_hour: number;
    startLocation: string;
    requests: string;
}

export const getCookConstraints = async (cookId: number): Promise<PendingCookDTO[]> => {
    try {
        console.log('Trying to retrive constraints for cook: ', cookId);
        const currentDate = new Date().toISOString().split('T')[0]; // Gets current date in YYYY-MM-DD format

        const response = await axios.post(`${API_BASE_URL}/cooking/constraints/latest`, {
                    cookId: cookId,
                    date: currentDate
                });

        console.log('Retrieved pending requests:', response.data);
        return response.data;
//         let duplicatedData: PendingCookDTO[] = [];
//                 for(let i = 0; i < 10; i++) {
//                     // For each copy, create new objects with slightly modified data
//                     const newData = response.data.map((item: PendingCookDTO, index: number) => ({
//                         ...item,
//                         name: `${item.cookId} ${i + 1}`,  // Add number to name to distinguish entries
//                         meal_amount: item.platesNum + i  // Slightly modify meal amount
//                     }));
//                     duplicatedData = [...duplicatedData, ...newData];
//                 }
//         return duplicatedData;
    } catch (error) {
        console.error('Error fetching pending requests:', error);
        throw new Error('Failed to fetch pending requests. Please try again later.');
    }
};

//need to add history for spesific driver also
export const getDriverConstraints = async (driverId: number): Promise<DriverConstraints[]> => {
    try {
            const response = await axios.get(`${API_BASE_URL}/driving/constraints/driver/futureNotApproved${driverId}`);
            console.log('Retrieved pending requests:', response.data);
            return response.data;
        } catch (error) {
            console.error('Error fetching pending requests:', error);
            throw new Error('Failed to fetch pending requests. Please try again later.');
        }
};


