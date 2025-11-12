import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MedicalClinicService } from '@/app/services/medical-clinic.service';
import { MedicalClinic } from '@/app/models/medical-clinic.model';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-update-medical-clinic',
  standalone: false,
  templateUrl: './update-medical-clinic.component.html',
  styleUrl: './update-medical-clinic.component.css'
})
export class UpdateMedicalClinicComponent implements OnInit {
  clinicForm: FormGroup;
  isSubmitting = false;
  submitSuccess = false;
  fileError: string | null = null;
  clinicId: number;

  constructor(
    private medicalClinicService: MedicalClinicService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.clinicId = +this.route.snapshot.paramMap.get('id')!;
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

  ngOnInit(): void {
    this.loadClinic();
  }

  loadClinic(): void {
    this.medicalClinicService.getClinicById(this.clinicId).subscribe({
      next: (clinic) => {
        this.clinicForm.patchValue(clinic);
        this.fileError = null;
      },
      error: (error) => {
        this.fileError = 'Failed to load clinic. Please try again.';
        console.error('Error loading clinic:', error);
      }
    });
  }

  onSubmit(): void {
    if (this.clinicForm.invalid) {
      this.markFormGroupTouched(this.clinicForm);

      return;
     
    }

    this.isSubmitting = true;
    const clinic: MedicalClinic = { ...this.clinicForm.value, id: this.clinicId };

    this.medicalClinicService.updateClinic(this.clinicId, clinic).subscribe({
      next: () => {
        this.submitSuccess = true;
        this.isSubmitting = false;
        setTimeout(() => {
          this.submitSuccess = false;
          this.router.navigate(['/clinic-list']);
        }, 3000);
      },
      error: (error) => {
        this.isSubmitting = false;
        this.fileError = 'Failed to update clinic. Please try again.';
        console.error('Error updating clinic:', error);
      }
    });
  }

  resetForm(): void {
    this.loadClinic();
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