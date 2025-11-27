import type { User } from "./user.model"
import type { PaymentTransaction } from "./payment-transaction.model"

export enum AppointmentStatus {
  PENDING = "PENDING",
  CONFIRMED = "CONFIRMED",
  CANCELLED = "CANCELLED",
  COMPLETED = "COMPLETED",
}

export interface Appointment {
  id?: number
  dateTime: Date | string
  patientId: string
  patient?: User
  doctorId: string
  doctor?: User
  status?: AppointmentStatus
  description: string
  payment?: PaymentTransaction
}
