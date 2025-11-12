import { Component, Inject, OnInit } from "@angular/core"
import { FormBuilder, FormGroup, Validators } from "@angular/forms"
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog"
import { MatSnackBar } from "@angular/material/snack-bar"
import { MedicalClinic } from "../../../../models/medical-clinic.model"
import { MedicalClinicService } from "../../../../services/medical-clinic.service"


@Component({
  selector: 'app-clinic-dialog',
  templateUrl: './clinic-dialog.component.html',
  styleUrls: ['./clinic-dialog.component.css'],
  standalone: false,
})
export class ClinicDialogComponent implements OnInit {
  clinicForm!: FormGroup
  loading = false

  constructor(
    private fb: FormBuilder,
    private clinicService: MedicalClinicService,
    private snackBar: MatSnackBar,
    public dialogRef: MatDialogRef<ClinicDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { clinic: MedicalClinic; isEdit: boolean },
  ) {}

  ngOnInit(): void {
    this.clinicForm = this.fb.group({
      id: [this.data.clinic.id || 0],
      name: [this.data.clinic.name || "", [Validators.required]],
      address: [this.data.clinic.address || "", [Validators.required]],
      phone: [this.data.clinic.phone || "", [Validators.required]],
      websiteUrl: [this.data.clinic.websiteUrl || ""],
      city: [this.data.clinic.city || "", [Validators.required]],
      country: [this.data.clinic.country || "", [Validators.required]],
    })
  }

  onSubmit(): void {
    if (this.clinicForm.valid) {
      this.loading = true
      const clinicData: MedicalClinic = this.clinicForm.value

      const operation = this.data.isEdit ? this.clinicService.update(clinicData) : this.clinicService.createMedicalClinic(clinicData)

      operation.subscribe({
        next: () => {
          const message = this.data.isEdit ? "Clinic updated successfully" : "Clinic created successfully"
          this.snackBar.open(message, "Close", { duration: 3000 })
          this.dialogRef.close(true)
        },
        error: (error: any) => {
          console.log(error)
          this.loading = false
          const message = this.data.isEdit ? "Error updating clinic" : "Error creating clinic"
          this.snackBar.open(message, "Close", { duration: 3000 })
        },
      })
    }
  }

  onCancel(): void {
    this.dialogRef.close()
  }
}
