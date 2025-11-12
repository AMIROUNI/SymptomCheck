import { Injectable } from "@angular/core"
import { HttpClient, HttpHeaders } from "@angular/common/http"
import { Observable } from "rxjs"
import { User } from "../models/user.model"
import { DoctorReview } from "../models/doctor-review.model"
import { DoctorAvailability } from "../models/doctor-availability.model"
import { AuthService } from "./auth.service"

@Injectable({
  providedIn: "root",
})
export class DoctorService {
  apiurl = "http://localhost:5190"

  constructor(private http: HttpClient,private authService: AuthService) {}

  getDoctors(): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiurl}/api/doctors`);
  }

  getDoctorById(id: number): Observable<User> {
    return this.http.get<User>(`${this.apiurl}/api/doctors/${id}`);
  }

  getDoctorReviews(doctorId: number): Observable<DoctorReview[]> {
    return this.http.get<DoctorReview[]>(`${this.apiurl}/api/doctorreview/doctor/${doctorId}`);
  }

  getDoctorAvailability(doctorId: number): Observable<DoctorAvailability[]> {
    return this.http.get<DoctorAvailability[]>(`${this.apiurl}/api/doctors/${doctorId}/availability`);
  }

  submitReview(review: Omit<DoctorReview, "id" | "datePosted">): Observable<DoctorReview> {
    return this.http.post<DoctorReview>(`${this.apiurl}/api/doctorreview`, review);
  }

  filterDoctorsBySpecialty(specialty: string): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiurl}/api/doctors?specialty=${specialty}`);
  }
}
