package syntactical.ast;

import syntactical.ast.visitors.Visitor;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AnonymousObjectConstructorExpressionNode extends ExpressionNode {

    private static final Type TYPE = new Type("Form");

    private DeclarationNode root;
    private Set<Name> fieldNames;

    public AnonymousObjectConstructorExpressionNode(DeclarationNode root) {
        super(TYPE);
        if (root != null && !(root instanceof VarDeclarationNode)) {
            throw new IllegalArgumentException("Anonymous objects must only contain variable declarations");
        }
        this.root = root;
        this.fieldNames = new HashSet<>();
        if (root != null) {
            root.forEach(v -> fieldNames.add(v.getName()));
        }
    }

    public AnonymousObjectConstructorExpressionNode() {
        this(null);
    }

    public boolean isEmpty() {
        return root == null;
    }

    public DeclarationNode getFields() {
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
