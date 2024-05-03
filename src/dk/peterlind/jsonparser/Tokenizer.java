package dk.peterlind.jsonparser;

import dk.peterlind.jsonparser.exceptions.*;

import java.io.*;

import static java.lang.Character.isWhitespace;

/**
 * The tokenizer reads the file as a stream of characters
 * and returns a list of tokens.
 * <p>
 * It can be used as a "producer" of tokens for the parser.
 */
public class Tokenizer {

  private String filename;
  private int lineCounter;
  private int columnCounter;
  private BufferedReader reader;
  private char currentCharacter;
  private boolean moreTokens = true;

  Tokenizer(String filename) throws IOException {
    // Store the filename for error messages
    this.filename = filename;
    // store the reader, so the nextCharacter method can be called from anywhere
    reader = new BufferedReader(new FileReader(filename));
    // read the first character - to be prepared for the first call to nextToken
    nextCharacter();
  }

  /**
   * Read the next character from the file.
   * Stores it in currentCharacter and returns it
   * @return the character read (also stored in currentCharacter)
   * @throws EndOfFileException when the file ends
   */
  private char nextCharacter() throws EndOfFileException {
    try {
      int value = reader.read();
      if (value == -1) {
        throw new EndOfFileException(filename, lineCounter, columnCounter, "EOF");
      } else {
        columnCounter++;
        if ((value == '\n' || value == '\r') && columnCounter > 0) {
          lineCounter++;
          columnCounter = 0;
        }
        currentCharacter = (char) value;
      }
    } catch (IOException e) {
      // TODO: Throw better custom exception telling that something awful happened
      throw new RuntimeException(e);
    }

    return currentCharacter;
  }

  public boolean hasNext() {
    // TODO: Implement this method better, so it checks if file is opened, and not at EOF, rather than just this boolean ...
    return moreTokens;
  }


  public Token nextToken() {
//    System.out.printf("Current character: %c\n", currentCharacter);

    Token token = null;

    try {
      token = switch (currentCharacter) {
        case '{' -> leftCurlyBracket(currentCharacter);
        case '}' -> rightCurlyBracket(currentCharacter);
        case '[' -> leftSquareBracket(currentCharacter);
        case ']' -> rightSquareBracket(currentCharacter);
        case ',' -> comma(currentCharacter);
        case ':' -> colon(currentCharacter);
        case ' ', '\n', '\r', '\t' -> whitespace(currentCharacter);
        case '"' -> string(currentCharacter);
        case '-','0','1','2','3','4','5','6','7','8','9' -> number(currentCharacter);
        case 'n' -> possibleNull(currentCharacter);
        case 't' -> possibleTrue(currentCharacter);
        case 'f' -> possibleFalse(currentCharacter);


        default ->
            throw new IllegalStateException("Unexpected value: " + nextCharacter());
      };
    } catch (EndOfFileException e) {
      // If the file ends during the tokenizing, it might not be an error,
      // but we need to stop tokenizing, so return an EOF token
      token = new Token(TokenType.EOF, "END OF FILE");
      moreTokens = false;
    }

    return token;
  }

  private Token possibleFalse(char c) {
    // if the next characters are "alse" we have a false token!
    if (nextCharacter() == 'a' && nextCharacter() == 'l' && nextCharacter() == 's' && nextCharacter() == 'e') {
      nextCharacter();
      return new Token(TokenType.FALSE, "false");
    } else {
      throw new UnexpectedKeywordException(filename, lineCounter, columnCounter, "false", "f" + currentCharacter, currentCharacter);
    }
  }

  private Token possibleTrue(char c) {
    // if the next characters are "rue" we have a true token!
    if (nextCharacter() == 'r' && nextCharacter() == 'u' && nextCharacter() == 'e') {
      nextCharacter();
      return new Token(TokenType.TRUE, "true");
    } else {
      throw new UnexpectedKeywordException(filename, lineCounter, columnCounter, "true", "t" + currentCharacter, currentCharacter);
    }
  }

  private Token possibleNull(char c) {
    // if the next characters are "ull" we have a null token!
    if (nextCharacter() == 'u' && nextCharacter() == 'l' && nextCharacter() == 'l') {
      nextCharacter();
      return new Token(TokenType.NULL, "null");
    } else {
      throw new UnexpectedKeywordException(filename, lineCounter, columnCounter, "null", "n" + currentCharacter, currentCharacter);
    }
  }

  /**
   * A number is defined as:
   * optional -
   * either 0 or
   *  1-9 followed by either 0-9 or
   * optional fraction - which is
   *  . followed by multiple 0-9s
   * optional exponent
   *
   */
  private Token number(char c) {
    StringBuilder numstr = new StringBuilder(c);

    // if currentCharacter is '-' accept it unconditionally, and get next
    if(c=='-') {
      numstr.append('-');
      nextCharacter();
    }

    // if currentCharacter is 0 expect that it is the only digit (or a fraction or exponent)
    if(currentCharacter=='0') {
      numstr.append('0');
      nextCharacter();
    } else {
      // if first character wasn't a 0 - it MUST be a digit 1-9
      if("123456789".indexOf(currentCharacter) != -1) {
        numstr.append(currentCharacter);

        // get next character - which can be 0-9
        nextCharacter();
        while("0123456789".indexOf(currentCharacter) != -1) {
          numstr.append(currentCharacter);
          nextCharacter();
        }
        // number ends when we find a non-digit character - but it might not fail yet!

      } else {
        // but it fails when the first (initial) digit isn't 1-9
        throw new IllegalInitialDigitInNumberException(filename, lineCounter, columnCounter, currentCharacter);
      }
    }

    // if currentCharacter is '.' we get a fraction
    if(currentCharacter=='.') {
      numstr.append('.');
      // A fraction is a . followed by multiple digits
      nextCharacter();
      // If there is no digit immediately after - it is an error
      if("0123456789".indexOf(currentCharacter) == -1) {
        throw new IllegalFractionInNumberException(filename, lineCounter, columnCounter, currentCharacter);
      }
      // otherwise, keep reading all the digits
      while("0123456789".indexOf(currentCharacter) != -1) {
        numstr.append(currentCharacter);
        nextCharacter();
      }
    }

    // if currentCharacter is E or e - we expect an exponent
    if(currentCharacter=='E' || currentCharacter =='e') {
      numstr.append('e');
      // an exponent is an e followed by optional + or - and some digits
      nextCharacter();
      // if currentCharacter is - or + we accept it unconditionally
      if(currentCharacter =='-'||currentCharacter =='+') {
        numstr.append(currentCharacter);
        nextCharacter();
      }

      // if next character isn't a digit - fail
      if ("0123456789".indexOf(currentCharacter) == -1) {
        throw new IllegalExponentInNumberException(filename, lineCounter, columnCounter, currentCharacter);
      }

      // otherwise - get the actual exponent - a series of digits 0-9
      while("0123456789".indexOf(currentCharacter) != -1) {
        numstr.append(currentCharacter);
        nextCharacter();
      }
    }

    return new Token(TokenType.NUMBER, numstr.toString());
  }

  private Token leftCurlyBracket(char c) {
    nextCharacter();
    return new Token(TokenType.LEFT_CURLY_BRACKET, c);
  }

  private Token rightCurlyBracket(char c) {
    nextCharacter();
    return new Token(TokenType.RIGHT_CURLY_BRACKET, c);
  }

  private Token leftSquareBracket(char c) {
    nextCharacter();
    return new Token(TokenType.LEFT_SQUARE_BRACKET, c);
  }

  private Token rightSquareBracket(char c) {
    nextCharacter();
    return new Token(TokenType.RIGHT_SQUARE_BRACKET, c);
  }

  private Token comma(char c) {
    nextCharacter();
    return new Token(TokenType.COMMA, c);
  }

  private Token colon(char c) {
    nextCharacter();
    return new Token(TokenType.COLON, c);
  }

  private Token whitespace(char c) {
    StringBuilder value = new StringBuilder(String.valueOf(c));
    while (isWhitespace(currentCharacter)) {
      value.append(currentCharacter);
      nextCharacter();
    }
    return new Token(TokenType.WHITESPACE, " ");
    //return new Token(TokenType.WHITESPACE, value);
  }

  private Token string(char c) {
    StringBuilder sb = new StringBuilder();

    // End the string when we find a " character
    while (nextCharacter() != '"') {
      // check if character is a \ and handle escape characters
      if (currentCharacter == '\\') {
        // next character can be ", \, /, b, f, n, r, t, or u
        nextCharacter();
        switch (currentCharacter) {
          case '"', '\\', '/' -> sb.append(currentCharacter);
          case 'b' -> sb.append('\b');
          case 'f' -> sb.append('\f');
          case 'n' -> sb.append('\n');
          case 'r' -> sb.append('\r');
          case 't' -> sb.append('\t');
          case 'u' -> {
            // next 4 characters should be hex digits
            StringBuilder hex = new StringBuilder();
            for (int i = 0; i < 4; i++) {
              nextCharacter();
              // Check if character is a valid hex digit
              if ("0123456789abcdefABCDEF".indexOf(currentCharacter) == -1) {
                throw new InvalidDigitInUnicodeStringException(filename, lineCounter, columnCounter, currentCharacter);
              } else {
                hex.append(currentCharacter);
              }
            }
            // Convert hex to char, and append to string
            sb.append((char) Integer.parseInt(hex.toString(), 16));
          }

          default -> throw new InvalidEscapeCharacterInStringException(filename, lineCounter, columnCounter, currentCharacter);
        }

      } else if (currentCharacter < 32) {
        // character is an unescaped control character
        throw new ControlCharacterInStringException(filename, lineCounter, columnCounter, currentCharacter);
      } else {
        // normal character - just append it
        sb.append(currentCharacter);
      }

    }
    // consume last " before ending the string
    nextCharacter();

    return new Token(TokenType.STRING, sb.toString());
  }


}
