package dk.peterlind.jsonparser.exceptions;

public class InvalidEscapeCharacterInStringException extends JsonFormatException {
  public InvalidEscapeCharacterInStringException(String filename, int line, int column, char value) {
    super(filename, line, column, String.valueOf(value), value + "is not a valid escape character in a string");
  }
}
