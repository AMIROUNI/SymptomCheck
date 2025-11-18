import { Injectable } from '@angular/core';
import { AuthService } from './auth.service';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DoctorProfileDto, User, UserUpdateDto } from '../models/user.model';
import { environment } from '@/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private apiUrl = `${environment.userserviceApiUrl}/users`;



  constructor(private http: HttpClient , private authService: AuthService) {}



 getAllUsersByRole(role: string):Observable<User[]> {
      const params = new HttpParams().set('role', role.toString());
    return this.http.get<User[]>(`${this.apiUrl}/by-role`, { params });
  }

  desableOrEnableUser(id: string, enable: boolean): Observable<any> {
  const params = new HttpParams().set('isEnable', enable.toString());
  return this.http.patch(`${this.apiUrl}/disable/${id}`, null, { params });
}

  getAllUsers():Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl);
  }

  getUserById(id: string): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${id}`);
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
