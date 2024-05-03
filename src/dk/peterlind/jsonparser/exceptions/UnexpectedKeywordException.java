package dk.peterlind.jsonparser.exceptions;

public class UnexpectedKeywordException extends JsonFormatException {
  public UnexpectedKeywordException(String filename, int lineCounter, int columnCounter, String expectedKeyword, String actualKeyword, char currentCharacter) {
    super(filename, lineCounter, columnCounter, String.valueOf(currentCharacter), "Unexpected keyword: " + actualKeyword + " - expected: " + expectedKeyword);
  }
}
