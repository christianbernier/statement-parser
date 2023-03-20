package model.parser.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

/**
 * Parses a PDF file using the Apache PDF Box library.
 */
public class PDFParserImpl implements PDFParser {
  private PDDocument pdDoc = null;
  private PDFTextStripper pdfStripper = null;

  @Override
  public void importFile(String filepath) throws IllegalStateException {
    File statementFile = new File(filepath);
    try {
      pdDoc = PDDocument.load(statementFile);
      pdfStripper = new PDFTextStripper();
    } catch (IOException e) {
      throw new IllegalStateException("Failed to load PDF.");
    }
  }

  @Override
  public String getTextContents() throws IllegalStateException {
    if (pdDoc == null || pdfStripper == null) {
      throw new IllegalStateException("There is no PDF file to parse.");
    }

    try {
      String parsedText = pdfStripper.getText(pdDoc);
      pdDoc.close();
      return parsedText;
    } catch (IOException e) {
      throw new IllegalStateException("Failed to parse PDF.");
    }
  }
}
