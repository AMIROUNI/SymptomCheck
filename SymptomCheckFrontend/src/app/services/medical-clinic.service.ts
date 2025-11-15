import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class MedicalClinicService {
  private apiUrl = 'http://localhost:8085/api/v1/medical/clinic';

  constructor(private http: HttpClient) {}

  // ➤ Create clinic
  createMedicalClinic(clinic: any): Observable<any> {
    return this.http.post(`${this.apiUrl}`, clinic);
  }

  // ➤ Get all clinics
  getMedicalClinics(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}`);
  }

  // Alias
  getClinics(): Observable<any[]> {
    return this.getMedicalClinics();
  }

  // ➤ Get clinic by id
  getMedicalClinicById(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${id}`);
  }

  // Alias
  getClinicById(id: number): Observable<any> {
    return this.getMedicalClinicById(id);
  }

  // ➤ Update clinic
  update(clinic: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/${clinic.id}`, clinic);
  }

  // Alias
  updateClinic(id: number, clinic: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, clinic);
  }

  // ➤ Delete clinic
  deleteClinic(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }
}
