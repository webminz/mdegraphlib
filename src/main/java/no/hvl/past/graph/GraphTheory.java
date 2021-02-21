package no.hvl.past.graph;

import no.hvl.past.logic.Formula;
import no.hvl.past.names.Name;

/**
 * A graph theory is a syntactical description of a class of typed graphs (= graph morphisms).
 * The instances against it can be validated or executed are graph morphisms whose codomain is a graph
 * that is equal to the arity of this theory.
 *
 * There are two types of theories:
 * - a diagrammatic predicate encodes a property that all instances of a graph should fulfill.
 * - a diagrammatic operation encodes elements in a graph that are derived by the elements in the given graph.
 */
public interface GraphTheory extends Formula<Graph>, Element {

    /**
     * Return true if this abstract operation is a Predicate, i.e. does not generate new elements only check validity.
     */
    boolean isPredicate();

    /**
     * Return true if this abstract operation is an Operation, i.e. creates new elements.
     */
    boolean isOperation();

    /**
     * Every Theory should provide its name as a String that is unique.
     */
    String nameAsString();


    /**
     * Provides the arity of this label.
     */
    Graph arity();


    @Override
    default Name getName() {
        return Name.identifier(nameAsString());
    }

    @Override
    default void accept(Visitor visitor) {
        visitor.handleElementName(getName());
    }

    @Override
    default boolean verify() {
        return true;
    }


}
