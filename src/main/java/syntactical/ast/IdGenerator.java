package syntactical.ast;

public class IdGenerator {

    private int id = 1024;

    public int get() {
        return id++;
    }

}
