package model.parser.statement.tdbank;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.date.Date;
import model.date.Month;
import model.money.MoneyAmount;
import model.parser.statement.StatementParser;
import model.transactions.AbstractTransaction;
import model.transactions.AbstractTransactionFactory;
import model.transactions.Deposit;
import model.transactions.Payment;

/**
 * Parses the text contents of a TD Bank statement.
 */
public class TDBankStatementParser implements StatementParser {
  private String statement;
  private boolean hasReceivedStatement;
  private List<Deposit> deposits;
  private List<Payment> payments;
  private Date startDate;
  private Date endDate;

  private static final Pattern DATE_RANGE_PATTERN = Pattern.compile("Statement Period: (\\w{3} \\d{1,2} \\d{4})-(\\w{3} \\d{1,2} \\d{4})");
  private static final Pattern TRANSACTION_PATTERN = Pattern.compile("(\\d{2})/(\\d{2}) (.*) (\\d{0,3},?\\d{0,3},?\\d{1,3})\\.(\\d{2})");
  private static final String[] BANNED_PATTERNS = {
    ".* DDA PUR",
    "\\*+\\d+",
    "\\* \\w{2}",
  };

  /**
   * Initializes a new {@code TDBankStatementParser} instance.
   */
  public TDBankStatementParser() {
    this.deposits = new ArrayList<>();
    this.payments = new ArrayList<>();
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

    Matcher dateRangeMatcher = DATE_RANGE_PATTERN.matcher(statement);
    if(!dateRangeMatcher.find() || dateRangeMatcher.groupCount() != 2) {
      throw new IllegalStateException("Cannot find date range in statement.");
    }

    this.startDate = this.abbreviatedDateStringAsDate(dateRangeMatcher.group(1));
    this.endDate = this.abbreviatedDateStringAsDate(dateRangeMatcher.group(2));

    this.parseStatement();
  }

  private void parseStatement() {
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

  // Parses a transaction line into a transaction
  private <T extends AbstractTransaction> T parseTransaction(String transactionString, AbstractTransactionFactory<T> factory) {
    Matcher transactionMatcher = TRANSACTION_PATTERN.matcher(transactionString);
    if (transactionMatcher.find()) {
      Month month = Month.asMonth(Integer.parseInt(transactionMatcher.group(1)));
      int day = Integer.parseInt(transactionMatcher.group(2));
      Date date = Date.withinRange(this.startDate, this.endDate, month, day);

      String description = transactionMatcher.group(3).replace(",", " ");
      for (String bannedPattern : BANNED_PATTERNS) {
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

  private Date abbreviatedDateStringAsDate(String dateString) throws IllegalStateException {
    String[] parts = dateString.split(" ");
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
  public List<Deposit> getDeposits() throws IllegalStateException {
    if (!this.hasReceivedStatement) {
      throw new IllegalStateException("No statement to parse. Statement must be provided using receiveStatement() method.");
    }

    return List.copyOf(this.deposits);
  }

  @Override
  public List<Payment> getPayments() throws IllegalStateException {
    if (!this.hasReceivedStatement) {
      throw new IllegalStateException("No statement to parse. Statement must be provided using receiveStatement() method.");
    }

    return List.copyOf(this.payments);
  }
}
