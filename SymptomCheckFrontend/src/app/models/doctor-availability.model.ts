import type { User } from "./user.model"

export interface DoctorAvailability {
  id: number
  doctorId: number
  doctor?: User
  startTime: Date
  endTime: Date
  day: number // 0-6 for Sunday-Saturday
}
