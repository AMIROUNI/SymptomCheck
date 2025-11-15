import type { User } from "./user.model"

export interface MedicalClinic {
  id?: number;
  name: string;
  address?: string;
  city?: string;
  country?: string;
  phone?: string;
  websiteUrl?: string;
}
