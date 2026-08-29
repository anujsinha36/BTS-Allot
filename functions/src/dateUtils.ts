const WEEKDAY_MAP: Record<string, number> = {
  Sunday: 0,
  Monday: 1,
  Tuesday: 2,
  Wednesday: 3,
  Thursday: 4,
  Friday: 5,
  Saturday: 6,
};

export function getNextMonth(): Date {
  const today = new Date();

  return new Date(
    today.getFullYear(),
    today.getMonth() + 1,
    1
  );
}

export function getWeekdayNumber(dayOfWeek: string): number {
  const weekday = WEEKDAY_MAP[dayOfWeek];

  if (weekday === undefined) {
    throw new Error(`Invalid weekday: ${dayOfWeek}`);
  }

  return weekday;
}

export function getDatesForWeekday(
  month: Date,
  weekday: number
): Date[] {
  const dates: Date[] = [];
  const current = new Date(month);

  const offset = (weekday - current.getDay() + 7) % 7;

  current.setDate(current.getDate() + offset);

  while (current.getMonth() === month.getMonth()) {
    dates.push(new Date(current));
    current.setDate(current.getDate() + 7);
  }

  return dates;
}

export function formatDate(date: Date): string {
  const year = date.getFullYear();
  // getMonth() is 0-indexed, so we add 1.
  // padStart(2, '0') ensures 1 becomes "01".
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");

  return `${year}-${month}-${day}`;
}
