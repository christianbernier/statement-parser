package controller;

/**
 * Controls an instance of a statement parser, organizing when different actions occur and
 * displaying information to the user via a provided view.
 */
public interface SynchronousController {
  /**
   * Run the program in a synchronous manner.
   * @throws IllegalStateException if the controller encounters an error in processing the file.
   */
  void run() throws IllegalStateException;
}
