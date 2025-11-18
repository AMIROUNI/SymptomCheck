import { Component, OnInit } from "@angular/core"
import { ActivatedRoute, Router } from "@angular/router"
import { DoctorService } from "../../../services/doctor.service"
import { User } from "../../../models/user.model"
import { DoctorReview } from "../../../models/doctor-review.model"
import { FormBuilder, FormGroup, Validators } from "@angular/forms"
import { AuthService } from "@/app/services/auth.service"
import { UserService } from "@/app/services/user.service"
import { environment } from "@/environments/environment"

@Component({
  selector: "app-doctor-detail",
  templateUrl: "./doctor-detail.component.html",
  styleUrls: ["./doctor-detail.component.scss"],
  standalone: false
})
export class DoctorDetailComponent implements OnInit {
  doctorId!: string;
  doctor?: User;
  isLoading: boolean = true;
  error: string = '';
  reviews: DoctorReview[] = [];
  showReviewForm: boolean = false;
  reviewForm: FormGroup;
  currentUser: User | null = null;
  constructor(
    private route: ActivatedRoute,
    private doctorService: UserService,
    private fb: FormBuilder,
    private authService:AuthService,
    private router: Router
  ) {
    this.reviewForm = this.fb.group({
      stars: [5, [Validators.required, Validators.min(1), Validators.max(5)]],
      comment: ['', [Validators.required, Validators.maxLength(1000)]]
    });
    this.currentUser = this.authService.getCurrentUser();

    // Redirect if not logged in
    if (!this.currentUser) {
      this.router.navigate(['/login']);
    }
  }

  ngOnInit(): void {
    this.doctorId = this.route.snapshot.paramMap.get('id')!;
    this.loadDoctor();
    this.loadReviews();
  }

  loadDoctor(): void {
    this.doctorService.getUserById(this.doctorId).subscribe({
      next: (doctor) => {
        this.doctor = doctor;
        this.isLoading = false;
      },
      error: (err) => {
        this.error = 'Doctor not found';
        console.error('Error loading doctor:', err);
        this.isLoading = false;
      },
    });
  }

  loadReviews(): void {
   /* this.doctorService.getDoctorReviews(this.doctorId).subscribe({
      next: (reviews) => {
        this.reviews = reviews;
      },
      error: (err) => {
        console.error('Error loading reviews:', err);
      }
    });*/
  }

  toggleReviewForm(): void {
    this.showReviewForm = !this.showReviewForm;
    if (this.showReviewForm) {
      this.reviewForm.reset({ stars: 5, comment: '' });
    }
  }

  submitReview(): void {
  /*  if (this.reviewForm.valid) {
      const reviewData = {
        doctorId: this.doctorId,
        patientId:this.currentUser?.id,
        ...this.reviewForm.value
      };
      console.log("review " ,reviewData)

      this.doctorService.submitReview(reviewData).subscribe({
        next: (review) => {
          this.reviews.unshift(review);
          this.showReviewForm = false;
          // Optionally update doctor's average rating if you have that field
        },
        error: (err) => {
          console.error('Error submitting review:', err);
        }
      });
    }*/
  }

  get averageRating(): number {
    if (!this.reviews.length) return 0;
    const sum = this.reviews.reduce((acc, review) => acc + review.stars, 0);
    return sum / this.reviews.length;
  }


   apiUrl = environment.uploadsUrl;
  
    getUserImage(filename: string| undefined): string {
      return `${environment.uploadsUrl}/${filename}`;
    }
}