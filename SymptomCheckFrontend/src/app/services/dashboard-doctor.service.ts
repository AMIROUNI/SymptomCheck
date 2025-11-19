import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class DashboardDoctorService {

  private doctorApi = environment.doctorserviceApiUrl; 
  private appointmentApi = environment.appointmentApiUrl;

  constructor(private http: HttpClient) {}

  // ============================================
  // Doctor Dashboard (From Doctor Service)
  // ============================================

  /** GET /api/v1/doctor/dashboard/{doctorId} */
  getDoctorDashboard(doctorId: string): Observable<any> {
    return this.http.get<any>(`${this.doctorApi}/dashboard/${doctorId}`);
  }

  /** GET /api/v1/doctor/dashboard/{doctorId}/service-categories */
  getServiceCategories(doctorId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.doctorApi}/dashboard/${doctorId}/service-categories`);
  }

  /** GET /api/v1/doctor/dashboard/{doctorId}/profile-status */
  getProfileStatus(doctorId: string): Observable<any> {
    return this.http.get<any>(`${this.doctorApi}/dashboard/${doctorId}/profile-status`);
  }

  // ============================================
  // Appointment Dashboard (From Appointment Service)
  // ============================================

  /** GET /api/v1/appointments/dashboard/doctor/{doctorId} */
  getAppointmentsDashboard(doctorId: string): Observable<any> {
    return this.http.get<any>(`${this.appointmentApi}/dashboard/doctor/${doctorId}`);
  }

  /** GET /api/v1/appointments/dashboard/doctor/{doctorId}/today */
  getTodayAppointments(doctorId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.appointmentApi}/dashboard/doctor/${doctorId}/today`);
  }

  /** GET /api/v1/appointments/dashboard/doctor/${doctorId}/upcoming */
  getUpcomingAppointments(doctorId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.appointmentApi}/dashboard/doctor/${doctorId}/upcoming`);
  }
}
