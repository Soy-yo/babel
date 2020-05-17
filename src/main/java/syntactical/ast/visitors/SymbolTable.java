package syntactical.ast.visitors;

import syntactical.ast.Type;

import java.util.*;

public class SymbolTable {

    // Only global function and classes allowed
    private final Map<Function, Type> functionTable;
    private final Set<Type> classTable;
    private Scope currentScope;

    public SymbolTable() {
        this.functionTable = new HashMap<>();
        this.classTable = new HashSet<>();
        // TODO arroba por ejemplo
        this.currentScope = new Scope("@global_scope", null);
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
        return functionTable.get(function);
    }

    public boolean putVariable(String variable, Type type) {
        return currentScope.variableTable.putIfAbsent(variable, type) == null;
    }

    public boolean putFunction(String name, List<Type> parameters, Type type) {
        Function function = new Function(name, parameters.toArray(new Type[0]));
        return functionTable.putIfAbsent(function, type) == null;
    }

    public boolean putClass(Type name) {
        return classTable.add(name);
    }

    public boolean existsScope(String name) {
        return currentScope.scopes.containsKey(name);
    }

    public SymbolTable openScope(String name) {
        // Enters if it already exists
        if (existsScope(name)) {
            currentScope = currentScope.scopes.get(name);
            return this;
        }
        // Creates it if it doesn't
        Scope scope = new Scope(name, currentScope);
        currentScope.scopes.put(name, scope);
        currentScope = scope;
        return this;
    }

    public SymbolTable closeScope() {
        currentScope = currentScope.parent;
        return this;
    }

    private static class Scope {

        final String name;
        final Map<String, Type> variableTable;
        final Map<String, Scope> scopes;
        final Scope parent;

        private Scope(String name, Scope parent) {
            this.name = name;
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
