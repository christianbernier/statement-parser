package model.parser.statement;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.date.Date;
import model.date.Month;
import model.money.MoneyAmount;
import model.transactions.AbstractTransaction;
import model.transactions.AbstractTransactionFactory;
import model.transactions.Deposit;
import model.transactions.Payment;

/**
 * Represents a statement parser, including various functions universal to all parsers.
 */
public abstract class AbstractStatementParser implements StatementParser {
  protected String statement;
  protected boolean hasReceivedStatement;
  protected List<Deposit> deposits;
  protected List<Payment> payments;
  protected Date startDate;
  protected Date endDate;

  protected AbstractStatementParser() {
    this.hasReceivedStatement = false;
    this.deposits = new ArrayList<>();
    this.payments = new ArrayList<>();
  }

  protected abstract Pattern getDateRangePattern();
  protected abstract Pattern getTransactionPattern();
  protected abstract String[] getBannedStrings();

  protected abstract Date fromDateRangeString(String dateRangeString) throws IllegalStateException;
  protected abstract void parseStatement();

  protected <T extends AbstractTransaction> T parseTransaction(String transactionString, AbstractTransactionFactory<T> factory) {
    Matcher transactionMatcher = this.getTransactionPattern().matcher(transactionString);
    if (transactionMatcher.find()) {
      Month month = Month.asMonth(Integer.parseInt(transactionMatcher.group(1)));
      int day = Integer.parseInt(transactionMatcher.group(2));
      Date date = Date.withinRange(this.startDate, this.endDate, month, day);

      String description = transactionMatcher.group(3).replace(",", " ");
      for (String bannedPattern : this.getBannedStrings()) {
        description = description.replaceAll(bannedPattern, "");
      }
      description = description.trim();

      int dollars = Integer.parseInt(transactionMatcher.group(4).replaceAll(",", ""));
      int cents = Integer.parseInt(transactionMatcher.group(5));
      MoneyAmount amount = new MoneyAmount(dollars, cents);

      return factory.make(date, description, amount);
    } else {
      return null;
    }
  }

  @Override
  public void receiveStatement(String statement) throws IllegalArgumentException, IllegalStateException {
    if (statement == null || statement.length() == 0) {
      throw new IllegalArgumentException("Statement cannot be empty.");
    }

    if (this.hasReceivedStatement) {
      throw new IllegalArgumentException("Already received statement.");
    }

    this.statement = statement;
    this.hasReceivedStatement = true;

    Matcher dateRangeMatcher = this.getDateRangePattern().matcher(statement);
    if(!dateRangeMatcher.find() || dateRangeMatcher.groupCount() != 2) {
      throw new IllegalStateException("Cannot find date range in statement.");
    }

    this.startDate = this.fromDateRangeString(dateRangeMatcher.group(1));
    this.endDate = this.fromDateRangeString(dateRangeMatcher.group(2));

    this.parseStatement();
  }

  @Override
  public List<Deposit> getDeposits() throws IllegalStateException {
    return List.copyOf(this.deposits);
  }

  @Override
  public List<Payment> getPayments() throws IllegalStateException {
    return List.copyOf(this.payments);
  }
}
