package no.hvl.past.graph.operations;

import no.hvl.past.graph.Graph;

public interface GraphDiagram {

    interface Visitor {

        void handle(GraphOperation graphOperation);

        void handle(Predicate predicate);
    }

    Graph arity();

    void accept(Visitor visitor);
}
