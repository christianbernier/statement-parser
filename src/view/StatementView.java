package view;

import java.io.IOException;

/**
 * Displays the progress of parsing a statement.
 */
public interface StatementView {
  /**
   * Render a message to the user, displaying the progress of the program.
   * @param message the message to be displayed to the user
   * @throws IOException if there is an error displaying the message.
   */
  void renderMessage(String message) throws IOException;
}
