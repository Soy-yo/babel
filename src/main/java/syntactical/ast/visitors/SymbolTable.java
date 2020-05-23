package syntactical.ast.visitors;

import syntactical.ast.Type;

import java.util.*;

public class SymbolTable {

    // Only global function and classes allowed
    private final Map<Function, Type> functionTable;
    private final Map<Type, Scope> classTable;
    private Scope currentScope;

    public SymbolTable() {
        this.functionTable = new HashMap<>();
        this.classTable = new HashMap<>();
        this.currentScope = new Scope(-1, null);
    }

    public int getCurrentScopeId() {
        return currentScope.blockId;
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
        return functionTable.get(function);
    }

    public boolean putVariable(String variable, Type type) {
        return currentScope.variableTable.putIfAbsent(variable, type) == null;
    }

    public boolean putFunction(String name, List<Type> parameters, Type type) {
        Function function = new Function(name, parameters.toArray(new Type[0]));
        return functionTable.putIfAbsent(function, type) == null;
    }

    public boolean existsScope(int id) {
        return currentScope.scopes.containsKey(id);
    }

    public boolean existsClassScope(Type type) {
        return classTable.containsKey(type);
    }

    public SymbolTable openScope(int id) {
        // Enters if it already exists
        if (existsScope(id)) {
            currentScope = currentScope.scopes.get(id);
            return this;
        }
        // Creates it if it doesn't
        Scope scope = new Scope(id, currentScope);
        currentScope.scopes.put(id, scope);
        currentScope = scope;
        return this;
    }

    public SymbolTable openClassScope(int id, Type type) {
        // Enters if it already exists
        if (existsClassScope(type)) {
            currentScope = classTable.get(type);
            return this;
        }
        // Creates it if it doesn't
        // Creates it if it doesn't
        Scope scope = new Scope(id, currentScope);
        classTable.put(type, scope);
        currentScope = scope;
        return this;
    }

    public SymbolTable closeScope() {
        currentScope = currentScope.parent;
        return this;
    }

    private static class Scope {

        final int blockId;
        final Map<String, Type> variableTable;
        final Map<Integer, Scope> scopes;
        final Scope parent;

        private Scope(int blockId, Scope parent) {
            this.blockId = blockId;
            this.variableTable = new HashMap<>();
            this.scopes = new HashMap<>();
            this.parent = parent;
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
