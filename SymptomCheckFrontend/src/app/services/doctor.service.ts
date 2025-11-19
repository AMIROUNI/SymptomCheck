import { Injectable } from "@angular/core"
import { HttpClient, HttpHeaders } from "@angular/common/http"
import { Observable } from "rxjs"
import { User } from "../models/user.model"
import { DoctorReview } from "../models/doctor-review.model"
import { DoctorAvailability } from "../models/doctor-availability.model"
import { AuthService } from "./auth.service"
import { DoctorProfileStatusDTO } from "../models/doctor-profile-status.model"
import { UserRole } from '../models/user.model';
import { Router } from "@angular/router"
import { environment } from "@/environments/environment"
import { AvailabilityHealthDto } from "../models/availability-health-dto"


@Injectable({
  providedIn: "root",
})
export class DoctorService {
  apiurl = "http://localhost:5190"
  private doctorApiUrl = `${environment.doctorserviceApiUrl}`;


  constructor(private http: HttpClient,private authService: AuthService, private router: Router) {}

  getDoctors(): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiurl}`);
  }

  getDoctorById(id: string): Observable<User> {
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

  getDoctorProfileStatus(doctorId: string): Observable<Boolean> {
    return this.http.get<Boolean>(`${this.doctorApiUrl}/profile/${doctorId}/profile-status`);
  }

  completeProfile(AvailabilityHealthDto: AvailabilityHealthDto): Observable<any> {
    return this.http.post<AvailabilityHealthDto>(`${this.doctorApiUrl}/profile/completeprofile`, AvailabilityHealthDto);
  }
 /* async loginAndCheckProfile(username: string, password: string): Promise<void> {
    try {
      await this.authService.loginWithCredentials(username, password);
      const user = this.authService.getCurrentUser();

      if (user && user.role === UserRole.Doctor) {
        // Vérifie le statut du profil du médecin
        this.getDoctorProfileStatus(user.id).subscribe(status => {
          if (status.profileCompleted) {
            this.router.navigate(['/home']); // profil complet → page d'accueil
          } else {
            // profil incomplet → afficher popup ou rediriger vers page complétion
            this.router.navigate(['/profile/completion']);
          }
        });
      } else {
        // utilisateur non médecin → redirection normale
        this.router.navigate(['/home']);
      }

    } catch (error) {
      console.error('Login or profile check failed', error);
      throw error;
    }
  }*/
}
