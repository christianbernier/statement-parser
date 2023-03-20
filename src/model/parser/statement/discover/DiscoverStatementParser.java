package model.parser.statement.discover;

import java.util.regex.Pattern;

import model.date.Date;
import model.date.Month;
import model.parser.statement.AbstractStatementParser;
import model.transactions.Deposit;
import model.transactions.Payment;

/**
 * Parses the text contents of a Discover statement.
 */
public class DiscoverStatementParser extends AbstractStatementParser {

  /**
   * Initializes a new {@code DiscoverStatementParser} instance.
   */
  public DiscoverStatementParser() {
    super();
  }

  @Override
  protected Pattern getDateRangePattern() {
    return Pattern.compile("OPEN TO CLOSE DATE: (\\d{2}/\\d{2}/\\d{4}) - (\\d{2}/\\d{2}/\\d{4})");
  }

  @Override
  protected Pattern getTransactionPattern() {
    return Pattern.compile("(\\d{2})/(\\d{2}) (.*) -?\\$(\\d{0,3},?\\d{0,3},?\\d{1,3}).(\\d{2})");
  }

  @Override
  protected String[] getBannedStrings() {
    return new String[] {
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
  }

  @Override
  protected Date fromDateRangeString(String dateRangeString) throws IllegalStateException {
    String[] parts = dateRangeString.split("/");
    if (parts.length != 3) {
      throw new IllegalStateException("Date does not have 3 parts: month, day, year.");
    }

    Month month = Month.asMonth(Integer.parseInt(parts[0]));
    int day = Integer.parseInt(parts[1]);
    int year = Integer.parseInt(parts[2]);

    return new Date(year, month, day);
  }

  @Override
  protected void parseStatement() {
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
}
