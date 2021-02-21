package no.hvl.past.graph.trees;

import no.hvl.past.graph.Graph;
import no.hvl.past.graph.GraphMorphism;
import no.hvl.past.graph.Visitor;


public interface TypedTree extends Tree, GraphMorphism {


    @Override
    default Graph domain() {
        return this;
    }


    @Override
    default boolean verify() {
        return false; // TODO
    }

    @Override
    default void accept(Visitor visitor) {
        visitor.beginMorphism();
        visitor.handleElementName(getName());
        domain().accept(visitor);
        codomain().accept(visitor);
        mappings().forEach(visitor::handleMapping);
        visitor.endMorphism();
    }

}
