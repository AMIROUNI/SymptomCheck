// models/doctor-review.model.ts
export interface DoctorReview {
  id: number;
  patientId: string;  // Changé de number à string (Keycloak ID)
  doctorId: string;   // Changé de number à string (Keycloak ID)
  rating: number;     // Changé de "stars" à "rating"
  comment: string;
  datePosted: string; // Instant/LocalDateTime du backend
  lastUpdated?: string;
  patientName?: string; // Pour l'affichage
}

export interface DoctorReviewRequest {
  doctorId: string;
  rating: number;
  comment: string;
}

export interface DoctorReviewStats {
  doctorId: string;
  averageRating: number;
  totalReviews: number;
  ratingDistribution: { [key: number]: number };
}