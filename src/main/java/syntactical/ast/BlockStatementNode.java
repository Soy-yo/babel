package syntactical.ast;

import syntactical.ast.visitors.Visitor;

public class BlockStatementNode extends StatementNode {

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
