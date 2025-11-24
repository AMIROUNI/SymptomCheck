

export interface AvailabilityHealthDto {
  doctorId: string;
  daysOfWeek: string[];
  startTime: string;
  endTime: string;
  name?: string;
  description?: string;
  imageUrl?: string;
  durationMinutes?: number;
  price?: number;
}


export enum DayOfWeek {
  MONDAY = 'MONDAY',
  TUESDAY = 'TUESDAY',
  WEDNESDAY = 'WEDNESDAY',
  THURSDAY = 'THURSDAY',
  FRIDAY = 'FRIDAY',
  SATURDAY = 'SATURDAY',
  SUNDAY = 'SUNDAY',
}
