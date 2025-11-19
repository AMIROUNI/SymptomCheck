import { NgModule } from "@angular/core"
import { RouterModule, type Routes } from "@angular/router"
import { HomeComponent } from "./components/home/home.component"
import { RegisterComponent } from "./components/auth/register/register.component"
import { LoginComponent } from "./components/auth/login/login.component"
import { DoctorListComponent } from "./components/doctors/doctor-list/doctor-list.component"
import { DoctorDetailComponent } from "./components/doctors/doctor-detail/doctor-detail.component"
import { ServiceListComponent } from "./components/services/service-list/service-list.component"
import { AppointmentComponent } from "./components/appointments/appointment/appointment.component"
import { UserDashboardComponent } from "./components/dashboard/user-dashboard/user-dashboard.component"
import { ProfileEditComponent } from "./components/dashboard/profile-edit/profile-edit.component"
import { AppointmentHistoryComponent } from "./components/dashboard/appointment-history/appointment-history.component"
import { AuthGuard } from "./guards/auth.guard"
import { AddServiceComponent } from "./components/services/add-service/add-service.component"
import { MedicalClinicFormComponent } from "./components/admin/medical-clinic-form/medical-clinic-form.component"
import { ClinicListComponent } from "./components/admin/clinic-list/clinic-list.component"
import { UpdateMedicalClinicComponent } from "./components/admin/update-medical-clinic/update-medical-clinic.component"
import { AdminDashboardComponent } from "./components/admin/admin-dashboard/admin-dashboard.component"
import { DashboardComponent } from "./components/admin/dashboard/dashboard.component"
import { UserManagementComponent } from "./components/admin/user-management/user-management.component"
import { DoctorManagementComponent } from "./components/admin/doctor-management/doctor-management.component"
import { ClinicManagementComponent } from "./components/admin/clinic-management/clinic-management.component"


const routes: Routes = [
  { path: "", component: HomeComponent },
  { path: "register", component: RegisterComponent },
  { path: "login", component: LoginComponent },
  { path: "doctors", component: DoctorListComponent },
  { path: "doctors/:id", component: DoctorDetailComponent },
  { path: "services", component: ServiceListComponent },
  //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
   {path: "add-service", component: AddServiceComponent },
   {path: "medical-clinic-form",component: MedicalClinicFormComponent},
   {path: "clinic-list", component: ClinicListComponent},
   {path: "update-clinic/:id", component: UpdateMedicalClinicComponent},
   {path: "test", component: ClinicListComponent},
   {path:"addclinic",component:MedicalClinicFormComponent},
  //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
  {
    path: "appointment",
    component: AppointmentComponent,
    canActivate: [AuthGuard],
  },
  {
    path: "admin",
    component: AdminDashboardComponent,
    children: [
      { path: "", redirectTo: "dashboard", pathMatch: "full" },
  //    { path: "dashboard", component: AdminOverviewComponent },
  //    { path: "users", component: UserManagementComponent },
  //    { path: "doctors", component: DoctorManagementComponent },
      { path: "clinics", component: ClinicManagementComponent },
  //    { path: "appointments", component: AppointmentManagementComponent },
  //    { path: "services", component: ServiceManagementComponent },
  //    { path: "reports", component: ReportsComponent },
  //    { path: "settings", component: SystemSettingsComponent },
      { path: "medical-clinic-form", component: MedicalClinicFormComponent },
      { path: "clinic-list", component: ClinicListComponent },
      { path: "update-clinic/:id", component: UpdateMedicalClinicComponent }
    ]
  },

  {
    path: "admoun",
    component: DashboardComponent,
    canActivate: [AuthGuard],
    children: [
      { path: "", redirectTo: "users", pathMatch: "full" },
      { path: "users", component: UserManagementComponent },
      { path: "doctors", component: DoctorManagementComponent },
      { path: "clinics", component: ClinicManagementComponent },
    ],
  },

  {
    path: "dashboard",
    component: UserDashboardComponent,
    canActivate: [AuthGuard],
    children: [
      { path: "", redirectTo: "appointments", pathMatch: "full" },
      { path: "profile", component: ProfileEditComponent },
      { path: "appointments", component: AppointmentHistoryComponent },
      {path:"add-service",component:AddServiceComponent},
     { path: "services", component: ServiceListComponent }
    ],
  },
  { path: "**", redirectTo: "" },
]

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}