import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';
import { environment } from '@/environments/environment';

@Injectable({
providedIn: 'root'
})
export class DashboardAdminService {

private userApi = environment.userserviceAdminApiUrl;
private doctorApi = environment.doctorserviceAdminApiUrl;
private clinicApi = environment.clinicAdminApiUrl;
private appointmentApi = environment.appointmentAdminApiUrl;

constructor(private http: HttpClient) {}

// ============================
// USERS SERVICE CALLS
// ============================

getUserStats(): Observable<any> {
return this.http.get(`${this.userApi}/dashboard/stats`);
}

getAllUsers(): Observable<any[]> {
return this.http.get<any[]>(`${this.userApi}/users`);
}

getUsersByRole(role: string): Observable<any[]> {
return this.http.get<any[]>(`${this.userApi}/users/role/${role}`);
}

updateUserProfileStatus(userId: string, profileComplete: boolean): Observable<any> {
return this.http.put(`${this.userApi}/users/${userId}/status?profileComplete=${profileComplete}`, {});
}

// ============================
// DOCTOR SERVICE CALLS
// ============================

getDoctorStats(): Observable<any> {
return this.http.get(`${this.doctorApi}/dashboard/stats`);
}

getAllDoctors(): Observable<any[]> {
return this.http.get<any[]>(`${this.doctorApi}/doctors`);
}

getDoctorsBySpeciality(speciality: string): Observable<any[]> {
return this.http.get<any[]>(`${this.doctorApi}/doctors/speciality/${speciality}`);
}

updateDoctorStatus(doctorId: string, status: string): Observable<any> {
return this.http.put(`${this.doctorApi}/doctors/${doctorId}/status?status=${status}`, {});
}

// ============================
// CLINIC SERVICE CALLS
// ============================

getClinicStats(): Observable<any> {
return this.http.get(`${this.clinicApi}/dashboard/stats`);
}

getAllClinics(): Observable<any[]> {
return this.http.get<any[]>(`${this.clinicApi}/clinics`);
}

getClinicsByCity(city: string): Observable<any[]> {
return this.http.get<any[]>(`${this.clinicApi}/clinics/city/${city}`);
}

createClinic(payload: any): Observable<any> {
return this.http.post(`${this.clinicApi}/clinics`, payload);
}

updateClinic(clinicId: number, payload: any): Observable<any> {
return this.http.put(`${this.clinicApi}/clinics/${clinicId}`, payload);
}

deleteClinic(clinicId: number): Observable<any> {
return this.http.delete(`${this.clinicApi}/clinics/${clinicId}`);
}

// ============================
// APPOINTMENT SERVICE CALLS
// ============================

getAppointmentStats(): Observable<any> {
return this.http.get(`${this.appointmentApi}/dashboard/stats`);
}

getAllAppointments(): Observable<any[]> {
return this.http.get<any[]>(`${this.appointmentApi}/appointments`);
}

getAppointmentsByStatus(status: string): Observable<any[]> {
return this.http.get<any[]>(`${this.appointmentApi}/appointments/status/${status}`);
}

getAppointmentsByDateRange(start: string, end: string): Observable<any[]> {
return this.http.get<any[]>(`${this.appointmentApi}/appointments/date-range?start=${start}&end=${end}`);
}

getAppointmentsByDoctor(doctorId: string): Observable<any[]> {
return this.http.get<any[]>(`${this.appointmentApi}/appointments/doctor/${doctorId}`);
}

updateAppointmentStatus(appointmentId: number, status: string): Observable<any> {
return this.http.put(`${this.appointmentApi}/appointments/${appointmentId}/status?status=${status}`, {});
}

}
