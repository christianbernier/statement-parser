package view;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import model.transactions.AbstractTransaction;
import model.transactions.Deposit;
import model.transactions.Payment;

/**
 * Writes the contents of a statement to a file.
 */
public class StatementFileWriter implements StatementExporter {
  // Invariant: filepath is not null.
  private final String filepath;

  /**
   * Initializes a new {@code StatementFileWriter} to export a statement to the provided filepath.
   * @param filepath the path to the file which will be written
   * @throws IllegalArgumentException if the provided {@code filepath} is {@code null}.
   */
  public StatementFileWriter(String filepath) throws IllegalArgumentException {
    if (filepath == null) {
      throw new IllegalArgumentException("Filepath cannot be null.");
    }

    this.filepath = filepath;
  }

  @Override
  public void write(List<AbstractTransaction> transactions) throws IllegalStateException {
    try {
      File outFile = new File(this.filepath);
      FileWriter fileWriter = new FileWriter(outFile);
      BufferedWriter writer = new BufferedWriter(fileWriter);

      writer.append("type,date,description,amount\n");

      for (AbstractTransaction transaction : transactions) {
        writer.append(transaction.toString() + "\n");
      }

      writer.close();
    } catch (IOException e) {
      throw new IllegalStateException("Writing to file failed.");
    }
  }

  @Override
  public String confirmationMessage() {
    return "Successfully exported as " + this.filepath;
  }
}
