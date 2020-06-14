package semantical;

import lexical.LexicalUnit;
import syntactical.Defaults;
import syntactical.OperatorOverloadConstants;
import syntactical.ast.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpecialFunctionCreator {

    private final SymbolTable symbolTable;
    private final IdGenerator generator;
    private final Map<String, Integer> arraysSeen;
    private int counter;

    public SpecialFunctionCreator(SymbolTable symbolTable, IdGenerator generator) {
        this.symbolTable = symbolTable;
        this.generator = generator;
        this.arraysSeen = new HashMap<>();
        this.counter = 0;
    }

    public FunctionDeclarationNode arrayEquals(Type type, FunctionCallExpressionNode original, LexicalUnit lexeme) {
        /*
            Bool _equals(Array<T> a, Array<T> b) {
                if a.size != b.size {
                    return false;
                }
                for Int i in 0 ... a.size - 1 {
                    if a[i] != b[i] {
                        return false;
                    }
                }
                return true;
            }
        */
        // Using string as we can't use realEquals
        if (arraysSeen.containsKey(type.toString())) {
            symbolTable.linkFunctionManually(original.getId(), arraysSeen.get(type.toString()));
            return null;
        }
        // All nodes have the same lexeme so errors are notified in the original call
        FunctionDeclarationNode function = new FunctionDeclarationNode(generator, lexeme,
                new Name(new LexicalUnit("~arrayEquals" + counter), Defaults.BOOL),
                Arrays.asList(
                        new Name(new LexicalUnit("a"), type),
                        new Name(new LexicalUnit("b"), type)
                ),
                new BlockStatementNode(generator,
                        new IfElseStatementNode(generator,
                                neq(point("a", "size"), point("b", "size"), lexeme),
                                new BlockStatementNode(generator,
                                        new ReturnStatementNode(generator, lexeme, bool(false))
                                ),
                                null
                        ).linkedTo(
                                new ForStatementNode(generator, new Name(new LexicalUnit("i"), Defaults.INT),
                                        FunctionCallExpressionNode.operator(generator, lexeme,
                                                integer(0),
                                                OperatorOverloadConstants._TO,
                                                FunctionCallExpressionNode.operator(generator, lexeme,
                                                        point("a", "size"),
                                                        OperatorOverloadConstants._MINUS,
                                                        integer(1)
                                                )
                                        ),
                                        new BlockStatementNode(generator,
                                                new IfElseStatementNode(generator,
                                                        neq(array("a", "i"), array("b", "i"), lexeme),
                                                        new BlockStatementNode(generator,
                                                                new ReturnStatementNode(generator, lexeme, bool(false))
                                                        ),
                                                        null
                                                )
                                        )
                                ).linkedTo(
                                        new ReturnStatementNode(generator, lexeme, bool(true))
                                )
                        )
                )
        );
        counter++;
        symbolTable.linkFunctionManually(original.getId(), function.getId());
        arraysSeen.put(type.toString(), function.getId());
        return function;
    }

    private VariableExpressionNode variable(String name) {
        return new VariableExpressionNode(generator, new LexicalUnit(name));
    }

    private PointExpressionNode point(String host, List<String> fields) {
        PointExpressionNode e = point(host, fields.get(0));
        for (int i = 1; i < fields.size(); i++) {
            e = point(e, fields.get(i));
        }
        return e;
    }

    private PointExpressionNode point(ExpressionNode host, String field) {
        return new PointExpressionNode(generator, host, variable(field));
    }

    private PointExpressionNode point(String host, String field) {
        return point(variable(host), field);
    }

    private ArrayAccessExpressionNode array(String array, String index) {
        return new ArrayAccessExpressionNode(generator, variable(array), variable(index));
    }

    private FunctionCallExpressionNode neq(ExpressionNode left, ExpressionNode right, LexicalUnit lexeme) {
        return FunctionCallExpressionNode.operator(generator, lexeme,
                OperatorOverloadConstants._NOT,
                FunctionCallExpressionNode.operator(generator, lexeme, left, OperatorOverloadConstants._EQUALS, right)
        );
    }

    private ConstantExpressionNode bool(boolean b) {
        return ConstantExpressionNode.fromBoolean(generator, new LexicalUnit("" + b));
    }

    private ConstantExpressionNode integer(int n) {
        return ConstantExpressionNode.fromInt(generator, new LexicalUnit("" + n));
    }

}
