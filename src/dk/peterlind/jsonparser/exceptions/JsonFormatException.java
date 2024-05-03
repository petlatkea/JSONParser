package dk.peterlind.jsonparser.exceptions;

public class JsonFormatException extends RuntimeException {

  protected String filename;
  protected int line;
  protected int column;
  protected String value;

  public JsonFormatException(String filename, int line, int column, String value, String message) {
    super("Error in file " + filename + " at line " + line + ", column " + column + ": " + value + " " + message);
    this.filename = filename;
    this.line = line;
    this.column = column;
    this.value = value;
  }

}