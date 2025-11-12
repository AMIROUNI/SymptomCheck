import { Component } from "@angular/core";
import { Router, NavigationEnd } from "@angular/router";
import { AuthService } from "../../../services/auth.service";
import { User } from "../../../models/user.model";
import { filter } from "rxjs/operators";

@Component({
  selector: "app-admin-dashboard",
  templateUrl: "./admin-dashboard.component.html",
  styleUrls: ["./admin-dashboard.component.scss"],
  standalone:false
})
export class AdminDashboardComponent {
  currentUser: User | null = null;
  currentPageTitle: string = 'Dashboard';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {
    this.currentUser = this.authService.getCurrentUser();

    // Redirect if not admin
    if (!this.currentUser ) {
      this.router.navigate(["/login"]);
    }

    // Update page title on route changes
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => {
        this.currentPageTitle = this.getPageTitle();
      });
  }

  getPageTitle(): string {
    const route = this.router.url.split('/').pop();
    switch(route) {
      case 'dashboard': return 'Dashboard Overview';
      case 'users': return 'User Management';
      case 'doctors': return 'Doctor Management';
      case 'clinics': return 'Clinic Management';
      case 'appointments': return 'Appointment Management';
      case 'services': return 'Healthcare Services';
      case 'reports': return 'Reports & Analytics';
      case 'settings': return 'System Settings';
      default: return 'Admin Dashboard';
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(["/"]);
  }
}
