
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FileUploadService {
  public apiUrl = 'http://localhost:5190/';
  constructor(private http: HttpClient) {}

  uploadProfilePhoto(file: File): Observable<{ imageUrl: string }> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<{ imageUrl: string }>(this.apiUrl+'api/file/upload-profile-photo', formData);
  }



  getFullImageUrl(relativePath: string | null | undefined): string {
    if (!relativePath) {
      return this.getDefaultProfileImage();
    }

    if (relativePath.startsWith('http')) {
      return relativePath;
    }

    return `${this.apiUrl}${relativePath}`;
  }

  getDefaultProfileImage(): string {
    return 'default-profile.png';
  }
}