import { Component, OnInit, OnDestroy, HostListener } from "@angular/core"
import { Router } from "@angular/router"
import { AuthService } from "../../../services/auth.service"
import { User, UserRole } from "../../../models/user.model"
import { FileUploadService } from "@/app/services/file-upload.service";
import { environment } from "../../../../environments/environment";
import { Subscription } from "rxjs";

@Component({
  selector: "app-header",
  templateUrl: "./header.component.html",
  styleUrls: ["./header.component.scss"],
  standalone: false
})
export class HeaderComponent implements OnInit, OnDestroy {
  profileImageUrl: string = '';
  currentUser: User | null = null;
  isMenuOpen = false;
  isDropdownOpen = false;
  private userSubscription: Subscription | undefined;

  // Make UserRole available in template
  UserRole = UserRole;

  constructor(
    private authService: AuthService,
    private router: Router,
    private fileUploadService: FileUploadService
  ) {}

  ngOnInit(): void {
    // Subscribe to current user changes
    this.userSubscription = this.authService.currentUser$.subscribe((user) => {
      this.currentUser = user;
      console.log("~###############################################################");
      
      console.log('Current user:', user);
      
      if (user?.profilePhotoUrl) {
        this.profileImageUrl = this.fileUploadService.getFullImageUrl(user.profilePhotoUrl);
      }
    });

    // Load current user if not already loaded
    if (this.authService.isAuthenticated() && !this.currentUser) {
      this.authService.loadCurrentUser();
    }
  }

  ngOnDestroy(): void {
    if (this.userSubscription) {
      this.userSubscription.unsubscribe();
    }
  }

  // Fermer le dropdown en cliquant ailleurs
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    if (!target.closest('.user-dropdown')) {
      this.isDropdownOpen = false;
    }
  }

  toggleMenu(): void {
    this.isMenuOpen = !this.isMenuOpen;
  }

  // Méthodes pour gérer le dropdown
  toggleDropdown(): void {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  openDropdown(): void {
    this.isDropdownOpen = true;
  }

  closeDropdown(): void {
    // Petit délai pour permettre le clic sur les liens
    setTimeout(() => {
      this.isDropdownOpen = false;
    }, 150);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(["/"]);
    this.isMenuOpen = false;
    this.isDropdownOpen = false;
  }

  get isLoggedIn(): boolean {
    return this.authService.isAuthenticated();
  }

  getUserImage(filename: string | undefined): string {
    if (!filename) {
      return '/assets/images/default-avatar.png'; // Fallback image
    }
    return `${environment.uploadsUrl}/${filename}`;
  }

  // Role-based navigation methods
  isPatient(): boolean {
    return this.currentUser?.roles?.includes(UserRole.PATIENT) ?? false;
  }

  isDoctor(): boolean {
    console.log('Current user roles:', this.currentUser?.roles);
    return this.currentUser?.roles?.includes(UserRole.DOCTOR) ?? false;
  }

  isAdmin(): boolean {
    return this.currentUser?.roles?.includes(UserRole.ADMIN) ?? false;
  }

  // AJOUT : Vérifier si l'utilisateur peut prendre des rendez-vous
  canBookAppointments(): boolean {
    return this.isPatient() || !this.currentUser?.roles; // Allow patients and users without specific role
  }

  // AJOUT : Obtenir le nom du dashboard selon le rôle
  getDashboardName(): string {
    const roles = this.currentUser?.roles;

    if (roles?.includes(UserRole.PATIENT)) {
      return 'Patient Dashboard';
    }
    if (roles?.includes(UserRole.DOCTOR)) {
      return 'Doctor Dashboard';
    }
    if (roles?.includes(UserRole.ADMIN)) {
      return 'Admin Dashboard';
    }

    return 'Dashboard';
  }

  // AJOUT : Obtenir la route du dashboard selon le rôle
  getDashboardRoute(): string {
    const roles = this.currentUser?.roles;

    if (roles?.includes(UserRole.PATIENT)) {
      return '/dashboard';
    }
    if (roles?.includes(UserRole.DOCTOR)) {
      return '/doctor-dashboard';
    }
    if (roles?.includes(UserRole.ADMIN)) {
      return '/admin';
    }

    return '/dashboard';
  }

  // AJOUT : Vérifier l'accès admin
  canAccessAdmin(): boolean {
    return this.isAdmin();
  }

  // Navigation methods with dropdown close
  navigateTo(route: string): void {
    this.router.navigate([route]);
    this.closeDropdown();
    this.isMenuOpen = false;
  }
}