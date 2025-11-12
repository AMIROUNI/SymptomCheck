import { Component,  OnInit } from "@angular/core"
import  { ServiceService } from "../../../services/service.service"
import  { HealthcareService } from "../../../models/healthcare-service.model"

@Component({
  selector: "app-service-list",
  templateUrl: "./service-list.component.html",
  styleUrls: ["./service-list.component.scss"],
  standalone:false
})
export class ServiceListComponent implements OnInit {
  services: HealthcareService[] = []
  filteredServices: HealthcareService[] = []
  categories: string[] = []
  isLoading = true
  searchTerm = ""
  selectedCategory = ""

  constructor(private serviceService: ServiceService) {}

  ngOnInit(): void {
    this.loadServices()
  }

  loadServices(): void {
    this.serviceService.getServices().subscribe({
      next: (services) => {
        this.services = services
        this.filteredServices = services

        // Extract unique categories
        const categoriesSet = new Set<string>()
        services.forEach((service) => {
          if (service.category) {
            categoriesSet.add(service.category)
          }
        })
        this.categories = Array.from(categoriesSet)

        this.isLoading = false
      },
      error: () => {
        this.isLoading = false
      },
    })
  }

  filterServices(): void {
    this.filteredServices = this.services.filter((service) => {
      const matchesSearch =
        service.name.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        service.description.toLowerCase().includes(this.searchTerm.toLowerCase())

      const matchesCategory = !this.selectedCategory || service.category === this.selectedCategory

      return matchesSearch && matchesCategory
    })
  }

  onSearchChange(): void {
    this.filterServices()
  }

  onCategoryChange(category: string): void {
    this.selectedCategory = category
    this.filterServices()
  }
}
