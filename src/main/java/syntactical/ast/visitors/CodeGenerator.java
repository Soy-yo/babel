package syntactical.ast.visitors;

import syntactical.Defaults;
import syntactical.ast.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class CodeGenerator implements Visitor {

    private final ASTNode root;
    private final String file;
    private final SymbolTable symbolTable;
    // Variable id -> dir
    private final Map<Integer, Integer> variableDirs;
    // Function id -> dir
    private final Map<Type, List<VarInitPair>> classFields;
    private boolean classFieldsGenerated;
    private int currentSP;
    private int globalSP;
    private int currentPC;
    private List<Instruction> code;
    private BufferedWriter writer;
    // Label generator
    private final NewLabel newLabel;
    private final Map<String, List<Integer>> missingLabels;
    private final Map<String, Integer> labels;

    // TODO probably when using Variable.depth we should say currentDepth - Variable.depth

    public CodeGenerator(ASTNode root, String file, SymbolTable symbolTable) throws IOException {
        this.root = root;
        this.file = file;
        this.symbolTable = symbolTable;
        this.variableDirs = new HashMap<>();
        this.classFields = new HashMap<>();
        this.classFieldsGenerated = false;
        // TODO theoretically STORE[0] = main program MP
        this.currentSP = 0;
        this.globalSP = 0;
        this.currentPC = 0;
        this.code = new ArrayList<>();
        this.newLabel = new NewLabel();
        this.missingLabels = new HashMap<>();
        this.labels = new HashMap<>();
        this.writer = new BufferedWriter(new FileWriter(file));
    }

    public void start() throws IOException {
        root.accept(this);
        writeCode();
        writer.close();
    }

    @Override
    public void visit(ProgramNode node) {
        if (node.root() != null) {
            for (DeclarationNode n : node.root()) {
                // Visit global variables first
                if (n instanceof VarDeclarationNode) {
                    n.accept(this);
                }
            }
            // Call main
            Function main = symbolTable.getMainFunction();
            issueLabeled("ujp", newLabel.getLabel(main), 0);
            for (DeclarationNode n : node.root()) {
                // Visit classes (just fields)
                if (n instanceof ClassDeclarationNode) {
                    n.accept(this);
                }
            }
            classFieldsGenerated = true;
            for (DeclarationNode n : node.root()) {
                // Visit classes (now just methods)
                if (n instanceof ClassDeclarationNode) {
                    n.accept(this);
                }
            }
            for (DeclarationNode n : node.root()) {
                // Finally visit functions
                if (n instanceof FunctionDeclarationNode) {
                    n.accept(this);
                }
            }
        }
        issue("stp");
    }

    @Override
    public void visit(VarDeclarationNode node) {
        Type type = node.getType();
        if (node.getInitialValue() == null) {
            issue("ldc", "0");
            updateSP(1);
        } else {
            node.getInitialValue().accept(this);
        }
        variableDirs.put(node.getId(), currentSP - 1);
    }

    @Override
    public void visit(FunctionDeclarationNode node) {
        Function f = symbolTable.getFunctionById(node.getId());
        issueLabel(newLabel.getLabel(f));
        // TODO make room for parameters (and "this" for methods)
        node.getCode().accept(this);
    }

    @Override
    public void visit(ConstructorDeclarationNode node) {
        visit((FunctionDeclarationNode) node);
    }

    @Override
    public void visit(ClassDeclarationNode node) {
        if (!classFieldsGenerated) {
            List<VarInitPair> fields = new ArrayList<>();
            classFields.put(node.getType(), fields);
            if (node.getContentRoot() != null) {
                for (DeclarationNode n : node.getContentRoot()) {
                    if (n instanceof VarDeclarationNode) {
                        // Just remember that this class has this variable declaration
                        Variable v = symbolTable.getVariableById(n.getId());
                        fields.add(new VarInitPair(v, ((VarDeclarationNode) n).getInitialValue()));
                    }
                }
            }
        } else {
            if (node.getContentRoot() != null) {
                for (DeclarationNode n : node.getContentRoot()) {
                    if (!(n instanceof VarDeclarationNode)) {
                        n.accept(this);
                    }
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
        issue("sto");
        updateSP(-1);
    }

    @Override
    public void visit(FunctionCallStatementNode node) {
        node.asExpression().accept(this);
    }

    @Override
    public void visit(ReturnStatementNode node) {
        issue("lda", "0", "0");
        node.getReturnExpression().accept(this);
        issue("sto");
    }

    @Override
    public void visit(IfElseStatementNode node) {
        String ifFalseLabel = newLabel.getLabel();
        if (node.getElsePart() != null) {
            String ifEndLabel = newLabel.getLabel();
            // Write code for condition
            node.getCondition().accept(this);
            // If condition is false jump to #ifFalseLabel
            issue("fjp", ifFalseLabel);
            // Write if block
            node.getIfBlock().accept(this);
            // Jump to #ifEndLabel
            issue("ujp", ifEndLabel);
            // Issue label so if knows were to jump when condition is false
            issueLabel(ifFalseLabel);
            // Write else block
            node.getElsePart().accept(this);
            // Issue label so if knows were to jump when if block ends
            issueLabel(ifEndLabel);
        } else {
            node.getCondition().accept(this);
            issue("fjp", ifFalseLabel);
            node.getIfBlock().accept(this);
            issueLabel(ifFalseLabel);
        }
    }

    @Override
    public void visit(SwitchStatementNode node) {
        // TODO update sp
        TreeMap<ConstantExpressionNode, StatementNode> map = (TreeMap<ConstantExpressionNode,
            StatementNode>) node.getCases();
        String endLabel = newLabel.getLabel();
        // switch value should be a primitive
        // we place it on top of the stack
        node.getSwitchExpression().accept(this);
        // duplicate to compare to first
        issue("dpl");
        // put first in stack to compare
        issue("ldc", String.valueOf(map.firstKey().getValue()));
        issue("geq");
        // if false skip switch
        issueLabeled("fjp", endLabel, 0);
        // duplicate to compare to last
        issue("dpl");
        // put last in stack to compare
        issue("ldc", String.valueOf(map.lastKey().getValue()));
        issue("leq");
        // if false skip switch
        issueLabeled("fjp", endLabel, 0);
        // we place the first value on top of the stack for indexing
        issue("ldc", String.valueOf(map.firstKey().getValue()));
        // we subtract them so now we index starting on the first possible value
        issue("sub");
        String defaultLabel;
        String jumpTable = newLabel.getLabel();
        // we jump to the end of the jumpTable + the value of the expression
        issueLabeled("ixj", jumpTable, 0);
        if(node.hasDefault()) {
            defaultLabel = newLabel.getLabel(node.getDef());
        } else {
            defaultLabel = endLabel;
        }
        // We create a list of labels, all of them point to the default one and it has exactly
        // the size we need
        String[] labels = new String[map.lastKey().getValue() - map.firstKey().getValue() + 1];
        Arrays.fill(labels, defaultLabel);
        for (Map.Entry<ConstantExpressionNode, StatementNode> entry : node.getCases().entrySet()) {
            String label = newLabel.getLabel();
            // we replace this element's label for the actual one
            labels[entry.getKey().getValue() - map.firstKey().getValue()] = label;
            // create the label
            issueLabel(label);
            // TODO: this could be a block of code, don't know if it'd work
            entry.getValue().accept(this);
            issueLabeled("ujp", endLabel, 0);
        }
        // so we know were the jump table starts
        issueLabel(jumpTable);
        for(int i = 0; i < labels.length; i++) {
            // create jump table
            issueLabeled("ujp", labels[i], 0);
        }
        issueLabel(endLabel);
    }

    @Override
    public void visit(WhileStatementNode node) {
        // TODO: using currentPC as direction, don't know if that's right (think it's okay)
        String whileLabel = String.valueOf(currentPC);
        String whileEndLabel = newLabel.getLabel();
        // Write code for condition
        node.getCondition().accept(this);
        // If condition is false jump to #whileEndLabel
        issue("fjp", whileEndLabel);
        // Write while block code
        node.getBlock().accept(this);
        // Jump back to first instruction of condition
        issue("ujp", whileLabel);
        // Issue label so while knows were to jump when condition is false
        issueLabel(whileEndLabel);
    }

    @Override
    public void visit(ForStatementNode node) {
        // TODO don't forget to update SP
        VarDeclarationNode variable = node.getVariable();
        ExpressionNode iterable = node.getIterable();
        BlockStatementNode block = node.getBlock();
        Variable v = symbolTable.getVariableById(variable.getId());
        // TODO we are now in a new block add MP, etc ?
        issue("ldc", "0");
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
                issue("str", "" + v.depth, "0");
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
        issue("inc", "1");
        // Return to the beginning of the loop
        issue("ujp", "" + forStart);
        // TODO (*) jump here
        // issueLabel(forEndLabel);
        // Remove duplicated loop index (random store because I can't find any pop instruction)
        issue("str", "" + v.depth, "0");
        // TODO close block etc
    }

    @Override
    public void visit(PointExpressionNode node) {

    }

    @Override
    public void visit(ArrayAccessExpressionNode node) {
        // Starts with SP and ends with SP + 1: contents of array[index] (if no error)
        String error = newLabel.getLabel();
        String noError = newLabel.getLabel();
        // TODO: version for arrays of arrays <- there's no need for such version
        ExpressionNode array = node.getArray();
        // Write the code to access the array pointer
        array.accept(this); // TODO: we need the direction of the array, check that it actually does that
        ExpressionNode index = node.getIndex();
        // Write the code to to access the array index
        index.accept(this);
        // We can't use chk because we don't know the size statically, so we have to fake it
        // Duplicate the index as we need to access twice
        issue("dpl");
        // This should retrieve the size of the array
        issue("ldd", "0");
        updateSP(2);
        // index < array.size ?
        issue("les");
        updateSP(-1);
        // If false jump to #error
        issueLabeled("fjp", error, 0);
        // Duplicate again to compare with 0
        issue("dpl");
        issue("ldc", "0");
        updateSP(2);
        // index >= 0 ?
        issue("geq");
        // If index < 0 jump to #error
        issueLabeled("fjp", error, 0);
        // If we are here everything is okay
        // Array direction + index
        issue("add");
        updateSP(-3);
        issue("ldc", "1");
        updateSP(1);
        // Finally we find the correct direction
        issue("add");
        updateSP(-2);
        // Get whatever it is in that direction
        issue("ind");
        // There has been no errors so jump after the error check
        issueLabeled("ujp", noError, 0);
        issueLabel(error);
        // Dummy error check -> if STORE[SP] not in (0, -1) then error
        issue("chk", "0", "-1");
        issueLabel(noError);

        // TODO why this ?
        /*
        if (!(index instanceof ConstantExpressionNode)) {
            issue("ind");
        }
        issue("ldc", "1");
        issue(binaryOperator(Defaults.Int.MULT_ID));
        issue(binaryOperator(Defaults.Int.PLUS_ID));
        */
    }

    @Override
    public void visit(FunctionCallExpressionNode node) {
        // TODO update SP
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
        // Starts with SP and ends with SP + 1: contents of variable
        Variable v = symbolTable.getVariableById(node.getId());
        int dir = variableDirs.get(v.id);
        issue("lod", "" + v.depth, "" + dir);
        updateSP(1);
    }

    @Override
    public void visit(ConstantExpressionNode node) {
        // TODO does it work with chars and true/false?
        // Starts with SP and ends with SP + 1: constant
        issue("ldc", "" + node.getValue());
        updateSP(1);
    }

    @Override
    public void visit(ListConstructorExpressionNode node) {
        // Starts with SP and ends with SP + 1: direction of the array
        // new instruction is stupid and needs to know where to put the pointer
        List<ExpressionNode> elements = node.getElements();
        // Allocate memory for all the elements (+1 for the size)
        alloc(elements.size() + 1);
        // TODO any better way to do this?
        // And we will use it multiple times
        issue("dpl");
        // Load again the size
        issue("ldc", "" + elements.size());
        updateSP(2);
        // And store it in the first direction of the array
        issue("sto");
        updateSP(-2);
        // We have the direction of the array on the top of the stack
        int i = 1;
        for (ExpressionNode n : elements) {
            // Duplicate direction so we don't lose it for the next iteration
            issue("dpl");
            updateSP(1);
            // Get next direction
            issue("inc", "" + i);
            n.accept(this);
            // Save the result of the expression
            issue("sto");
            updateSP(-2);
            i++;
        }
    }

    @Override
    public void visit(AnonymousObjectConstructorExpressionNode node) {

    }

    @Override
    public void visit(ConstructorCallExpressionNode node) {
        Function f = symbolTable.getFunctionById(node.getId());
        if (f.id == Defaults.Array.CONSTRUCTOR_ID) {
            buildArray(node.getArguments(), node.getType(), 0);
        } else {

        }
    }

    private void buildArray(List<ExpressionNode> dimensions, Type type, int index) {
        // TODO this won't work, just do it by hand or something
        if (index == dimensions.size()) {
            return;
        }
        Type parameter = type.getParameter();
        // Allocate memory for the main array
        alloc(dimensions.get(0), 1);
        if (parameter.isPrimitive()) {

        } else {
            // TODO don't know if recursion will work but something like this
            buildArray(dimensions, parameter, index + 1);
        }
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

    // TODO remove: this won't work
    private void alloc(ExpressionNode sizeExpression, int extra) {
        // Starts with SP and NP and ends with SP + 1 and NP - size with pointer to new NP in STORE[SP]
        issue("ldc", "" + globalSP);
        // Duplicate so we can use this position to save the result of new
        issue("dpl");
        updateSP(2);
        // This must return an integer on top of the stack
        sizeExpression.accept(this);
        if (extra > 0) {
            issue("lcd", "" + extra);
            updateSP(1);
            issue("add");
            updateSP(-1);
        }
        // Create room for size elements in the heap and put the pointer on top of the stack
        // (with "ldc currentSP" hack)
        issue("new");
        updateSP(-2);
    }

    private void alloc(int size) {
        // Starts with SP and NP and ends with SP + 1 and NP - size with pointer to new NP in STORE[SP]
        issue("ldc", "" + globalSP);
        // Duplicate so we can use this position to save the result of new
        issue("dpl");
        // Load the number of elements
        issue("ldc", "" + size);
        updateSP(3);
        // Create room for size elements in the heap and put the pointer on top of the stack
        // (with "ldc currentSP" hack)
        issue("new");
        updateSP(-2);
    }

    private String binaryOperator(int operator) {
        switch (operator) {
            case Defaults.Int.PLUS_ID:
            case Defaults.Real.PLUS_ID:
                return "add";
            case Defaults.Int.MINUS_ID:
            case Defaults.Real.MINUS_ID:
                return "sub";
            case Defaults.Int.MULT_ID:
            case Defaults.Real.MULT_ID:
                return "mul";
            case Defaults.Int.DIV_ID:
            case Defaults.Real.DIV_ID:
                return "div";
            case Defaults.Int.EQUALS_ID:
            case Defaults.Real.EQUALS_ID:
            case Defaults.Bool.EQUALS_ID:
            case Defaults.Char.EQUALS_ID:
                return "equ";
            // NEQ is translated as x._equals(y)._not()
            //case "!=":
            //    return "neq";
            case Defaults.Int.LE_ID:
            case Defaults.Real.LE_ID:
            case Defaults.Char.LE_ID:
                return "leq";
            case Defaults.Int.GE_ID:
            case Defaults.Real.GE_ID:
            case Defaults.Char.GE_ID:
                return "geq";
            case Defaults.Int.LT_ID:
            case Defaults.Real.LT_ID:
            case Defaults.Char.LT_ID:
                return "les";
            case Defaults.Int.GT_ID:
            case Defaults.Real.GT_ID:
            case Defaults.Char.GT_ID:
                return "grt";
            case Defaults.Bool.AND_ID:
                return "and";
            case Defaults.Bool.OR_ID:
                return "or";
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
                return "neg";
            case Defaults.Bool.NOT_ID:
                return "not";
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

    private void issue(String instruction, String... parameters) {
        code.add(new Instruction(instruction, Arrays.asList(parameters)));
        currentPC++;
    }

    private void issueLabeled(String instruction, String label, int index, String... parameters) {
        // TODO: I'm using currentPC as indexing for code list, if that's not right change it for
        //  actual indexing
        // Make room for the last parameter
        if (index == parameters.length) {
            parameters = Arrays.copyOf(parameters, parameters.length + 1);
            parameters[parameters.length - 1] = null;
        } else if (index > parameters.length) {
            throw new IllegalArgumentException("Cannot label a parameter that doesn't exist");
        }
        // If the label has already been issued just replace it
        if (labels.containsKey(label)) {
            String value = "" + labels.get(label);
            code.add(new Instruction(instruction, Arrays.asList(parameters), index).withLabelValue(value));
            return;
        }
        // Otherwise add the instruction and the missing label
        code.add(new Instruction(instruction, Arrays.asList(parameters), index));
        List<Integer> values = missingLabels.get(label);
        if (values == null) {
            missingLabels.put(label, new ArrayList<>(Collections.singletonList(currentPC)));
        } else {
            values.add(currentPC);
        }
        currentPC++;
    }

    private void issueLabel(String label) {
        // TODO: I don't know if currentPC applies here, if not change for actual direction of the
        //  label
        List<Integer> missing = missingLabels.get(label);
        if (missing != null) {
            for (Integer i : missing) {
                code.get(i).withLabelValue("" + currentPC);
            }
            missingLabels.remove(label);
        }
        labels.put(label, currentPC);
    }

    private void updateSP(int n) {
        // TODO update EP if needed
        currentSP += n;
        globalSP += n;
    }

    private void writeCode() throws IOException {
        for (Instruction i : code) {
            writer.write(i.toString() + "\n");
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

    private static class Instruction {

        private final String instruction;
        private final List<String> parameters;
        private final int labelIndex;

        private Instruction(String instruction, List<String> parameters, int labelIndex) {
            this.instruction = instruction;
            this.parameters = new ArrayList<>(parameters);
            this.labelIndex = labelIndex;
        }

        private Instruction(String instruction, List<String> parameters) {
            this(instruction, parameters, -1);
        }

        Instruction withLabelValue(String labelValue) {
            parameters.set(labelIndex, labelValue);
            return this;
        }

        @Override
        public String toString() {
            return instruction + (parameters.isEmpty() ? "" : " " + String.join(" ", parameters)) + ";";
        }

    }

}
