package no.hvl.past.logic;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class PropositionalTheory implements Theory<PropositionalTheory> {

    private final boolean closedWorld;
    private final Set<String> variables;
    private final Map<String, Boolean> requirements;

    public PropositionalTheory(
            boolean closedWorld,
            Set<String> variables,
            Map<String, Boolean> requirements) {
        this.closedWorld = closedWorld;
        this.variables = variables;
        this.requirements = requirements;
    }

    @Override
    public boolean isInstance(Model<PropositionalTheory> model) {
        if (model instanceof PropositionalWorld) {
            PropositionalWorld instance = (PropositionalWorld) model;
            this.requirements.entrySet().stream().allMatch(
                    entry -> entry.getValue() ? instance.isTrue(entry.getKey()) : instance.isFalse(entry.getKey()));
        }
        return false;
    }

    @Override
    public boolean isEnumeratable() {
        return closedWorld;
    }

    @Override
    public boolean isFinite() {
        return closedWorld;
    }

    @Override
    public Stream<Model<PropositionalTheory>> models() {
        return null; // TODO
    }
}
