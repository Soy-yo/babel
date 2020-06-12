package syntactical.ast.visitors;

import lexical.LexicalUnit;
import syntactical.Defaults;
import syntactical.OperatorOverloadConstants;
import syntactical.ast.*;

import java.util.*;

import static syntactical.ast.Designator.ofArray;

public class SpecialFunctionCreator {

    private final SymbolTable symbolTable;
    private final IdGenerator generator;
    private final Map<String, Integer> arraysSeen;
    private final Map<String, Integer> toSeen;
    private int counter;

    public SpecialFunctionCreator(SymbolTable symbolTable, IdGenerator generator) {
        this.symbolTable = symbolTable;
        this.generator = generator;
        this.arraysSeen = new HashMap<>();
        this.toSeen = new HashMap<>();
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


    public FunctionDeclarationNode to(Type type, FunctionCallExpressionNode original,
                                      LexicalUnit lexeme) {
        /*
        Array<type> _to(type a, type b) {
            if b < a {return null;}
            else { Array<type> arr = Array(b - a);}
            for type i in a..b {
                arr[i - a] = i;
            }
            return arr;
        }
        */
        if (toSeen.containsKey(type.toString())) {
            symbolTable.linkFunctionManually(original.getId(), toSeen.get(type.toString()));
            return null;
        }
        FunctionDeclarationNode function = new FunctionDeclarationNode(generator, lexeme,
            new Name(new LexicalUnit("_to" + counter), Defaults.ARRAY),
            Arrays.asList(
                new Name(new LexicalUnit("a"), type),
                new Name(new LexicalUnit("b"), type)
            ),
            new BlockStatementNode(generator,
                new IfElseStatementNode(generator,
                    lt(variable("b"), variable("a"), lexeme),
                    new BlockStatementNode(generator,
                        new ReturnStatementNode(generator, lexeme, nothing())
                    ), new VarDeclarationNode(generator,
                    new Name(new LexicalUnit("arr"), Defaults.ARRAY),
                    new ConstructorCallExpressionNode(generator, Defaults.ARRAY,
                        Collections.singleton(FunctionCallExpressionNode.operator(generator, lexeme, variable("b"),
                            OperatorOverloadConstants._MINUS, variable("a"))))).asStatement()
                ).linkedTo(
                    new ForStatementNode(generator, new Name(new LexicalUnit("i"), type),
                        FunctionCallExpressionNode.operator(generator, lexeme,
                            variable("a"),
                            OperatorOverloadConstants._TO, variable("b")),
                        new BlockStatementNode(generator, new AssignmentStatementNode(generator,
                            ofArray(new ArrayAccessExpressionNode(generator, variable("arr"),
                                FunctionCallExpressionNode.operator(generator, lexeme, variable(
                                    "i"), OperatorOverloadConstants._MINUS, variable("a")))),
                                variable("i")))
                    ).linkedTo(
                        new ReturnStatementNode(generator, lexeme, variable("arr"))
                    )
                )
            )
        );
        counter++;
        symbolTable.linkFunctionManually(original.getId(), function.getId());
        toSeen.put(type.toString(), function.getId());
        return function;
    }

//    public FunctionDeclarationNode formEquals(int leftId,
//                                              int rightId,
//                                              FunctionCallExpressionNode original,
//                                              LexicalUnit lexeme) {
//        Map<Integer, Set<Variable>> leftTree = formTree(leftId);
//        Map<Integer, Set<Variable>> rightTree = formTree(rightId);
//        /*
//            Bool _equals(Form f, Form g) {
//                return f.a_1 == g.a_1 && ... && f.a_n == g.a_n &&
//                        f.a_{k_1}.b1 == g.a_{k_1}.b1 ... ;
//            }
//        */
//        StatementNode contents = !sameTree(leftTree, leftId, rightTree, rightId) ?
//                new ReturnStatementNode(generator, lexeme, bool(false)) :
//                new ReturnStatementNode(generator, lexeme,
//                        formEqualsRecursion(leftId, new ArrayList<>(), leftTree, lexeme)
//                );
//        FunctionDeclarationNode function = new FunctionDeclarationNode(generator, lexeme,
//                new Name(new LexicalUnit("~formEquals" + counter), Defaults.BOOL),
//                Arrays.asList(
//                        new Name(new LexicalUnit("f"), Defaults.FORM),
//                        new Name(new LexicalUnit("g"), Defaults.FORM)
//                ),
//                new BlockStatementNode(generator, contents)
//        );
//        counter++;
//        symbolTable.linkFunctionManually(original.getId(), function.getId());
//        return function;
//    }
//
//    private ExpressionNode formEqualsRecursion(int id,
//                                               List<String> fields,
//                                               Map<Integer, Set<Variable>> tree,
//                                               LexicalUnit lexeme) {
//        Set<Variable> f = tree.get(id);
//        ExpressionNode expr = null;
//        // Generate bases: f.a. ... .y, g.a. ... .y
//        ExpressionNode left = fields.isEmpty() ? variable("f") : point("f", fields);
//        ExpressionNode right = fields.isEmpty() ? variable("g") : point("g", fields);
//        for (Variable v : f) {
//            // Add every field to the base and do equals: f.a. ... .y.z == g.a. ... y.z
//            ExpressionNode e = FunctionCallExpressionNode.operator(generator, lexeme,
//                    point(left, v.name), OperatorOverloadConstants._EQUALS, point(right, v.name));
//            expr = expr == null ? e : FunctionCallExpressionNode.operator(generator, lexeme,
//                    expr, OperatorOverloadConstants._AND, e);
//            if (Defaults.FORM.equals(v.type)) {
//                // Repeat recursively if this variable is a Form
//                fields.add(v.name);
//                expr = FunctionCallExpressionNode.operator(generator, lexeme,
//                        expr, OperatorOverloadConstants._AND, formEqualsRecursion(v.id, fields, tree, lexeme));
//                fields.remove(fields.size() - 1);
//            }
//        }
//        return expr;
//    }
//
//    private Map<Integer, Set<Variable>> formTree(int id) {
//        Map<Integer, Set<Variable>> tree = new HashMap<>();
//        Deque<Integer> path = symbolTable.openPreviousScope(id);
//        Set<Variable> fields = new HashSet<>();
//        tree.put(id, fields);
//        for (Variable v : symbolTable.variables()) {
//            fields.add(v);
//            if (Defaults.FORM.equals(v.type)) {
//                formTree(v.id, tree);
//            }
//        }
//        symbolTable.closeScope();
//        symbolTable.restoreScope(path);
//        return tree;
//    }
//
//    private void formTree(int id, Map<Integer, Set<Variable>> tree) {
//        symbolTable.openScope(id);
//        Set<Variable> fields = new HashSet<>();
//        tree.put(id, fields);
//        for (Variable v : symbolTable.variables()) {
//            fields.add(v);
//            if (Defaults.FORM.equals(v.type)) {
//                formTree(v.id, tree);
//            }
//        }
//        symbolTable.closeScope();
//    }
//
//    private boolean sameTree(Map<Integer, Set<Variable>> leftTree, int leftId,
//                             Map<Integer, Set<Variable>> rightTree, int rightId) {
//        Set<Variable> ls = leftTree.get(leftId);
//        Set<Variable> rs = rightTree.get(rightId);
//        if (!ls.equals(rs)) {
//            return false;
//        }
//        List<Variable> r = new ArrayList<>(rs);
//        for (Variable v : ls) {
//            if (Defaults.FORM.equals(v.type)) {
//                int j = r.indexOf(v);
//                if (!sameTree(leftTree, v.id, rightTree, r.get(j).id)) {
//                    return false;
//                }
//            }
//        }
//        return true;
//    }

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

    private FunctionCallExpressionNode lt(ExpressionNode left, ExpressionNode right,
                                         LexicalUnit lexeme) {
        return FunctionCallExpressionNode.operator(generator, lexeme, left,
            OperatorOverloadConstants._LT, right);
    }

    private ConstantExpressionNode bool(boolean b) {
        return ConstantExpressionNode.fromBoolean(generator, new LexicalUnit("" + b));
    }

    private ConstantExpressionNode integer(int n) {
        return ConstantExpressionNode.fromInt(generator, new LexicalUnit("" + n));
    }

    private ConstantExpressionNode nothing() {
        return ConstantExpressionNode.ofNull(generator, new LexicalUnit(""));
    }

}
