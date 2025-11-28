// doctor-reviews.component.ts
import { Component, OnInit } from "@angular/core"
import { FormBuilder, FormGroup } from "@angular/forms"
import { AuthService } from "@/app/services/auth.service"
import { ReviewService } from "@/app/services/review.service"
import { UserService } from "@/app/services/user.service"
import { User, UserRole } from "@/app/models/user.model"
import { DoctorReview, DoctorReviewStats } from "@/app/models/doctor-review.model"
import { environment } from "@/environments/environment"

@Component({
  selector: "app-doctor-reviews",
  templateUrl: "./doctor-reviews.component.html",
  styleUrls: ["./doctor-reviews.component.css"],
  standalone: false
})
export class DoctorReviewsComponent implements OnInit {
  currentDoctor?: User;
  isLoading: boolean = true;
  error: string = '';
  reviews: DoctorReview[] = [];
  reviewStats?: DoctorReviewStats;
  filteredReviews: DoctorReview[] = [];
  
  // Filtres et recherche
  searchForm: FormGroup;
  filterRating: number = 0;
  sortBy: string = 'newest';
  
  // Pagination
  currentPage: number = 1;
  pageSize: number = 10;
  totalPages: number = 1;

  // Exposer Math pour le template
  Math = Math;

  constructor(
    private authService: AuthService,
    private reviewService: ReviewService,
    private userService: UserService,
    private fb: FormBuilder
  ) {
    this.searchForm = this.fb.group({
      searchTerm: ['']
    });
  }

  ngOnInit(): void {
    console.log('🔄 DoctorReviewsComponent - Initialization started');
    this.loadCurrentDoctor();
  }

  loadCurrentDoctor(): void {
    console.log('👤 Loading current doctor information');
    
    const currentUser = this.authService.getCurrentUser();
    if (!currentUser || !currentUser.roles?.includes(UserRole.DOCTOR)) {
      console.error('❌ User is not a doctor or not logged in');
      this.error = 'Access denied. Doctor privileges required.';
      this.isLoading = false;
      return;
    }

    console.log('✅ Current user is a doctor, loading details');
    this.currentDoctor = currentUser;
    this.loadReviews();
    this.loadReviewStats();
  }

  loadReviews(): void {
    console.log('🔄 Loading doctor reviews');
    this.isLoading = true;
    
    if (!this.currentDoctor?.id) {
      console.error('❌ No doctor ID available');
      this.error = 'Doctor information not available';
      this.isLoading = false;
      return;
    }

    this.reviewService.getDoctorReviews(this.currentDoctor.id).subscribe({
      next: (reviews) => {
        console.log('✅ Reviews loaded successfully:', reviews.length, 'reviews');
        this.reviews = reviews;
        this.applyFilters();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('❌ Error loading reviews:', err);
        this.error = 'Failed to load reviews';
        this.isLoading = false;
      }
    });
  }

  loadReviewStats(): void {
    console.log('📊 Loading review statistics');
    
    if (!this.currentDoctor?.id) return;

    this.reviewService.getDoctorStats(this.currentDoctor.id).subscribe({
      next: (stats) => {
        console.log('✅ Review stats loaded:', stats);
        this.reviewStats = stats;
      },
      error: (err) => {
        console.error('❌ Error loading review stats:', err);
      }
    });
  }

  applyFilters(): void {
    console.log('🔍 Applying filters and sorting');
    
    let filtered = [...this.reviews];
    const searchTerm = this.searchForm.get('searchTerm')?.value?.toLowerCase();

    // Filtre par recherche
    if (searchTerm) {
      filtered = filtered.filter(review => 
        review.comment?.toLowerCase().includes(searchTerm)
      );
    }

    // Filtre par rating
    if (this.filterRating > 0) {
      filtered = filtered.filter(review => review.rating === this.filterRating);
    }

    // Tri
    filtered = this.sortReviews(filtered);

    this.filteredReviews = filtered;
    this.updatePagination();
    
    console.log('✅ Filters applied:', {
      searchTerm,
      filterRating: this.filterRating,
      sortBy: this.sortBy,
      results: filtered.length
    });
  }

  sortReviews(reviews: DoctorReview[]): DoctorReview[] {
    switch (this.sortBy) {
      case 'newest':
        return reviews.sort((a, b) => 
          new Date(b.datePosted).getTime() - new Date(a.datePosted).getTime()
        );
      
      case 'oldest':
        return reviews.sort((a, b) => 
          new Date(a.datePosted).getTime() - new Date(b.datePosted).getTime()
        );
      
      case 'highest':
        return reviews.sort((a, b) => b.rating - a.rating);
      
      case 'lowest':
        return reviews.sort((a, b) => a.rating - b.rating);
      
      default:
        return reviews;
    }
  }

  updatePagination(): void {
    this.totalPages = Math.ceil(this.filteredReviews.length / this.pageSize);
    this.currentPage = Math.max(1, Math.min(this.currentPage, this.totalPages));
  }

  get paginatedReviews(): DoctorReview[] {
    const startIndex = (this.currentPage - 1) * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    return this.filteredReviews.slice(startIndex, endIndex);
  }

  onSearch(): void {
    console.log('🔍 Performing search');
    this.currentPage = 1;
    this.applyFilters();
  }

  onFilterChange(): void {
    console.log('🎛️ Filter changed');
    this.currentPage = 1;
    this.applyFilters();
  }

  onSortChange(): void {
    console.log('📊 Sort changed to:', this.sortBy);
    this.applyFilters();
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      console.log('📄 Navigating to page:', page);
    }
  }

  getRatingDistribution(): { rating: number, count: number, percentage: number }[] {
    if (!this.reviewStats?.ratingDistribution) return [];

    const total = this.reviewStats.totalReviews;
    return [5, 4, 3, 2, 1].map(rating => {
      const count = this.reviewStats?.ratingDistribution?.[rating] || 0;
      const percentage = total > 0 ? (count / total) * 100 : 0;
      return { rating, count, percentage };
    });
  }

  getStars(rating: number): string {
    return '★'.repeat(rating) + '☆'.repeat(5 - rating);
  }

  getRoundedAverageRating(): number {
    return Math.round(this.averageRating);
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }

  // Répondre à un review (fonctionnalité future)
  replyToReview(review: DoctorReview): void {
    console.log('💬 Replying to review:', review.id);
    // Implémenter la logique de réponse
    alert('Reply functionality coming soon!');
  }

  // Télécharger les reviews (fonctionnalité future)
  exportReviews(): void {
    console.log('📥 Exporting reviews');
    // Implémenter l'export CSV/PDF
    alert('Export functionality coming soon!');
  }

  // Marquer un review comme utile (fonctionnalité future)
  markAsHelpful(review: DoctorReview): void {
    console.log('👍 Marking review as helpful:', review.id);
    // Implémenter la logique "helpful"
    alert('Helpful functionality coming soon!');
  }

  get averageRating(): number {
    return this.reviewStats?.averageRating || 0;
  }

  get totalReviews(): number {
    return this.reviewStats?.totalReviews || 0;
  }

  get fiveStarReviews(): number {
    return this.reviewStats?.ratingDistribution?.[5] || 0;
  }
}