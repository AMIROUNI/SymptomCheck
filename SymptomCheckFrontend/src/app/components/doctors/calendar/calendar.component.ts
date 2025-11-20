import { Component, OnInit, ChangeDetectorRef, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FullCalendarModule } from '@fullcalendar/angular';
import { CalendarOptions, EventClickArg, EventInput } from '@fullcalendar/core';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';
import listPlugin from '@fullcalendar/list';
import { Appointment, AppointmentStatus } from '@/app/models/appointment.model';
import { AppointmentService } from '@/app/services/appointment.service';
import { AuthService } from '@/app/services/auth.service';


@Component({
  selector: 'app-calendar',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    FullCalendarModule
  ],
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class CalendarComponent implements OnInit {
  calendarOptions!: CalendarOptions;
  appointments: Appointment[] = [];
  filteredAppointments: Appointment[] = [];
  selectedEvent: any = null;
  showEventModal: boolean = false;
  loading: boolean = true;
  currentView: string = 'dayGridMonth';
  selectedStatus: string = 'all';
  searchQuery: string = '';
  currentDoctorId: string = '';
AppointmentStatus: any;

  constructor(
    private appointmentService: AppointmentService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadCurrentDoctorAppointments();
  }

  loadCurrentDoctorAppointments(): void {
    this.loading = true;
    
    // Get current user from auth service
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser || !currentUser.id) {
      console.error('No current user found or user ID missing');
      this.loading = false;
      return;
    }

    // Use the current user's ID as doctor ID
    this.currentDoctorId = currentUser.id;
    
    this.appointmentService.getByDoctorIde(this.currentDoctorId).subscribe({
      next: (appointments: Appointment[]) => {
        this.appointments = appointments;
        this.filteredAppointments = appointments;
        this.initializeCalendar(appointments);
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error loading appointments:', error);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  initializeCalendar(appointments: Appointment[]): void {
    const events = appointments.map(appointment => ({
      id: appointment.id?.toString(),
      title: this.getEventTitle(appointment),
      start: appointment.dateTime,
      end: this.calculateEndTime(appointment),
      extendedProps: {
        status: appointment.status,
        description: appointment.description,
        patientId: appointment.patientId,
        doctorId: appointment.doctorId,
        appointment: appointment
      },
      className: this.getEventClass(appointment.status!)
    }));

    this.calendarOptions = {
      initialView: this.currentView,
      plugins: [dayGridPlugin, interactionPlugin, listPlugin],
      events: events,
      selectable: true,
      headerToolbar: {
        left: 'prev,next today',
        center: 'title',
        right: 'dayGridMonth,dayGridWeek,dayGridDay,listWeek'
      },
      eventClick: this.handleEventClick.bind(this),
      eventClassNames: (arg) => ['fc-event-custom'],
      dayMaxEvents: 3,
      height: 'auto',
      views: {
        listWeek: {
          eventLimit: 10
        }
      },
      eventTimeFormat: {
        hour: '2-digit',
        minute: '2-digit',
        hour12: true
      }
    };
  }

  getEventTitle(appointment: Appointment): string {
    const status = appointment.status!.toLowerCase();
    const patientRef = appointment.patientId ? `Patient: ${appointment.patientId.substring(0, 8)}...` : 'Unknown Patient';
    
    switch (status) {
      case 'confirmed':
        return `✅ ${patientRef}`;
      case 'pending':
        return `⏳ ${patientRef}`;
      case 'cancelled':
        return `❌ ${patientRef}`;
      case 'completed':
        return `✓ ${patientRef}`;
      default:
        return patientRef;
    }
  }

  getEventClass(status: AppointmentStatus): string {
    switch (status) {
      case AppointmentStatus.CONFIRMED:
        return 'fc-event-status-confirmed';
      case AppointmentStatus.PENDING:
        return 'fc-event-status-pending';
      case AppointmentStatus.CANCELLED:
        return 'fc-event-status-cancelled';
      case AppointmentStatus.COMPLETED:
        return 'fc-event-status-completed';
      default:
        return 'fc-event-status-pending';
    }
  }

  calculateEndTime(appointment: Appointment): Date | undefined {
    if (!appointment.dateTime) return undefined;
    
    // Assuming appointments are 30 minutes by default
    const startTime = new Date(appointment.dateTime);
    const endTime = new Date(startTime.getTime() + 30 * 60000); // Add 30 minutes
    return endTime;
  }

  handleEventClick(clickInfo: EventClickArg): void {
    const appointment = clickInfo.event.extendedProps['appointment'];
    this.selectedEvent = {
      id: clickInfo.event.id,
      title: clickInfo.event.title,
      start: clickInfo.event.start,
      end: clickInfo.event.end,
      status: appointment.status,
      description: appointment.description,
      patientId: appointment.patientId,
      doctorId: appointment.doctorId,
      appointment: appointment
    };
    this.showEventModal = true;
    this.cdr.detectChanges();
  }

  closeEventModal(): void {
    this.showEventModal = false;
    this.selectedEvent = null;
    this.cdr.detectChanges();
  }

  getStatusClass(status: AppointmentStatus): string {
    return status.toLowerCase().replace(/\s+/g, '');
  }

  getAppointmentsByStatus(status: AppointmentStatus ): Appointment[] {
 
    return this.filteredAppointments.filter(item => item.status === status);
  }

  filterEvents(): void {
    let filtered = [...this.appointments];

    // Status filter
    if (this.selectedStatus !== 'all') {
      filtered = filtered.filter(item => 
        item.status!.toLowerCase() === this.selectedStatus.toLowerCase()
      );
    }

    // Search filter
    if (this.searchQuery) {
      const query = this.searchQuery.toLowerCase();
      filtered = filtered.filter(item => 
        item.description?.toLowerCase().includes(query) ||
        item.patientId.toLowerCase().includes(query) ||
        item.status!.toLowerCase().includes(query)
      );
    }

    this.filteredAppointments = filtered;
    this.initializeCalendar(filtered);
    this.cdr.detectChanges();
  }

  changeView(view: string): void {
    this.currentView = view;
    if (this.calendarOptions) {
      this.calendarOptions.initialView = view;
      this.initializeCalendar(this.filteredAppointments);
    }
    this.cdr.detectChanges();
  }

  handleSearch(): void {
    this.filterEvents();
  }

  refreshAppointments(): void {
    this.loadCurrentDoctorAppointments();
  }

  getStatusDisplayText(status: AppointmentStatus): string {
    switch (status) {
      case AppointmentStatus.PENDING:
        return 'Pending';
      case AppointmentStatus.CONFIRMED:
        return 'Confirmed';
      case AppointmentStatus.CANCELLED:
        return 'Cancelled';
      case AppointmentStatus.COMPLETED:
        return 'Completed';
      default:
        return status;
    }
  }

  formatAppointmentTime(dateTime: string | Date): string {
    const date = new Date(dateTime);
    return date.toLocaleString('en-US', {
      weekday: 'short',
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}