package no.hvl.past.graph;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import com.google.common.collect.Sets;
import com.google.common.collect.Streams;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.graph.predicates.DataTypePredicate;
import no.hvl.past.logic.Formula;
import no.hvl.past.logic.Model;
import no.hvl.past.logic.Signature;
import no.hvl.past.names.Name;
import no.hvl.past.util.Pair;

/**
 * A sketch (a.k.a diagrammatic graph) is a graph together with a set of diagrams on this graph.
 */
public interface Sketch extends Element, Formula<Graph> {

    /**
     * The underlying graph of the sketch.
     */
    Graph carrier();

    /**
     * The collection of all diagrams.
     */
    Stream<Diagram> diagrams();

    default Stream<Diagram> diagramsOn(Triple element) {
        return diagrams().filter(diag ->
                diag.binding().domain().elements().anyMatch(e -> diag.binding().apply(e).map(element::equals).orElse(false))
        );
    }


    default Optional<Diagram> diagramByName(Name diagramName) {
        return diagrams().filter(diagram -> diagram.getName().equals(diagramName)).findFirst();
    }

    default Stream<Triple> groundElements() {
        return carrier()
                .elements()
                .filter(t -> diagrams().noneMatch(diag -> diag.generatedElements().anyMatch(t::equals)));
    }

    default Stream<Triple> derivedElements() {
        return carrier()
                .elements()
                .filter(t -> diagrams().anyMatch(diag -> diag.generatedElements().anyMatch(t::equals)));
    }

    default Sketch restrict(Collection<Diagram> extraDiagrams) {
        return new Sketch() {
            @Override
            public Graph carrier() {
                return Sketch.this.carrier();
            }

            @Override
            public Stream<Diagram> diagrams() {
                return Streams.concat(Sketch.this.diagrams(), extraDiagrams.stream());
            }

            @Override
            public Name getName() {
                return getName();
            }
        };
    }



    default GraphMorphism extend(Name name, GraphMorphism instance, ExecutionContext executionContext) throws GraphError {
        if (instance.codomain().equals(carrier())) {
            return new DiagrammaticWorkflow(name, instance, this, executionContext).execute();
        } else {
            throw new GraphError(GraphError.ERROR_TYPE.CODOMAIN_MISMATCH, Sets.newHashSet(
                    Triple.node(carrier().getName()),
                    Triple.node(instance.codomain().getName())
            ));
        }
    }

    @Override
    default boolean verify() {
        return carrier().verify() && this.diagrams().allMatch(Diagram::verify);
    }

    @Override
    default void accept(Visitor visitor) {
        visitor.beginSketch();
        visitor.handleElementName(getName());
        carrier().accept(visitor);
        diagrams().forEach(d -> d.accept(visitor));
        visitor.endSketch();
    }

    @Override
    default boolean isSatisfied(Model<Graph> model) {
        // Workflows have already verified consistency
        if (model instanceof DiagrammaticWorkflow) {
            DiagrammaticWorkflow wf = ((DiagrammaticWorkflow) model);
            return wf.executedCorrectly(this);
        }
        // Regular validation: pullback for every diagram predicate arity with the instance morph and checking the result
        if (model instanceof GraphMorphism) {
            GraphMorphism instance = (GraphMorphism) model;
            if (instance.codomain().equals(carrier()) && instance.verify()) {
                return this.diagrams().allMatch(diagram -> {
                    try {
                        Pair<GraphMorphism, GraphMorphism> pullback = diagram.binding().pullback(instance);
                        return diagram.label().isSatisfied(pullback.getFirst());
                    } catch (GraphError error) {
                        return false;
                    }
                });
            }
        }
        // otherwise false
        return false;
    }

    default boolean isDerived(Triple edge) {
        return diagrams().anyMatch(diag -> diag.generatedElements().anyMatch(edge::equals));
    }
}
