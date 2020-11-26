package no.hvl.past.logic;

import java.util.List;
import java.util.stream.Stream;

public class DisjunctiveTheory<This extends Theory<This>> implements Theory<This> {

    private final List<Theory<This>> subtheories;

    public DisjunctiveTheory(List<Theory<This>> subtheories) {
        this.subtheories = subtheories;
    }

    @Override
    public boolean isInstance(Model<This> model) {
        return this.subtheories.stream().anyMatch(t -> t.isInstance(model));
    }

    @Override
    public boolean isEnumeratable() {
        return this.subtheories.stream().allMatch(Theory::isEnumeratable);
    }

    @Override
    public boolean isFinite() {
        return this.subtheories.stream().allMatch(Theory::isFinite);
    }

    @Override
    public Stream<Model<This>> models() {
        Stream<Model<This>> result = Stream.empty();
        for (Theory<This> t : subtheories) {
            if (t.isEnumeratable()) {
                result = Stream.concat(t.models(), result);
            }
        }
        return result;
    }
}
