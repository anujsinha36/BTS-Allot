export interface DutyForm {
  meetingName: string;
  startMinutes: number;
  endMinutes: number;
  btsRequired: number;
  location: string;
  notes?: string | null;
}

export interface DutyTemplate {
  id: string;
  dayOfWeek: string;
  duty: DutyForm;
}

export interface Duty {
  id: string;
  date: string;
  btsReservedCount: number;
  duty: DutyForm;
  isBatch?: boolean; // Flag to identify duties created in a monthly batch}
}
