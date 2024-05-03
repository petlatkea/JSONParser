package dk.peterlind.jsonparser;

public class Token {
  TokenType type;
  String value;

  public Token(TokenType type, String value) {
    this.type = type;
    this.value = value;
  }
  public Token(TokenType type, char value) {
    this.type = type;
    this.value = String.valueOf(value);
  }

  @Override
  public String toString() {
    return value + " ... TOKEN: " + type;
  }
}
