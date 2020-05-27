package syntactical.ast;

import lexical.LexicalUnit;
import syntactical.ast.visitors.Visitor;

import java.util.Collection;

public class ConstructorDeclarationNode extends FunctionDeclarationNode {

    public ConstructorDeclarationNode(IdGenerator id, LexicalUnit lexeme, Collection<Name> parameterNames, BlockStatementNode code) {
        super(id, lexeme, new Name(lexeme, null), parameterNames, code);
    }

    public void setType(Type type) {
        name = new Name(lexeme, type);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
