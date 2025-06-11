import axios from 'axios';
import { User, UserInformationRequest, UserInformationResponse } from '../types/User';
import _ from 'lodash';

const API_URL = 'http://localhost:9090/users';

// Додаємо перехоплювач для логування запитів
axios.interceptors.request.use(request => {
    console.log('Starting Request:', request);
    return request;
});

axios.interceptors.response.use(response => {
    console.log('Response:', response);
    return response;
}, error => {
    console.error('API Error:', error);
    return Promise.reject(error);
});

function toSnakeCase(obj: Record<string, any>): Record<string, any> {
    return _.mapKeys(obj, (_v: any, k: string) => _.snakeCase(k));
}

function toCamelCase(obj: any): any {
    if (Array.isArray(obj)) {
        return obj.map(v => toCamelCase(v));
    } else if (obj !== null && typeof obj === 'object') {
        return Object.keys(obj).reduce((result: any, key: string) => {
            result[_.camelCase(key)] = toCamelCase(obj[key]);
            return result;
        }, {});
    }
    return obj;
}

export const userApi = {
    getAllUsers: async (page: number = 0, size: number = 5) => {
        const response = await axios.get(`${API_URL}?page=${page}&size=${size}`);
        return toCamelCase(response.data);
    },

    searchUsers: async (firstName: string, page: number = 0, size: number = 5) => {
        const response = await axios.get(`${API_URL}/search?firstName=${firstName}&page=${page}&size=${size}`);
        return toCamelCase(response.data);
    },

    createUser: async (userData: UserInformationRequest) => {
        const response = await axios.post(API_URL, toSnakeCase(userData));
        return toCamelCase(response.data);
    },

    updateUser: async (id: number, userData: UserInformationRequest) => {
        const response = await axios.put(`${API_URL}/${id}`, toSnakeCase(userData));
        return toCamelCase(response.data);
    },

    deleteUser: async (id: number) => {
        const response = await axios.delete(`${API_URL}/${id}`);
        return toCamelCase(response.data);
    }
}; 