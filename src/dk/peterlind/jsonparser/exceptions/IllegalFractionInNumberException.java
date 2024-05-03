package dk.peterlind.jsonparser.exceptions;

public class IllegalFractionInNumberException extends JsonFormatException {
  public IllegalFractionInNumberException(String filename, int lineCounter, int columnCounter, char currentCharacter) {
    super(filename, lineCounter, columnCounter, String.valueOf(currentCharacter), currentCharacter + " following a . is not a valid fractionalnumber character.");
  }
}
