package syntactical.ast;

import lexical.LexicalUnit;

import java.util.Collection;

public class ConstructorDeclarationNode extends FunctionDeclarationNode {

    private static final String NAME = "constructor";
    private static final Type UNKNOWN_TYPE = new Type("?");

    public ConstructorDeclarationNode(IdGenerator id, LexicalUnit lexeme, Collection<Name> parameterNames, BlockStatementNode code) {
        super(id, lexeme, new Name(NAME, UNKNOWN_TYPE), parameterNames, code);
    }

    public ConstructorDeclarationNode(IdGenerator id, Collection<Name> parameterNames, BlockStatementNode code) {
        this(id, null, parameterNames, code);
    }

    public void setType(ClassDeclarationNode classNode) {
        name = new Name(NAME, classNode.getType());
    }

}
