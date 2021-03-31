package no.hvl.past.graph.trees;

import java.util.Optional;

public interface TreeBuilder<R> {

    default boolean hasParentBuilder() {
        return parentBuilder().isPresent();
    }

    <P> Optional<TreeBuilder<P>> parentBuilder();

    R build();

}
