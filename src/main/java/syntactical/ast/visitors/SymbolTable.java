package syntactical.ast.visitors;

import syntactical.ast.Type;

import java.util.*;

public class SymbolTable {

    private static final int GLOBAL_SCOPE_ID = -1;

    // Only global classes allowed
    private final Map<Type, Integer> classTable;
    private final Map<Integer, Variable> variableRelations;
    private final Map<Integer, Function> functionRelations;
    private Scope currentScope;

    private Func mainFunc;
    private Function mainFunction;

    public SymbolTable() {
        this.classTable = new HashMap<>();
        this.variableRelations = new HashMap<>();
        this.functionRelations = new HashMap<>();
        this.currentScope = new Scope(GLOBAL_SCOPE_ID, null);
        this.mainFunc = null;
        this.mainFunction = null;
    }

    public SymbolTable(String mainFunction, Type... arguments) {
        this();
        this.mainFunc = new Func(mainFunction, Arrays.copyOf(arguments, arguments.length));
    }

    public boolean inGlobalScope() {
        return currentScope.blockId == GLOBAL_SCOPE_ID;
    }

    public int getCurrentScopeId() {
        return currentScope.blockId;
    }

    public Function getMainFunction() {
        return mainFunction;
    }

    public Variable getVariableById(int id) {
        return variableRelations.get(id);
    }

    private Variable getVariable(String variable) {
        Scope current = currentScope;
        Variable result = null;
        // If variable wasn't in this scope try to find it in previous ones
        while (result == null && current != null) {
            result = current.variableTable.get(variable);
            current = current.parent;
        }
        return result;
    }

    public boolean existsVariable(String variable) {
        return getVariable(variable) != null;
    }

    public Variable getVariable(String variable, int id) {
        if (variableRelations.containsKey(id)) {
            return variableRelations.get(id);
        }
        Variable result = getVariable(variable);
        if (result != null) {
            variableRelations.put(id, result);
        }
        return result;
    }

    public Variable getVariableHere(String variable, int id) {
        Variable result = currentScope.variableTable.get(variable);
        if (result != null) {
            variableRelations.put(id, result);
        }
        return result;
    }

    public Function getFunctionById(int id) {
        return functionRelations.get(id);
    }

    private Function getFunction(Func function) {
        Scope current = currentScope;
        Function result = current.functionTable.get(function);
        // If variable wasn't in this scope try to find it in previous ones
        while (result == null && current.parent != null) {
            current = current.parent;
            result = current.functionTable.get(function);
        }
        return result;
    }

    public boolean existsFunction(String name, Collection<Type> parameters) {
        Func function = new Func(name, parameters.toArray(new Type[0]));
        return getFunction(function) != null;
    }

    public Function getFunction(String name, Collection<Type> parameters, int id) {
        if (functionRelations.containsKey(id)) {
            return functionRelations.get(id);
        }
        Func function = new Func(name, parameters.toArray(new Type[0]));
        Function result = getFunction(function);
        if (result != null) {
            functionRelations.put(id, result);
        }
        return result;
    }

    public Function getFunctionHere(String name, Collection<Type> parameters, int id) {
        Function result = currentScope.functionTable.get(new Func(name, parameters.toArray(new Type[0])));
        if (result != null) {
            functionRelations.put(id, result);
        }
        return result;
    }

    public boolean putVariable(int id, String variable, Type type, boolean isConst) {
        boolean result = currentScope.variableTable.putIfAbsent(
                variable, new Variable(id, variable, type, isConst, currentScope.depth)
        ) == null;
        if (result) {
            variableRelations.put(id, currentScope.variableTable.get(variable));
        }
        return result;
    }

    public boolean putVariable(int id, String variable, Type type) {
        return putVariable(id, variable, type, false);
    }

    public boolean putFunction(int id, String name, List<Type> parameters, Type type) {
        Func func = new Func(name, parameters.toArray(new Type[0]));
        Function function = new Function(id, name, parameters.toArray(new Type[0]), type);
        if (func.equals(mainFunc)) {
            mainFunction = function;
        }
        boolean result = currentScope.functionTable.putIfAbsent(func, function) == null;
        if (result) {
            functionRelations.put(id, function);
        }
        return result;
    }

    public boolean existsScope(int id) {
        return currentScope.scopes.containsKey(id);
    }

    public void openScope(int id) {
        // Enters if it already exists
        if (existsScope(id)) {
            currentScope = currentScope.scopes.get(id);
            return;
        }
        // Creates it if it doesn't
        Scope scope = new Scope(id, currentScope);
        currentScope.scopes.put(id, scope);
        currentScope = scope;
    }

    public Deque<Integer> openPreviousScope(int id) {
        // Stack
        Deque<Integer> result = new ArrayDeque<>();
        while (currentScope != null && !existsScope(id)) {
            result.push(currentScope.blockId);
            currentScope = currentScope.parent;
        }
        // Couldn't find it: restore scope and return null
        if (currentScope == null) {
            restoreScope(result);
            return null;
        }
        openScope(id);
        return result;
    }

    public void restoreScope(Deque<Integer> stack) {
        while (!stack.isEmpty()) {
            int block = stack.pop();
            openScope(block);
        }
    }

    public boolean existsClassScope(Type type) {
        return classTable.containsKey(type);
    }

    public void createClassScope(int id, Type type) {
        classTable.putIfAbsent(type, id);
        openScope(id);
    }

    public Deque<Integer> openClassScope(Type type) {
        Integer id = classTable.get(type);
        if (id == null) {
            return null;
        }
        return openPreviousScope(id);
    }

    public void closeScope() {
        currentScope = currentScope.parent;
    }

    public Iterable<Variable> variables() {
        return currentScope.variableTable.values();
    }

    private static class Scope {

        final int blockId;
        final Map<String, Variable> variableTable;
        final Map<Func, Function> functionTable;
        final Map<Integer, Scope> scopes;
        final Scope parent;
        final int depth;

        private Scope(int blockId, Scope parent) {
            this.blockId = blockId;
            this.variableTable = new HashMap<>();
            this.functionTable = new HashMap<>();
            this.scopes = new HashMap<>();
            this.parent = parent;
            this.depth = parent == null ? 0 : parent.depth + 1;
        }

    }

    private static class Func {

        final String name;
        final Type[] parameters;

        Func(String name, Type[] parameters) {
            this.name = name;
            this.parameters = parameters;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Func func = (Func) o;
            return name.equals(func.name) && Arrays.equals(parameters, func.parameters);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

    }

}
