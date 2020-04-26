package syntactical.ast;

import syntactical.ast.visitors.Visitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListConstructorExpressionNode extends ExpressionNode {

    private static final Type CHAR = new Type("Char");
    private static final Type STRING = new Type("Array", CHAR);

    private List<ExpressionNode> elements;
    private List<ExpressionNode> view;

    private ListConstructorExpressionNode(List<ExpressionNode> elements, boolean copy, Type type) {
        if (copy) {
            this.elements = new ArrayList<>(elements);
        } else {
            this.elements = elements;
        }
        this.view = Collections.unmodifiableList(elements);
        this.type = type;
    }

    public ListConstructorExpressionNode(List<ExpressionNode> elements) {
        this(elements, true, null);
    }

    public ListConstructorExpressionNode() {
        this(new ArrayList<>(), false, null);
    }

    public static ListConstructorExpressionNode fromString(String s) {
        if (s.charAt(0) == '"') {
            s = s.substring(1, s.length() - 1);
        }
        List<ExpressionNode> elements = new ArrayList<>();
        boolean escaping = false;
        for (char c : s.toCharArray()) {
            if (escaping) {
                switch (c) {
                    case 't':
                        elements.add(new ConstantExpressionNode('\t', "\\t", CHAR));
                        break;
                    case 'b':
                        elements.add(new ConstantExpressionNode('\b', "\\b", CHAR));
                        break;
                    case 'n':
                        elements.add(new ConstantExpressionNode('\n', "\\n", CHAR));
                        break;
                    case 'r':
                        elements.add(new ConstantExpressionNode('\r', "\\r", CHAR));
                        break;
                    case 'f':
                        elements.add(new ConstantExpressionNode('\f', "\\f", CHAR));
                        break;
                    case '0':
                        elements.add(new ConstantExpressionNode('\0', "\\0", CHAR));
                        break;
                    case '\\':
                        elements.add(new ConstantExpressionNode('\\', "\\\\", CHAR));
                        break;
                    case '"':
                        elements.add(new ConstantExpressionNode('"'));
                        break;
                }
                escaping = false;
            } else {
                if (c == '\\') {
                    escaping = true;
                } else {
                    elements.add(new ConstantExpressionNode(c));
                }
            }
        }
        return new ListConstructorExpressionNode(elements, false, STRING);
    }

    public List<ExpressionNode> getElements() {
        return view;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return super.toString()
                + " {type:" + type
                + ", value:" + elements
                + "}";
    }

}
