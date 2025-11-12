import { Component, Input } from "@angular/core"
import  { Router } from "@angular/router"
import  { HealthcareService } from "../../../models/healthcare-service.model"

@Component({
  selector: "app-service-card",
  templateUrl: "./service-card.component.html",
  styleUrls: ["./service-card.component.scss"],
  standalone:false
})
export class ServiceCardComponent {
  @Input() service!: HealthcareService

  constructor(private router: Router) {}

  bookAppointment(): void {
    this.router.navigate(["/appointment"], {
      queryParams: {
        doctorId: this.service.doctorId,
        serviceId: this.service.id,
      },
    })
  }

  viewDoctor(): void {
    this.router.navigate(["/doctors", this.service.doctorId])
  }
}
