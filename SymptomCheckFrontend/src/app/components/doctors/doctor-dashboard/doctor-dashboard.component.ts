import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { DashboardDoctorService } from '@/app/services/dashboard-doctor.service';
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
export class DoctorDashboardComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  
  loading = true;
  profileCompleted = false;
  profileCompletionPercentage = 0;

  // Dashboard data
  doctorStats: any = {};
  appointmentStats: any = {};
  doctorServices: any[] = [];
  doctorAvailability: any[] = [];
  todayAppointments: any[] = [];
  
  // Chart data
  weeklyAppointmentsData: number[] = [];
  weeklyAppointmentsCategories: string[] = [];
  appointmentStatusData: number[] = [];
  appointmentStatusCategories: string[] = [];

  // Calculated stats
  totalAppointments = 0;
  completionRate = 0;
  averageWeeklyAppointments = 0;

  // Profile Completion Modal
  showCompleteProfileModal = false;
  step = 1;
  availabilityForm: FormGroup;
  serviceForm: FormGroup;
  daysOfWeek = Object.values(DayOfWeek);
  doctorId: string = '';

  constructor(
    private dashboardService: DashboardDoctorService,
    private authService: AuthService,
    private doctorService: DoctorService,
    private router: Router,
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
    this.loadDoctorData();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadDoctorData(): void {
    this.authService.currentUser$
      .pipe(takeUntil(this.destroy$))
      .subscribe(user => {
        if (user && user.id) {
          this.doctorId = user.id.toString();
          this.loadDashboardData();
          
          // Check profile status
          this.doctorService.getDoctorProfileStatus(this.doctorId).subscribe({
            next: (status) => {
              // Ensure we assign a primitive boolean (not the Boolean wrapper)
              const isCompleted = !!status;
              this.profileCompleted = isCompleted;
              if (!isCompleted) {
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

  loadDashboardData(): void {
    this.loading = true;

    // Load doctor dashboard data
    this.dashboardService.getDoctorDashboard(this.doctorId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.doctorStats = data.stats || {};
          this.doctorServices = data.services || [];
          this.doctorAvailability = data.availability || [];
          this.profileCompleted = data.profileCompletion?.completionPercentage === 100;
          this.profileCompletionPercentage = data.profileCompletion?.completionPercentage || 0;
        },
        error: (error) => {
          console.error('Error loading doctor dashboard:', error);
        }
      });

    // Load appointment dashboard data
    this.dashboardService.getAppointmentsDashboard(this.doctorId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.appointmentStats = data.stats || {};
          this.setupChartData(data);
          this.calculateStats(data);
        },
        error: (error) => {
          console.error('Error loading appointment dashboard:', error);
        }
      });

    // Load today's appointments
    this.dashboardService.getTodayAppointments(this.doctorId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (appointments) => {
          this.todayAppointments = appointments || [];
          this.loading = false;
        },
        error: (error) => {
          console.error('Error loading today appointments:', error);
          this.loading = false;
        }
      });
  }

  setupChartData(dashboardData: any): void {
    // Weekly appointments chart
    if (dashboardData.weeklyAppointments && Object.keys(dashboardData.weeklyAppointments).length > 0) {
      this.weeklyAppointmentsData = Object.values(dashboardData.weeklyAppointments) as number[];
      this.weeklyAppointmentsCategories = Object.keys(dashboardData.weeklyAppointments);
    } else {
      this.weeklyAppointmentsData = [0, 0, 0, 0, 0, 0, 0];
      this.weeklyAppointmentsCategories = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
    }

    // Appointment status distribution
    if (dashboardData.appointmentsByStatus && Object.keys(dashboardData.appointmentsByStatus).length > 0) {
      this.appointmentStatusData = Object.values(dashboardData.appointmentsByStatus) as number[];
      this.appointmentStatusCategories = Object.keys(dashboardData.appointmentsByStatus);
    } else {
      this.appointmentStatusData = [0, 0, 0, 0];
      this.appointmentStatusCategories = ['Pending', 'Confirmed', 'Completed', 'Cancelled'];
    }
  }

  calculateStats(dashboardData: any): void {
    this.totalAppointments = this.appointmentStats.totalAppointments || 0;
    
    const completed = this.appointmentStats.completedAppointments || 0;
    const total = this.totalAppointments;
    this.completionRate = total > 0 ? Math.round((completed / total) * 100) : 0;
    
    this.averageWeeklyAppointments = Math.round(this.totalAppointments / 4);
  }

  // Profile Completion Modal Methods
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

  submitProfile() {
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
        this.profileCompleted = true;
        this.profileCompletionPercentage = 100;
        this.resetForms();
        // Reload dashboard data to reflect changes
        this.loadDashboardData();
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

  // Existing Dashboard Methods
  refreshData(): void {
    this.loadDashboardData();
  }

  onChartPeriodChange(event: any): void {
    const period = event.target.value;
    console.log('Period changed to:', period);
    // Implement period-based data loading here
  }

  viewAllAppointments(): void {
    this.router.navigate(['/doctor/appointments']);
  }

  goToProfile(): void {
    this.router.navigate(['/doctor/profile']);
  }

  getInitials(name: string): string {
    if (!name) return 'P';
    return name.split(' ').map(n => n[0]).join('').toUpperCase().substring(0, 2);
  }

  formatTime(dateTime: string): string {
    if (!dateTime) return '--:--';
    const date = new Date(dateTime);
    return date.toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' });
  }

  getStatusClass(status: string): string {
    const statusMap: { [key: string]: string } = {
      'COMPLETED': 'completed',
      'CONFIRMED': 'confirmed', 
      'PENDING': 'pending',
      'CANCELLED': 'cancelled'
    };
    return statusMap[status] || 'pending';
  }

  getStatusText(status: string): string {
    const statusMap: { [key: string]: string } = {
      'COMPLETED': 'Terminé',
      'CONFIRMED': 'Confirmé',
      'PENDING': 'En Attente',
      'CANCELLED': 'Annulé'
    };
    return statusMap[status] || status;
  }

  getDayName(day: string): string {
    const dayMap: { [key: string]: string } = {
      'MONDAY': 'Lundi',
      'TUESDAY': 'Mardi',
      'WEDNESDAY': 'Mercredi',
      'THURSDAY': 'Jeudi',
      'FRIDAY': 'Vendredi',
      'SATURDAY': 'Samedi',
      'SUNDAY': 'Dimanche'
    };
    return dayMap[day] || day;
  }
}