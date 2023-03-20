package model.transactions;

import java.util.Objects;

import model.date.Date;
import model.money.MoneyAmount;

/**
 * Represents a monetary transaction.
 */
public abstract class AbstractTransaction {
  // Invariant: date is not null.
  private final Date date;

  // Invariant: description is not null.
  private final String description;

  // Invariant: amount is not null.
  private final MoneyAmount amount;

  // Invariant: type is not null.
  protected TransactionType type;

  /**
   * Initializes a new monetary transaction according to the provided parameters.
   * @param type the kind of this transaction
   * @param date the date on which this transaction occurred
   * @param description a description of this transaction
   * @param amount the amount of money exchanged
   * @throws IllegalArgumentException if any of the provided arguments is {@code null}.
   */
  protected AbstractTransaction(TransactionType type, Date date, String description, MoneyAmount amount) throws IllegalArgumentException {
    if (type == null) {
      throw new IllegalArgumentException("Type cannot be null.");
    }

    if (date == null) {
      throw new IllegalArgumentException("Date cannot be null.");
    }

    if (description == null) {
      throw new IllegalArgumentException("Description cannot be null");
    }

    if (amount == null) {
      throw new IllegalArgumentException("Amount cannot be null");
    }

    this.type = type;
    this.date = date;
    this.description = (description.length() == 0) ? "Unknown" : description;
    this.amount = amount;
  }

  /**
   * Gets the date associated with this transaction.
   * @return the date of with this transaction
   */
  public Date getDate() {
    return this.date;
  }

  @Override
  public String toString() {
    return this.type + "," + this.date + "," + this.description + "," + this.amount;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.type, this.date, this.description, this.amount);
  }
}
