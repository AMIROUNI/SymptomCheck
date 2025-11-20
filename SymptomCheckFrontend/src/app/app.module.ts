import { NgModule, APP_INITIALIZER } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from "@angular/common/http";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { RouterModule } from "@angular/router";
import { AppRoutingModule } from "./app-routing.module";

// Material Imports
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';

// Components Imports
import { AppComponent } from "./app.component";
import { RegisterComponent } from "./components/auth/register/register.component";
import { LoginComponent } from "./components/auth/login/login.component";
import { HomeComponent } from "./components/home/home.component";
import { DoctorListComponent } from "./components/doctors/doctor-list/doctor-list.component";
import { DoctorDetailComponent } from "./components/doctors/doctor-detail/doctor-detail.component";
import { ServiceListComponent } from "./components/services/service-list/service-list.component";
import { AppointmentComponent } from "./components/appointments/appointment/appointment.component";
import { UserDashboardComponent } from "./components/dashboard/user-dashboard/user-dashboard.component";
import { HeaderComponent } from "./components/shared/header/header.component";
import { FooterComponent } from "./components/shared/footer/footer.component";
import { StarRatingComponent } from "./components/shared/star-rating/star-rating.component";
import { TestimonialsComponent } from "./components/shared/testimonials/testimonials.component";
import { AppointmentHistoryComponent } from "./components/dashboard/appointment-history/appointment-history.component";
import { ProfileEditComponent } from "./components/dashboard/profile-edit/profile-edit.component";
import { ServiceCardComponent } from "./components/services/service-card/service-card.component";
import { DoctorCardComponent } from "./components/doctors/doctor-card/doctor-card.component";
import { AddServiceComponent } from './components/services/add-service/add-service.component';
import { MedicalClinicFormComponent } from './components/admin/medical-clinic-form/medical-clinic-form.component';
import { ClinicListComponent } from './components/admin/clinic-list/clinic-list.component';
import { UpdateMedicalClinicComponent } from './components/admin/update-medical-clinic/update-medical-clinic.component';
import { AdminDashboardComponent } from './components/admin/admin-dashboard/admin-dashboard.component';
import { DataTableComponent } from './components/admin/shared/data-table/data-table.component';
import { ClinicManagementComponent } from "./components/admin/clinic-management/clinic-management.component";
import { UserManagementComponent } from "./components/admin/user-management/user-management.component";
import { DoctorManagementComponent } from "./components/admin/doctor-management/doctor-management.component";
import { DashboardComponent } from "./components/admin/dashboard/dashboard.component";
import { ClinicDialogComponent } from "./components/admin/clinic-management/clinic-dialog/clinic-dialog.component";
import { UserDialogComponent } from "./components/admin/user-management/user-dialog/user-dialog.component";
import { DoctorDialogComponent } from "./components/admin/doctor-management/doctor-dialog/doctor-dialog.component";
import { NgApexchartsModule } from "ng-apexcharts";
import { AuthGuard } from "./guards/auth.guard";
// ❌ REMOVED: import { AuthInterceptor } from "./interceptors/auth.interceptor";
import { initializeKeycloak } from "./keycloak-init";
import { KeycloakAngularModule, KeycloakService } from "keycloak-angular";
import { CompleteProfileComponent } from './components/doctors/complete-profile/complete-profile.component';
import { PopupComponent } from "./components/shared/popup/popup.component";
import { DoctorDashboardComponent } from './components/doctors/doctor-dashboard/doctor-dashboard.component';
import { GenericChartComponent } from "./components/shared/dashboard-components/growth-chart/generic-chart.component";
import { BarGrowthChartComponent } from "./components/shared/dashboard-components/bar-growth-chart/bar-growth-chart.component";

@NgModule({
  declarations: [
    AppComponent,
    RegisterComponent,
    LoginComponent,
    HomeComponent,
    DoctorListComponent,
    DoctorDetailComponent,
    ServiceListComponent,
    AppointmentComponent,
    UserDashboardComponent,
    HeaderComponent,
    FooterComponent,
    StarRatingComponent,
    TestimonialsComponent,
    AppointmentHistoryComponent,
    ProfileEditComponent,
    ServiceCardComponent,
    DoctorCardComponent,
    AddServiceComponent,
    MedicalClinicFormComponent,
    ClinicListComponent,
    UpdateMedicalClinicComponent,
    AdminDashboardComponent,
    ClinicManagementComponent,
    UserManagementComponent,
    DoctorManagementComponent,
    DoctorDialogComponent,
    DashboardComponent,
    ClinicDialogComponent,
    DataTableComponent,
    UserDialogComponent,
    CompleteProfileComponent,
    DoctorDashboardComponent
      ],
      imports: [
    BarGrowthChartComponent,
    NgApexchartsModule,
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    BrowserAnimationsModule,
    RouterModule,
    MatToolbarModule,
    MatSidenavModule,
    MatListModule,
    MatIconModule,
    MatButtonModule,
    MatCardModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatInputModule,
    MatFormFieldModule,
    MatSelectModule,
    MatDialogModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatChipsModule,
    KeycloakAngularModule,
    PopupComponent,
    GenericChartComponent
],
  providers: [
    AuthGuard,
    {
      provide: APP_INITIALIZER,
      useFactory: initializeKeycloak,
      multi: true,
      deps: [KeycloakService]
    }
    // ❌ REMOVED: Custom AuthInterceptor - Keycloak handles it now
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
