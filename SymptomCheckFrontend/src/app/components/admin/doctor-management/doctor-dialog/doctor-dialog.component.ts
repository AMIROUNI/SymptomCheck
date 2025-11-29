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
      // ✅ SUPPRIMÉ : id n'est plus nécessaire dans le formulaire
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
      
      // ✅ CORRIGÉ : Créer le DTO sans l'ID
      const profileData: DoctorProfileDto = {
        speciality: this.doctorForm.value.speciality,
        description: this.doctorForm.value.description,
        diploma: this.doctorForm.value.diploma,
        profilePhotoUrl: this.doctorForm.value.profilePhotoUrl,
        clinicId: this.doctorForm.value.clinicId
      }

      // ✅ CORRIGÉ : Passer l'ID de l'utilisateur comme premier paramètre
      this.userService.completeDoctorProfile(this.data.doctor.id, profileData).subscribe({
        next: () => {
          this.snackBar.open("Doctor profile updated successfully", "Close", { duration: 3000 })
          this.dialogRef.close(true)
        },
        error: (error) => {
          this.loading = false
          console.error('Error updating doctor profile:', error)
          this.snackBar.open("Error updating doctor profile", "Close", { duration: 3000 })
        },
        complete: () => {
          this.loading = false
        }
      })
    } else {
      // Marquer tous les champs comme touchés pour afficher les erreurs de validation
      this.markFormGroupTouched()
    }
  }

  onCancel(): void {
    this.dialogRef.close()
  }

  // Méthode pour marquer tous les champs comme touchés
  private markFormGroupTouched(): void {
    Object.keys(this.doctorForm.controls).forEach(key => {
      const control = this.doctorForm.get(key)
      control?.markAsTouched()
    })
  }

  // Getters pour les erreurs de formulaire (utiles pour le template)
  get specialityError(): string {
    const control = this.doctorForm.get('speciality')
    if (control?.errors?.['required'] && control.touched) return 'Speciality is required'
    return ''
  }

  get descriptionError(): string {
    const control = this.doctorForm.get('description')
    if (control?.errors?.['required'] && control.touched) return 'Description is required'
    return ''
  }

  get diplomaError(): string {
    const control = this.doctorForm.get('diploma')
    if (control?.errors?.['required'] && control.touched) return 'Diploma is required'
    return ''
  }
}