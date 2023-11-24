package no.hvl.past.graph;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.logic.Formula;
import no.hvl.past.names.Name;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * A diagram in the categorical sense, i.e. a selection of graphs (as nodes)
 * and morphisms between them (as edges).
 *
 * We assume that all diagrams we are dealing with are small and finite.
 * Moreover, there is a total order over nodes and edges.
 */
public interface Diagram extends Element {

    /**
     * Returns the label of this diagram.
     */
    Formula<Graph> label();

    /**
     * The binding of the shape into elements in a concrete graph.
     */
    GraphMorphism binding();

    /**
     * The shape of the diagram (= a graph).
     */
    default Graph arity() {
        return binding().domain();
    }

    default Stream<Triple> generatedElements() {
        if (label() instanceof GraphOperation) {
            GraphOperation op = (GraphOperation) label();
            return op.outputArity()
                    .elements()
                    .map(t -> t.map(binding()::map))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(t -> op.inputArity()
                            .elements()
                            .map(binding()::apply)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .noneMatch(t::equals));

        }
        return Stream.empty();
    }

    @Override
    default void accept(Visitor visitor) {
        visitor.handleDiagram(this);
    }

    @Override
    default  boolean verify() {
        return binding().verify() && binding().isTotal();
    }

    default boolean directlyDependsOn(Diagram diagram) {
        return this.arity()
                .elements()
                .filter(this.binding()::definedAt)
                .map(this.binding()::apply)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .anyMatch(required ->
                        diagram.generatedElements().anyMatch(provided ->
                                required.equals(provided)
                        )
                );
    }

    // TODO this method should be used more! and might be abstract
    default Diagram substitue(GraphMorphism morphism) {
        return new Diagram() {
            @Override
            public Formula<Graph> label() {
                return Diagram.this.label();
            }

            @Override
            public GraphMorphism binding() {
                return Diagram.this.binding().compose(morphism);
            }

            @Override
            public Name getName() {
                return Diagram.this.getName().substitution(morphism.getName());
            }
        };
    }

    default Optional<Name> nodeBinding() {
        if (binding().domain().equals(Universe.ONE_NODE)) {
            return binding().map(Universe.ONE_NODE_THE_NODE);
        }
        return Optional.empty();
    }

    default Optional<Triple> edgeBinding() {
        if (binding().domain().equals(Universe.ARROW)) {
            return edgeBinding(Universe.ARROW_THE_ARROW);
        }
        return Optional.empty();
    }

    default Optional<Triple> edgeBinding(Triple edge) {
        return binding().map(edge.getSource())
                .flatMap(src -> binding().map(edge.getLabel())
                        .flatMap(lbl -> binding().map(edge.getTarget())
                                .map(trg -> Triple.edge(src, lbl, trg))));
    }
}
