import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { MedicalClinicService } from '@/app/services/medical-clinic.service';
import { HealthcareService } from '@/app/models/healthcare-service.model';
import { ServiceService } from '@/app/services/service.service';
import { AuthService } from '@/app/services/auth.service';
import { FileUploadService } from '@/app/services/file-upload.service';
import { User } from '@/app/models/user.model';



@Component({
  selector: 'app-add-service',
  templateUrl: './add-service.component.html',
  styleUrls: ['./add-service.component.css'],
  standalone:false
})
export class AddServiceComponent  implements OnInit {

  // popup variables ///////////////////////////////////////////////////////////////
  showPopup = false;
  popupTitle = '';
  popupMessage = '';
  popupIsSuccess = false;
  popupRedirectPath: string | null = null;
  showCancelButton = false;

  serviceForm: FormGroup;
  isSubmitting = false;
  submitSuccess = false;
  imagePreview: string | null = null;
  fileError: string | null = null;
  selectedFile: File | null = null;
  healthcareService!: HealthcareService;
  PhotoUrl: string ="";
  errorMessage="";
  me: User | null = null;

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
  ngOnInit(): void {
    this.me = this.authService.getCurrentUser();
  }

  onFileSelected(event: any): void {
    const file: File = event.target.files[0];
    if (file) {
      this.selectedFile = file;
  
    }
  }

  onSubmit(): void {


    console.log("serviceForm.value:::::", this.serviceForm.value);
    const formData = {
      ...this.serviceForm.value,
      doctorId: this.me?.id,
      imageUrl: this.PhotoUrl,
   

    };
   /* this.healthcareService = this.serviceForm.value;
    this.healthcareService.doctorId = this.authService.getUserId();
    this.healthcareService.imageUrl = this.PhotoUrl;*/
    this.service.addService(formData, this.selectedFile).subscribe((response) => {
      console.log("response:::::", response);
      this.showSuccessPopup();
    },(error)=>{
      console.log("error adding service : ", error);
      this.showErrorPopup("Failed to add service. Please try again.");
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


  
  /// popup methods //////////////////////////////////////////

  showSuccessPopup(title: string = 'Success', message: string = 'Operation completed successfully.,',) {
    this.popupTitle =title;
    this.popupMessage = message;
    this.popupIsSuccess = true ;
    this.popupRedirectPath = '/login';
    this.showCancelButton = false;
    this.showPopup = true;
  }

  showErrorPopup(errorMessage: string) {
    this.popupTitle = 'Login Failed';
    this.popupMessage = errorMessage;
    this.popupIsSuccess = false;
    this.popupRedirectPath = null;
    this.showCancelButton = true;
    this.showPopup = true;
  }

  closePopup() {
    this.showPopup = false;
  }
////////////////////////////////////

}
