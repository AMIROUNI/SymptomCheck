import { Component, Inject, OnInit } from "@angular/core"
import { MatDialog } from "@angular/material/dialog"
import { MatSnackBar } from "@angular/material/snack-bar"

import { UserDialogComponent } from "./user-dialog/user-dialog.component"
import { UserService } from "@/app/services/user.service"
import { User, UserRole } from "@/app/models/user.model"

@Component({
  selector: "app-user-management",
  templateUrl: "./user-management.component.html",
  styleUrls: ["./user-management.component.css"],
  standalone:false
})
export class UserManagementComponent implements OnInit {
  users: User[] = []
  displayedColumns: string[] = ["id", "username", "firstName", "lastName", "email", "role", "actions"]
  loading = false

  constructor(
    private userService: UserService,
    @Inject(MatDialog) private dialog: MatDialog,
    @Inject(MatSnackBar) private snackBar: MatSnackBar,
  ) {}

  ngOnInit(): void {
    this.loadUsers()
  }

  loadUsers(): void {
    this.loading = true
    this.userService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users
        this.loading = false
      },
      error: (error) => {
        this.loading = false
        this.snackBar.open("Error loading users", "Close", { duration: 3000 })
      },
    })
  }

  editUser(user: User): void {
    const dialogRef = this.dialog.open(UserDialogComponent, {
      width: "500px",
      data: { user, isEdit: true },
    })

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.loadUsers()
      }
    })
  }

  deleteUser(user: User): void {
    if (confirm(`Are you sure you want to delete user ${user.username}?`)) {
      this.userService.delete(user.id).subscribe({
        next: () => {
          this.snackBar.open("User deleted successfully", "Close", { duration: 3000 })
          this.loadUsers()
        },
        error: (error) => {
          this.snackBar.open("Error deleting user", "Close", { duration: 3000 })
        },
      })
    }
  }

  getRoleName(role: UserRole): string {
    return UserRole[role]
  }
}
