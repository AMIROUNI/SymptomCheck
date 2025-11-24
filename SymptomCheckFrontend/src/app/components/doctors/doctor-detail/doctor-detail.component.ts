// doctor-detail.component.ts
import { Component, OnInit } from "@angular/core"
import { ActivatedRoute, Router } from "@angular/router"
import { FormBuilder, FormGroup, Validators } from "@angular/forms"
import { AuthService } from "@/app/services/auth.service"
import { UserService } from "@/app/services/user.service"
import { ReviewService } from "@/app/services/review.service"
import { User, UserRole } from "@/app/models/user.model"
import { DoctorReview, DoctorReviewStats } from "@/app/models/doctor-review.model"
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
  reviewStats?: DoctorReviewStats;
  showReviewForm: boolean = false;
  reviewForm: FormGroup;
  currentUser: User | null = null;
  hasUserReviewed: boolean = false;
  userReview?: DoctorReview;
  UserRole = UserRole; // Exposer UserRole pour le template

  constructor(
    private route: ActivatedRoute,
    private userService: UserService,
    private reviewService: ReviewService,
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    console.log('🔧 DoctorDetailComponent - Constructor started');
    console.log('📝 Initializing review form');
    
    this.reviewForm = this.fb.group({
      rating: [5, [Validators.required, Validators.min(1), Validators.max(5)]],
      comment: ['', [Validators.required, Validators.maxLength(2000)]]
    });

    console.log('👤 Getting current user from AuthService');
    this.currentUser = this.authService.getCurrentUser();
    console.log('✅ Current user:', this.currentUser ? `${this.currentUser.firstName} ${this.currentUser.lastName} (${this.currentUser.roles})` : 'No user');

    // Redirect if not logged in
    if (!this.currentUser) {
      console.log('🚫 No user logged in - redirecting to login page');
      this.router.navigate(['/login']);
    } else {
      console.log('✅ User is logged in, proceeding with component initialization');
    }
    
    console.log('🔧 DoctorDetailComponent - Constructor completed');
  }

  ngOnInit(): void {
    console.log('🔄 ngOnInit - Component initialization started');
    console.log('📍 Getting doctor ID from route parameters');
    
    this.doctorId = this.route.snapshot.paramMap.get('id')!;
    console.log('✅ Doctor ID from route:', this.doctorId);
    
    this.loadDoctor();
    console.log('🔄 ngOnInit - Component initialization completed');
  }

  loadDoctor(): void {
    console.log('🔄 loadDoctor - Loading doctor details started');
    console.log('📋 Doctor ID:', this.doctorId);
    console.log('⏳ Setting loading state to true');
    
    this.isLoading = true;
    this.error = '';
    
    console.log('📡 Calling UserService.getUserById with doctor ID:', this.doctorId);
    
    this.userService.getUserById(this.doctorId).subscribe({
      next: (doctor) => {
        console.log('✅ loadDoctor - Success response received');
        console.log('👨‍⚕️ Doctor data loaded:', {
          id: doctor.id,
          name: `${doctor.firstName} ${doctor.lastName}`,
          speciality: doctor.speciality,
          email: doctor.email
        });
        
        this.doctor = doctor;
        console.log('✅ Doctor object assigned to component');
        
        console.log('📋 Starting parallel data loading operations:');
        console.log('  1. Loading reviews');
        console.log('  2. Loading review stats');
        console.log('  3. Checking if user reviewed');
        
        this.loadReviews();
        this.loadReviewStats();
        this.checkIfUserReviewed();
        
        console.log('⏳ Setting loading state to false');
        this.isLoading = false;
        console.log('✅ loadDoctor - Completed successfully');
      },
      error: (err) => {
        console.error('❌ loadDoctor - Error loading doctor:', err);
        console.log('📝 Setting error message');
        
        this.error = 'Doctor not found';
        console.log('⏳ Setting loading state to false');
        this.isLoading = false;
        
        console.log('❌ loadDoctor - Completed with errors');
      },
    });
  }

  loadReviews(): void {
    console.log('🔄 loadReviews - Loading doctor reviews started');
    console.log('📋 Doctor ID:', this.doctorId);
    
    this.reviewService.getDoctorReviews(this.doctorId).subscribe({
      next: (reviews) => {
        console.log('✅ loadReviews - Success response received');
        console.log('📊 Reviews loaded:', reviews.length, 'reviews found');
        
        if (reviews.length > 0) {
          console.log('📝 Sample review data:', {
            firstReview: {
              id: reviews[0].id,
              rating: reviews[0].rating,
              comment: reviews[0].comment?.substring(0, 50) + '...'
            }
          });
        }
        
        this.reviews = reviews;
        console.log('✅ Reviews array updated with', this.reviews.length, 'reviews');
        console.log('✅ loadReviews - Completed successfully');
      },
      error: (err) => {
        console.error('❌ loadReviews - Error loading reviews:', err);
        console.log('❌ loadReviews - Completed with errors');
      }
    });
  }

  loadReviewStats(): void {
    console.log('🔄 loadReviewStats - Loading review statistics started');
    console.log('📋 Doctor ID:', this.doctorId);
    
    this.reviewService.getDoctorStats(this.doctorId).subscribe({
      next: (stats) => {
        console.log('✅ loadReviewStats - Success response received');
        console.log('📊 Review stats loaded:', {
          averageRating: stats.averageRating,
          totalReviews: stats.totalReviews
        });
        
        this.reviewStats = stats;
        console.log('✅ Review stats assigned to component');
        console.log('✅ loadReviewStats - Completed successfully');
      },
      error: (err) => {
        console.error('❌ loadReviewStats - Error loading review stats:', err);
        console.log('❌ loadReviewStats - Completed with errors');
      }
    });
  }

  checkIfUserReviewed(): void {
    console.log('🔄 checkIfUserReviewed - Checking if user reviewed this doctor');
    console.log('👤 Current user:', this.currentUser ? `${this.currentUser.firstName} ${this.currentUser.lastName}` : 'No user');
    
    if (this.currentUser?.roles?.includes(UserRole.PATIENT)) {
      console.log('✅ User is a PATIENT - proceeding with review check');
      console.log('📋 Doctor ID:', this.doctorId);
      
      this.reviewService.hasReviewedDoctor(this.doctorId).subscribe({
        next: (hasReviewed) => {
          console.log('✅ checkIfUserReviewed - Success response received');
          console.log('📝 User review status:', hasReviewed ? 'HAS REVIEWED' : 'HAS NOT REVIEWED');
          
          this.hasUserReviewed = hasReviewed;
          console.log('✅ hasUserReviewed flag set to:', this.hasUserReviewed);
          
          if (hasReviewed) {
            console.log('📋 User has reviewed - loading user review details');
            this.loadUserReview();
          } else {
            console.log('📋 User has not reviewed - no further action needed');
          }
          
          console.log('✅ checkIfUserReviewed - Completed successfully');
        },
        error: (err) => {
          console.error('❌ checkIfUserReviewed - Error checking review status:', err);
          console.log('❌ checkIfUserReviewed - Completed with errors');
        }
      });
    } else {
      console.log('🚫 User is not a PATIENT - skipping review check');
      console.log('👤 User roles:', this.currentUser?.roles);
    }
  }

  loadUserReview(): void {
    console.log('🔄 loadUserReview - Loading user-specific review started');
    console.log('📋 Doctor ID:', this.doctorId);
    
    this.reviewService.getMyReviewForDoctor(this.doctorId).subscribe({
      next: (review) => {
        console.log('✅ loadUserReview - Success response received');
        console.log('📝 User review loaded:', {
          id: review.id,
          rating: review.rating,
          comment: review.comment?.substring(0, 50) + '...',
          datePosted: review.datePosted
        });
        
        this.userReview = review;
        console.log('✅ User review assigned to component');
        
        // S'assurer que le review de l'utilisateur est dans la liste des reviews
        console.log('🔄 Ensuring user review is in the reviews list');
        this.ensureUserReviewInList();
        
        console.log('✅ loadUserReview - Completed successfully');
      },
      error: (err) => {
        console.error('❌ loadUserReview - Error loading user review:', err);
        console.log('❌ loadUserReview - Completed with errors');
      }
    });
  }

  // S'assurer que le review de l'utilisateur est dans la liste
  private ensureUserReviewInList(): void {
    console.log('🔄 ensureUserReviewInList - Ensuring user review is in reviews list');
    console.log('📝 User review:', this.userReview ? `ID: ${this.userReview.id}` : 'No user review');
    console.log('📊 Current reviews count:', this.reviews.length);
    
    if (this.userReview && !this.reviews.some(r => r.id === this.userReview!.id)) {
      console.log('📝 User review not found in reviews list - adding it to the beginning');
      console.log('📝 Review to add:', {
        id: this.userReview.id,
        rating: this.userReview.rating,
        comment: this.userReview.comment?.substring(0, 30) + '...'
      });
      
      this.reviews.unshift(this.userReview);
      console.log('✅ User review added to reviews list');
      console.log('📊 New reviews count:', this.reviews.length);
    } else if (this.userReview) {
      console.log('✅ User review already exists in reviews list - no action needed');
    } else {
      console.log('🚫 No user review available - skipping');
    }
    
    console.log('✅ ensureUserReviewInList - Completed');
  }

  toggleReviewForm(): void {
    console.log('🔄 toggleReviewForm - Toggling review form visibility');
    console.log('📝 Current showReviewForm state:', this.showReviewForm);
    console.log('📝 User review status - hasUserReviewed:', this.hasUserReviewed);
    console.log('📝 User review object:', this.userReview);
    
    if (this.hasUserReviewed && this.userReview) {
      console.log('📝 User has existing review - switching to EDIT mode');
      console.log('📝 Pre-filling form with existing review data:', {
        rating: this.userReview.rating,
        comment: this.userReview.comment?.substring(0, 30) + '...'
      });
      
      this.reviewForm.patchValue({
        rating: this.userReview.rating,
        comment: this.userReview.comment
      });
      
      console.log('✅ Form patched with existing review data');
    } else {
      console.log('📝 User has no existing review - switching to CREATE mode');
      console.log('📝 Resetting form to default values');
      
      this.reviewForm.reset({ rating: 5, comment: '' });
      console.log('✅ Form reset to default values');
    }
    
    this.showReviewForm = !this.showReviewForm;
    console.log('📝 New showReviewForm state:', this.showReviewForm);
    console.log('✅ toggleReviewForm - Completed');
  }

  submitReview(): void {
    console.log('🔄 submitReview - Submitting review started');
    console.log('📝 Form valid:', this.reviewForm.valid);
    console.log('👤 Current user:', this.currentUser ? 'Available' : 'Not available');
    
    if (this.reviewForm.valid && this.currentUser) {
      console.log('✅ Form is valid and user is available - proceeding with submission');
      
      const reviewData = {
        doctorId: this.doctorId,
        rating: this.reviewForm.value.rating,
        comment: this.reviewForm.value.comment
      };
      
      console.log('📝 Review data to submit:', {
        doctorId: reviewData.doctorId,
        rating: reviewData.rating,
        comment: reviewData.comment?.substring(0, 30) + '...'
      });
      
      console.log('📝 Submission mode:', this.hasUserReviewed && this.userReview ? 'UPDATE' : 'CREATE');

      if (this.hasUserReviewed && this.userReview) {
        console.log('📝 UPDATE mode - updating existing review');
        console.log('📝 Review ID to update:', this.userReview.id);
        
        this.reviewService.updateReview(this.userReview.id, reviewData).subscribe({
          next: (updatedReview) => {
            console.log('✅ submitReview - Update successful');
            console.log('📝 Updated review received:', {
              id: updatedReview.id,
              rating: updatedReview.rating,
              comment: updatedReview.comment?.substring(0, 30) + '...'
            });
            
            // Mettre à jour la liste des reviews
            console.log('🔄 Updating reviews list with updated review');
            const index = this.reviews.findIndex(r => r.id === updatedReview.id);
            console.log('📝 Found review at index:', index);
            
            if (index !== -1) {
              console.log('📝 Replacing existing review in list at index:', index);
              this.reviews[index] = updatedReview;
            } else {
              console.log('📝 Review not found in list - adding to beginning');
              this.reviews.unshift(updatedReview);
            }
            
            this.userReview = updatedReview;
            console.log('✅ User review updated');
            
            this.showReviewForm = false;
            console.log('📝 Review form hidden');
            
            console.log('🔄 Reloading review statistics');
            this.loadReviewStats();
            
            console.log('✅ submitReview - Update completed successfully');
          },
          error: (err) => {
            console.error('❌ submitReview - Error updating review:', err);
            console.log('📝 Error details:', {
              status: err.status,
              message: err.message,
              error: err.error
            });
            
            this.handleReviewError(err, 'updating');
            console.log('❌ submitReview - Update completed with errors');
          }
        });
      } else {
        console.log('📝 CREATE mode - creating new review');
        
        this.reviewService.createReview(reviewData).subscribe({
          next: (newReview) => {
            console.log('✅ submitReview - Create successful');
            console.log('📝 New review received:', {
              id: newReview.id,
              rating: newReview.rating,
              comment: newReview.comment?.substring(0, 30) + '...'
            });
            
            console.log('📝 Adding new review to beginning of reviews list');
            this.reviews.unshift(newReview);
            console.log('📊 New reviews count:', this.reviews.length);
            
            this.hasUserReviewed = true;
            console.log('✅ hasUserReviewed flag set to true');
            
            this.userReview = newReview;
            console.log('✅ User review assigned');
            
            this.showReviewForm = false;
            console.log('📝 Review form hidden');
            
            console.log('🔄 Reloading review statistics');
            this.loadReviewStats();
            
            console.log('✅ submitReview - Create completed successfully');
          },
          error: (err) => {
            console.error('❌ submitReview - Error submitting review:', err);
            console.log('📝 Error details:', {
              status: err.status,
              message: err.message,
              error: err.error
            });
            
            this.handleReviewError(err, 'submitting');
            console.log('❌ submitReview - Create completed with errors');
          }
        });
      }
    } else {
      console.log('❌ Form is invalid or user not available - showing validation errors');
      console.log('📝 Form validation errors:', this.reviewForm.errors);
      console.log('📝 Rating field errors:', this.reviewForm.get('rating')?.errors);
      console.log('📝 Comment field errors:', this.reviewForm.get('comment')?.errors);
      
      // Marquer tous les champs comme touchés pour afficher les erreurs
      this.markFormGroupTouched();
      console.log('✅ All form fields marked as touched');
    }
  }

  private handleReviewError(err: any, action: string): void {
    console.log(`🔄 handleReviewError - Handling ${action} review error`);
    console.log('📝 Error object:', err);
    console.log('📝 Error status:', err.status);
    console.log('📝 Error message:', err.error?.message);
    
    if (err.status === 409) {
      console.log('📝 Conflict error - user has already reviewed this doctor');
      alert('You have already reviewed this doctor. Please edit your existing review.');
      this.hasUserReviewed = true;
      console.log('✅ hasUserReviewed flag set to true due to conflict');
      
      console.log('🔄 Loading user review after conflict detection');
      this.loadUserReview();
    } else if (err.error?.message) {
      console.log('📝 Specific error message available - showing alert');
      alert(`Error ${action} review: ${err.error.message}`);
    } else {
      console.log('📝 Generic error - showing generic alert');
      alert(`Error ${action} review. Please try again.`);
    }
    
    console.log(`✅ handleReviewError - ${action} error handling completed`);
  }

  private markFormGroupTouched(): void {
    console.log('🔄 markFormGroupTouched - Marking all form controls as touched');
    console.log('📝 Form controls count:', Object.keys(this.reviewForm.controls).length);
    
    Object.keys(this.reviewForm.controls).forEach(key => {
      console.log(`📝 Marking control '${key}' as touched`);
      const control = this.reviewForm.get(key);
      control?.markAsTouched();
      
      console.log(`📝 Control '${key}' status:`, {
        touched: control?.touched,
        dirty: control?.dirty,
        valid: control?.valid,
        errors: control?.errors
      });
    });
    
    console.log('✅ markFormGroupTouched - All controls marked as touched');
  }

  deleteReview(): void {
    console.log('🔄 deleteReview - Deleting user review started');
    console.log('📝 User review to delete:', this.userReview ? `ID: ${this.userReview.id}` : 'No user review');
    
    if (this.userReview && confirm('Are you sure you want to delete your review?')) {
      console.log('✅ User confirmed deletion - proceeding');
      console.log('📝 Review ID to delete:', this.userReview.id);
      
      this.reviewService.deleteReview(this.userReview.id).subscribe({
        next: () => {
          console.log('✅ deleteReview - Delete successful');
          
          // Retirer le review de la liste
          console.log('🔄 Removing review from reviews list');
          const initialCount = this.reviews.length;
          this.reviews = this.reviews.filter(r => r.id !== this.userReview!.id);
          console.log(`📝 Reviews list updated: ${initialCount} -> ${this.reviews.length} reviews`);
          
          this.hasUserReviewed = false;
          console.log('✅ hasUserReviewed flag set to false');
          
          this.userReview = undefined;
          console.log('✅ User review cleared');
          
          this.showReviewForm = false;
          console.log('📝 Review form hidden');
          
          console.log('🔄 Reloading review statistics');
          this.loadReviewStats();
          
          console.log('✅ deleteReview - Delete completed successfully');
        },
        error: (err) => {
          console.error('❌ deleteReview - Error deleting review:', err);
          console.log('📝 Error details:', {
            status: err.status,
            message: err.message,
            error: err.error
          });
          
          alert('Error deleting review: ' + (err.error?.message || 'Please try again.'));
          console.log('❌ deleteReview - Delete completed with errors');
        }
      });
    } else {
      console.log('🚫 Delete cancelled by user or no review available');
    }
  }

  get averageRating(): number {
    const rating = this.reviewStats?.averageRating || 0;
    console.log('📊 get averageRating - Returning:', rating);
    return rating;
  }

  get totalReviews(): number {
    const total = this.reviewStats?.totalReviews || 0;
    console.log('📊 get totalReviews - Returning:', total);
    return total;
  }

  apiUrl = environment.uploadsUrl;
  
  getUserImage(filename: string | undefined): string {
    console.log('🖼️ getUserImage - Getting user image URL');
    console.log('📝 Filename provided:', filename);
    
    if (!filename) {
      console.log('📝 No filename - returning default avatar');
      return 'assets/images/default-avatar.png';
    }
    
    const imageUrl = `${environment.uploadsUrl}/${filename}`;
    console.log('✅ Image URL constructed:', imageUrl);
    return imageUrl;
  }

  // Formater la date pour l'affichage
  formatDate(dateString: string): string {
    console.log('📅 formatDate - Formatting date string');
    console.log('📝 Input date string:', dateString);
    
    const date = new Date(dateString);
    const formattedDate = date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
    
    console.log('✅ Formatted date:', formattedDate);
    return formattedDate;
  }

  // Getter pour le compteur de caractères
  get commentLength(): number {
    return this.reviewForm.get('comment')?.value?.length || 0;
  }

  // Vérifier si on approche de la limite
  get isNearLimit(): boolean {
    return this.commentLength > 1800;
  }

  // Vérifier si on dépasse la limite
  get isOverLimit(): boolean {
    return this.commentLength > 2000;
  }
}