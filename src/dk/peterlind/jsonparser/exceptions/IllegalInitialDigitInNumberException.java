package dk.peterlind.jsonparser.exceptions;

public class IllegalInitialDigitInNumberException extends JsonFormatException {

  public IllegalInitialDigitInNumberException(String filename, int line, int column, char value) {
    super(filename, line, column, String.valueOf(value), "A number cannot start with the digit " + value);
  }
}
