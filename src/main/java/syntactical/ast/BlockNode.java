package syntactical.ast;

import syntactical.ast.visitors.Visitor;

public class BlockNode extends StatementNode {

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
