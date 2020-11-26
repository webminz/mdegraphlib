package no.hvl.past.graph;

import no.hvl.past.logic.Theory;

import java.util.Optional;

/**
 * A Label for a diagram, which can be used to express some underlying semantic interpretation
 * for diagrams to e.g. express constraints.
 */
public interface Label extends Element {

    /**
     * Provides the arity of this label.
     */
    Graph arity();

    /**
     * May return a theory (syntactic representation)
     * of the collection of models that provides
     * semantics for this label.
     */
    Optional<Theory<?>> semantics();

    @Override
    default FrameworkElement elementType() {
        return FrameworkElement.LABEL;
    }

    @Override
    default boolean verify() {
        return true;
    }

    @Override
    default void accept(Visitor visitor) {
        visitor.handleDiagramLabel(getName());
    }
}
