package model.parser.pdf;

/**
 * Parses a PDF file into its text content.
 */
public interface PDFParser {
  /**
   * Imports a PDF into this PDF parser.
   * @param filepath the path to the PDF file
   * @throws IllegalStateException if there is an error importing the PDF file.
   */
  void importFile(String filepath) throws IllegalStateException;

  /**
   * Gets the text contents of the imported PDF.
   * @return the text contents of the PDF file imported using the {@code importFile} method.
   * @throws IllegalStateException if a PDF has not been imported OR if there is an error parsing
   * the PDF.
   */
  String getTextContents() throws IllegalStateException;
}
