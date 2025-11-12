import type { User } from "./user.model"

export interface HealthcareService {
  id: number
  doctorId: number
  doctor?: User
  name: string
  description: string
  category: string
  imageUrl: string
  price?: number
  durationMinutes: number
}
