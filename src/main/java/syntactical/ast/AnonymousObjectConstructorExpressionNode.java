package syntactical.ast;

import lexical.LexicalUnit;
import syntactical.Defaults;
import syntactical.ast.visitors.Visitor;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AnonymousObjectConstructorExpressionNode extends ExpressionNode {

    private VarDeclarationNode root;
    private Set<Name> fieldNames;

    public AnonymousObjectConstructorExpressionNode(IdGenerator id,
                                                    LexicalUnit lexeme,
                                                    VarDeclarationNode root) {
        super(id, lexeme, Defaults.FORM);
        this.root = root;
        this.fieldNames = new HashSet<>();
        if (root != null) {
            root.forEach(v -> fieldNames.add(v.getName()));
        }
    }

    public AnonymousObjectConstructorExpressionNode(IdGenerator id, VarDeclarationNode root) {
        this(id, null, root);
    }

    public AnonymousObjectConstructorExpressionNode(IdGenerator id) {
        this(id, null, null);
    }

    public boolean isEmpty() {
        return root == null;
    }

    public VarDeclarationNode getFields() {
        return root;
    }

    private String fieldsToString() {
        if (root == null) {
            return "";
        }
        return StreamSupport.stream(root.spliterator(), false)
                .map(DeclarationNode::toString)
                .collect(Collectors.joining(", ", ", ", ""));
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return super.toString()
                + "{type:" + type
                + fieldsToString()
                + "}";
    }

}
