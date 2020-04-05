package syntactical.ast;

import java.util.Objects;

public class Name {

    private final String identifier;
    private final String type;

    public Name(String identifier, String type) {
        this.identifier = identifier;
        this.type = type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return identifier + ": " + type;
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
        return Objects.equals(identifier, name.identifier) &&
                Objects.equals(type, name.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, type);
    }

}
