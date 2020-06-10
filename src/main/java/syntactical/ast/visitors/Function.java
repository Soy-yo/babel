package syntactical.ast.visitors;

import syntactical.ast.Type;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class Function {

    final int id;
    final String name;
    final Type[] parameters;
    final Type returnType;
    final boolean isMethod;

    Function(int id, String name, Type[] parameters, Type returnType, boolean isMethod) {
        this.id = id;
        this.name = name;
        this.parameters = parameters;
        this.returnType = returnType;
        this.isMethod = isMethod;
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
        return name.equals(function.name) &&
                Arrays.equals(parameters, function.parameters) &&
                returnType.equals(function.returnType);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name, returnType);
        result = 31 * result + Arrays.hashCode(parameters);
        return result;
    }

    @Override
    public String toString() {
        return "Function @" + id + " - " + name + "(" +
                Arrays.stream(parameters).map(Type::toString).collect(Collectors.joining(", "))
                + "): " + returnType;
    }

}
