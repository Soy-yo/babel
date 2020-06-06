package syntactical;

public class NewLabel {

  private char letter;
  private int number;

  public NewLabel() {
    this.letter = 'A';
    this.number = 1;
  }

  public String getLabel() {
    String result = String.valueOf(letter) + String.valueOf(number);
    number = (number + 1) % 100;
    letter = (char) ((letter + 1) % 'Z');
    return result;
  }
}
