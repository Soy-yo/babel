package syntactical.ast.visitors;

import syntactical.ast.Type;

import java.util.*;

public class SymbolTable {

    // Only global function and classes allowed
    private final Map<Type, Scope> classTable;
    private Scope currentScope;

    public SymbolTable() {
        this.classTable = new HashMap<>();
        this.currentScope = new Scope(-1, null);
    }

    public int getCurrentScopeId() {
        return currentScope.blockId;
    }

    public String getCurrentScopeName() {
        return currentScope.name;
    }

    public Type getVariable(String variable) {
        Scope current = currentScope;
        Type result = current.variableTable.get(variable);
        // If variable wasn't in this scope try to find it in previous ones
        while (result == null && current.parent != null) {
            current = current.parent;
            result = current.variableTable.get(variable);
        }
        return result;
    }

    public Type getFunction(String name, Collection<Type> parameters) {
        Function function = new Function(name, parameters.toArray(new Type[0]));
        Scope current = currentScope;
        Type result = current.functionTable.get(function);
        // If variable wasn't in this scope try to find it in previous ones
        while (result == null && current.parent != null) {
            current = current.parent;
            result = current.functionTable.get(function);
        }
        return result;
    }

    public boolean putVariable(String variable, Type type) {
        return currentScope.variableTable.putIfAbsent(variable, type) == null;
    }

    public boolean putFunction(String name, List<Type> parameters, Type type) {
        Function function = new Function(name, parameters.toArray(new Type[0]));
        return currentScope.functionTable.putIfAbsent(function, type) == null;
    }

    public boolean existsScope(int id) {
        return currentScope.scopes.containsKey(id);
    }

    public boolean existsClassScope(Type type) {
        return classTable.containsKey(type);
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
            while (!result.isEmpty()) {
                int block = result.pop();
                openScope(block);
            }
            return null;
        }
        return result;
    }

    public void createClassScope(int id, Type type) {
        // Creates it if it doesn't
        Scope scope = new Scope(id, type.getName(), currentScope);
        classTable.put(type, scope);
        currentScope = scope;
    }

    public SymbolTable closeScope() {
        currentScope = currentScope.parent;
        return this;
    }

    private static class Scope {

        final int blockId;
        final String name;
        final Map<String, Type> variableTable;
        final Map<Function, Type> functionTable;
        final Map<Integer, Scope> scopes;
        final Scope parent;

        private Scope(int blockId, String name, Scope parent) {
            this.blockId = blockId;
            this.name = name;
            this.variableTable = new HashMap<>();
            this.functionTable = new HashMap<>();
            this.scopes = new HashMap<>();
            this.parent = parent;
        }

        private Scope(int blockId, Scope parent) {
            this(blockId, null, parent);
        }

    }

    private static class Function {

        final String name;
        final Type[] parameters;

        private Function(String name, Type[] parameters) {
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
            Function function = (Function) o;
            return Objects.equals(name, function.name) &&
                    Arrays.equals(parameters, function.parameters);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(name);
            result = 31 * result + Arrays.hashCode(parameters);
            return result;
        }

    }

}
