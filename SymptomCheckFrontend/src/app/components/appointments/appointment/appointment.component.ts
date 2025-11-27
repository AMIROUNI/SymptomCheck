import { Component, OnInit } from "@angular/core"
import { FormBuilder, FormGroup, Validators } from "@angular/forms"
import { ActivatedRoute, Router } from "@angular/router"
import { DoctorService } from "../../../services/doctor.service"
import { ServiceService } from "../../../services/service.service"
import { AppointmentService } from "../../../services/appointment.service"
import { AuthService } from "../../../services/auth.service"
import { User } from "../../../models/user.model"
import { HealthcareService } from "../../../models/healthcare-service.model"
import { Appointment, AppointmentStatus } from "../../../models/appointment.model"
import { UserService } from "@/app/services/user.service"
import { AvailabilityHealthDto } from "@/app/models/availability-health-dto"
import { DoctorAvailabilityService } from "@/app/services/availability.service"

@Component({
  selector: "app-appointment",
  templateUrl: "./appointment.component.html",
  styleUrls: ["./appointment.component.scss"],
  standalone: false
})
export class AppointmentComponent implements OnInit {
  appointmentForm!: FormGroup
  doctors: User[] = []
  services: HealthcareService[] = []
  filteredServices: HealthcareService[] = []
  availableDates: Date[] = []
  availableTimeSlots: string[] = []
  currentUser:User|null= null

  isLoading = true
  isSubmitting = false
  errorMessage = ""
  successMessage = ""
  currentStep = 1

  minDate = new Date()

  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private doctorService: DoctorService,
    private serviceService: ServiceService,
    private appointmentService: AppointmentService,
    private authService: AuthService,
    private userService: UserService,
    private doctorAvailabilityService: DoctorAvailabilityService,
  ) {
    // Set min date to tomorrow
    this.minDate.setDate(this.minDate.getDate() + 1)
  }

  ngOnInit(): void {


    console.log("appointement  Init now !!!!!!!!!!!!!!!!!!!!!!!!!");

    this.initForm()
    this.loadDoctors()
    this.loadServices()

    // Check for query params (doctor and service selection)



      this.authService.currentUser$.subscribe((user) => {
      this.currentUser = user;
      console.log("~###############################################################");

      console.log('Current user:', user);
          const doctorId = this.currentUser?.id
          console.log("***************************************");

          console.log("doctorId = = = = ="+doctorId);

        if (doctorId) {
        this.appointmentForm.get("doctorId")?.setValue(+doctorId)
        this.onDoctorChange()
      }





    });
  }

  initForm(): void {
    this.appointmentForm = this.formBuilder.group({
      doctorId: ["", [Validators.required]],
      serviceId: ["", [Validators.required]],
      date: ["", [Validators.required]],
      time: ["", [Validators.required]],
      description: ["", [Validators.required, Validators.maxLength(1000)]],
    })
  }

  loadDoctors(): void {
    this.userService.getAllUsersByRole('Doctor').subscribe({
      next: (doctors) => {
        this.doctors = doctors
        this.isLoading = false
      },
      error: () => {
        this.isLoading = false
        this.errorMessage = "Failed to load doctors. Please try again."
      },
    })
  }

  loadServices(): void {
    this.serviceService.getServices().subscribe({
      next: (services) => {
        this.services = services
        this.filteredServices = services

        const doctorId = this.appointmentForm.get("doctorId")?.value
        if (doctorId) {
          this.filterServicesByDoctor(doctorId)
        }
      },
    })
  }

  onDoctorChange(): void {
    const doctorId = this.appointmentForm.get("doctorId")?.value

    if (doctorId) {
      this.filterServicesByDoctor(doctorId)
      this.loadAvailableDates(doctorId)

      this.appointmentForm.get("serviceId")?.setValue("")
      this.appointmentForm.get("date")?.setValue("")
      this.appointmentForm.get("time")?.setValue("")
    } else {
      this.filteredServices = this.services
      this.availableDates = []
      this.availableTimeSlots = []
    }
  }

  filterServicesByDoctor(doctorId: string): void {
    this.serviceService.getServicesByDoctor(doctorId).subscribe((services) => {
      this.filteredServices = services
    })
  }

  loadAvailableDates(doctorId: string): void {
    this.appointmentService.getDateTimeOfAppointments(doctorId.toString()).subscribe({
      next: (dateTimes) => {
        // Process the available dates from the backend
        this.availableDates = dateTimes.map(dt => new Date(dt))
        console.log("Available dates:", this.availableDates)
      },
      error: (error) => {
        console.error("Error fetching available dates:", error)
      }
    })
  }

  onDateChange(): void {
  const doctorId = this.appointmentForm.get("doctorId")?.value;
  const dateStr = this.appointmentForm.get("date")?.value;

  if (doctorId && dateStr) {
    // 1️⃣ Get available slots
    this.doctorAvailabilityService.getAvailableTimeSlots(doctorId, dateStr).subscribe({
      next: (slots) => {
        console.log("Available slots from availability service:", slots);
        // 2️⃣ Get taken appointments
        this.appointmentService.getTakenAppointments(doctorId, dateStr).subscribe({
          next: (takenSlots) => {
            console.log("Taken slots from appointment service:", takenSlots);
            // 3️⃣ Remove taken slots from available slots
            this.availableTimeSlots = slots.filter(slot => !takenSlots.includes(slot));
          },
          error: (err) => {

            console.error("Error fetching taken appointments", err);
            this.availableTimeSlots = slots; // fallback
          }
        });
      },
      error: (err) => {
        console.error("Error fetching available slots", err);
        this.availableTimeSlots = [];
      }
    });
  }
}


  loadAvailableTimeSlots(doctorId: string, date: string): void {
  this.availableTimeSlots = [];

  this.doctorAvailabilityService.getAvailableTimeSlots(doctorId, date).subscribe({
    next: (slots) => {
      this.availableTimeSlots = slots;
      console.log("Available time slots:", this.availableTimeSlots);
    },
    error: (error) => {
      console.error("Error fetching slots:", error);
      this.availableTimeSlots = [];
    }
  });
}


  nextStep(): void {
    if (this.currentStep === 1 && this.appointmentForm.get('doctorId')?.valid && this.appointmentForm.get('serviceId')?.valid) {
      this.currentStep = 2
    } else if (this.currentStep === 2 && this.appointmentForm.get('date')?.valid && this.appointmentForm.get('time')?.valid) {
      this.currentStep = 3
    }
  }

  prevStep(): void {
    if (this.currentStep > 1) {
      this.currentStep--
    }
  }

  onSubmit(): void {
  if (this.appointmentForm.invalid) {
    return;
  }

  this.isSubmitting = true;
  this.errorMessage = "";
  this.successMessage = "";

  const formData = this.appointmentForm.value;
  const currentUser = this.authService.getCurrentUser();

  if (!currentUser) {
    this.errorMessage = "You must be logged in to book an appointment.";
    this.isSubmitting = false;
    return;
  }

  // Build date + time into a single Date object (local time)
  const appointmentDate = new Date(formData.date);
  const [hours, minutes] = formData.time.split(":").map(Number);
  appointmentDate.setHours(hours, minutes, 0, 0);
  appointmentDate.setHours(appointmentDate.getHours() + 1);


  // Convert to local datetime WITHOUT timezone
  const localDateTime = appointmentDate.toISOString().slice(0, 19);

  const appointmentData: Appointment = {
    dateTime: localDateTime,
    patientId: currentUser.id,
    doctorId: formData.doctorId,
    description: formData.description,
  };

  console.log("Sending appointmentData:", appointmentData);

  this.appointmentService.createAppointment(appointmentData).subscribe({
    next: () => {
      this.successMessage = "Appointment booked successfully!";
      this.isSubmitting = false;

      setTimeout(() => {
        this.router.navigate(["/dashboard/appointments"]);
      }, 2000);
    },
    error: (error) => {
      this.errorMessage = error.message || "Failed to book appointment. Please try again.";
      this.isSubmitting = false;
    },
  });
}


  formatAppointmentDateTime(): string {
    const dateStr = this.appointmentForm.get("date")?.value
    const timeStr = this.appointmentForm.get("time")?.value
    return  `${dateStr} at ${timeStr}`
  }

  getSelectedDoctorName(): string {
    const doctorId = this.appointmentForm.get("doctorId")?.value
    const doctor = this.doctors.find(d => d.id === doctorId)
    return doctor ? `${doctor.firstName} ${doctor.lastName}` : ""
  }
  getSelectedServiceName(): string {
    const serviceId = this.appointmentForm.get("serviceId")?.value
    const service = this.services.find(s => s.id === serviceId)
    return service ? service.name : ""
  }
}
