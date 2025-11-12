import { AuthService } from "@/app/services/auth.service"
import { Component } from "@angular/core"
import { Router } from "@angular/router"

@Component({
  selector: "app-dashboard",
  templateUrl: "./dashboard.component.html",
  styleUrls: ["./dashboard.component.css"],
  standalone:false
})
export class DashboardComponent {
  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  logout(): void {
    this.authService.logout()
    this.router.navigate(["/login"])
  }
}
