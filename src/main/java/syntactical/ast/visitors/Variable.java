package syntactical.ast.visitors;

import syntactical.ast.Type;

import java.util.Objects;

public class Variable {

    final int id;
    final String name;
    final Type type;
    final boolean isConst;

    Variable(int id, String name, Type type, boolean isConst) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.isConst = isConst;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Variable variable = (Variable) o;
        return name.equals(variable.name) &&
                type.equals(variable.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }
}
