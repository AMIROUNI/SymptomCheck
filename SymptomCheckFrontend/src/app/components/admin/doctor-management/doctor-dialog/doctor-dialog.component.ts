import { MedicalClinic } from "@/app/models/medical-clinic.model"
import { DoctorProfileDto, User } from "@/app/models/user.model"
import { MedicalClinicService } from "@/app/services/medical-clinic.service"
import { UserService } from "@/app/services/user.service"
import { Component, Inject, OnInit } from "@angular/core"
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from "@angular/forms"
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from "@angular/material/dialog"
import { MatSnackBar, MatSnackBarModule } from "@angular/material/snack-bar"


@Component({
  selector: "app-doctor-dialog",
  templateUrl: "./doctor-dialog.component.html",
  styleUrls: ["./doctor-dialog.component.css"],
  standalone: false,
})
export class DoctorDialogComponent implements OnInit {
  doctorForm!: FormGroup
  clinics: MedicalClinic[] = []
  loading = false;

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private clinicService: MedicalClinicService,
    private snackBar: MatSnackBar,
    public dialogRef: MatDialogRef<DoctorDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { doctor: User; isEdit: boolean }
  ) { }

  ngOnInit(): void {
    this.loadClinics()
    this.doctorForm = this.fb.group({
      id: [this.data.doctor.id],
      speciality: [this.data.doctor.speciality || "", [Validators.required]],
      description: [this.data.doctor.description || "", [Validators.required]],
      diploma: [this.data.doctor.diploma || "", [Validators.required]],
      profilePhotoUrl: [this.data.doctor.profilePhotoUrl || ""],
      clinicId: [this.data.doctor.clinicId || null],
    })
  }

  loadClinics(): void {
    this.clinicService.getClinics().subscribe({
      next: (clinics) => {
        this.clinics = clinics
      },
      error: (error) => {
        this.snackBar.open("Error loading clinics", "Close", { duration: 3000 })
      },
    })
  }

  onSubmit(): void {
    if (this.doctorForm.valid) {
      this.loading = true
      const profileData: DoctorProfileDto = this.doctorForm.value

      this.userService.completeDoctorProfile(profileData).subscribe({
        next: () => {
          this.snackBar.open("Doctor profile updated successfully", "Close", { duration: 3000 })
          this.dialogRef.close(true)
        },
        error: (error) => {
          this.loading = false
          this.snackBar.open("Error updating doctor profile", "Close", { duration: 3000 })
        },
      })
    }
  }

  onCancel(): void {
    this.dialogRef.close()
  }
}
