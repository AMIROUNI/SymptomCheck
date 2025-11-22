import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DoctorAvailability {
  id: number;
  doctorId: string;
  daysOfWeek: string[];
  startTime: string;
  endTime: string;
}

@Injectable({
  providedIn: 'root'
})
export class DoctorAvailabilityService {

  private baseUrl = 'http://localhost:8087/api/v1/doctor/availability';

  constructor(private http: HttpClient) {}

  getAvailabilityByDoctorId(doctorId: string): Observable<DoctorAvailability[]> {
    return this.http.get<DoctorAvailability[]>(`${this.baseUrl}/${doctorId}`);
  }


   getAvailableTimeSlots(doctorId: string, date: string): Observable<string[]> {
    return this.http.get<string[]>(
      `${this.baseUrl}/daily`,
      { params: { doctorId, date } }
    );
  }



}
