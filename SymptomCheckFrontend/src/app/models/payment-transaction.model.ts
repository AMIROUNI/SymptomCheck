import type { Appointment } from "./appointment.model"
import type { User } from "./user.model"

export enum PaymentStatus {
  Pending = "Pending",
  Paid = "Paid",
  Failed = "Failed",
}

export interface PaymentTransaction {
  id: number
  appointmentId: number
  appointment?: Appointment
  amount: number
  paymentDate: Date
  status: PaymentStatus
  userId: number
  user?: User
}
