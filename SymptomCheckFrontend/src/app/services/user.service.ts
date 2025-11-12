import { Injectable } from '@angular/core';
import { AuthService } from './auth.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DoctorProfileDto, User, UserUpdateDto } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private apiUrl = 'http://localhost:5190/api/user';



  constructor(private http: HttpClient , private authService: AuthService) {}


  getAllUsers():Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl);
  }


  getDoctors(): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/doctors`);
  }

  delete(id: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`)
  }

  completeDoctorProfile(profile: DoctorProfileDto): Observable<any> {
    return this.http.put(`${this.apiUrl}/${profile.id}/complete-profile`, profile)
  }

  update(user: UserUpdateDto): Observable<any> {
    return this.http.put(`${this.apiUrl}/${user.id}`, user)
  }
}
