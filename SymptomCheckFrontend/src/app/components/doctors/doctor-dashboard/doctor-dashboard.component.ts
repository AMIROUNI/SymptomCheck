import { Component, OnInit } from '@angular/core';
import { AuthService } from '@/app/services/auth.service';
import { DoctorService } from '@/app/services/doctor.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AvailabilityHealthDto, DayOfWeek } from '@/app/models/availability-health-dto';

@Component({
  selector: 'app-doctor-dashboard',
  templateUrl: './doctor-dashboard.component.html',
  styleUrls: ['./doctor-dashboard.component.css'],
  standalone: false
})
export class DoctorDashboardComponent implements OnInit {
  showCompleteProfileModal = false;
  step = 1;

  availabilityForm: FormGroup;
  serviceForm: FormGroup;

  daysOfWeek = Object.values(DayOfWeek);
  doctorId: string = '';

  constructor(
    private doctorService: DoctorService,
    private authService: AuthService,
    private fb: FormBuilder
  ) {
    // Step 1: Availability fields
    this.availabilityForm = this.fb.group({
      dayOfWeek: [null, Validators.required],
      startTime: ['', Validators.required],
      endTime: ['', Validators.required]
    });

    // Step 2: Service fields
    this.serviceForm = this.fb.group({
      name: ['', Validators.required],
      description: [''],
      imageUrl: [''],
      durationMinutes: [30, [Validators.required, Validators.min(1)]],
      price: [0, [Validators.required, Validators.min(0)]]
    });
  }

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      if (user) {
        this.doctorId = user.id.toString();
        this.doctorService.getDoctorProfileStatus(this.doctorId).subscribe({
          next: (status) => {
            if (!status) {
              console.log('Profile incomplete, showing modal.');
              this.showCompleteProfileModal = true;
            }
          },
          error: (error) => {
            console.error('Error fetching profile status:', error);
          }
        });
      }
    });
  }

  nextStep() {
    if (this.availabilityForm.valid) {
      this.step = 2;
    } else {
      this.availabilityForm.markAllAsTouched();
    }
  }

  previousStep() {
    this.step = 1;
  }

  submit() {
    if (!this.serviceForm.valid) {
      this.serviceForm.markAllAsTouched();
      return;
    }

    // Combine both forms into the DTO
    const completeProfileData: AvailabilityHealthDto = {
      doctorId: this.doctorId,
      // Availability fields from step 1
      dayOfWeek: this.availabilityForm.value.dayOfWeek,
      startTime: this.availabilityForm.value.startTime,
      endTime: this.availabilityForm.value.endTime,
      // Service fields from step 2
      name: this.serviceForm.value.name,
      description: this.serviceForm.value.description,
      imageUrl: this.serviceForm.value.imageUrl || undefined,
      durationMinutes: this.serviceForm.value.durationMinutes,
      price: this.serviceForm.value.price
    };

    this.doctorService.completeProfile(completeProfileData).subscribe({
      next: (res) => {
        console.log('Profile completed successfully', res);
        this.showCompleteProfileModal = false;
        this.resetForms();
      },
      error: (err) => {
        console.error('Error completing profile', err);
        alert('Failed to complete profile. Please try again.');
      }
    });
  }

  closeModal() {
    this.showCompleteProfileModal = false;
    this.resetForms();
  }

  private resetForms() {
    this.step = 1;
    this.availabilityForm.reset();
    this.serviceForm.reset({ durationMinutes: 30, price: 0 });
  }
}
