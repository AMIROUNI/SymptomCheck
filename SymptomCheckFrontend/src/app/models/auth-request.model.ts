export interface UserRegistrationRequest {
  username: string;
    password: string;
    email:  string;
    firstName: string;
    lastName: string;
    phoneNumber:  string;
    profilePhotoUrl:  string | null;
    role: 'PATIENT' | 'DOCTOR'
}

export interface LoginRequest {
  username: string;
  password: string;
}
