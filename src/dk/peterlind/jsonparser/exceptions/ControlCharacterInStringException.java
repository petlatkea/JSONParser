package dk.peterlind.jsonparser.exceptions;

public class ControlCharacterInStringException extends JsonFormatException {
  public ControlCharacterInStringException(String filename, int line, int column, char value) {
    super(filename, line, column, String.valueOf(value), value + "is a control character and not allowed in a string");
  }
}
