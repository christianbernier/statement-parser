package model.parser.statement;

import java.util.List;

import model.transactions.Deposit;
import model.transactions.Payment;

/**
 * Parses the text of a statement into the relevant details.
 */
public interface StatementParser {
  /**
   * Receives a statement to parse, saving its text contents.
   * @param statement the text contents of a statement to parse.
   * @throws IllegalArgumentException if the provided statement is {@code null} OR if the provided
   * statement is empty
   * @throws IllegalStateException if there is an error parsing the statement text contents
   */
  void receiveStatement(String statement) throws IllegalArgumentException, IllegalStateException;

  /**
   * Gets all the deposits mentioned on the statement provided via the {@code receiveStatement}
   * method.
   * @return a {@code List} of deposits from the statement.
   * @throws IllegalStateException if there was no statement provided via the
   * {@code receiveStatement} method.
   */
  List<Deposit> getDeposits() throws IllegalStateException;

  /**
   * Gets all the payments mentioned on the statement provided via the {@code receiveStatement}
   * method.
   * @return a {@code List} of payments from the statement.
   * @throws IllegalStateException if there was no statement provided via the
   * {@code receiveStatement} method.
   */
  List<Payment> getPayments() throws IllegalStateException;
}
