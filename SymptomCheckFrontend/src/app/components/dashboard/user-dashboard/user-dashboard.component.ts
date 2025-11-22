import { Component } from "@angular/core"
import  { Router } from "@angular/router"
import  { AuthService } from "../../../services/auth.service"
import  { User, UserRole } from "../../../models/user.model"
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

      console.log(this.currentUser);
      
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
    isDoctor(): boolean {
      console.log('Current user roles:', this.currentUser?.roles);
      return this.currentUser?.roles?.includes(UserRole.DOCTOR) ?? false;
    }
}
