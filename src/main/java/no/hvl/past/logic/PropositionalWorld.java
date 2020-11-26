package no.hvl.past.logic;

import java.util.Map;

public class PropositionalWorld implements Model<PropositionalTheory> {

    private final Map<String, Boolean> assigments;

    public PropositionalWorld(Map<String, Boolean> assigments) {
        this.assigments = assigments;
    }

    public boolean isTrue(String var) {
        return assigments.containsKey(var) && assigments.get(var);
    }

    public boolean isFalse(String var) {
        return assigments.containsKey(var) && !assigments.get(var);
    }


}
