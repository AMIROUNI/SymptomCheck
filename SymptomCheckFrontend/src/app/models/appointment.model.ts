import type { User } from "./user.model"
import type { PaymentTransaction } from "./payment-transaction.model"

export enum AppointmentStatus {
  Pending = "Pending",
  Confirmed = "Confirmed",
  Cancelled = "Cancelled",
  Completed = "Completed",
}

export interface Appointment {
  id: number
  date: Date
  patientId: number
  patient?: User
  doctorId: number
  doctor?: User
  status?: AppointmentStatus
  description: string
  payment?: PaymentTransaction
}
