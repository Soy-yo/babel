package syntactical.ast.visitors;

import syntactical.Defaults;
import syntactical.ast.ExpressionNode;
import syntactical.ast.Type;

import java.util.*;
import java.util.stream.Collectors;

public class Directions {

    private final Map<Integer, Integer> variableDirections;
    private final Map<Type, List<FieldData>> classFields;
    private final Map<Integer, List<FieldData>> formFields;

    public Directions() {
        this.variableDirections = new HashMap<>();
        this.classFields = new HashMap<>();
        this.formFields = new HashMap<>();
    }

    public void registerVariable(int id, int relativeDir) {
        variableDirections.put(id, relativeDir);
    }

    public boolean existsVariable(int id) {
        return variableDirections.containsKey(id);
    }

    public int getVariableDir(int id) {
        return variableDirections.get(id);
    }

    public void registerClassField(Type type, Variable v, ExpressionNode initialValue) {
        if (!classFields.containsKey(type)) {
            classFields.put(type, new ArrayList<>());
        }
        List<FieldData> vars = classFields.get(type);
        vars.add(new FieldData(v, initialValue, vars.size()));
    }

    public FieldData getClassField(Type type, Variable v) {
        return getField(classFields, type, v);
    }

    public void registerFormField(int id, Variable v, ExpressionNode initialValue) {
        if (!formFields.containsKey(id)) {
            formFields.put(id, new ArrayList<>());
        }
        List<FieldData> vars = formFields.get(id);
        vars.add(new FieldData(v, initialValue, vars.size()));
    }

    public FieldData getFormField(int id, Variable v) {
        return getField(formFields, id, v);
    }

    public FormTree getFormTree(int id) {
        if (!formFields.containsKey(id)) {
            return null;
        }
        return new FormTree(id);
    }

    private static <T> FieldData getField(Map<T, List<FieldData>> map, T key, Variable v) {
        if (!map.containsKey(key)) {
            return null;
        }
        List<FieldData> vars = map.get(key);
        for (FieldData fd : vars) {
            if (fd.variable.equals(v)) {
                return fd;
            }
        }
        return null;
    }

    static class FieldData {
        final Variable variable;
        final ExpressionNode initialValue;
        final int index;

        private FieldData(Variable variable, ExpressionNode initialValue, int index) {
            this.variable = variable;
            this.initialValue = initialValue;
            this.index = index;
        }
    }

    class FormTree {
        private final int rootId;

        private FormTree(int rootId) {
            this.rootId = rootId;
        }

        Set<Variable> fields() {
            List<FieldData> fields = formFields.get(rootId);
            return Collections.unmodifiableSet(
                    fields.stream().map(d -> d.variable).collect(Collectors.toSet())
            );
        }

        boolean equals(FormTree other) {
            Set<Variable> thisFields = fields();
            Set<Variable> otherFields = other.fields();
            if (!thisFields.equals(otherFields)) {
                return false;
            }
            // Recursively check Forms
            for (Variable v : thisFields) {
                if (Defaults.FORM.equals(v.type)) {
                    Variable w = otherFields.stream().filter(u -> u.equals(v)).findFirst().get();
                    if (!new FormTree(v.id).equals(new FormTree(w.id))) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

}
