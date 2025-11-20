import { Injectable } from "@angular/core"
import { HttpClient, HttpHeaders } from "@angular/common/http"
import { Observable, of } from "rxjs"
import { HealthcareService } from "../models/healthcare-service.model"
import { AuthService } from "./auth.service";
import { environment } from "@/environments/environment";



export interface HealthcareServiceDto {
doctorId: string; // UUID → string
name: string;
description?: string; // optional because backend may return null
category?: string;
durationMinutes?: number;
price?: number;
}


@Injectable({
  providedIn: "root",
})
export class ServiceService {
  private apiUrl = `${environment.doctorserviceApiUrl}/healthcare/service`;



  constructor(private http: HttpClient , private authService: AuthService) {}


  getServices(): Observable<HealthcareService[]> {
    return this.http.get<HealthcareService[]>(this.apiUrl);
  }


  getServicesByDoctor(doctorId: string): Observable<HealthcareService[]> {
        return this.http.get<HealthcareService[]>(`${this.apiUrl}/doctor/${doctorId}`);

  }

  addService(service : HealthcareServiceDto , file: File | null): Observable<HealthcareService> {
     const formData = new FormData();
    formData.append('dto', new Blob([JSON.stringify(service)], { type: 'application/json' }));

    if (file) {
      formData.append('file', file);
    }
      return this.http.post<HealthcareService>(this.apiUrl, formData);
  }


}
