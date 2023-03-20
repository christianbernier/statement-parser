package view;

import java.util.List;

import model.transactions.AbstractTransaction;
import model.transactions.Deposit;
import model.transactions.Payment;

/**
 * Exports the information in a statement.
 */
public interface StatementExporter {
  /**
   * Writes the contents of a statement to some other format.
   * @param transactions all the transactions on the given statement
   * @throws IllegalStateException if there is an error exporting the contents of this statement.
   */
  void write(List<AbstractTransaction> transactions) throws IllegalStateException;

  /**
   * Returns a confirmation message for a successful export.
   * @return the confirmation message
   */
  String confirmationMessage();
}
