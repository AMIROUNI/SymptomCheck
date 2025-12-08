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
  daysOfWeek = [
    { label: 'Lundi', value: 'MONDAY' },
    { label: 'Mardi', value: 'TUESDAY' },
    { label: 'Mercredi', value: 'WEDNESDAY' },
    { label: 'Jeudi', value: 'THURSDAY' },
    { label: 'Vendredi', value: 'FRIDAY' },
    { label: 'Samedi', value: 'SATURDAY' },
    { label: 'Dimanche', value: 'SUNDAY' }
  ];

  private doctorApiUrl = 'http://doctorservice/api/v1/doctor/profile';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    public router: Router, // ✅ Rendre public pour le template
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

    // Ajouter une disponibilité par défaut
    this.addAvailability();
  }

  get availabilities(): FormArray {
    return this.profileForm.get('availabilities') as FormArray;
  }

  get services(): FormArray {
    return this.profileForm.get('services') as FormArray;
  }

  addAvailability() {
    this.availabilities.push(
      this.fb.group({
        days: [[], Validators.required],
        startTime: ['09:00', Validators.required],
        endTime: ['17:00', Validators.required]
      })
    );
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
      price: [0, [Validators.required, Validators.min(0)]],
      durationMinutes: [30, [Validators.required, Validators.min(1)]]
    }));
  }

  removeService(index: number) {
    this.services.removeAt(index);
  }

  toggleDay(index: number, day: string, event: any) {
    const daysArray = this.availabilities.at(index).get('days')!.value as string[];

    if (event.target.checked) {
      if (!daysArray.includes(day)) {
        daysArray.push(day);
      }
    } else {
      const idx = daysArray.indexOf(day);
      if (idx !== -1) {
        daysArray.splice(idx, 1);
      }
    }

    this.availabilities.at(index).get('days')!.setValue([...daysArray]);
  }

  isDaySelected(index: number, day: string): boolean {
    const daysArray = this.availabilities.at(index).get('days')!.value as string[];
    return daysArray.includes(day);
  }

  submitForm() {
    if (this.profileForm.invalid) {
      this.markFormGroupTouched(this.profileForm);
      return;
    }

    this.loading = true;
    const formData = this.profileForm.value;

    const doctorData = {
      ...formData,
      availabilities: formData.availabilities.map((avail: any) => ({
        ...avail,
        daysOfWeek: avail.days
      }))
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
          this.router.navigate(['/doctor/dashboard']);
        },
        error: (err) => {
          console.error('Erreur mise à jour profil:', err);
          this.errorMessage = 'Erreur lors de la mise à jour du profil.';
          this.loading = false;
        }
      });
  }

  private markFormGroupTouched(formGroup: FormGroup) {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      } else if (control instanceof FormArray) {
        control.controls.forEach((arrayControl: any) => {
          if (arrayControl instanceof FormGroup) {
            this.markFormGroupTouched(arrayControl);
          } else {
            arrayControl.markAsTouched();
          }
        });
      } else {
        control?.markAsTouched();
      }
    });
  }

  // ✅ Ajouter la méthode pour naviguer
  navigateToDashboard() {
    this.router.navigate(['/dashboard']);
  }
}
