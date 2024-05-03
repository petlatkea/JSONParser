package dk.peterlind.jsonparser.exceptions;

public class InvalidDigitInUnicodeStringException extends JsonFormatException {

  public InvalidDigitInUnicodeStringException(String filename, int line, int column, char value) {
    super(filename, line, column, String.valueOf(value), value + "is not a valid digit in a unicode hex string");
  }
}
