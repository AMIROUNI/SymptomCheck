import { User } from "./user.model";

export interface PatientRegisterData {
  username: string;
  password: string;
  firstName: string;
  lastName: string;
  phoneNumber: string;
  email: string;
  profilePhotoUrl?: string;
}

export interface DoctorRegisterData {
  username: string;
  password: string;
  firstName: string;
  lastName: string;
  phoneNumber: string;
  email: string;
  speciality: string;
  description: string;
  diploma: string;
  profilePhotoUrl?: string;
  clinicId?: number;
}

export interface LoginData {
  username: string;
  password: string;
}

export interface AuthResponse {
  user: User;
  token: string;
}