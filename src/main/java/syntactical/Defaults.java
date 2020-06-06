package syntactical;

import syntactical.ast.Type;

public class Defaults {

    public static final Type FORM = new Type("Form");
    public static final Type INT = new Type("Int");
    public static final Type REAL = new Type("Real");
    public static final Type BOOL = new Type("Bool");
    public static final Type CHAR = new Type("Char");
    public static final Type VOID = new Type("Void");
    public static final Type STRING = new Type("String");
    public static final Type DEFAULT = new Type("~Default");
    public static final java.lang.String ARRAY_STR = "Array";
    public static final Type ARRAY = new Type(ARRAY_STR, Type.WILDCARD);
    public static final java.lang.String THIS = "this";
    public static final java.lang.String ARRAY_SIZE = "size";

    public static int IDENTITY_ID = 0;

    public static class Int {
        public static final int ID = 1;
        public static final int PLUS_ID = 2;
        public static final int MINUS_ID = 3;
        public static final int MULT_ID = 4;
        public static final int DIV_ID = 5;
        public static final int MOD_ID = 6;
        public static final int GE_ID = 7;
        public static final int GT_ID = 8;
        public static final int LE_ID = 9;
        public static final int LT_ID = 10;
        public static final int UNARY_PLUS_ID = 11;
        public static final int UNARY_MINUS_ID = 12;
        public static final int EQUALS_ID = 13;
        public static final int TO_ID = 14;
    }

    public static class Form {
        public static final int ID = 21;
        public static final int EQUALS_ID = 22;
    }

    public static class Real {
        public static final int ID = 31;
        public static final int PLUS_ID = 32;
        public static final int MINUS_ID = 33;
        public static final int MULT_ID = 34;
        public static final int DIV_ID = 35;
        public static final int GE_ID = 36;
        public static final int GT_ID = 37;
        public static final int LE_ID = 38;
        public static final int LT_ID = 39;
        public static final int UNARY_PLUS_ID = 40;
        public static final int UNARY_MINUS_ID = 41;
        public static final int EQUALS_ID = 42;
        public static final int TO_ID = 43;
    }

    public static class Bool {
        public static final int ID = 51;
        public static final int EQUALS_ID = 52;
        public static final int AND_ID = 53;
        public static final int OR_ID = 54;
        public static final int NOT_ID = 55;
    }

    public static class Char {
        public static final int ID = 71;
        public static final int GE_ID = 72;
        public static final int GT_ID = 73;
        public static final int LE_ID = 74;
        public static final int LT_ID = 75;
        public static final int EQUALS_ID = 76;
        public static final int TO_ID = 77;
    }

    public static class Void {
        public static final int ID = 91;
    }

    public static class Array {
        public static final int ID = 101;
        public static final int SIZE_ID = 102;
        public static final int EQUALS_ID = 103;
        public static final int CONSTRUCTOR_ID = 104;
    }

    public static class String {
        public static final int ID = 121;
    }

}
