package no.hvl.past.logic;

import java.util.List;
import java.util.stream.Stream;

public class ConjunctiveTheory<This extends Theory<This>> implements Theory<This> {

    private final List<Theory<This>> subtheories;

    public ConjunctiveTheory(List<Theory<This>> subtheories) {
        this.subtheories = subtheories;
    }

    @Override
    public boolean isInstance(Model<This> model) {
        return this.subtheories.stream().allMatch(t -> t.isInstance(model));
    }

    @Override
    public boolean isEnumeratable() {
        return this.subtheories.stream().anyMatch(Theory::isEnumeratable);
    }

    @Override
    public boolean isFinite() {
        return this.subtheories.stream().anyMatch(Theory::isFinite);
    }

    @Override
    public Stream<Model<This>> models() {
        return this.subtheories.stream().filter(Theory::isEnumeratable).findFirst().map(
                t -> t.models().filter(this::isInstance)).orElse(Stream.empty());
    }
}
