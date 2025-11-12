import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: "app-login",
  templateUrl: "./login.component.html",
  styleUrls: ["./login.component.scss"],
  standalone: false
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  isSubmitting = false;
  errorMessage = "";
  returnUrl = "";

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.loginForm = this.formBuilder.group({
      username: ["", [Validators.required]],
      password: ["", [Validators.required]],
    });

    // Get return url from route parameters or default to '/'
    this.returnUrl = this.route.snapshot.queryParams["returnUrl"] || "/";

    // Redirect if already logged in
    if (this.authService.isAuthenticated()) {
      this.router.navigate(["/"]);
    }
  }

  async onSubmit(): Promise<void> {
    if (this.loginForm.invalid) {
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = "";

    try {
      const { username, password } = this.loginForm.value;

      // ✅ Login using Keycloak with custom credentials
      await this.authService.loginWithCredentials(username, password);

      console.log('✅ Login successful!');

      // Navigate to return URL or dashboard
      this.router.navigateByUrl(this.returnUrl);

    } catch (error: any) {
      console.error('❌ Login error:', error);
      this.errorMessage = error.message || "Login failed. Please check your credentials.";
      this.isSubmitting = false;
    }
  }
}
