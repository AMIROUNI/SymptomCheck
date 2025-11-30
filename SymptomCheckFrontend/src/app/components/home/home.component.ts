import { Component,  OnInit } from "@angular/core"
import  { Router } from "@angular/router"
import  { DoctorService } from "../../services/doctor.service"
import  { User } from "../../models/user.model"

@Component({
  selector: "app-home",
  templateUrl: "./home.component.html",
  styleUrls: ["./home.component.scss"],
  standalone:false
})
export class HomeComponent implements OnInit {
  featuredDoctors: User[] = []
  isLoading = true

  constructor(
    private doctorService: DoctorService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.loadFeaturedDoctors()
  }

  loadFeaturedDoctors(): void {
    this.doctorService.getDoctors().subscribe({
      next: (doctors) => {
        // Get first 3 doctors for featured section
        this.featuredDoctors = doctors.slice(0, 3)
        console.log("doctor detected")
        this.isLoading = false
      },
      error: () => {
        this.isLoading = false
      },
    })
  }

  navigateToDoctors(): void {
    this.router.navigate(["/doctors"])
  }

  navigateToAppointment(): void {
    this.router.navigate(["/appointment"])
  }
}
