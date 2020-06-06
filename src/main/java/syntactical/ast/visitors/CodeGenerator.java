package syntactical.ast.visitors;

import syntactical.Defaults;
import syntactical.NewLabel;
import syntactical.ast.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class CodeGenerator implements Visitor {

    private static final String MAIN_FUNCTION = "main";

    private final ASTNode root;
    private final String file;
    private final SymbolTable symbolTable;
    // Variable id -> dir
    private final Map<Integer, Integer> variableDirs;
    // Function id -> dir
    private final Map<Integer, Integer> functionDirs;
    private final Map<Type, List<VarInitPair>> classFields;
    private int currentSP;
    private int currentPC;
    private List<Instruction> code;
    private BufferedWriter writer;
    // Label generator
    private NewLabel newLabel;
    private Map<String, List<Integer>> missingLabels;

    // TODO probably when using Variable.depth we should say currentDepth - Variable.depth

    public CodeGenerator(ASTNode root, String file, SymbolTable symbolTable) {
        this.root = root;
        this.file = file;
        this.symbolTable = symbolTable;
        this.variableDirs = new HashMap<>();
        this.functionDirs = new HashMap<>();
        this.classFields = new HashMap<>();
        // TODO theoretically STORE[0] = main program MP
        this.currentSP = 0;
        this.currentPC = 0;
        this.newLabel = new NewLabel();
        this.missingLabels = new HashMap<>();
        try {
            this.writer = new BufferedWriter(new FileWriter(file));
        } catch (IOException e) {
            // TODO show error message or just throw it
            e.printStackTrace();
        }
    }

    public void start() throws IOException {
        root.accept(this);
        writer.close();
    }

    @Override
    public void visit(ProgramNode node) {
        if (node.root() != null) {
            // TODO make sure that main exists before starting to generate code
            int mainId = 0;
            for (DeclarationNode n : node.root()) {
                // Visit global variables first
                if (n instanceof VarDeclarationNode) {
                    n.accept(this);
                } else if (n instanceof FunctionDeclarationNode && MAIN_FUNCTION.equals(n.getIdentifier()) &&
                        ((FunctionDeclarationNode) n).getParameters().isEmpty()) {
                    mainId = n.getId();
                }
            }
            // TODO add jump to an unknown label (this should be fixed)
            for (DeclarationNode n : node.root()) {
                // Visit all other of declarations
                if (!(n instanceof VarDeclarationNode)) {
                    n.accept(this);
                }
            }
            // TODO set previous label to main label
            //int label = functionDirs.get(mainId);
        }
        issue("stp");
    }

    @Override
    public void visit(VarDeclarationNode node) {
        Type type = node.getType();
        if (node.getInitialValue() == null) {
            issue("ldc", new ArrayList<>(Collections.singletonList("0")));
        } else {
            node.getInitialValue().accept(this);
        }
        variableDirs.put(node.getId(), currentSP);
        currentSP++;
    }

    @Override
    public void visit(FunctionDeclarationNode node) {
        functionDirs.put(node.getId(), currentPC);
        // TODO make room for parameters (and "this" for methods)
        node.getCode().accept(this);
    }

    @Override
    public void visit(ConstructorDeclarationNode node) {
        visit((FunctionDeclarationNode) node);
    }

    @Override
    public void visit(ClassDeclarationNode node) {
        List<VarInitPair> fields = new ArrayList<>();
        classFields.put(node.getType(), fields);
        if (node.getContentRoot() != null) {
            for (DeclarationNode n : node.getContentRoot()) {
                if (n instanceof VarDeclarationNode) {
                    // Just remember that this class has this variable declaration
                    Variable v = symbolTable.getVariableById(n.getId());
                    fields.add(new VarInitPair(v, ((VarDeclarationNode) n).getInitialValue()));
                } else {
                    // Just add methods and constructors as if they were normal functions
                    n.accept(this);
                }
            }
        }
    }

    @Override
    public void visit(BlockStatementNode node) {
        if (node.root() != null) {
            // TODO add MP, etc ?
            int dir = currentSP;
            currentSP = 0;
            for (StatementNode n : node.root()) {
                n.accept(this);
            }
            currentSP = dir;
            // TODO somehow remove or ignore memory used
        }
    }

    @Override
    public void visit(VarDeclarationStatementNode node) {
        node.asDeclaration().accept(this);
    }

    @Override
    public void visit(AssignmentStatementNode node) {
        // TODO check arrays and fields
        node.getTarget().accept(this);
        node.getValue().accept(this);
        Type type = node.getTarget().getType();
        issue("sto", new ArrayList<>(Collections.singletonList(convertType(type))));
    }

    @Override
    public void visit(FunctionCallStatementNode node) {
        node.asExpression().accept(this);
    }

    @Override
    public void visit(ReturnStatementNode node) {
        Type type = node.getReturnExpression().getType();
        issue("lod",new ArrayList<>(Arrays.asList(convertType(type), "0", "0")));
    }

    @Override
    public void visit(IfElseStatementNode node) {
      String ifLabel = newLabel.getLabel();
      String elseLabel = newLabel.getLabel();
        if (node.getElsePart() != null) {
            node.getCondition().accept(this);
            issue("fjp", ifLabel);
            node.getIfBlock().accept(this);
            issue("ujp", elseLabel);
            issueLabel(ifLabel);
            node.getElsePart().accept(this);
            issueLabel(elseLabel);
        } else {
            node.getCondition().accept(this);
            issue("fjp", new ArrayList<>(Collections.singletonList("l")));
            node.getIfBlock().accept(this);
            issueLabel("l:");
        }

    }

    @Override
    public void visit(SwitchStatementNode node) {

    }

    @Override
    public void visit(WhileStatementNode node) {
        // TODO: using currentPC as direction, don't now if that's right
        String whileLabel = String.valueOf(currentPC);
        String whileEndLabel = newLabel.getLabel();
        node.getCondition().accept(this);
        issue("fjp", whileEndLabel);
        node.getBlock().accept(this);
        issue("ujp", whileLabel);
        issueLabel(whileEndLabel);
    }

    @Override
    public void visit(ForStatementNode node) {
        VarDeclarationNode variable = node.getVariable();
        ExpressionNode iterable = node.getIterable();
        BlockStatementNode block = node.getBlock();
        Variable v = symbolTable.getVariableById(variable.getId());
        // TODO we are now in a new block add MP, etc ?
        issue("ldc", new ArrayList<>(Collections.singletonList("0")));
        // Duplicate value so we can remember it
        int forStart = currentPC;
        issue("dpl");
        // First variable of block
        variableDirs.put(variable.getId(), 0);
        if (iterable instanceof FunctionCallExpressionNode) {
            FunctionCallExpressionNode iterableFunction = (FunctionCallExpressionNode) iterable;
            Function f = symbolTable.getFunctionById(iterableFunction.getId());
            ExpressionNode function = iterableFunction.getFunction();
            List<ExpressionNode> arguments = iterableFunction.getArguments();
            if (f.id == Defaults.Int.TO_ID || f.id == Defaults.Real.TO_ID || f.id == Defaults.Char.TO_ID) {
                // Primitive ... Primitive
                // TODO i + left and compare to right
                // TODO if left > right go down somehow
                // String forEndLabel = newLabel.getLabel();
                // issue("fjp", forEndLabel);
                ExpressionNode left = ((PointExpressionNode) function).getHost();
                ExpressionNode right = arguments.get(0);
                left.accept(this);
                // variable = i + left
                issue("add");
                issue("dpl");
                issue("str", new ArrayList<>(Arrays.asList(String.valueOf(v.depth), "0")));
                right.accept(this);
                // variable > right
                issue("grt");
                // TODO jump if true (*)
              // issue("fjp", forEndLabel);
            }
        } else {
            // TODO access to iterable[i]
        }
        if (block.root() != null) {
            for (StatementNode n : block.root()) {
                n.accept(this);
            }
        }
        // Now the top of the stack should be i value so i++
        issue("inc", new ArrayList<>(Collections.singletonList("1")));
        // Return to the beginning of the loop
        issue("ujp", new ArrayList<>(Collections.singletonList(String.valueOf(forStart))));
        // TODO (*) jump here
        // issueLabel(forEndLabel);
        // Remove duplicated loop index (random store because I can't find any pop instruction)
        issue("str", new ArrayList<>(Arrays.asList(String.valueOf(v.depth), "0")));
        // TODO close block etc
    }

    @Override
    public void visit(PointExpressionNode node) {

    }

    @Override
    public void visit(ArrayAccessExpressionNode node) {
      // TODO: version for arrays of arrays
      ExpressionNode array = node.getArray();
      array.accept(this); // TODO: we need the direction of the array, check that it
      // actually does that
      ExpressionNode index = node.getIndex();
      index.accept(this);
      if(!(index instanceof  ConstantExpressionNode)) {
        issue("ind");
      }
      issue("ldc", new ArrayList<>(Arrays.asList("1")));
      issue(binaryOperator(Defaults.Int.MULT_ID));
      issue(binaryOperator(Defaults.Int.PLUS_ID));
    }

    @Override
    public void visit(FunctionCallExpressionNode node) {
        // TODO special cases as well for Form == Form, Array == Array, Primitive ... Primitive and maybe Int % Int
        Function f = symbolTable.getFunctionById(node.getId());
        ExpressionNode function = node.getFunction();
        List<ExpressionNode> args = node.getArguments();
        boolean generated = false;
        if (f.id == Defaults.IDENTITY_ID) {
            args.get(0).accept(this);
            args.get(1).accept(this);
            issue("equ");
            generated = true;
        } else if (function instanceof PointExpressionNode) {
            // May be an operator
            if (args.isEmpty()) {
                // Unary
                String code = unaryOperator(f.id);
                if (code != null) {
                    ((PointExpressionNode) function).getHost().accept(this);
                    issue(code);
                    generated = true;
                }
            } else if (args.size() == 1) {
                // Binary
                String code = binaryOperator(f.id);
                if (code != null) {
                    ((PointExpressionNode) function).getHost().accept(this);
                    args.get(0).accept(this);
                    issue(code);
                    generated = true;
                }
            }
        }
        if (!generated) {
            // TODO usual function call
        }
    }

    @Override
    public void visit(VariableExpressionNode node) {
        Variable v = symbolTable.getVariableById(node.getId());
        int dir = variableDirs.get(v.id);
        issue("lod", new ArrayList<>(Arrays.asList(String.valueOf(v.depth), String.valueOf(dir))));
    }

    @Override
    public void visit(ConstantExpressionNode node) {
        issue("ldc", new ArrayList<>(Collections.singletonList(String.valueOf(node.getValue()))));
    }

    @Override
    public void visit(ListConstructorExpressionNode node) {

    }

    @Override
    public void visit(AnonymousObjectConstructorExpressionNode node) {

    }

    @Override
    public void visit(ConstructorCallExpressionNode node) {

    }

    @Override
    public void visit(ErrorDeclarationNode node) {
        // If we are generating code we can't find these kinds of nodes
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(ErrorStatementNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(ErrorExpressionNode node) {
        throw new UnsupportedOperationException();
    }

    private String binaryOperator(int operator) {
        switch (operator) {
            case Defaults.Int.PLUS_ID:
            case Defaults.Real.PLUS_ID:
                return "add;";
            case Defaults.Int.MINUS_ID:
            case Defaults.Real.MINUS_ID:
                return "sub;";
            case Defaults.Int.MULT_ID:
            case Defaults.Real.MULT_ID:
                return "mul;";
            case Defaults.Int.DIV_ID:
            case Defaults.Real.DIV_ID:
                return "div;";
            case Defaults.Int.EQUALS_ID:
            case Defaults.Real.EQUALS_ID:
            case Defaults.Bool.EQUALS_ID:
            case Defaults.Char.EQUALS_ID:
                return "equ;";
            // NEQ is translated as x._equals(y)._not()
            //case "!=":
            //    return "neq;";
            case Defaults.Int.LE_ID:
            case Defaults.Real.LE_ID:
            case Defaults.Char.LE_ID:
                return "leq;";
            case Defaults.Int.GE_ID:
            case Defaults.Real.GE_ID:
            case Defaults.Char.GE_ID:
                return "geq;";
            case Defaults.Int.LT_ID:
            case Defaults.Real.LT_ID:
            case Defaults.Char.LT_ID:
                return "les;";
            case Defaults.Int.GT_ID:
            case Defaults.Real.GT_ID:
            case Defaults.Char.GT_ID:
                return "grt;";
            case Defaults.Bool.AND_ID:
                return "and;";
            case Defaults.Bool.OR_ID:
                return "or;";
        }
        return null;
    }

    private String unaryOperator(int operator) {
        switch (operator) {
            case Defaults.Int.UNARY_PLUS_ID:
            case Defaults.Real.UNARY_PLUS_ID:
                // Do nothing
                return "";
            case Defaults.Int.UNARY_MINUS_ID:
            case Defaults.Real.UNARY_MINUS_ID:
                return "neg;";
            case Defaults.Bool.NOT_ID:
                return "not;";
        }
        return null;
    }

    private String convertType(Type type) {
        if (Defaults.INT.realEquals(type)) {
            return "i";
        } else if (Defaults.BOOL.realEquals(type)) {
            return "b";
        } else if (Defaults.REAL.realEquals(type)) {
            return "r";
        } else if (Defaults.CHAR.realEquals(type)) {
            return "c";
        }
        return "a";
    }

    private void issue(String instruction, List<String> parameters) {
      code.add(new Instruction(instruction, parameters));
      currentPC++;
    }

    private void issue(String instruction) {
      code.add(new Instruction(instruction));
      currentPC++;
    }

    private void issue(String instruction, String label) {
      // TODO: I'm using currentPC as indexing for code list, if that's not right change it for
      //  actual indexing
      code.add(new Instruction(instruction, label));
      List<Integer> values = missingLabels.get(label);
      if (values == null) {
        missingLabels.put(label, new ArrayList<>(Collections.singletonList(currentPC)));
      } else {
        values.add(currentPC);
        missingLabels.replace(label, values);
      }
      currentPC++;
    }

    private void issueLabel(String label) {
      // TODO: I don't know if currentPC applies here, if not change for actual direction of the
      //  label
      List<Integer> missing = missingLabels.get(label);
      for(Integer i : missing) {
        code.set(i, new Instruction(code.get(i).instruction, String.valueOf(currentPC)));
      }
    }

    private void writeCode() throws IOException {
      for(Instruction i : code) {
        writer.write(i.instruction);
        for(String p : i.parameters) {
          writer.write(" " + p);
        }
        writer.write(";\n");
      }
    }

    private static class VarInitPair {
        final Variable variable;
        final ExpressionNode initialValue;

        private VarInitPair(Variable variable, ExpressionNode initialValue) {
            this.variable = variable;
            this.initialValue = initialValue;
        }
    }

    private class Instruction {
      private String instruction;
      private List<String> parameters;
      private String label;

      private Instruction(String instruction, List<String> parameters, String label) {
        this.instruction = instruction;
        this.parameters = new ArrayList<>(parameters);
        this.label = label;
      }

      private Instruction(String instruction, List<String> parameters) {
        this(instruction, parameters, "");
      }

      private Instruction(String instruction, String label) {
        this(instruction, new ArrayList<>(), label);
      }

      private Instruction(String instruction) {
        this(instruction, new ArrayList<>(), "");
      }

    }

}
