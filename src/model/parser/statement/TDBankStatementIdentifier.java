package model.parser.statement;

/**
 * Identifies a TD Bank statement.
 */
public class TDBankStatementIdentifier implements StatementTypeIdentifier {
  @Override
  public boolean matches(String statement) throws IllegalArgumentException{
    if (statement == null) {
      throw new IllegalArgumentException("Statement cannot be null.");
    }

    return statement.contains("tdbank.com");
  }

  @Override
  public String name() {
    return "TD Bank";
  }
}
