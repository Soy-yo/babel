package semantical;

import syntactical.ast.ExpressionNode;
import syntactical.ast.Type;

import java.util.*;

public class Directions {

    private final Map<Integer, Integer> variableDirections;
    private final Map<Type, List<FieldData>> classFields;
    private final Map<Integer, List<FieldData>> formFields;
    private final Map<Integer, Integer> functionSize;

    public Directions() {
        this.variableDirections = new HashMap<>();
        this.classFields = new HashMap<>();
        this.formFields = new HashMap<>();
        this.functionSize = new HashMap<>();
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

    public void registerClass(Type type) {
        if (!classFields.containsKey(type)) {
            classFields.put(type, new ArrayList<>());
        }
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

    public List<FieldData> getClassFields(Type type) {
        return Collections.unmodifiableList(classFields.get(type));
    }

    public void registerForm(int id) {
        if (!formFields.containsKey(id)) {
            formFields.put(id, new ArrayList<>());
        }
    }

    public void registerFormField(int id, Variable v, ExpressionNode initialValue) {
        registerForm(id);
        List<FieldData> vars = formFields.get(id);
        vars.add(new FieldData(v, initialValue, vars.size()));
    }

    public List<FieldData> getFormFields(int id) {
        return Collections.unmodifiableList(formFields.get(id));
    }

    public FieldData getFormField(int id, Variable v) {
        return getField(formFields, id, v);
    }

    public void setFunctionSize(int id, int size) {
        functionSize.put(id, size);
    }

    public int getFunctionSize(int id) {
        return functionSize.get(id);
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

}
