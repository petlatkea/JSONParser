package dk.peterlind.jsonparser;

import java.io.IOException;

public class JSONParser {

  public void parseFile(String filename) throws IOException {
    // Read the file as Tokens - for now, just print them to the screen
    Tokenizer tokenizer = new Tokenizer(filename);
    while(tokenizer.hasNext()) {
      Token token = tokenizer.nextToken();
      System.out.println(token);
    }
  }

  public static void main(String[] args) throws IOException {
    if(args.length<1) {
      System.out.println("Please provide an argument with the jsonfile to parse");
    } else {
      new JSONParser().parseFile(args[0]);
    }
  }
}