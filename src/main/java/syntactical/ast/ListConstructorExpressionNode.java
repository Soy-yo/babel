package syntactical.ast;

import lexical.LexicalUnit;
import syntactical.Defaults;
import syntactical.ast.visitors.Visitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListConstructorExpressionNode extends ExpressionNode {


    private List<ExpressionNode> elements;
    private List<ExpressionNode> view;

    private ListConstructorExpressionNode(IdGenerator id,
                                          LexicalUnit lexeme,
                                          List<ExpressionNode> elements,
                                          boolean copy,
                                          Type type) {
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

    public ListConstructorExpressionNode(IdGenerator id, LexicalUnit lexeme, Type type) {
        this(id, lexeme, new ArrayList<>(), false, new Type(Defaults.ARRAY_STR, type));
    }

    public ListConstructorExpressionNode(IdGenerator id, LexicalUnit lexeme) {
        this(id, lexeme, new ArrayList<>(), false, null);
    }

    public static ListConstructorExpressionNode fromString(IdGenerator id, LexicalUnit lexeme) {
        String s = lexeme.lexeme();
        if (s.charAt(0) == '"') {
            s = s.substring(1, s.length() - 1);
        }
        List<ExpressionNode> elements = new ArrayList<>();
        boolean escaping = false;
        for (char c : s.toCharArray()) {
            if (escaping) {
                switch (c) {
                    case 't':
                        elements.add(new ConstantExpressionNode(id, new LexicalUnit("'\\t'"), '\t', Defaults.CHAR));
                        break;
                    case 'b':
                        elements.add(new ConstantExpressionNode(id, new LexicalUnit("'\\b'"), '\b', Defaults.CHAR));
                        break;
                    case 'n':
                        elements.add(new ConstantExpressionNode(id, new LexicalUnit("'\\n'"), '\n', Defaults.CHAR));
                        break;
                    case 'r':
                        elements.add(new ConstantExpressionNode(id, new LexicalUnit("'\\r'"), '\r', Defaults.CHAR));
                        break;
                    case 'f':
                        elements.add(new ConstantExpressionNode(id, new LexicalUnit("'\\f'"), '\f', Defaults.CHAR));
                        break;
                    case '0':
                        elements.add(new ConstantExpressionNode(id, new LexicalUnit("'\\0'"), '\0', Defaults.CHAR));
                        break;
                    case '\\':
                        elements.add(new ConstantExpressionNode(id, new LexicalUnit("'\\\\'"), '\\', Defaults.CHAR));
                        break;
                    case '"':
                        elements.add(new ConstantExpressionNode(id, new LexicalUnit("'\\\"'"), '"', Defaults.CHAR));
                        break;
                }
                escaping = false;
            } else {
                if (c == '\\') {
                    escaping = true;
                } else {
                    elements.add(new ConstantExpressionNode(id, new LexicalUnit("'" + c + "'"), c));
                }
            }
        }
        return new ListConstructorExpressionNode(id, lexeme, elements, false, Defaults.STRING);
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
