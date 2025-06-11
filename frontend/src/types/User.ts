export interface User {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
}

export interface UserInformationRequest {
    firstName: string;
    lastName: string;
    email: string;
}

export interface UserInformationResponse {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
} 