// data-table.component.ts
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-data-table',
  templateUrl: './data-table.component.html',
  standalone:false
})
export class DataTableComponent {
  @Input() columns: {key: string, label: string}[] = [];
  @Input() data: any[] = [];
  @Input() actions: boolean = true;
}