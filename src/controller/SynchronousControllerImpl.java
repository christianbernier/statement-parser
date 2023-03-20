package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.parser.pdf.PDFParser;
import model.parser.statement.StatementParser;
import model.parser.statement.StatementTypeIdentifier;
import model.parser.statement.discover.DiscoverStatementIdentifier;
import model.parser.statement.discover.DiscoverStatementParser;
import model.parser.statement.tdbank.TDBankStatementIdentifier;
import model.parser.statement.tdbank.TDBankStatementParser;
import model.transactions.AbstractTransaction;
import model.transactions.Deposit;
import model.transactions.Payment;
import model.transactions.TransactionDateComparator;
import view.StatementExporter;
import view.StatementView;

/**
 * Controls a statement parser to read a PDF file, extract the payments and deposits, and export
 * the summary into a CSV file.
 */
public class SynchronousControllerImpl implements SynchronousController {
  // Invariant: view is not null.
  private final StatementView view;

  // Invariant: pdfParser is not null.
  private final PDFParser pdfParser;

  // Invariant: statementExporter is not null.
  private final StatementExporter statementExporter;

  // Invariant: inFile is not null.
  private final String inFile;

  private static final Map<StatementTypeIdentifier, StatementParser> STATEMENT_TYPES = new HashMap<>() {{
    put(new TDBankStatementIdentifier(), new TDBankStatementParser());
    put(new DiscoverStatementIdentifier(), new DiscoverStatementParser());
  }};

  /**
   * Initializes a synchronous controller for a statement parser instance, using the provided implementations
   * for various interfaces.
   * @param view the view to which statuses about the progress of the parsing and exporting will be sent
   * @param pdfParser the method of parsing a PDF file into a {@code String}
   * @param statementExporter the method of exporting the contents of the statement
   * @param inFile the filepath of the PDF file to be parsed
   * @throws IllegalArgumentException if any of the provided arguments is {@code null}.
   */
  public SynchronousControllerImpl(StatementView view, PDFParser pdfParser, StatementExporter statementExporter, String inFile) throws IllegalArgumentException {
    if (view == null) {
      throw new IllegalArgumentException("View cannot be null.");
    }

    if (pdfParser == null) {
      throw new IllegalArgumentException("PDF parser cannot be null.");
    }

    if (statementExporter == null) {
      throw new IllegalArgumentException("Statement statementExporter cannot be null.");
    }

    if (inFile == null) {
      throw new IllegalArgumentException("Input filepath cannot be null.");
    }

    this.view = view;
    this.pdfParser = pdfParser;
    this.statementExporter = statementExporter;
    this.inFile = inFile;
  }

  @Override
  public void run() throws IllegalStateException {
    // Welcome
    this.transitMessage("Welcome to the statement parser.\n");
    this.transitMessage("File: " + this.inFile);

    // Import PDF
    this.transitMessage("\n\nTrying to import PDF...");
    this.pdfParser.importFile(this.inFile);
    this.transitMessage(" Success!");

    // Parse PDF into String
    this.transitMessage("\nTrying to parse PDF...");
    String textContents = this.pdfParser.getTextContents();
    this.transitMessage(" Success!");

    // Match to known statement type
    StatementParser statementParser = null;
    for (Map.Entry<StatementTypeIdentifier, StatementParser> type : STATEMENT_TYPES.entrySet()) {
      StatementTypeIdentifier identifier = type.getKey();
      if (identifier.matches(textContents)) {
        statementParser = type.getValue();

        this.transitMessage("\nStatement identified as type: " + identifier.name());
      }
    }

    if (statementParser == null) {
      throw new IllegalStateException("Could not identify statement as a recognized type.");
    }

    // Process statement
    statementParser.receiveStatement(textContents);
    List<Deposit> deposits = statementParser.getDeposits();
    List<Payment> payments = statementParser.getPayments();

    List<AbstractTransaction> allTransactions = new ArrayList<>();
    allTransactions.addAll(deposits);
    allTransactions.addAll(payments);
    allTransactions.sort(new TransactionDateComparator());

    this.transitMessage("\n\nFound " + deposits.size() + " deposit(s).");
    this.transitMessage("\nFound " + payments.size() + " payment(s).");

    // Export
    this.transitMessage("\n\nTrying to export CSV...");
    this.statementExporter.write(allTransactions);
    this.transitMessage(" Success!\n");

    this.transitMessage(this.statementExporter.confirmationMessage());

    // Close
    this.transitMessage("\n\nThank you for using the statement processor.");
  }

  // Sends a message to the view, throwing an IllegalStateException if there is an error.
  private void transitMessage(String message) throws IllegalStateException {
    try {
      this.view.renderMessage(message);
    } catch (IOException e) {
      throw new IllegalStateException("Encountered issue while transmitting message to view.");
    }
  }
}
