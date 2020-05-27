package syntactical.ast;

import lexical.LexicalUnit;

import java.util.Objects;

public class Name {

    private final LexicalUnit identifier;
    private final Type type;

    public Name(LexicalUnit identifier, Type type) {
        this.identifier = identifier;
        this.type = type;
    }

    public LexicalUnit getIdentifierLexicalUnit() {
        return identifier;
    }

    public String getIdentifier() {
        return identifier.lexeme();
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return getIdentifier() + ": " + type;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Name name = (Name) other;
        return Objects.equals(getIdentifier(), name.getIdentifier()) &&
                Objects.equals(type, name.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdentifier(), type);
    }

}
