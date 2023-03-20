package model.date;

/**
 * Represents a date in the calendar year as a month and day.
 */
public class Date implements Comparable<Date> {
  private final int year;

  // Invariant: month is not null.
  private final Month month;

  // Invariant: if month is JANUARY, MARCH, MAY, JULY, AUGUST, OCTOBER, or DECEMBER, day is between
  // 1 and 31, inclusive.
  // Invariant: if month is APRIL, JUNE, SEPTEMBER, or NOVEMBER, day is between 1 and 30, inclusive.
  // Invariant: if month is FEBRUARY and year is divisible by 4, day is between 1 and 29, inclusive.
  // Invariant: if month is FEBRUARY and year is divisible by 100 but not by 400, day is between 1 and 28, inclusive.
  // Invariant: if month is FEBRUARY and year is not divisible by 4, day is between 1 and 28, inclusive.
  private final int day;

  /**
   * Initializes a {@code Date} instance for the given calendar date.
   * @param month the month in which this date occurs
   * @param day the day of the provided month this date occurs
   * @param year the year of this date
   * @throws IllegalArgumentException if {@code month} is {@code null} OR if the provided month/day
   * combination does not exist in the provided {@code year}.
   */
  public Date(int year, Month month, int day) throws IllegalArgumentException {
    this.year = year;
    this.month = month;
    this.day = day;

    this.validateDate();
  }

  /**
   * Creates a new {@code Date} object in range provided by the {@code start} and {@code end}
   * parameters.
   * @param start the start of the date range
   * @param end the end of the date range
   * @param month the month of the new date
   * @param day the day of the new date
   * @return a new {@code Date} object within the range, with the provided {@code month} and
   * {@code day}
   * @throws IllegalArgumentException if the resulting {@code Date} is invalid OR if the date range
   * is more than one year.
   */
  public static Date withinRange(Date start, Date end, Month month, int day) throws IllegalArgumentException {
    if (start.year == end.year) {
      return new Date(start.year, month, day);
    }

    if (end.month.isAfter(start.month) || start.month == end.month) {
      throw new IllegalArgumentException("Date range is longer than one year.");
    }

    if (month.isAfter(start.month) || month == start.month) {
      return new Date(start.year, month, day);
    }

    return new Date(end.year, month, day);
  }

  // Validates the date
  private void validateDate() throws IllegalArgumentException {
    int daysInMonth = 0;

    switch (this.month) {
      case JANUARY:
      case MARCH:
      case MAY:
      case JULY:
      case AUGUST:
      case OCTOBER:
      case DECEMBER:
        daysInMonth = 31;
        break;
      case APRIL:
      case JUNE:
      case SEPTEMBER:
      case NOVEMBER:
        daysInMonth = 30;
        break;
      case FEBRUARY:
        if (year % 400 == 0 || (year % 100 != 0 && year % 4 == 0)) {
          daysInMonth = 29;
        } else {
          daysInMonth = 28;
        }
        break;
    }

    if (this.month == null) {
      throw new IllegalArgumentException("Month cannot be null.");
    }

    if (this.day < 1 || this.day > daysInMonth) {
      throw new IllegalArgumentException("Day does not exist in month.");
    }
  }

  /**
   * Formats a date in YYYY-MM-DD format.
   * @return the formatted date
   */
  @Override
  public String toString() {
    return String.format("%04d-%02d-%02d", this.year, this.month.getIndex(), this.day);
  }

  @Override
  public int compareTo(Date o) {
    if (this.year == o.year) {
      if (this.month == o.month) {
        return o.day - this.day;
      }

      return o.month.getIndex() - this.month.getIndex();
    }

    return o.year - this.year;
  }
}
