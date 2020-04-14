package syntactical.ast;

import syntactical.ast.visitors.Visitor;

import java.util.Objects;

public class VarDeclarationNode extends DeclarationNode {

    private ExpressionNode initialValue;
    private boolean isConst;
    private boolean isGlobal;

    public VarDeclarationNode(
            Name name,
            ExpressionNode initialValue,
            boolean isConst,
            boolean isGlobal
    ) {
        super(name);
        this.initialValue = initialValue;
        this.isConst = isConst;
        this.isGlobal = isGlobal;
    }

    public VarDeclarationNode(Name name, boolean isGlobal) {
        this(name, null, false, isGlobal);
    }

    public VarDeclarationNode(Name name, ExpressionNode initialValue) {
        this(name, initialValue, false, false);
    }

    public VarDeclarationNode(Name name) {
        this(name, null);
    }

    public ExpressionNode getInitialValue() {
        return initialValue;
    }

    public boolean isConst() {
        return isConst;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public VarDeclarationNode constant() {
        return new VarDeclarationNode(name, initialValue, true, isGlobal);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return super.toString()
                + " {initialValue:" + initialValue
                + ", const:" + isConst
                + ", global:" + isGlobal
                + "}";
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        VarDeclarationNode node = (VarDeclarationNode) other;
        return Objects.equals(name, node.name);
    }

}
