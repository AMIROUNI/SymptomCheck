// medical-clinic-form.component.ts
import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MedicalClinicService } from '@/app/services/medical-clinic.service';
import { MedicalClinic } from '@/app/models/medical-clinic.model';

@Component({
  selector: 'app-medical-clinic-form',
  standalone: false,
  templateUrl: './medical-clinic-form.component.html',
  styleUrls: ['./medical-clinic-form.component.css']
})
export class MedicalClinicFormComponent implements OnInit {
  clinicForm: FormGroup;
  isSubmitting = false;
  submitSuccess = false;
  fileError: string | null = null;

  constructor(private medicalClinicService: MedicalClinicService) {
    this.clinicForm = new FormGroup({
      name: new FormControl('', [Validators.required, Validators.maxLength(100)]),
      address: new FormControl('', [Validators.required, Validators.maxLength(200)]),
      phone: new FormControl('', [
        Validators.required,
        Validators.pattern(/^[\+]?[(]?[0-9]{3}[)]?[-\s\.]?[0-9]{3}[-\s\.]?[0-9]{4,6}$/)
      ]),
      websiteUrl: new FormControl('', [
        Validators.pattern(/^(https?:\/\/)?([\da-z\.-]+)\.([a-z\.]{2,6})([\/\w \.-]*)*\/?$/)
      ]),
      city: new FormControl('', [Validators.required, Validators.maxLength(50)]),
      country: new FormControl('', [Validators.required, Validators.maxLength(50)])
    });
  }

  ngOnInit(): void {}

  onSubmit(): void {
    if (this.clinicForm.invalid) {
      this.markFormGroupTouched(this.clinicForm);
      return;
    }

    this.isSubmitting = true;
    const clinic: MedicalClinic = this.clinicForm.value;

    this.medicalClinicService.createMedicalClinic(clinic).subscribe({
      next: (response) => {
        console.log('Clinic created successfully:', response);
        this.submitSuccess = true;
        this.isSubmitting = false;

        // Hide success message after 3 seconds
        setTimeout(() => {
          this.submitSuccess = false;
        }, 3000);

        // Reset form after successful submission
        this.resetForm();
      },
      error: (error) => {
        this.isSubmitting = false;
        this.fileError = 'Failed to create clinic. Please try again.';
        console.error('Error creating clinic:', error);
      }
    });
  }

  resetForm(): void {
    this.clinicForm.reset();
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

  get f() {
    return this.clinicForm.controls;
  }
}