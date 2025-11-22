import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { DashboardDoctorService } from '@/app/services/dashboard-doctor.service';
import { AuthService } from '@/app/services/auth.service';
import { DoctorService } from '@/app/services/doctor.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AvailabilityHealthDto } from '@/app/models/availability-health-dto';

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
  daysOfWeek = [
    { label: 'Lundi', value: 'MONDAY' },
    { label: 'Mardi', value: 'TUESDAY' },
    { label: 'Mercredi', value: 'WEDNESDAY' },
    { label: 'Jeudi', value: 'THURSDAY' },
    { label: 'Vendredi', value: 'FRIDAY' },
    { label: 'Samedi', value: 'SATURDAY' },
    { label: 'Dimanche', value: 'SUNDAY' }
  ];
  doctorId: string = '';

  constructor(
    private dashboardService: DashboardDoctorService,
    private authService: AuthService,
    private doctorService: DoctorService,
    private router: Router,
    private fb: FormBuilder
  ) {
    // Step 1: Availability fields avec liste de jours
    this.availabilityForm = this.fb.group({
      daysOfWeek: [[], Validators.required],
      startTime: ['09:00', Validators.required],
      endTime: ['17:00', Validators.required]
    });

    // Step 2: Service fields
    this.serviceForm = this.fb.group({
      name: ['', Validators.required],
      description: [''],
      imageUrl: [''],
      durationMinutes: [30, [Validators.required, Validators.min(1)]],
      price: [0, [Validators.required, Validators.min(0)]],
      category: ['']
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
          
          this.doctorService.getDoctorProfileStatus(this.doctorId).subscribe({
            next: (status) => {
              this.profileCompleted = !!status;
              if (!this.profileCompleted) {
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

  // ✅ Ajouter la méthode manquante
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

  // ✅ Méthodes pour le template
  refreshData(): void {
    this.loadDashboardData();
  }

  onChartPeriodChange(event: any): void {
    const period = event.target.value;
    console.log('Period changed to:', period);
    // Implémenter le chargement des données par période ici
  }

  viewAllAppointments(): void {
    this.router.navigate(['/doctor/appointments']);
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

  // Méthodes pour gérer les jours dans le modal
  toggleDay(day: string, event: any) {
    const daysArray = this.availabilityForm.get('daysOfWeek')!.value as string[];
    
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
    
    this.availabilityForm.get('daysOfWeek')!.setValue([...daysArray]);
  }

  isDaySelected(day: string): boolean {
    const daysArray = this.availabilityForm.get('daysOfWeek')!.value as string[];
    return daysArray.includes(day);
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

    // ✅ Corriger le DTO - utiliser dayOfWeek au lieu de daysOfWeek si c'est le nom dans le DTO
    const completeProfileData: any = {
      doctorId: this.doctorId,
      // Utiliser le bon nom de propriété selon votre DTO
      dayOfWeek: this.availabilityForm.value.daysOfWeek, // ou daysOfWeek selon votre DTO
      startTime: this.availabilityForm.value.startTime,
      endTime: this.availabilityForm.value.endTime,
      name: this.serviceForm.value.name,
      description: this.serviceForm.value.description,
      imageUrl: this.serviceForm.value.imageUrl || undefined,
      durationMinutes: this.serviceForm.value.durationMinutes,
      price: this.serviceForm.value.price,
      category: this.serviceForm.value.category || 'General'
    };

    this.doctorService.completeProfile(completeProfileData).subscribe({
      next: (res) => {
        console.log('Profile completed successfully', res);
        this.showCompleteProfileModal = false;
        this.profileCompleted = true;
        this.profileCompletionPercentage = 100;
        this.resetForms();
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
    this.availabilityForm.reset({
      daysOfWeek: [],
      startTime: '09:00',
      endTime: '17:00'
    });
    this.serviceForm.reset({ 
      durationMinutes: 30, 
      price: 0,
      category: ''
    });
  }

  // Méthode pour afficher plusieurs jours dans le dashboard
  getDaysDisplay(days: string[]): string {
    if (!days || days.length === 0) return 'Aucun jour';
    if (days.length === 7) return 'Tous les jours';
    
    return days.map(day => this.getDayName(day)).join(', ');
  }
}