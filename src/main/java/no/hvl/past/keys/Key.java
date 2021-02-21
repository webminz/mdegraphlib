package no.hvl.past.keys;

import no.hvl.past.graph.Diagram;
import no.hvl.past.graph.Graph;
import no.hvl.past.graph.GraphMorphism;
import no.hvl.past.graph.Universe;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.logic.Formula;
import no.hvl.past.logic.Model;
import no.hvl.past.names.Name;

import java.util.List;
import java.util.Optional;

/**
 * A means to "globally" identify an element.
 */
public interface Key extends Diagram {

    /**
     * The carrier graph where is defined on.
     *
     */
    Graph targetGraph();

    /**
     * The name of the type upon which the key is defined.
     */
    Name definedOnType();

    /**
     * The type of the elements that are required to evaluate the key.
     */
    List<Triple> requiredProperties();

    /**
     * Evaluates the key on a given element, the element is identified by its name
     * in the current containener = a graph morphism, i.e. typed graph.
     */
    Name evaluate(Name element, GraphMorphism typedContainer) throws KeyNotEvaluated;

    /**
     * Evaluates this key on a given java object if possible.
     */
    Name evaluate(Object element) throws KeyNotEvaluated;

    @Override
    default Formula<Graph> label() {
        return new Formula<Graph>() {
            @Override
            public boolean isSatisfied(Model<Graph> model) {
                if (model instanceof GraphMorphism) {
                    // if it is a type graph, we can check if we can actually evaluate it
                    GraphMorphism morphism = (GraphMorphism) model;
                    return morphism.codomain().mentions(definedOnType()) && requiredProperties().stream().allMatch(t -> morphism.codomain().contains(t));
                }
                return false;
            }
        };
    }


    @Override
    default GraphMorphism binding() {
        int arity = requiredProperties().size();
        Graph graph = Universe.multiSpan(arity);
        return new GraphMorphism() {
            @Override
            public Graph domain() {
                return graph;
            }

            @Override
            public Graph codomain() {
                return targetGraph();
            }

            @Override
            public Optional<Name> map(Name name) {
                if (Universe.ONE_NODE_THE_NODE.equals(name)) {
                    return Optional.of(definedOnType());
                }
                if (graph.mentions(name)) {
                    for (int i = 1; i <= arity; i++) {
                        Triple edge = Universe.multiSpanEdge(i);
                        if (edge.getLabel().equals(name)) {
                            return Optional.of(requiredProperties().get(i).getLabel());
                        } else if (edge.getTarget().equals(name)) {
                            return Optional.of(requiredProperties().get(i).getTarget());
                        }
                    }
                }

                return Optional.empty();
            }

            @Override
            public Name getName() {
                return Key.this.getName().absolute();
            }
        };
    }
}
