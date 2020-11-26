package no.hvl.past.logic;

import java.util.stream.Stream;

public class NegatedTheory<This extends Theory<This>> implements Theory<This> {

    private final Theory<This> negatedTHeory;

    public NegatedTheory(Theory<This> negatedTHeory) {
        this.negatedTHeory = negatedTHeory;
    }

    @Override
    public boolean isInstance(Model<This> model) {
        return !negatedTHeory.isInstance(model);
    }

    @Override
    public boolean isEnumeratable() {
        // Unfortunately negation breaks enumeration because otherwise we would need complements which are seldom
        return false;
    }

    @Override
    public boolean isFinite() {
        // Unfortunately negation breaks finiteness considerations because otherwise we would need complements which are seldom
        return false;
    }

    @Override
    public Stream<Model<This>> models() {
        // Unfortunately negation breaks enumeration because otherwise we would need complements which are seldom
        return Stream.empty();
    }
}
