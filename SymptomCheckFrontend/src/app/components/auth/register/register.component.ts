import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { UserRole } from '../../../models/user.model';
import { MedicalClinic } from '@/app/models/medical-clinic.model';
import { MedicalClinicService } from '@/app/services/medical-clinic.service';
import { FileUploadService } from '@/app/services/file-upload.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
  standalone: false,
})
export class RegisterComponent implements OnInit {
  registerForm!: FormGroup;
  isSubmitting = false;
  errorMessage = '';
  isPatient = true;
  clinics: MedicalClinic[] = [];
  isLoadingClinics = false;
  selectedFile: File | null = null;
  profilePhotoPreview: string | null = null;

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private medicalClinicService: MedicalClinicService,
    private fileUploadService: FileUploadService,
  ) {}

  ngOnInit(): void {
    this.initializeForm();
    this.loadClinics();
  }

  loadClinics(): void {
    this.isLoadingClinics = true;
    this.medicalClinicService.getMedicalClinics().subscribe({
      next: (clinics) => {
        this.clinics = clinics;
        console.log(this.clinics);
        this.isLoadingClinics = false;
      },
      error: (error) => {
        console.error('Failed to load clinics', error);
        this.isLoadingClinics = false;
      },
    });
  }

  initializeForm(): void {
    this.registerForm = this.formBuilder.group({
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      username: ['', [Validators.required, Validators.minLength(4)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]],
      phoneNumber: ['', [Validators.required]],
      // Doctor specific fields
      speciality: [''],
      description: [''],
      diploma: [''],
      clinicId: [null]
    }, {
      validators: this.passwordMatchValidator
    });

    this.updateFormValidation();
  }

  setRole(role: 'patient' | 'doctor'): void {
    this.isPatient = role === 'patient';
    this.updateFormValidation();
  }

  updateFormValidation(): void {
    const specialityControl = this.registerForm.get('speciality');
    const descriptionControl = this.registerForm.get('description');
    const diplomaControl = this.registerForm.get('diploma');

    if (this.isPatient) {
      specialityControl?.clearValidators();
      descriptionControl?.clearValidators();
      diplomaControl?.clearValidators();
    } else {
      specialityControl?.setValidators([Validators.required]);
      descriptionControl?.setValidators([Validators.required]);
      diplomaControl?.setValidators([Validators.required]);
    }

    specialityControl?.updateValueAndValidity();
    descriptionControl?.updateValueAndValidity();
    diplomaControl?.updateValueAndValidity();
  }

  passwordMatchValidator(form: FormGroup) {
    const password = form.get('password')?.value;
    const confirmPassword = form.get('confirmPassword')?.value;

    return password === confirmPassword ? null : { passwordMismatch: true };
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];

      // Create preview URL
      const reader = new FileReader();
      reader.onload = (e: ProgressEvent<FileReader>) => {
        this.profilePhotoPreview = e.target?.result as string;
      };
      reader.readAsDataURL(this.selectedFile);

      console.log('File selected:', this.selectedFile);
    } else {
      this.selectedFile = null;
      this.profilePhotoPreview = null;
    }
  }

  onSubmit(): void {
    if (this.registerForm.invalid) return;

    this.isSubmitting = true;
    this.errorMessage = '';

    const payload = {
      username: this.registerForm.value.username,
      password: this.registerForm.value.password,
      email: this.registerForm.value.email,
      firstName: this.registerForm.value.firstName,
      lastName: this.registerForm.value.lastName,
      phoneNumber: this.registerForm.value.phoneNumber,
      role: this.isPatient ? 'PATIENT' : 'DOCTOR',
      speciality: this.registerForm.value.speciality,
      description: this.registerForm.value.description,
      diploma: this.registerForm.value.diploma,
      clinicId: this.registerForm.value.clinicId
    };

    console.log('Payload:', payload);
    console.log('Selected file:', this.selectedFile);

    this.authService.registerUser(payload, this.selectedFile).subscribe({
      next: () => {
         this.router.navigate(['/login']);
      },
      error: (error) => {
        this.errorMessage = error?.error?.message || 'Registration failed. Please try again.';
        this.isSubmitting = false;
      }
    });
  }
}
