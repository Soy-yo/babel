package syntactical.ast;

import syntactical.ast.visitors.Visitor;

public class ErrorStatementNode extends StatementNode {

    public ErrorStatementNode(IdGenerator id) {
        super(id);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
