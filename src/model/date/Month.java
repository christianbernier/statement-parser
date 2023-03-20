package model.date;

/**
 * Represents a month in the calendar year.
 */
public enum Month {
  JANUARY(1),
  FEBRUARY(2),
  MARCH(3),
  APRIL(4),
  MAY(5),
  JUNE(6),
  JULY(7),
  AUGUST(8),
  SEPTEMBER(9),
  OCTOBER(10),
  NOVEMBER(11),
  DECEMBER(12);

  private final int index;

  /**
   * Initializes a {@code Month} with its appropriate index.
   * @param index the index of the desired month in the calendar year
   */
  Month(int index) {
    this.index = index;
  }

  /**
   * Finds the {@code Month} type corresponding to the provided index.
   * @param index the index of the desired month in the calendar year (from 1 to 12, inclusive)
   * @return the corresponding {@code Month} type
   * @throws IllegalArgumentException if the provided index is out of bounds (not between 1 and 12,
   * inclusive)
   */
  public static Month asMonth(int index) throws IllegalArgumentException {
    if (index < 1 || index > 12) {
      throw new IllegalArgumentException("Invalid month index");
    }

    return Month.values()[index - 1];
  }

  /**
   * Returns whether the {@code this} {@code Month} is after {@code other} {@code Month}.
   * @param other the {@code Month} being compared
   * @return {@code true} if, and only if, {@code this} comes after {@code other} in the calendar.
   */
  public boolean isAfter(Month other) {
    return this.index > other.index;
  }

  /**
   * Gets the index of this {@code Month} in the calendar year.
   * @return the index
   */
  public int getIndex() {
    return this.index;
  }

  @Override
  public String toString() {
    return "" + this.index;
  }
}
