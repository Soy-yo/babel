package syntactical.ast;

import lexical.LexicalUnit;

import java.util.Objects;

public class Type {

    public static final Type WILDCARD = new Type("*");

    private LexicalUnit name;
    private Type parameter;

    public Type(LexicalUnit name, Type parameter) {
        this.name = name;
        this.parameter = parameter;
    }

    public Type(LexicalUnit name) {
        this(name, null);
    }

    public Type(String name, Type parameter) {
        this(new LexicalUnit(name), parameter);
    }

    public Type(String name) {
        this(new LexicalUnit(name));
    }

    public Type() {
        super();
    }

    public LexicalUnit getNameLexicalUnit() {
        return name;
    }

    public String getName() {
        return name.lexeme();
    }

    public Type getParameter() {
        return parameter;
    }

    public boolean contains(Type type) {
        boolean result = false;
        Type current = this;
        while (current != null && !result) {
            result = current.equals(type);
            current = current.parameter;
        }
        return result;
    }

    @Override
    public String toString() {
        return getName() + (parameter != null ? "<" + parameter + ">" : "");
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
        return this == WILDCARD || o == WILDCARD ||
                Objects.equals(getName(), type.getName()) &&
                        Objects.equals(parameter, type.parameter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), parameter);
    }

}
