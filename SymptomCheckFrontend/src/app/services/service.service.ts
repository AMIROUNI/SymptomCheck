import { Injectable } from "@angular/core"
import { HttpClient, HttpHeaders } from "@angular/common/http"
import { Observable, of } from "rxjs"
import { HealthcareService } from "../models/healthcare-service.model"
import { AuthService } from "./auth.service";
import { environment } from "@/environments/environment";

@Injectable({
  providedIn: "root",
})
export class ServiceService {
  private apiUrl = `${environment.doctorserviceApiUrl}/healthcare/service`;



  constructor(private http: HttpClient , private authService: AuthService) {}


  getServices(): Observable<HealthcareService[]> {
    return this.http.get<HealthcareService[]>(this.apiUrl);

  }


  getServicesByDoctor(doctorId: number): Observable<HealthcareService[]> {
        return this.http.get<HealthcareService[]>(`${this.apiUrl}/doctor/${doctorId}`);

  }

  addService(service : HealthcareService): Observable<HealthcareService> {
    return this.http.post<HealthcareService>(this.apiUrl, service);
  }


}
