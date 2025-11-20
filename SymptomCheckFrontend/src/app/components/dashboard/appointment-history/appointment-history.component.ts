import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AppointmentService } from '../../../services/appointment.service';
import { DoctorService } from '../../../services/doctor.service';
import { AuthService } from '../../../services/auth.service';
import {
  Appointment,
  AppointmentStatus,
} from '../../../models/appointment.model';
import { User, UserRole } from '@/app/models/user.model';

@Component({
  selector: 'app-appointment-history',
  templateUrl: './appointment-history.component.html',
  styleUrls: ['./appointment-history.component.scss'],
  standalone: false,
})
export class AppointmentHistoryComponent implements OnInit {
  appointments: Appointment[] = [];
  filteredAppointments: Appointment[] = [];
  isLoading = true;
  errorMessage = '';
  statusFilter = 'all';
  currentUser: User | null = null;
  constructor(
    private appointmentService: AppointmentService,
    private doctorService: DoctorService,
    private authService: AuthService,
    private router: Router
  ) {
    this.currentUser = this.authService.getCurrentUser();

    // Redirect if not logged in
    if (!this.currentUser) {
      this.router.navigate(['/login']);
    }
  }

  ngOnInit(): void {
    console.log('current user id= ', this.currentUser?.id);
    this.loadAppointments();
  }

  loadAppointments(): void {
    this.isLoading = true;
    if (this.currentUser) {
      console.log(this.currentUser.roles, '      ', UserRole.PATIENT);
      if (this.currentUser.roles?.includes(UserRole.PATIENT))
        this.appointmentService
          .getPatientAppointments(Number(this.currentUser.id))
          .subscribe({
            next: (appointments) => {
              this.appointments = appointments.map((app) =>
                this.normalizeAppointmentStatus(app)
              );
              this.appointments.forEach((appointment) =>
                this.loadDoctorDetails(appointment)
              );
              this.filterAppointments();
              this.isLoading = false;
            },
            error: () => {
              this.errorMessage =
                'Failed to load appointments. Please try again.';
              this.isLoading = false;
            },
          });
      else
        this.appointmentService
          .getDoctorAppointments(this.currentUser.id)
          .subscribe({
            next: (appointments) => {
              this.appointments = appointments.map((app) =>
                this.normalizeAppointmentStatus(app)
              );
              this.appointments.forEach((appointment) =>
                this.loadDoctorDetails(appointment)
              );
              this.filterAppointments();
              this.isLoading = false;
            },
            error: () => {
              this.errorMessage =
                'Failed to load appointments. Please try again.';
              this.isLoading = false;
            },
          });
    }
  }

  loadDoctorDetails(appointment: Appointment): void {
    this.doctorService.getDoctorById(appointment.doctorId).subscribe({
      next: (doctor) => {
        appointment.doctor = doctor;
      },
    });
  }

  filterAppointments(): void {
    if (this.statusFilter === 'all') {
      this.filteredAppointments = [...this.appointments];
    } else {
      this.filteredAppointments = this.appointments.filter(
        (appointment) => appointment.status === this.statusFilter
      );
    }

    // Sort by date (newest first)
    this.filteredAppointments.sort((a, b) => {
      return new Date(b.dateTime).getTime() - new Date(a.dateTime).getTime();
    });
  }

  onStatusFilterChange(status: string): void {
    this.statusFilter = status;
    this.filterAppointments();
  }

  cancelAppointment(appointmentId: number): void {
    if (confirm('Are you sure you want to cancel this appointment?')) {
      this.appointmentService
        .updateAppointmentStatus(appointmentId, 2)
        .subscribe({
          next: () => {
            // Update local appointment status
            const appointment = this.appointments.find(
              (a) => a.id === appointmentId
            );
            if (appointment) {
              appointment.status = AppointmentStatus.CANCELLED;
              this.filterAppointments();
            }
          },
          error: () => {
            this.errorMessage =
              'Failed to cancel appointment. Please try again.';
          },
        });
    }
  }

  getStatusClass(status: AppointmentStatus): string {
    switch (status) {
      case AppointmentStatus.PENDING:
        return 'status-pending';
      case AppointmentStatus.CONFIRMED:
        return 'status-confirmed';
      case AppointmentStatus.COMPLETED:
        return 'status-completed';
      case AppointmentStatus.CANCELLED:
        return 'status-cancelled';
      default:
        return '';
    }
  }

  bookAppointment(): void {
    this.router.navigate(['/appointment']);
  }

  get isDoctor(): boolean {
   // return this.authService.isDoctor();
   return false;
  }

  confirmAppointment(appointmentId: number): void {
    if (
      this.isDoctor &&
      confirm('Are you sure you want to confirm this appointment?')
    ) {

      this.appointmentService
        .updateAppointmentStatus(appointmentId, 1)
        .subscribe({
          next: () => {
            // Update local appointment status
            const appointment = this.appointments.find(
              (a) => a.id === appointmentId
            );
            if (appointment) {
              appointment.status = AppointmentStatus.CONFIRMED;
              this.filterAppointments();
            }
          },
          error: () => {
            this.errorMessage =
              'Failed to confirm appointment. Please try again.';
          },
        });
    }
  }

  completeAppointment(appointmentId: number): void {
    if (
      this.isDoctor &&
      confirm('Are you sure you want to mark this appointment as completed?')
    ) {
      this.appointmentService
        .updateAppointmentStatus(appointmentId, 3)
        .subscribe({
          next: () => {
            // Update local appointment status
            const appointment = this.appointments.find(
              (a) => a.id === appointmentId
            );
            if (appointment) {
              appointment.status = AppointmentStatus.COMPLETED;
              this.filterAppointments();
            }
          },
          error: () => {
            this.errorMessage =
              'Failed to complete appointment. Please try again.';
          },
        });
    }
  }

  private normalizeAppointmentStatus(appointment: Appointment): Appointment {
    const statusMap: { [key: number]: AppointmentStatus } = {
      0: AppointmentStatus.PENDING,
      1: AppointmentStatus.CONFIRMED,
      2: AppointmentStatus.CANCELLED,
      3: AppointmentStatus.COMPLETED,
    };

    if (typeof appointment.status === 'number') {
      appointment.status = statusMap[appointment.status];
    }

    return appointment;
  }
}
