package model.money;

/**
 * Represents an amount of money in US dollars and cents.
 */
public class MoneyAmount {
  // Invariant: dollars is non-negative.
  private final int dollars;

  // Invariant: cents is between 0 and 99, inclusive.
  private final int cents;

  /**
   * Initializes an amount of money for the corresponding amount of US dollars and cents.
   * @param dollars the number of whole US dollars
   * @param cents the number of US cents
   * @throws IllegalArgumentException if the provided amount of money is negative.
   */
  public MoneyAmount(int dollars, int cents) throws IllegalArgumentException {
    int totalCents = 100 * dollars + cents;

    if (totalCents < 0) {
      throw new IllegalArgumentException("Amount cannot be negative.");
    }

    this.dollars = totalCents / 100;
    this.cents = totalCents % 100;
  }

  @Override
  public String toString() {
    return "$" + String.format("%01d", this.dollars) + "." + String.format("%02d", this.cents);
  }
}
