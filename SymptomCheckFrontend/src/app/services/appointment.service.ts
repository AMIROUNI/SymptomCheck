import { Injectable } from "@angular/core"
import { HttpClient, HttpHeaders } from "@angular/common/http"
import { Observable, of } from "rxjs"
import { Appointment, AppointmentStatus } from "../models/appointment.model"
import { AuthService } from "./auth.service"
import { environment } from "@/environments/environment"

@Injectable({
  providedIn: "root",
})
export class AppointmentService {

  private apiUrl = `${environment.appointmentApiUrl}`;


  constructor(
    private http: HttpClient,
    private authService: AuthService,
  ) {}


getDateTimeOfAppointments(doctorId: string): Observable<string[]> {
  return this.http.get<string[]>(`${this.apiUrl}/available-date/${doctorId}`);
}


getByDoctorIde(doctorId: string): Observable<Appointment[]> {
  return this.http.get<Appointment[]>(`${this.apiUrl}/doctor/${doctorId}`);
}


getPatientAppointments(id:string): Observable<Appointment[]> {
    return this.http.get<Appointment[]>(`${this.apiUrl}/${id}`);
  }


   updateAppointmentStatus(id: number, status: number): Observable<Appointment> {
    return this.http.put<Appointment>(
      `${this.apiUrl}/${id}/status/${status}`,{}
    );
  }
  //--------------------------------------------------------------------------------------//

  getAppointments(id:number): Observable<Appointment[]> {
    return this.http.get<Appointment[]>(`${this.apiUrl}/${id}`);
  }

  getDoctorAppointments(id:string): Observable<Appointment[]> {
    return this.http.get<Appointment[]>(`${this.apiUrl}/doctor/${id}`);
  }

  getAppointmentById(id: number): Observable<Appointment | undefined> {
   return this.http.get<Appointment>(`${this.apiUrl}/GetById/${id}`);
  }

  createAppointment(appointmentData: Appointment): Observable<Appointment> {
    console.log("appointmentData in service:::::", appointmentData);
     return this.http.post<Appointment>(`${this.apiUrl}/create`, appointmentData);
  }






  ////////////////
  getTakenAppointments(doctorId: string, date: string): Observable<string[]> {

    return this.http.get<string[]>(`${this.apiUrl}/taken-appointments/${doctorId}?date=${date}`);
  }

}
