import controller.SynchronousController;
import controller.SynchronousControllerImpl;
import model.parser.pdf.PDFParser;
import model.parser.pdf.PDFParserImpl;
import view.StatementExporter;
import view.StatementFileWriter;
import view.StatementTextView;
import view.StatementView;

import static java.lang.System.exit;

/**
 * Runs the program using implementations of necessary interfaces.
 */
public class StatementParserRunner {

  /**
   * Main method to run the program.
   *
   * @param args program arguments, which should include the filepath of the PDF file as the
   *             first one.
   */
  public static void main(String[] args) {
    if (args.length == 0) {
      System.out.println("Please include an input file path.");
      exit(1);
    }

    String inFile = args[0];

    if (!inFile.endsWith(".pdf")) {
      System.out.println("Please provide a PDF file.");
      exit(2);
    }

    String outFile = inFile.replace(".pdf", ".csv");

    if (inFile.equals("") || outFile.equals("")) {
      System.out.println("Please provide input and output file paths.");
      exit(3);
    }

    try {
      StatementView view = new StatementTextView(System.out);
      PDFParser pdfParser = new PDFParserImpl();
      StatementExporter exporter = new StatementFileWriter(outFile);
      SynchronousController controller = new SynchronousControllerImpl(view, pdfParser, exporter, inFile);

      controller.run();
    } catch (IllegalStateException e) {
      System.out.println("\n\n\nEncountered an error: " + e.getMessage());
      e.printStackTrace();
    } catch (Exception e) {
      System.out.println("\n\n\nEncountered an unknown error: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
