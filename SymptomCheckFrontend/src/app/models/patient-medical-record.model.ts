import type { User } from "./user.model"

export interface PatientMedicalRecord {
  id: number
  patientId: number
  patient?: User
  createdAt: Date
  recordDetails: string
}
