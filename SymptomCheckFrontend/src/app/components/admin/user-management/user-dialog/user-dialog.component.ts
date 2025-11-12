import { User, UserRole, UserUpdateDto } from "@/app/models/user.model"
import { UserService } from "@/app/services/user.service"
import { Component, Inject,  OnInit } from "@angular/core"
import {  FormBuilder,  FormGroup, Validators } from "@angular/forms"
import {  MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog"
import  { MatSnackBar } from "@angular/material/snack-bar"


@Component({
  selector: "app-user-dialog",
  templateUrl: "./user-dialog.component.html",
  styleUrls: ["./user-dialog.component.css"],
  standalone:false
})
export class UserDialogComponent implements OnInit {
  userForm!: FormGroup
  userRoles = Object.keys(UserRole).filter((key) => isNaN(Number(key)))
  loading = false;

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public data: { user: User; isEdit: boolean },
    public dialogRef: MatDialogRef<UserDialogComponent>
  ) {}

  ngOnInit(): void {
    this.userForm = this.fb.group({
      id: [this.data.user.id],
      firstName: [this.data.user.firstName || "", [Validators.required]],
      lastName: [this.data.user.lastName || "", [Validators.required]],
      email: [this.data.user.email || "", [Validators.required, Validators.email]],
      phoneNumber: [this.data.user.phoneNumber || ""],
      role: [this.data.user.role, [Validators.required]],
    })
  }

  onSubmit(): void {
    if (this.userForm.valid) {
      this.loading = true
      const userData: UserUpdateDto = this.userForm.value

      this.userService.update(userData).subscribe({
        next: () => {
          this.snackBar.open("User updated successfully", "Close", { duration: 3000 })
          this.dialogRef.close(true)
        },
        error: (error) => {
          this.loading = false
          this.snackBar.open("Error updating user", "Close", { duration: 3000 })
        },
      })
    }
  }

  onCancel(): void {
    this.dialogRef.close()
  }

  getRoleValue(roleName: string): UserRole {
    return UserRole[roleName as keyof typeof UserRole]
  }
}
