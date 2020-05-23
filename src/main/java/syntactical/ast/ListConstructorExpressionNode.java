package syntactical.ast;

import lexical.LexicalUnit;
import syntactical.ast.visitors.Visitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListConstructorExpressionNode extends ExpressionNode {

    private static final Type CHAR = new Type("Char");
    private static final Type STRING = new Type("Array", CHAR);

    private List<ExpressionNode> elements;
    private List<ExpressionNode> view;

    private ListConstructorExpressionNode(IdGenerator id, LexicalUnit lexeme, List<ExpressionNode> elements, boolean copy, Type type) {
        super(id, lexeme, type);
        if (copy) {
            this.elements = new ArrayList<>(elements);
        } else {
            this.elements = elements;
        }
        this.view = Collections.unmodifiableList(elements);
    }

    public ListConstructorExpressionNode(IdGenerator id, LexicalUnit lexeme, List<ExpressionNode> elements) {
        this(id, lexeme, elements, true, null);
    }

    public ListConstructorExpressionNode(IdGenerator id, LexicalUnit lexeme) {
        this(id, lexeme, new ArrayList<>(), false, null);
    }

    public static ListConstructorExpressionNode fromString(IdGenerator id, LexicalUnit lexeme, String s) {
        if (s.charAt(0) == '"') {
            s = s.substring(1, s.length() - 1);
        }
        List<ExpressionNode> elements = new ArrayList<>();
        boolean escaping = false;
        for (char c : s.toCharArray()) {
            if (escaping) {
                switch (c) {
                    case 't':
                        elements.add(new ConstantExpressionNode(id, lexeme, '\t', "\\t", CHAR));
                        break;
                    case 'b':
                        elements.add(new ConstantExpressionNode(id, lexeme, '\b', "\\b", CHAR));
                        break;
                    case 'n':
                        elements.add(new ConstantExpressionNode(id, lexeme, '\n', "\\n", CHAR));
                        break;
                    case 'r':
                        elements.add(new ConstantExpressionNode(id, lexeme, '\r', "\\r", CHAR));
                        break;
                    case 'f':
                        elements.add(new ConstantExpressionNode(id, lexeme, '\f', "\\f", CHAR));
                        break;
                    case '0':
                        elements.add(new ConstantExpressionNode(id, lexeme, '\0', "\\0", CHAR));
                        break;
                    case '\\':
                        elements.add(new ConstantExpressionNode(id, lexeme, '\\', "\\\\", CHAR));
                        break;
                    case '"':
                        elements.add(new ConstantExpressionNode(id, lexeme, '"'));
                        break;
                }
                escaping = false;
            } else {
                if (c == '\\') {
                    escaping = true;
                } else {
                    elements.add(new ConstantExpressionNode(id, lexeme, c));
                }
            }
        }
        return new ListConstructorExpressionNode(id, lexeme, elements, false, STRING);
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
