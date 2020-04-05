package syntactical.ast;

import syntactical.ast.visitors.Visitable;
import syntactical.ast.visitors.Visitor;

public abstract class StatementNode implements ASTNode, Visitable {

    @Override
    public abstract void accept(Visitor visitor);

}
