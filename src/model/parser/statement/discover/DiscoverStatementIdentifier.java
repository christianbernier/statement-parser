package model.parser.statement.discover;

import model.parser.statement.StatementTypeIdentifier;

public class DiscoverStatementIdentifier implements StatementTypeIdentifier {
  @Override
  public boolean matches(String statement) throws IllegalArgumentException {
    if (statement == null) {
      throw new IllegalArgumentException("Statement cannot be null.");
    }

    return statement.contains("Discover.com");
  }

  @Override
  public String name() {
    return "Discover";
  }
}
