package model.parser.statement;

/**
 * Functional classes that identify if a statement {@code String} is of a certain type.
 */
public interface StatementTypeIdentifier {
  /**
   * Does this statement {@code String} fit the format of this type of statement?
   * @param statement the text contents of a statement
   * @return whether the provided text contents fit this type of statement
   * @throws IllegalArgumentException if the provided {@code statement} is {@code null}.
   */
  boolean matches(String statement) throws IllegalArgumentException;

  /**
   * Gets the name of this type of statement.
   * @return the name of this type of statement.
   */
  String name();
}
