import { Component,  OnInit } from "@angular/core"
import  { DoctorService } from "../../../services/doctor.service"
import  { User } from "../../../models/user.model"
import { UserService } from "@/app/services/user.service"
import { environment } from "@/environments/environment"

@Component({
  selector: "app-doctor-list",
  templateUrl: "./doctor-list.component.html",
  styleUrls: ["./doctor-list.component.scss"],
  standalone:false
})
export class DoctorListComponent implements OnInit {
  doctors: User[] = []
  filteredDoctors: User[] = []
  isLoading = true
  searchTerm = ""
  selectedSpecialty = ""

  specialties: string[] = []

  constructor(private doctorService: UserService) {}

  ngOnInit(): void {
    this.loadDoctors()
  }

  loadDoctors(): void {
    this.isLoading = true;
    this.doctorService.getAllUsersByRole("DOCTOR").subscribe({
      next: (doctors) => {
        this.doctors = doctors;
        this.filteredDoctors = doctors;
        console.log(doctors)
  
        const specialtiesSet = new Set<string>(
          doctors
            .filter((doctor) => doctor.speciality)
            .map((doctor) => doctor.speciality!)
        );
        this.specialties = Array.from(specialtiesSet);
  
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Failed to load doctors:', err);
        this.isLoading = false;
      },
    });
  }
  

  filterDoctors(): void {
    this.filteredDoctors = this.doctors.filter((doctor) => {
      const matchesSearch =
        doctor.firstName?.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        doctor.lastName?.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        doctor.speciality?.toLowerCase().includes(this.searchTerm.toLowerCase())

      const matchesSpecialty = !this.selectedSpecialty || doctor.speciality === this.selectedSpecialty

      return matchesSearch && matchesSpecialty
    })
  }

  onSearchChange(): void {
    this.filterDoctors()
  }

  onSpecialtyChange(specialty: string): void {
    this.selectedSpecialty = specialty
    this.filterDoctors()
  }



  

}
