package syntactical.ast;

import java.util.Objects;

public class Type {

    private String name;
    private Type parameter;

    public Type(String name, Type parameter) {
        this.name = name;
        this.parameter = parameter;
    }

    public Type(String name) {
        this(name, null);
    }

    public String getName() {
        return name;
    }

    public Type getParameter() {
        return parameter;
    }

    public Type() {
        super();
    }

    @Override
    public String toString() {
        return name + (parameter != null ? "<" + parameter + ">" : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Type type = (Type) o;
        return name.equals(type.name) &&
                Objects.equals(parameter, type.parameter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, parameter);
    }

}
