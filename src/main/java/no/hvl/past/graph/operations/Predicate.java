package no.hvl.past.graph.operations;


import no.hvl.past.graph.AbstractMorphism;

public interface Predicate extends GraphDiagram {

    boolean check(AbstractMorphism instance);

    @Override
    default void accept(Visitor visitor) {
        visitor.handle(this);
    }
}
