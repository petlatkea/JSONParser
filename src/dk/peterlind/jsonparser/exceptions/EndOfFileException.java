package dk.peterlind.jsonparser.exceptions;

/**
 * Exception thrown when the end of the file is reached.
 * extends Exception to make it a checked exception
 *
 * Technically it isn't exceptional that a file has ended, but since the tokenizers'
 * nextCharacter returns a char, there is no other way to inform the caller that the
 * file has ended.
 *
 */
public class EndOfFileException extends RuntimeException {

  public EndOfFileException(String filename, int line, int column, String value) {
    super("End of file " + filename + " reached after line " + line);
  }

}
