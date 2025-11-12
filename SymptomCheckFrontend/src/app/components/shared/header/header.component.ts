import { Component,  OnInit } from "@angular/core"
import  { Router } from "@angular/router"
import  { AuthService } from "../../../services/auth.service"
import  { User } from "../../../models/user.model"
import { FileUploadService } from "@/app/services/file-upload.service";

import { environment } from "../../../../environments/environment";

@Component({
  selector: "app-header",
  templateUrl: "./header.component.html",
  styleUrls: ["./header.component.scss"],
  standalone:false
})
export class HeaderComponent implements OnInit {
  profileImageUrl: string = '';

  currentUser: User | null = null
  isMenuOpen = false

  constructor(
    private authService: AuthService,
    private router: Router,
    private fileUploadService: FileUploadService
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe((user) => {
      this.currentUser = user
      console.log(this.currentUser?.profilePhotoUrl)
    })

    this.profileImageUrl = this.fileUploadService.getFullImageUrl(this.currentUser?.profilePhotoUrl)
  }

  toggleMenu(): void {
    this.isMenuOpen = !this.isMenuOpen
  }

  logout(): void {
    this.authService.logout()
    this.router.navigate(["/"])
  }

  get isLoggedIn(): boolean {
    return this.authService.isAuthenticated()
  }


  apiUrl = environment.uploadsUrl;

  getUserImage(filename: string| undefined): string {
    return `${environment.uploadsUrl}/${filename}`;
  }

}

