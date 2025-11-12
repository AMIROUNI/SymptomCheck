import { Injectable } from "@angular/core"
import { HttpClient, HttpHeaders } from "@angular/common/http"
import { Observable, of } from "rxjs"
import { Appointment, AppointmentStatus } from "../models/appointment.model"
import { AuthService } from "./auth.service"

@Injectable({
  providedIn: "root",
})
export class AppointmentService {

  private apiUrl = 'http://localhost:5190/api/appointment';
  constructor(
    private http: HttpClient,
    private authService: AuthService,
  ) {}

  getAppointments(id:number): Observable<Appointment[]> {
    return this.http.get<Appointment[]>(`${this.apiUrl}/${id}`);
  }
  getPatientAppointments(id:number): Observable<Appointment[]> {
    return this.http.get<Appointment[]>(`${this.apiUrl}/patient/${id}`);
  }
  getDoctorAppointments(id:number): Observable<Appointment[]> {
    return this.http.get<Appointment[]>(`${this.apiUrl}/doctor/${id}`);
  }

  getAppointmentById(id: number): Observable<Appointment | undefined> {
   return this.http.get<Appointment>(`${this.apiUrl}/GetById/${id}`);
  }

  createAppointment(appointmentData: Appointment): Observable<Appointment> {
    console.log("appointmentData in service:::::", appointmentData);
     return this.http.post<Appointment>(`${this.apiUrl}`, appointmentData);
  }

  updateAppointmentStatus(id: number, status: number): Observable<Appointment> {
    return this.http.put<Appointment>(
      `${this.apiUrl}/${id}`,status
    );
  }

}
