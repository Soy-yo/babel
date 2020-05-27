package syntactical.ast;

import lexical.LexicalUnit;

import java.util.Collection;

public class ConstructorDeclarationNode extends FunctionDeclarationNode {

    public ConstructorDeclarationNode(IdGenerator id, LexicalUnit lexeme, Collection<Name> parameterNames, BlockStatementNode code) {
        super(id, lexeme, new Name(lexeme, null), parameterNames, code);
    }

    public void setType(ClassDeclarationNode classNode) {
        name = new Name(lexeme, classNode.getType());
    }

}
