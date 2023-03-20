package model.transactions;

import model.date.Date;
import model.money.MoneyAmount;

/**
 * Factory class for generating types of transactions.
 * @param <T> the type of transaction this factory class generates
 */
public abstract class AbstractTransactionFactory<T extends AbstractTransaction> {
  /**
   * Creates an instance of some type of monetary transaction, depending on the parameter type.
   * @param date the date on which this transaction occurred
   * @param description a description of this transaction
   * @param amount the amount of money exchanged
   * @return an instance of a monetary transaction
   * @throws IllegalArgumentException if any of the provided parameters are null or invalid,
   * according to those class requirements
   */
  public abstract T make(Date date, String description, MoneyAmount amount) throws IllegalArgumentException ;
}
