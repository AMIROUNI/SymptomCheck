import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { MedicalClinicService } from '@/app/services/medical-clinic.service';
import { HealthcareService } from '@/app/models/healthcare-service.model';
import { ServiceService } from '@/app/services/service.service';
import { AuthService } from '@/app/services/auth.service';
import { FileUploadService } from '@/app/services/file-upload.service';



@Component({
  selector: 'app-add-service',
  templateUrl: './add-service.component.html',
  styleUrls: ['./add-service.component.css'],
  standalone:false
})
export class AddServiceComponent {
  serviceForm: FormGroup;
  isSubmitting = false;
  submitSuccess = false;
  imagePreview: string | null = null;
  fileError: string | null = null;
  selectedFile: File | null = null;
  healthcareService!: HealthcareService;
  PhotoUrl: string ="";
  errorMessage="";

  constructor(private fb: FormBuilder, private authService:AuthService,private service:ServiceService,private fileUploadService:FileUploadService) {
    this.serviceForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      category: ['', Validators.required],
      price: [0, [Validators.required, Validators.min(0)]],
      durationMinutes: [30, [Validators.required, Validators.min(1)]],
      imageUrl: ['']
    });
  }

  onFileSelected(event: any): void {
    const file: File = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      this.uploadPhoto();
    }
  }

  uploadPhoto(): void {
    if (!this.selectedFile) return;

    this.isSubmitting = true;
    this.fileUploadService.uploadProfilePhoto(this.selectedFile).subscribe({
      next: (response) => {
        this.PhotoUrl = response.imageUrl;
        this.isSubmitting = false;
      },
      error: (error) => {
        this.errorMessage = 'Failed to upload profile photo. Please try again.';
        this.isSubmitting = false;
      }
    });
  }

  onSubmit(): void {


    console.log("serviceForm.value:::::", this.serviceForm.value);
    const formData = {
      ...this.serviceForm.value,
      imageUrl: this.PhotoUrl,
   

    };
   /* this.healthcareService = this.serviceForm.value;
    this.healthcareService.doctorId = this.authService.getUserId();
    this.healthcareService.imageUrl = this.PhotoUrl;*/
    this.service.addService(formData).subscribe((response) => {
      console.log("response:::::", response);
    },(error)=>{
      console.log("error adding service : ", error);
    });



  }

  resetForm(): void {
    this.serviceForm.reset({
      durationMinutes: 30
    });
    this.imagePreview = null;
    this.selectedFile = null;
    this.fileError = null;
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }
}
