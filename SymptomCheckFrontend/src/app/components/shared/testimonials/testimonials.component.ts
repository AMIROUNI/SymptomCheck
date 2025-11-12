import { Component } from "@angular/core"

interface Testimonial {
  id: number
  name: string
  role: string
  image: string
  comment: string
  rating: number
}

@Component({
  selector: "app-testimonials",
  templateUrl: "./testimonials.component.html",
  styleUrls: ["./testimonials.component.scss"],
  standalone:false
})
export class TestimonialsComponent {
  testimonials: Testimonial[] = [
    {
      id: 1,
      name: "Emily Johnson",
      role: "Patient",
      image: "assets/images/testimonials/patient1.jpg",
      comment:
        "The doctors are very professional and caring. The appointment booking process was seamless and I got the care I needed quickly.",
      rating: 5,
    },
    {
      id: 2,
      name: "Mark Wilson",
      role: "Patient",
      image: "assets/images/testimonials/patient2.jpg",
      comment:
        "I've been using this platform for all my family's healthcare needs. The service quality and follow-up care are exceptional.",
      rating: 4,
    },
    {
      id: 3,
      name: "Sarah Thompson",
      role: "Patient",
      image: "assets/images/testimonials/patient3.jpg",
      comment:
        "Finding the right specialist was so easy with this platform. I highly recommend it to anyone looking for quality healthcare.",
      rating: 5,
    },
  ]

  activeSlide = 0

  prevSlide(): void {
    this.activeSlide = this.activeSlide === 0 ? this.testimonials.length - 1 : this.activeSlide - 1
  }

  nextSlide(): void {
    this.activeSlide = this.activeSlide === this.testimonials.length - 1 ? 0 : this.activeSlide + 1
  }

  setSlide(index: number): void {
    this.activeSlide = index
  }
}
