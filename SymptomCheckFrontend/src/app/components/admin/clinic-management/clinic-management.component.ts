import { Component, OnInit } from "@angular/core"
import { MatDialog,MatDialogModule  } from "@angular/material/dialog"
import { MatSnackBar } from "@angular/material/snack-bar"

import { ClinicDialogComponent } from "./clinic-dialog/clinic-dialog.component"
import { MedicalClinicService } from "@/app/services/medical-clinic.service"
import { MedicalClinic } from "@/app/models/medical-clinic.model"


@Component({
  selector: "app-clinic-management",
  templateUrl: "./clinic-management.component.html",
  styleUrls:  ["./clinic-management.component.css"],
  standalone:false
})

export class ClinicManagementComponent implements OnInit {
  clinics: MedicalClinic[] = []
  displayedColumns: string[] = ["id", "name", "address", "city", "country", "phone", "actions"]
  loading = false

  constructor(
    private clinicService: MedicalClinicService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
  ) {}

  ngOnInit(): void {
    this.loadClinics()
  }

  loadClinics(): void {
    this.loading = true
    this.clinicService.getClinics().subscribe({
      next: (clinics) => {
        this.clinics = clinics
        this.loading = false
      },
      error: (error) => {
        this.loading = false
        this.snackBar.open("Error loading clinics", "Close", { duration: 3000 })
      },
    })
  }

  addClinic(): void {
    const dialogRef = this.dialog.open(ClinicDialogComponent, {
      width: "600px",
      data: { clinic: {}, isEdit: false },
    })

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.loadClinics()
      }
    })
  }

  editClinic(clinic: MedicalClinic): void {
    const dialogRef = this.dialog.open(ClinicDialogComponent, {
      width: "600px",
      data: { clinic, isEdit: true },
    })

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.loadClinics()
      }
    })
  }

  deleteClinic(clinic: MedicalClinic): void {
    if (confirm(`Are you sure you want to delete clinic ${clinic.name}?`)) {
      if(clinic.id)
      this.clinicService.deleteClinic(clinic.id).subscribe({
        next: () => {
          this.snackBar.open("Clinic deleted successfully", "Close", { duration: 3000 })
          this.loadClinics()
        },
        error: (error) => {
          this.snackBar.open("Error deleting clinic", "Close", { duration: 3000 })
        },
      })
    }
  }
}
