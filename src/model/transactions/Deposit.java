package model.transactions;

import model.date.Date;
import model.money.MoneyAmount;

/**
 * Represents a deposit into an account.
 */
public class Deposit extends AbstractTransaction {
  private Deposit(Date date, String description, MoneyAmount amount) {
    super(TransactionType.DEPOSIT, date, description, amount);
  }

  /**
   * Factory class for instantiating {@code Deposit} instances.
   */
  public static class DepositFactory extends AbstractTransactionFactory<Deposit> {
    @Override
    public Deposit make(Date date, String description, MoneyAmount amount) throws IllegalArgumentException {
      return new Deposit(date, description, amount);
    }
  }
}
