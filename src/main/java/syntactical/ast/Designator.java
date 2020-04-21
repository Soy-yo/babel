package syntactical.ast;

public class Designator {

    private ExpressionNode accessibleElement;
    private ExpressionNode access;

    public Designator(ExpressionNode accessibleElement, ExpressionNode access) {
        this.accessibleElement = accessibleElement;
        this.access = access;
    }

    @Override
    public String toString() {
        return accessibleElement + "" + access;
    }

}
