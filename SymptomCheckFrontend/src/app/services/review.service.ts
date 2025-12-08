// review.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DoctorReview, DoctorReviewStats } from '@/app/models/doctor-review.model';

@Injectable({
  providedIn: 'root'
})
export class ReviewService {
  private apiUrl = 'http://reviewservice/api/v1/reviews';

  constructor(private http: HttpClient) {}

  // Créer un review
  createReview(reviewData: any): Observable<DoctorReview> {
    return this.http.post<DoctorReview>(`${this.apiUrl}`, reviewData);
  }

  // Mettre à jour un review
  updateReview(reviewId: number, reviewData: any): Observable<DoctorReview> {
    return this.http.put<DoctorReview>(`${this.apiUrl}/${reviewId}`, reviewData);
  }

  // Supprimer un review
  deleteReview(reviewId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${reviewId}`);
  }

  // Récupérer les reviews d'un docteur
  getDoctorReviews(doctorId: string): Observable<DoctorReview[]> {
    return this.http.get<DoctorReview[]>(`${this.apiUrl}/doctor/${doctorId}`);
  }

  // Vérifier si l'utilisateur a déjà reviewé ce docteur
  hasReviewedDoctor(doctorId: string): Observable<boolean> {

    return this.http.get<boolean>(`${this.apiUrl}/doctor/${doctorId}/has-reviewed`);
  }

  // Récupérer le review de l'utilisateur pour ce docteur
  getMyReviewForDoctor(doctorId: string): Observable<DoctorReview> {
    return this.http.get<DoctorReview>(`${this.apiUrl}/doctor/${doctorId}/my-review`);
  }

  // Récupérer les statistiques du docteur
  getDoctorStats(doctorId: string): Observable<DoctorReviewStats> {
    return this.http.get<DoctorReviewStats>(`${this.apiUrl}/doctor/${doctorId}/stats`);
  }
}
