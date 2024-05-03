package dk.peterlind.jsonparser.exceptions;

public class IllegalExponentInNumberException extends JsonFormatException {
  public IllegalExponentInNumberException(String filename, int lineCounter, int columnCounter, char currentCharacter) {
    super(filename,lineCounter,columnCounter, String.valueOf(currentCharacter), currentCharacter + " is and illegal part of a number exponent");
  }
}
