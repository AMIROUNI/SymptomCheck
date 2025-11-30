import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@/environments/environment';

export interface PredictionResponse {
  class_name: string;  
  confidence: number;
  message?: string;
  image_size_used?: string;
}
@Injectable({
  providedIn: 'root'
})
export class AiService {
  private apiUrl = `${environment.aiApiUrl}`;

  constructor(private http: HttpClient) { }

  /**
   * Send image to AI model for prediction
   * @param imageFile - The image file to analyze
   * @returns Observable with prediction results
   */
  getPrediction(imageFile: File): Observable<PredictionResponse> {
    // Create FormData to send the file
    const formData = new FormData();
    formData.append('image', imageFile, imageFile.name);

    // Note: Don't set Content-Type header for FormData - browser will set it automatically
    // with the correct boundary for multipart/form-data

    return this.http.post<PredictionResponse>(this.apiUrl, formData);
  }

  /**
   * Alternative method with progress reporting (for large files)
   */
  getPredictionWithProgress(imageFile: File): Observable<any> {
    const formData = new FormData();
    formData.append('image', imageFile, imageFile.name);

    return this.http.post(this.apiUrl, formData, {
      reportProgress: true,
      observe: 'events'
    });
  }
}