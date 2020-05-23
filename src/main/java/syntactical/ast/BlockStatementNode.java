package syntactical.ast;

import lexical.LexicalUnit;
import syntactical.ast.visitors.Visitor;

public class BlockStatementNode extends StatementNode {

    private StatementNode root;

    public BlockStatementNode(IdGenerator id, LexicalUnit lexeme, StatementNode root) {
        super(id, lexeme);
        this.root = root;
    }

    public BlockStatementNode(IdGenerator id, StatementNode root) {
        this(id, null, root);
    }

    public StatementNode root() {
        return root;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
