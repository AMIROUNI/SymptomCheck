import { Component, OnInit } from "@angular/core";
import { Router, NavigationEnd } from "@angular/router";
import { AuthService } from "../../../services/auth.service";
import { User } from "../../../models/user.model";
import { filter } from "rxjs/operators";
import { DashboardAdminService } from '@/app/services/dashboard-admin.service';
import { BarGrowthChartComponent } from '../../shared/dashboard-components/bar-growth-chart/bar-growth-chart.component';




interface DashboardStats {
  totalUsers: number;
  totalDoctors: number;
  totalClinics: number;
  totalAppointments: number;
  pendingDoctors: number;
  activeAppointments: number;
  totalRevenue: number;
  newUsersThisWeek: number;
}

interface ChartConfig {
  data: number[];
  categories: string[];
  title: string;
  type?: string;
  color?: string;
}

@Component({
  selector: "app-admin-dashboard",
  templateUrl: "./admin-dashboard.component.html",
  styleUrls: ["./admin-dashboard.component.scss"],
  standalone:false
})



export class AdminDashboardComponent  implements OnInit{
 dashboardStats: DashboardStats = {
    totalUsers: 0,
    totalDoctors: 0,
    totalClinics: 0,
    totalAppointments: 0,
    pendingDoctors: 0,
    activeAppointments: 0,
    totalRevenue: 0,
    newUsersThisWeek: 0
  };

  // Chart Data - Fixed structure
  userGrowthChart: ChartConfig = {
    data: [65, 78, 90, 82, 105, 120, 135],
    categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul'],
    title: 'User Growth Trend'
  };
  
  appointmentStatusChart: ChartConfig = {
    data: [45, 78, 32, 15],
    categories: ['Pending', 'Confirmed', 'Completed', 'Cancelled'],
    title: 'Appointment Status Distribution'
  };

  revenueChart: ChartConfig = {
    data: [12000, 19000, 15000, 18000, 22000, 28000, 25000],
    categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul'],
    title: 'Revenue Overview'
  };

  // Loading states
  isLoading: boolean = true;
  isRefreshing: boolean = false;
  chartLoading: boolean = false;

  // Recent activity data with icons
  recentActivities = [
    { type: 'user', message: 'New patient registered', time: '2 min ago', icon: '👤' },
    { type: 'appointment', message: 'New appointment booked', time: '5 min ago', icon: '📅' },
    { type: 'doctor', message: 'Doctor profile approved', time: '10 min ago', icon: '👨‍⚕️' },
    { type: 'payment', message: 'Payment received', time: '15 min ago', icon: '💰' }
  ];

  constructor(
    private authService: AuthService,
    private router: Router,
    private dashboardService: DashboardAdminService
  ) {}

  ngOnInit(): void {
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    this.isLoading = true;
    
    // Load all statistics in parallel
    Promise.all([
      this.dashboardService.getUserStats().toPromise(),
      this.dashboardService.getDoctorStats().toPromise(),
      this.dashboardService.getClinicStats().toPromise(),
      this.dashboardService.getAppointmentStats().toPromise()
    ]).then(([userStats, doctorStats, clinicStats, appointmentStats]) => {
      // Combine all stats
      this.dashboardStats = {
        totalUsers: userStats?.totalUsers || 0,
        totalDoctors: doctorStats?.totalDoctors || 0,
        totalClinics: clinicStats?.totalClinics || 0,
        totalAppointments: appointmentStats?.totalAppointments || 0,
        pendingDoctors: doctorStats?.pendingDoctors || 0,
        activeAppointments: appointmentStats?.todayAppointments || 0,
        totalRevenue: appointmentStats?.totalRevenue || 0,
        newUsersThisWeek: userStats?.newUsersThisWeek || 0
      };
      
      this.isLoading = false;
      this.isRefreshing = false;
    }).catch(error => {
      console.error('Error loading dashboard data:', error);
      this.isLoading = false;
      this.isRefreshing = false;
    });
  }

  refreshData(): void {
    this.isRefreshing = true;
    this.loadDashboardData();
  }

  navigateTo(section: string): void {
    this.router.navigate([`/admin/${section}`]);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  // Quick action methods
  approveDoctors(): void {
    this.router.navigate(['/admin/doctors'], { queryParams: { status: 'pending' } });
  }

  viewTodayAppointments(): void {
    const today = new Date().toISOString().split('T')[0];
    this.router.navigate(['/admin/appointments'], { 
      queryParams: { date: today } 
    });
  }

  manageUsers(): void {
    this.router.navigate(['/admin/users']);
  }

  // Chart event handlers
  onChartClick(event: any): void {
    console.log('Chart clicked:', event);
    // Handle chart click events here
  }

  // TrackBy functions for ngFor
  trackByActivity(index: number, activity: any): number {
    return index;
  }

  // Utility methods for template
  getCurrentTime(): string {
    return new Date().toLocaleTimeString('en-US', { 
      hour: '2-digit', 
      minute: '2-digit',
      hour12: true 
    });
  }

  getCurrentDate(): string {
    return new Date().toLocaleDateString('en-US', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }
}
