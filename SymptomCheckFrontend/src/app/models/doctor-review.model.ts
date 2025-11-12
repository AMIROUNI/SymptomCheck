import type { User } from "./user.model"

export interface DoctorReview {
  id: number
  patientId: number
  patient?: User
  doctorId: number
  doctor?: User
  stars: number
  comment: string
  datePosted: Date
}
