package syntactical.ast;

import syntactical.ast.visitors.Visitor;

public class ForStatementNode extends StatementNode {

    private VarDeclarationNode variable;
    private ExpressionNode iterable;
    private BlockStatementNode block;

    public ForStatementNode(VarDeclarationNode variable, ExpressionNode iterable, BlockStatementNode block) {
        this.variable = variable;
        this.iterable = iterable;
        this.block = block;
    }

    public ForStatementNode(Name variable, ExpressionNode iterable, BlockStatementNode block) {
        this(new VarDeclarationNode(variable), iterable, block);
    }

    public VarDeclarationNode getVariable() {
        return variable;
    }

    public ExpressionNode getIterable() {
        return iterable;
    }

    public BlockStatementNode getBlock() {
        return block;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return super.toString()
                + " {variable:" + variable
                + " iterable: " + iterable
                + "}";
    }

}
