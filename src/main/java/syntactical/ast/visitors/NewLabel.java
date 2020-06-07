package syntactical.ast.visitors;

public class NewLabel {

    private char letter;
    private int number;

    public NewLabel() {
        this.letter = 'A';
        this.number = 0;
    }

    public String getLabel() {
        String result = String.valueOf(letter) + number;
        number = (number + 1) % 100;
        if (number == 0) {
            letter++;
        }
        return result;
    }

    public String getLabel(Function f) {
        return "FN" + f.id;
    }

}
