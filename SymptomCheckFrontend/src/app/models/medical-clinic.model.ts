import type { User } from "./user.model"

export interface MedicalClinic {
  id?: number
  name: string
  address: string
  phone: string
  websiteUrl?: string
  city: string
  country: string
  doctors?: User[]
}
