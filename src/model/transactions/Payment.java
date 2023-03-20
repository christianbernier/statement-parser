package model.transactions;

import model.date.Date;
import model.money.MoneyAmount;

/**
 * Represents a payment out of an account.
 */
public class Payment extends AbstractTransaction {

  public Payment(Date date, String description, MoneyAmount amount) {
    super(TransactionType.PAYMENT, date, description, amount);
  }

  /**
   * Factory class for instantiating {@code Payment} instances.
   */
  public static class PaymentFactory extends AbstractTransactionFactory<Payment> {
    @Override
    public Payment make(Date date, String description, MoneyAmount amount) throws IllegalArgumentException {
      return new Payment(date, description, amount);
    }
  }
}
