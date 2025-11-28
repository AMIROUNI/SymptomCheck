import type { MedicalClinic } from "./medical-clinic.model"
import type { DoctorAvailability } from "./doctor-availability.model"
import type { DoctorReview } from "./doctor-review.model"
import type { HealthcareService } from "./healthcare-service.model"
import type { Appointment } from "./appointment.model"
import type { PaymentTransaction } from "./payment-transaction.model"

export enum UserRole {
  PATIENT = "PATIENT",
  DOCTOR = "DOCTOR",
  ADMIN = "ADMIN"
}

export interface User {
  id: string
  username: string
  roles: UserRole[] | string[]  
  firstName?: string
  lastName?: string
  phoneNumber?: string
  email?: string
  speciality?: string
  description?: string
  diploma?: string
  profilePhotoUrl?: string
  isProfileComplete: boolean
  clinicId?: number
  clinic?: MedicalClinic
  availabilities?: DoctorAvailability[]
  reviewsReceived?: DoctorReview[]
  reviewsWritten?: DoctorReview[]
  servicesOffered?: HealthcareService[]
  doctorAppointments?: Appointment[]
  patientAppointments?: Appointment[]
  paymentTransactions?: PaymentTransaction[]
  role: UserRole 
  enabled?: boolean
}

export interface DoctorDto {
  username: string
  firstName?: string
  lastName?: string
  phoneNumber?: string
  email?: string
  speciality?: string
  description?: string
  diploma?: string
  profilePhotoUrl?: string
  clinicId?: number
}

export interface UserUpdateDto {
  // ✅ SUPPRIMÉ : id n'est plus nécessaire ici
  firstName: string
  lastName: string
  email: string
  phoneNumber?: string
}

export interface DoctorProfileDto {
  // ✅ SUPPRIMÉ : id n'est plus nécessaire ici
  speciality: string
  description: string
  diploma: string
  profilePhotoUrl?: string
  clinicId?: number
}