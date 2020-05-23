package syntactical.ast;

public class IdGenerator {

    private int id = 0;

    public int get() {
        return id++;
    }

}
