import { Component,  OnInit } from "@angular/core"
import {  FormBuilder,  FormGroup, Validators } from "@angular/forms"
import  { AuthService } from "../../../services/auth.service"
import  { User } from "../../../models/user.model"

@Component({
  selector: "app-profile-edit",
  templateUrl: "./profile-edit.component.html",
  styleUrls: ["./profile-edit.component.scss"],
  standalone:false
})
export class ProfileEditComponent implements OnInit {
  profileForm!: FormGroup
  currentUser: User | null = null
  isSubmitting = false
  errorMessage = ""
  successMessage = ""
  isDoctor: boolean = false;

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser()
    this.initForm()
  }

  initForm(): void {
    this.profileForm = this.formBuilder.group({
      firstName: [this.currentUser?.firstName || "", [Validators.required]],
      lastName: [this.currentUser?.lastName || "", [Validators.required]],
      email: [this.currentUser?.email || "", [Validators.required, Validators.email]],
      phoneNumber: [
        this.currentUser?.phoneNumber || "",
        [Validators.required, Validators.pattern(/^\d{3}-\d{3}-\d{4}$/)],
      ],
      // Additional fields for doctors
      speciality: [this.currentUser?.speciality || ""],
      description: [this.currentUser?.description || ""],
      diploma: [this.currentUser?.diploma || ""],
    })

    // Disable doctor-specific fields for patients
    if (!this.isDoctor) {
      this.profileForm.get("speciality")?.disable()
      this.profileForm.get("description")?.disable()
      this.profileForm.get("diploma")?.disable()
    }
  }

  onSubmit(): void {
    if (this.profileForm.invalid) {
      return
    }

    this.isSubmitting = true
    this.errorMessage = ""
    this.successMessage = ""

    const formData = { ...this.profileForm.value }

    // Only include doctor fields if user is a doctor
    if (!this.isDoctor) {
      delete formData.speciality
      delete formData.description
      delete formData.diploma
    }

   /* this.authService.updateUserProfile(formData).subscribe({
      next: (updatedUser) => {
        this.currentUser = updatedUser
        this.successMessage = "Profile updated successfully!"
        this.isSubmitting = false
      },
      error: (error) => {
        this.errorMessage = error.message || "Failed to update profile. Please try again."
        this.isSubmitting = false
      },
    })
  }*/}


}
