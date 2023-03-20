package model.parser.statement.tdbank;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.date.Date;
import model.date.Month;
import model.parser.statement.AbstractStatementParser;
import model.transactions.AbstractTransaction;
import model.transactions.AbstractTransactionFactory;
import model.transactions.Deposit;
import model.transactions.Payment;

/**
 * Parses the text contents of a TD Bank statement.
 */
public class TDBankStatementParser extends AbstractStatementParser {
  /**
   * Initializes a new {@code TDBankStatementParser} instance.
   */
  public TDBankStatementParser() {
    super();
  }

  @Override
  protected Pattern getDateRangePattern() {
    return Pattern.compile("Statement Period: (\\w{3} \\d{1,2} \\d{4})-(\\w{3} \\d{1,2} \\d{4})");
  }

  @Override
  protected Pattern getTransactionPattern() {
    return Pattern.compile("(\\d{2})/(\\d{2}) (.*) (\\d{0,3},?\\d{0,3},?\\d{1,3})\\.(\\d{2})");
  }

  @Override
  protected String[] getBannedStrings() {
    return new String[] {
      ".* DDA PUR",
      "\\*+\\d+",
      "\\* \\w{2}",
    };
  }

  @Override
  protected Date fromDateRangeString(String dateRangeString) throws IllegalStateException {
    String[] parts = dateRangeString.split(" ");
    if (parts.length != 3) {
      throw new IllegalStateException("Date does not have 3 parts: month, day, year.");
    }

    String monthStr = parts[0];
    int day = Integer.parseInt(parts[1]);
    int year = Integer.parseInt(parts[2]);

    Month month;
    switch(monthStr) {
      case "Jan": month = Month.JANUARY; break;
      case "Feb": month = Month.FEBRUARY; break;
      case "Mar": month = Month.MARCH; break;
      case "Apr": month = Month.APRIL; break;
      case "May": month = Month.MAY; break;
      case "Jun": month = Month.JUNE; break;
      case "Jul": month = Month.JULY; break;
      case "Aug": month = Month.AUGUST; break;
      case "Sep": month = Month.SEPTEMBER; break;
      case "Oct": month = Month.OCTOBER; break;
      case "Nov": month = Month.NOVEMBER; break;
      case "Dec": month = Month.DECEMBER; break;
      default: throw new IllegalStateException("Cannot recognize month string: " + monthStr);
    }

    return new Date(year, month, day);
  }

  @Override
  protected void parseStatement() {
    // Statement info is between these two markers
    int startIndex = this.statement.indexOf("DAILY ACCOUNT ACTIVITY");
    int endIndex = this.statement.indexOf("DAILY BALANCE SUMMARY");

    String transactionDetails =
      this.statement.substring(startIndex, endIndex)
        .replaceAll("POSTING DATE DESCRIPTION AMOUNT\n", "")
        .replaceAll("DAILY ACCOUNT\\s+", "")
        .replaceAll("Subtotal: \\d{1,3}?,?\\d{1,3}?,?\\d{1,3}.\\d{2}\n", "")
        .replaceAll("\n", " ")
        .replaceAll("(\\d{0,3},?\\d{0,3},?\\d{1,3}\\.\\d{2})", "$0\n")
        .replaceAll("\\w*? ?Deposits", "");

    Pattern paymentHeaderPattern = Pattern.compile("\\w*? ?Payments");
    Matcher paymentHeaderMatcher = paymentHeaderPattern.matcher(transactionDetails);
    paymentHeaderMatcher.find();
    MatchResult paymentsHeader = paymentHeaderMatcher.toMatchResult();

    String depositsDetails = transactionDetails.substring(0, paymentsHeader.start()).trim();
    String paymentsDetails = transactionDetails.substring(paymentsHeader.end()).trim();

    this.deposits = this.transactionStringAsList(depositsDetails, new Deposit.DepositFactory());
    this.payments = this.transactionStringAsList(paymentsDetails, new Payment.PaymentFactory());
  }

  // Parses a string of transactions into a list of transactions
  private <T extends AbstractTransaction> List<T> transactionStringAsList(String transactionsString, AbstractTransactionFactory<T> factory) {
    List<T> transactions = new ArrayList<>();
    String[] transactionLines = transactionsString.split("\n");

    for (String transactionLine : transactionLines) {
      T item = this.parseTransaction(transactionLine, factory);
      if (item != null) {
        transactions.add(item);
      }
    }

    return transactions;
  }
}
