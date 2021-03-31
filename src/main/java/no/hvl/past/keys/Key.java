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
    Graph container();


    /**
     * The name of the global (merged) type the key is defined on.
     */
    Name targetType();

    /**
     * The type of the (local) elements that are required to evaluate the key.
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
                    return requiredProperties().stream().allMatch(t -> morphism.codomain().contains(t));
                }
                return false;
            }
        };
    }


    @Override
    default GraphMorphism binding() {
        int arity = requiredProperties().size();
        return new GraphMorphism() {
            @Override
            public Graph domain() {
                return Universe.ONE_NODE;
            }

            @Override
            public Graph codomain() {
                return container();
            }

            @Override
            public Optional<Name> map(Name name) {
                if (Universe.ONE_NODE_THE_NODE.equals(name)) {
                    return Optional.of(targetType());
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
