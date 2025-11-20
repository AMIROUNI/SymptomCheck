import { Component, Inject, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatSnackBar } from "@angular/material/snack-bar";

import { UserService } from "@/app/services/user.service";
import { User, UserRole } from "@/app/models/user.model";

@Component({
  selector: "app-user-management",
  templateUrl: "./user-management.component.html",
  styleUrls: ["./user-management.component.css"],
  standalone: false
})
export class UserManagementComponent implements OnInit {

//////////////////////////////////
    showPopup = false;
  popupTitle = '';
  popupMessage = '';
  popupIsSuccess = false;
  popupRedirectPath: string | null = null;
  showCancelButton = false;
  //////////////////////////////////
  users: User[] = [];
  displayedColumns: string[] = ["id", "username", "firstName", "lastName", "email", "role", "actions"];
  loading = false;




  constructor(
    private userService: UserService,
    @Inject(MatDialog) private dialog: MatDialog,
    @Inject(MatSnackBar) private snackBar: MatSnackBar,
  ) {}

  ngOnInit(): void {
    console.log('ng on init');
    
    this.loadAllUsers();
    console.log("UserManagementComponent initialized");
    console.log("**************************************");
    
    console.log(this.users);
    
  }

  private loadAllUsers(): void {
    this.loading = true;

    // Load patients first
    this.userService.getAllUsersByRole("PATIENT").subscribe({
      next: (patients) => {
        // Load doctors next
        this.userService.getAllUsersByRole("DOCTOR").subscribe({
          next: (doctors) => {
            // Merge both lists
            this.users = [...doctors, ...patients];
            this.loading = false;
          },
          error: (error) => {
            this.loading = false;
            this.snackBar.open("Error loading doctors", "Close", { duration: 3000 });
          },
        });
      },
      error: (error) => {
        this.loading = false;
        this.snackBar.open("Error loading patients", "Close", { duration: 3000 });
      },
    });
  }

  desactivateUser(user: User): void {
    if (!confirm(`Are you sure you want to deactivate the account ${user.username}?`)) return;

    this.userService.desableOrEnableUser(user.id, false).subscribe({
      next: () => {
        user.enabled = false; // update table immediately
        this.snackBar.open("User deactivated successfully", "Close", { duration: 3000 });
           this.showSuccessPopup("User deactivated successfully")
      },
      error: () =>{this.snackBar.open("Error deactivating user", "Close", { duration: 3000 })
     this.showErrorPopup("Error deactivating user")} ,
    });
  }

  activateUser(user: User): void {
    if (!confirm(`Are you sure you want to activate the account ${user.username}?`)) return;

    this.userService.desableOrEnableUser(user.id, true).subscribe({
      next: () => {
        user.enabled = true; // update table immediately
        this.snackBar.open("User activated successfully", "Close", { duration: 3000 });
        this.showSuccessPopup("User activated successfully")
      },
      error: () =>{ this.snackBar.open("Error activating user", "Close", { duration: 3000 })
        this.showErrorPopup("Error activating user")
    
    }
    });
  }

  getRoleName(role: UserRole): string {
    return UserRole[role];
  }


  /// popup methods //////////////////////////////////////////

  showSuccessPopup(title: string = 'Success', message: string = 'Operation completed successfully.,',) {
    this.popupTitle =title;
    this.popupMessage = message;
    this.popupIsSuccess = true ;
    this.popupRedirectPath = '/login';
    this.showCancelButton = false;
    this.showPopup = true;
  }

  showErrorPopup(errorMessage: string) {
    this.popupTitle = 'Login Failed';
    this.popupMessage = errorMessage;
    this.popupIsSuccess = false;
    this.popupRedirectPath = null;
    this.showCancelButton = true;
    this.showPopup = true;
  }

  closePopup() {
    this.showPopup = false;
  }
////////////////////////////////////

}
