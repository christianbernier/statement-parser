package model.transactions;

/**
 * Represents a type of transaction in or out of a bank account.
 */
public enum TransactionType {
  DEPOSIT("Deposit"),
  PAYMENT("Payment");

  // Invariant: name is not null.
  private final String name;

  /**
   * Initialize a transaction type with the given name.
   * @param name the name of this type of transaction
   */
  TransactionType(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return this.name;
  }
}
