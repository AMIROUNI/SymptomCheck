import { DoctorProfileStatusDTO } from '@/app/models/doctor-profile-status.model';
import { AuthService } from '@/app/services/auth.service';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-complete-profile',
  standalone: false,
  templateUrl: './complete-profile.component.html',
  styleUrl: './complete-profile.component.css'
})
export class CompleteProfileComponent implements OnInit {
  profileForm!: FormGroup;
  loading = false;
  errorMessage = '';

  private doctorApiUrl = 'http://localhost:8083/api/v1/doctor/profile'; // backend doctor-service

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.profileForm = this.fb.group({
      speciality: ['', Validators.required],
      description: ['', Validators.required],
      diploma: ['', Validators.required],
      profilePhotoUrl: [''],
      clinicId: [''],
      availabilities: this.fb.array([]),
      services: this.fb.array([])
    });

    const currentUser = this.authService.getCurrentUser();
    if (currentUser) {
      this.profileForm.patchValue({
        speciality: currentUser.speciality || '',
        description: currentUser.description || '',
        diploma: currentUser.diploma || '',
        profilePhotoUrl: currentUser.profilePhotoUrl || '',
        clinicId: currentUser.clinicId || ''
      });
    }
  }

  get availabilities(): FormArray {
    return this.profileForm.get('availabilities') as FormArray;
  }

  get services(): FormArray {
    return this.profileForm.get('services') as FormArray;
  }

  addAvailability() {
    this.availabilities.push(this.fb.group({
      day: [0, Validators.required],
      startTime: ['', Validators.required],
      endTime: ['', Validators.required]
    }));
  }

  removeAvailability(index: number) {
    this.availabilities.removeAt(index);
  }

  addService() {
    this.services.push(this.fb.group({
      name: ['', Validators.required],
      description: [''],
      category: [''],
      imageUrl: [''],
      price: [0],
      durationMinutes: [30, Validators.required]
    }));
  }

  removeService(index: number) {
    this.services.removeAt(index);
  }

  submitForm() {
    if (this.profileForm.invalid) return;

    this.loading = true;
    const doctorData = this.profileForm.value as DoctorProfileStatusDTO & {
      availabilities: any[],
      services: any[]
    };

    const currentUser = this.authService.getCurrentUser();
    if (!currentUser) {
      this.errorMessage = 'Utilisateur non connecté.';
      this.loading = false;
      return;
    }

    this.http.put(`${this.doctorApiUrl}/${currentUser.id}`, doctorData)
      .subscribe({
        next: () => {
          this.loading = false;
          this.authService.loadCurrentUser();
          this.router.navigate(['/home']);
        },
        error: (err) => {
          console.error('Erreur mise à jour profil:', err);
          this.errorMessage = 'Erreur lors de la mise à jour du profil.';
          this.loading = false;
        }
      });
  }
}