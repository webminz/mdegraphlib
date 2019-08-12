package no.hvl.past.graph.operations;

import no.hvl.past.graph.AbstractMorphism;
import no.hvl.past.graph.GraphModification;

import java.util.List;

public interface GraphOperation extends GraphDiagram {

    GraphModification execute(AbstractMorphism binding, List<Application> otherOperationApplications);

    @Override
    default void accept(Visitor visitor) {
        visitor.handle(this);
    }
}
