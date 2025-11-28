import { DoctorProfileDto, User, UserRole, UserUpdateDto } from '@/app/models/user.model';
import { AuthService } from '@/app/services/auth.service';
import { FileUploadService } from '@/app/services/file-upload.service';
import { UserService } from '@/app/services/user.service';
import { environment } from '@/environments/environment';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

interface UploadResponse {
  filename: string;
  message: string;
}

@Component({
  selector: 'app-profile',
  standalone: false,
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {
  user: User | null = null;
  currentUser: User | null = null;
  isLoading: boolean = true;
  error: string = '';
  isEditing: boolean = false;
  profileForm: FormGroup;
  isUploading: boolean = false;
  uploadProgress: number = 0;
  keycloakRoles: string[] = [];

  UserRole = UserRole;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    public authService: AuthService,
    private userService: UserService,
    private fileUploadService: FileUploadService,
    private fb: FormBuilder
  ) {
    this.profileForm = this.fb.group({
      // Champs UserUpdateDto
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      phoneNumber: [''],
      // Champs DoctorProfileDto
      speciality: [''],
      description: ['', [Validators.maxLength(500)]],
      diploma: [''],
      clinicId: [null]
    });
  }

  ngOnInit(): void {
    console.log('🔄 UserProfileComponent - Initialization started');
    
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      console.log('✅ Current user updated:', user);
    });

    this.loadKeycloakRoles();
    
    this.route.paramMap.subscribe(params => {
      const userId = params.get('id');
      if (userId) {
        this.loadUserProfile(userId);
      } else {
        this.loadCurrentUserProfile();
      }
    });
  }

  loadKeycloakRoles(): void {
    try {
      this.keycloakRoles = this.authService.getUserRoles();
      console.log('🔑 Keycloak roles:', this.keycloakRoles);
    } catch (error) {
      console.error('❌ Error loading Keycloak roles:', error);
      this.keycloakRoles = [];
    }
  }

  loadUserProfile(userId: string): void {
    console.log('👤 Loading user profile for ID:', userId);
    this.isLoading = true;
    this.error = '';

    this.userService.getUserById(userId).subscribe({
      next: (user) => {
        console.log('✅ User profile loaded successfully');
        this.user = user;
        this.updateFormValidators();
        this.populateForm();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('❌ Error loading user profile:', err);
        this.error = 'Failed to load user profile';
        this.isLoading = false;
      }
    });
  }

  loadCurrentUserProfile(): void {
    console.log('👤 Loading current user profile');
    
    if (!this.authService.isAuthenticated()) {
      this.error = 'User not authenticated';
      this.isLoading = false;
      return;
    }

    this.currentUser = this.authService.getCurrentUser();
    
    if (this.currentUser) {
      console.log('✅ Using current user from AuthService');
      this.user = this.currentUser;
      this.updateFormValidators();
      this.populateForm();
      this.isLoading = false;
    } else {
      console.log('⏳ Waiting for user data to load...');
      const subscription = this.authService.currentUser$.subscribe(user => {
        if (user) {
          this.user = user;
          this.currentUser = user;
          this.updateFormValidators();
          this.populateForm();
          this.isLoading = false;
          subscription.unsubscribe();
        }
      });

      setTimeout(() => {
        if (this.isLoading) {
          this.error = 'Timeout loading user profile';
          this.isLoading = false;
          subscription.unsubscribe();
        }
      }, 5000);
    }
  }

  updateFormValidators(): void {
    const specialityControl = this.profileForm.get('speciality');
    const diplomaControl = this.profileForm.get('diploma');

    if (this.isDoctor()) {
      specialityControl?.setValidators([Validators.required]);
      diplomaControl?.setValidators([Validators.required]);
    } else {
      specialityControl?.clearValidators();
      diplomaControl?.clearValidators();
    }

    specialityControl?.updateValueAndValidity();
    diplomaControl?.updateValueAndValidity();
  }

  populateForm(): void {
    if (!this.user) return;

    this.profileForm.patchValue({
      firstName: this.user.firstName || '',
      lastName: this.user.lastName || '',
      email: this.user.email || '',
      phoneNumber: this.user.phoneNumber || '',
      speciality: this.user.speciality || '',
      description: this.user.description || '',
      diploma: this.user.diploma || '',
      clinicId: this.user.clinicId || null
    });

    if (this.isEditing) {
      this.profileForm.get('email')?.disable();
    }
  }

  toggleEdit(): void {
    this.isEditing = !this.isEditing;
    if (this.isEditing) {
      this.populateForm();
    } else {
      this.profileForm.get('email')?.enable();
      this.error = '';
    }
  }

  // Dans profile.component.ts
onSubmit(): void {
  if (this.profileForm.valid && this.user) {
    console.log('💾 Saving profile updates');
    
    this.profileForm.get('email')?.enable();
    const formData = this.profileForm.value;
    
    // ✅ CORRIGÉ : Ne pas inclure le rôle dans les données
    const userUpdateData: UserUpdateDto = {
      firstName: formData.firstName,
      lastName: formData.lastName,
      email: formData.email,
      phoneNumber: formData.phoneNumber
      // ❌ NE PAS INCLURE : role: formData.role
    };
    
    console.log('📤 Sending user update data:', userUpdateData);
    
    this.userService.update(this.user.id, userUpdateData).subscribe({
      next: (response) => {
        console.log('✅ User profile updated successfully', response);
        // ... reste du code
        this.reloadUserData();

        
      },
      error: (err: any) => {
        console.error('❌ Error updating user profile:', err);
        this.error = this.getErrorMessage(err);
        this.profileForm.get('email')?.disable();
      }
    });
  } else {
    this.markFormGroupTouched();
  }
}

  private updateDoctorProfile(formData: any): void {
    // ✅ CORRIGÉ : Pas d'ID dans DoctorProfileDto
    const doctorProfileData: DoctorProfileDto = {
      speciality: formData.speciality,
      description: formData.description,
      diploma: formData.diploma,
      profilePhotoUrl: this.user?.profilePhotoUrl,
      clinicId: formData.clinicId
    };

    console.log('📤 Sending doctor profile data:', doctorProfileData);

    // ✅ CORRIGÉ : Appeler avec userId séparé
    this.userService.completeDoctorProfile(this.user!.id, doctorProfileData).subscribe({
      next: (response) => {
        console.log('✅ Doctor profile updated successfully', response);
        this.reloadUserData();
      },
      error: (err: any) => {
        console.error('❌ Error updating doctor profile:', err);
        this.error = 'User profile updated but doctor details failed';
        this.reloadUserData();
      }
    });
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file && this.user) {
      console.log('📤 Uploading profile picture');
      
      if (!this.validateFile(file)) {
        return;
      }

      this.isUploading = true;
      this.uploadProgress = 0;

      // ✅ CORRIGÉ : Utiliser uploadProfilePhoto avec userId
      this.userService.uploadProfilePhoto(this.user.id, file).subscribe({
        next: (response) => {
          console.log('✅ Profile photo uploaded successfully', response);
          
          // Si c'est un docteur, mettre à jour le profil avec la nouvelle photo
          if (this.isDoctor() && response.profilePhotoUrl) {
            const doctorProfileData: DoctorProfileDto = {
              speciality: this.user!.speciality || '',
              description: this.user!.description || '',
              diploma: this.user!.diploma || '',
              profilePhotoUrl: response.profilePhotoUrl,
              clinicId: this.user!.clinicId || undefined
            };

            // ✅ CORRIGÉ : Appeler avec userId séparé
            this.userService.completeDoctorProfile(this.user!.id, doctorProfileData).subscribe({
              next: (profileResponse) => {
                console.log('✅ Profile picture updated via doctor profile', profileResponse);
                this.reloadUserData();
                this.isUploading = false;
                this.uploadProgress = 0;
              },
              error: (err: any) => {
                console.error('❌ Error updating profile picture:', err);
                this.error = 'Failed to update profile picture';
                this.isUploading = false;
              }
            });
          } else {
            // Pour les non-docteurs, simplement recharger les données
            this.reloadUserData();
            this.isUploading = false;
            this.uploadProgress = 0;
          }
        },
        error: (err: any) => {
          console.error('❌ Error uploading file:', err);
          this.error = 'Failed to upload image';
          this.isUploading = false;
          this.uploadProgress = 0;
        }
      });
    }
  }

  validateFile(file: File): boolean {
    const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
    const maxSize = 5 * 1024 * 1024; // 5MB

    if (!validTypes.includes(file.type)) {
      this.error = 'Please select a valid image file (JPEG, PNG, GIF, WebP)';
      return false;
    }

    if (file.size > maxSize) {
      this.error = 'Image size must be less than 5MB';
      return false;
    }

    return true;
  }

  cancelEdit(): void {
    this.isEditing = false;
    this.populateForm();
    this.error = '';
    this.profileForm.get('email')?.enable();
  }

  private getUserRole(): UserRole {
    if (!this.user?.roles || this.user.roles.length === 0) {
      if (this.keycloakRoles.includes('doctor')) return UserRole.DOCTOR;
      if (this.keycloakRoles.includes('admin')) return UserRole.ADMIN;
      if (this.keycloakRoles.includes('patient')) return UserRole.PATIENT;
      return UserRole.PATIENT;
    }
    return this.user.roles[0] as UserRole;
  }

  getRoleDisplayName(roles: string[] | undefined): string {
    if (!roles || roles.length === 0) {
      if (this.keycloakRoles.length > 0) {
        return this.keycloakRoles.map(role => this.formatRoleName(role)).join(', ');
      }
      return 'User';
    }
    
    const roleNames = roles.map(role => this.formatRoleName(role));
    return roleNames.join(', ');
  }

  private formatRoleName(role: string): string {
    switch (role) {
      case UserRole.PATIENT:
      case 'patient': return 'Patient';
      case UserRole.DOCTOR:
      case 'doctor': return 'Doctor';
      case UserRole.ADMIN:
      case 'admin': return 'Administrator';
      default: return role.charAt(0).toUpperCase() + role.slice(1);
    }
  }

  getProfileImageUrl(): string {
    if (!this.user?.profilePhotoUrl) {
      return 'assets/images/default-avatar.png';
    }
    return this.fileUploadService.getFullImageUrl(this.user.profilePhotoUrl);
  }

  canEditProfile(): boolean {
    if (!this.currentUser || !this.user) return false;
    
    if (this.currentUser.id === this.user.id) return true;
    
    if (this.currentUser.roles?.includes(UserRole.ADMIN) || this.keycloakRoles.includes('admin')) {
      return true;
    }
    
    return false;
  }

  isCurrentUserProfile(): boolean {
    return this.currentUser?.id === this.user?.id;
  }

  hasRole(role: UserRole): boolean {
    if (this.user?.roles?.includes(role)) {
      return true;
    }
    
    const keycloakRole = role.toLowerCase();
    return this.keycloakRoles.includes(keycloakRole);
  }

  isDoctor(): boolean {
    return this.hasRole(UserRole.DOCTOR);
  }

  isAdmin(): boolean {
    return this.hasRole(UserRole.ADMIN);
  }

  isPatient(): boolean {
    return this.hasRole(UserRole.PATIENT);
  }

  private reloadUserData(): void {
    this.userService.getUserById(this.user!.id).subscribe({
      next: (updatedUser) => {
        this.user = updatedUser;
        this.isEditing = false;
        
        if (this.currentUser && this.currentUser.id === updatedUser.id) {
          this.authService.loadCurrentUser();
        }

        this.profileForm.get('email')?.disable();
      },
      error: (err: any) => {
        console.error('❌ Error reloading user data:', err);
        this.error = 'Profile updated but failed to reload data';
        this.profileForm.get('email')?.disable();
      }
    });
  }

  private markFormGroupTouched(): void {
    Object.keys(this.profileForm.controls).forEach(key => {
      const control = this.profileForm.get(key);
      control?.markAsTouched();
    });
  }

  private getErrorMessage(error: any): string {
    if (error.status === 400) {
      return 'Invalid data provided';
    } else if (error.status === 401) {
      return 'Authentication required';
    } else if (error.status === 403) {
      return 'You do not have permission to perform this action';
    } else if (error.status === 404) {
      return 'User not found';
    } else if (error.status === 409) {
      return 'A user with this email already exists';
    } else if (error.status >= 500) {
      return 'Server error, please try again later';
    } else {
      return 'An unexpected error occurred';
    }
  }

  // Getters pour les erreurs du formulaire
  get firstNameError(): string {
    const control = this.profileForm.get('firstName');
    if (control?.errors?.['required'] && control.touched) return 'First name is required';
    if (control?.errors?.['minlength'] && control.touched) return 'First name must be at least 2 characters';
    return '';
  }

  get lastNameError(): string {
    const control = this.profileForm.get('lastName');
    if (control?.errors?.['required'] && control.touched) return 'Last name is required';
    if (control?.errors?.['minlength'] && control.touched) return 'Last name must be at least 2 characters';
    return '';
  }

  get emailError(): string {
    const control = this.profileForm.get('email');
    if (control?.errors?.['required'] && control.touched) return 'Email is required';
    if (control?.errors?.['email'] && control.touched) return 'Please enter a valid email';
    return '';
  }

  get specialityError(): string {
    const control = this.profileForm.get('speciality');
    if (control?.errors?.['required'] && control.touched) return 'Speciality is required for doctors';
    return '';
  }

  get diplomaError(): string {
    const control = this.profileForm.get('diploma');
    if (control?.errors?.['required'] && control.touched) return 'Diploma is required for doctors';
    return '';
  }

  // Vérifier si le profil docteur est complet
  isDoctorProfileComplete(): boolean {
    if (!this.isDoctor()) return true;
    
    return !!(this.user?.speciality && this.user?.diploma);
  }

  getDoctorCompletionPercentage(): number {
    if (!this.isDoctor()) return 100;
    
    let completedFields = 0;
    const totalFields = 3; // speciality, diploma, description (clinicId optionnel)
    
    if (this.user?.speciality) completedFields++;
    if (this.user?.diploma) completedFields++;
    if (this.user?.description) completedFields++;
    
    return Math.round((completedFields / totalFields) * 100);
  }

  getUserImage(filename: string | undefined): string {
    return `${environment.uploadsUrl}/${filename}`;
  }
}