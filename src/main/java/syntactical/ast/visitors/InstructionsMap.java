package syntactical.ast.visitors;

import java.util.HashMap;
import java.util.Map;

public class InstructionsMap {

    private static final Map<String, Integer> map = new HashMap<>();

    public static int get(String instruction) {
        return map.get(instruction);
    }

    static {
        // TODO check none is missing
        map.put("add", -1);
        map.put("sub", -1);
        map.put("mul", -1);
        map.put("div", -1);
        map.put("neg", 0);
        map.put("and", -1);
        map.put("or", -1);
        map.put("not", 0);
        map.put("equ", -1);
        map.put("geq", -1);
        map.put("leq", -1);
        map.put("les", -1);
        map.put("grt", -1);
        map.put("neq", -1);
        map.put("ldo", 1);
        map.put("ldc", 1);
        map.put("ind", 0);
        map.put("sro", -1);
        map.put("sto", -2);
        map.put("ujp", 0);
        map.put("fjp", -1);
        map.put("ixj", -1);
        map.put("ixa", -1);
        map.put("inc", 0);
        map.put("dec", 0);
        map.put("chk", 0);
        map.put("dpl", 1);
        map.put("ldd", 1);
        map.put("sli", -1);
        map.put("new", -2);
        map.put("lod", 1);
        map.put("lda", 1);
        map.put("str", -1);
        map.put("mst", 5);
        map.put("cup", 0);
        // TODO not constant: check manually
        map.put("ssp", 0);
        map.put("sep", 0);
        // TODO not constant: check manually
        map.put("ent", 0);
        // TODO not constant: check manually
        map.put("retf", 0);
        // TODO not constant: check manually
        map.put("retp", -1);
        // TODO not constant: check manually
        map.put("movs", 0);
        // TODO not constant: check manually
        map.put("movd", 0);
        map.put("smp", 0);
        map.put("cupi", 0);
        map.put("mstf", 5);
        map.put("stp", 0);
    }

}
