import { Component, OnInit } from '@angular/core';
import { MedicalClinicService } from '@/app/services/medical-clinic.service';
import { MedicalClinic } from '@/app/models/medical-clinic.model';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-clinic-list',
  templateUrl: './clinic-list.component.html',
  styleUrls: ['./clinic-list.component.css'],
  standalone: false,
})
export class ClinicListComponent implements OnInit {
  clinics: MedicalClinic[] = [];
  errorMessage: string | null = null;

  constructor(
    private medicalClinicService: MedicalClinicService,
    private router: Router
  ) {}

 ngOnInit(): void {
    this.loadClinics();
  }

  loadClinics(): void {
    this.medicalClinicService.getClinics().subscribe({
      next: (clinics) => {
        this.clinics = clinics;
        this.errorMessage = null;
      },
      error: (error) => {
        this.errorMessage = 'Failed to load clinics. Please try again.';
        console.error('Error loading clinics:', error);
      }
    });
  }

  onUpdate(clinicId: number): void {
    this.router.navigate(['/update-clinic', clinicId]);
  }

  onDelete(clinicId: number): void {
    if (confirm('Are you sure you want to delete this clinic?')) {
      this.medicalClinicService.deleteClinic(clinicId).subscribe({
        next: () => {
          this.clinics = this.clinics.filter(c => c.id !== clinicId);
        },
        error: (error) => {
          this.errorMessage = 'Failed to delete clinic. Please try again.';
        }
      });
    }
  }
}