package no.hvl.past.graph.trees;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

import java.util.Optional;

public interface QueryNodeChildren extends TypedChildrenRelation {

    Triple feature();

    @Override
    QueryNode child();

    @Override
    default Optional<Name> edgeTyping() {
        return Optional.of(feature().getLabel());
    }

    boolean isProjection();

    boolean isSelection();

    interface Projection extends QueryNodeChildren {
        @Override
        default boolean isProjection() {
            return true;
        }

        @Override
        default boolean isSelection() {
            return false;
        }
    }

    interface Selection extends QueryNodeChildren {
        @Override
        default boolean isProjection() {
            return false;
        }

        @Override
        default boolean isSelection() {
            return true;
        }
    }



}
