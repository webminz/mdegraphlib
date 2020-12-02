package no.hvl.past.graph;

import no.hvl.past.logic.Model;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import no.hvl.past.util.StreamExtensions;

import java.util.*;
import java.util.stream.Stream;

public interface TypedGraph extends GraphMorphism, Model<Graph> {



    /**
     * Interprets the given graph morphism as a typing.
     */
    static TypedGraph interpret(GraphMorphism morphism) {
        return new TypedGraph() {
            @Override
            public Graph domain() {
                return morphism.domain();
            }

            @Override
            public Graph codomain() {
                return morphism.codomain();
            }

            @Override
            public Optional<Name> map(Name name) {
                return morphism.map(name);
            }

            @Override
            public Name getName() {
                return morphism.getName();
            }
        };
    }

}
