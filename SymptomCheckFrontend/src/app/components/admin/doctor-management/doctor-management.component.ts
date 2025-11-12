import { Component, OnInit } from "@angular/core"
import { MatDialog } from "@angular/material/dialog"
import { MatSnackBar } from "@angular/material/snack-bar"

import { DoctorDialogComponent } from "./doctor-dialog/doctor-dialog.component"
import { User } from "@/app/models/user.model"
import { UserService } from "@/app/services/user.service"

@Component({
  selector: "app-doctor-management",
  templateUrl: "./doctor-management.component.html",
  styleUrls: ["./doctor-management.component.css"],
  standalone:false
})
export class DoctorManagementComponent implements OnInit {
  doctors: User[] = []
  displayedColumns: string[] = ["id", "name", "email", "speciality", "profileComplete", "actions"]
  loading = false

  constructor(
    private userService: UserService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
  ) {}

  ngOnInit(): void {
    this.loadDoctors()
  }

  loadDoctors(): void {
    this.loading = true
    this.userService.getDoctors().subscribe({
      next: (doctors) => {
        this.doctors = doctors
        this.loading = false
      },
      error: (error) => {
        this.loading = false
        this.snackBar.open("Error loading doctors", "Close", { duration: 3000 })
      },
    })
  }

  editDoctor(doctor: User): void {
    const dialogRef = this.dialog.open(DoctorDialogComponent, {
      width: "600px",
      data: { doctor, isEdit: true },
    })

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.loadDoctors()
      }
    })
  }

  deleteDoctor(doctor: User): void {
    if (confirm(`Are you sure you want to delete doctor ${doctor.firstName} ${doctor.lastName}?`)) {
      this.userService.delete(doctor.id).subscribe({
        next: () => {
          this.snackBar.open("Doctor deleted successfully", "Close", { duration: 3000 })
          this.loadDoctors()
        },
        error: (error) => {
          this.snackBar.open("Error deleting doctor", "Close", { duration: 3000 })
        },
      })
    }
  }

  getFullName(doctor: User): string {
    return `${doctor.firstName || ""} ${doctor.lastName || ""}`.trim() || doctor.username
  }
}
