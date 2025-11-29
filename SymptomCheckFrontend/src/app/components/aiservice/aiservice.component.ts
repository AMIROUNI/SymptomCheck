import { Component } from '@angular/core';
import { AiService, PredictionResponse } from '@/app/services/ai.service';

@Component({
  selector: 'app-aiservice',
  templateUrl: './aiservice.component.html',
  styleUrls: ['./aiservice.component.css'],
  standalone: false
})
export class AiserviceComponent {
  selectedFile: File | null = null;
  previewUrl: string | ArrayBuffer | null = null;
  predictionResult: PredictionResponse | null = null;
  loading = false;
  error = '';
  dragOver = false;

  constructor(private aiService: AiService) {}

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    this.processFile(file);
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    this.dragOver = true;
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    this.dragOver = false;
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    this.dragOver = false;
    
    const files = event.dataTransfer?.files;
    if (files && files.length > 0) {
      this.processFile(files[0]);
    }
  }

  processFile(file: File): void {
    if (!file) return;

    // Validate file type
    if (!file.type.startsWith('image/')) {
      this.error = 'Please select a valid image file (JPEG, PNG, GIF, etc.)';
      return;
    }

    // Validate file size (5MB max)
    if (file.size > 5 * 1024 * 1024) {
      this.error = 'Image size must be less than 5MB';
      return;
    }

    this.selectedFile = file;
    this.error = '';
    this.predictionResult = null;

    // Create preview
    const reader = new FileReader();
    reader.onload = () => {
      this.previewUrl = reader.result;
    };
    reader.readAsDataURL(file);
  }

  uploadImage(): void {
    if (!this.selectedFile) return;

    this.loading = true;
    this.error = '';
    this.predictionResult = null;

    this.aiService.getPrediction(this.selectedFile).subscribe({
      next: (response) => {
        this.loading = false;
        this.predictionResult = response;
        console.log('Prediction result:', response);
      },
      error: (error) => {
        this.loading = false;
        this.error = error.error?.error || error.message || 'Failed to process image. Please try again.';
        console.error('Prediction error:', error);
      }
    });
  }

  removeImage(): void {
    this.selectedFile = null;
    this.previewUrl = null;
    this.predictionResult = null;
    this.error = '';
  }

  getConfidenceColor(confidence: number): string {
    if (confidence >= 0.8) return '#10b981'; // High confidence - green
    if (confidence >= 0.6) return '#f59e0b'; // Medium confidence - amber
    return '#ef4444'; // Low confidence - red
  }

  // Remove getClassName method since we get class_name directly from API
  // getClassName(classIndex: number): string {
  //   const classes = [
  //     'Class A', 'Class B', 'Class C', 'Class D', 'Class E',
  //     'Class F', 'Class G', 'Class H', 'Class I', 'Class J'
  //   ];
  //   return classes[classIndex] || `Class ${classIndex}`;
  // }
}