package view;

import java.io.IOException;

/**
 * Displays the progress of the statement exporter to the user in text.
 */
public class StatementTextView implements StatementView {
  // Invariant: destination is not null
  private final Appendable destination;

  /**
   * Initializes a {@code StatementTextView} with the provided {@code Appendable}.
   * @param destination the destination of text transmissions from this view.
   * @throws IllegalArgumentException if the provided {@code destination} is {@code null}.
   */
  public StatementTextView(Appendable destination) throws IllegalArgumentException {
    if (destination == null) {
      throw new IllegalArgumentException("Destination cannot be null.");
    }

    this.destination = destination;
  }

  @Override
  public void renderMessage(String message) throws IOException {
    try {
      this.destination.append(message);
    }
    catch (IOException e) {
      throw new IOException("Transmission of the message to the destination failed");
    }
  }
}
