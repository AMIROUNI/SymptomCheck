import {
  Component,
  Input,
  Output,
  EventEmitter,
  forwardRef
} from '@angular/core';
import {
  ControlValueAccessor,
  NG_VALUE_ACCESSOR
} from '@angular/forms';

@Component({
  standalone:false,
  selector: 'app-star-rating',
  templateUrl: './star-rating.component.html',
  styleUrls: ['./star-rating.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => StarRatingComponent),
      multi: true
    }
  ]
})
export class StarRatingComponent implements ControlValueAccessor {
  @Input() maxRating = 5;
  @Input() readOnly = false;

  @Output() ratingChange = new EventEmitter<number>();

  @Input() rating = 0;
  hoverRating = 0;

  private onChange = (value: number) => {};
  private onTouched = () => {};

  get stars(): number[] {
    return Array(this.maxRating)
      .fill(0)
      .map((_, i) => i + 1);
  }

  setRating(value: number): void {
    if (!this.readOnly) {
      this.rating = value;
      this.onChange(value); // Notify form control
      this.onTouched();
      this.ratingChange.emit(value);
    }
  }

  setHoverRating(value: number): void {
    if (!this.readOnly) {
      this.hoverRating = value;
    }
  }

  resetHoverRating(): void {
    if (!this.readOnly) {
      this.hoverRating = 0;
    }
  }

  getStarClass(star: number): string {
    const currentRating = this.hoverRating || this.rating;

    if (currentRating >= star) {
      return 'fas fa-star filled';
    } else if (currentRating > star - 1 && currentRating < star) {
      return 'fas fa-star-half-alt filled';
    } else {
      return 'far fa-star';
    }
  }

  // ControlValueAccessor methods
  writeValue(value: number): void {
    this.rating = value;
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setDisabledState?(isDisabled: boolean): void {
    this.readOnly = isDisabled;
  }
}
