package model.parser.statement.discover;

import java.util.ArrayList;
import java.util.List;
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
 * Parses the text contents of a Discover statement.
 */
public class DiscoverStatementParser implements StatementParser {
  private String statement;
  private boolean hasReceivedStatement;
  private List<Deposit> deposits;
  private List<Payment> payments;
  private Date startDate;
  private Date endDate;

  private static final Pattern DATE_RANGE_PATTERN = Pattern.compile("OPEN TO CLOSE DATE: (\\d{2}/\\d{2}/\\d{4}) - (\\d{2}/\\d{2}/\\d{4})");
  private static final Pattern TRANSACTION_PATTERN = Pattern.compile("(\\d{2})/(\\d{2}) (.*)( -?)\\$(\\d{0,3},?\\d{0,3},?\\d{1,3}).(\\d{2})");
  private static final String[] BANNED_PATTERNS = {
    "TST\\*",
    "\\d{3}-\\d{3}-\\d{4}",
    "\\d{3} \\d{3} \\d{4}",
    "Automotive",
    "Department Stores",
    "Education",
    "Gasoline",
    "Government Services",
    "Home Improvement",
    "Medical Services",
    "Merchandise",
    "Restaurants",
    "Services",
    "Supermarkets",
    "Travel/Entertainment",
    "Warehouse Clubs",
    "Awards and Rebate Credits",
    "Balance Transfers",
    "Cash Advances",
    "Fees",
    "Interest",
    "Other/Miscellaneous",
    "Payments and Credits",
  };

  /**
   * Initializes a new {@code DiscoverStatementParser} instance.
   */
  public DiscoverStatementParser() {
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

    this.startDate = this.dateStringAsDate(dateRangeMatcher.group(1));
    this.endDate = this.dateStringAsDate(dateRangeMatcher.group(2));

    this.parseStatement();
  }

  private Date dateStringAsDate(String dateString) throws IllegalStateException {
    String[] parts = dateString.split("/");
    if (parts.length != 3) {
      throw new IllegalStateException("Date does not have 3 parts: month, day, year.");
    }

    Month month = Month.asMonth(Integer.parseInt(parts[0]));
    int day = Integer.parseInt(parts[1]);
    int year = Integer.parseInt(parts[2]);

    return new Date(year, month, day);
  }

  private void parseStatement() {
    // Statement info is between these two markers
    int startIndex = this.statement.indexOf("DATE PAYMENTS AND CREDITS AMOUNT");
    int endIndex = this.statement.indexOf("TOTAL FEES FOR THIS PERIOD");

    String transactionDetails =
      this.statement.substring(startIndex, endIndex)
        .replaceAll("\n", " ")
        .replaceAll("(\\d{2}/\\d{2})", "\n$0")
        .replaceAll("PREVIOUS BALANCE [\\w\\W]* EXPIRES BALANCE SUBJECT TO INTEREST RATE INTEREST CHARGE", "");

    String[] transactionLines = transactionDetails.split("\n");

    for (String transactionLine : transactionLines) {
      if (transactionLine.contains("-$")) {
        Deposit deposit = this.parseTransaction(transactionLine, new Deposit.DepositFactory());
        if (deposit != null) {
          this.deposits.add(deposit);
        }
      } else {
        Payment payment = this.parseTransaction(transactionLine, new Payment.PaymentFactory());
        if (payment != null) {
          this.payments.add(payment);
        }
      }
    }
  }

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

      int dollars = Integer.parseInt(transactionMatcher.group(5).replaceAll(",", ""));
      int cents = Integer.parseInt(transactionMatcher.group(6));
      MoneyAmount amount = new MoneyAmount(dollars, cents);

      return factory.make(date, description, amount);
    } else {
      return null;
    }
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
