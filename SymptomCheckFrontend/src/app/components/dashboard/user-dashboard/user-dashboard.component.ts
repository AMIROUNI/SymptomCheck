import { Component } from "@angular/core"
import  { Router } from "@angular/router"
import  { AuthService } from "../../../services/auth.service"
import  { User } from "../../../models/user.model"
import { environment } from "@/environments/environment"

@Component({
  selector: "app-user-dashboard",
  templateUrl: "./user-dashboard.component.html",
  styleUrls: ["./user-dashboard.component.scss"],
  standalone:false
})
export class UserDashboardComponent {
  currentUser: User | null = null

  constructor(
    private authService: AuthService,
    private router: Router,
  ) {
    this.currentUser = this.authService.getCurrentUser()

    // Redirect if not logged in
    if (!this.currentUser) {
      this.router.navigate(["/login"])
    }
  }

   ngOnInit(): void {
    this.authService.currentUser$.subscribe((user) => {
      this.currentUser = user
      console.log(this.currentUser?.profilePhotoUrl)
    })


  }
 profileImageUrl: string = '';


  logout(): void {
    this.authService.logout()
    this.router.navigate(["/"])
  }



    apiUrl = environment.uploadsUrl;

    getUserImage(filename: string| undefined): string {
      return `${environment.uploadsUrl}/${filename}`;
    }
}
