package no.hvl.past.graph;

import no.hvl.past.logic.Model;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import no.hvl.past.util.StreamExtensions;

import java.util.*;
import java.util.stream.Stream;

public interface TypedGraph extends GraphMorphism, Model<GraphLabelTheory> {

    /**
     * Interprets this morphism as a typing such that one can perform
     * a type based traversal outgoing at a selected node in the domain.
     * Given the name of the start node, all (possible composed) edges
     * are retrieved that admit to typing over the given path.
     * This can be compared to common feature in object oriented programming
     * where properties of an object are traversed, e.g.
     * "Person p1 = new Person(...); p1.address.city.name = ...".
     *
     */
    default Stream<Triple> selectViaTypePath(Name startNode, Name... typePath) {
        Set<Triple> result = new HashSet<>();
        List<Name> parameter = new ArrayList<>(Arrays.asList(typePath));
        return StreamExtensions.variablePipleline(
                parameter,
                Stream.of(Triple.node(startNode)),
                (triple, typeName) -> selectByLabel(typeName).map(triple::compose).filter(Optional::isPresent).map(Optional::get));
    }

    default Stream<Triple> allInstances(Triple type) {
        return this.select(type).map(t -> t.mapName(Name::firstPart));
    }

    default Stream<Triple> queryEdge(Name src, Name label, Name trg) {
        return this.allInstances(Triple.edge(src, label, trg));
    }

    default Stream<Triple> allInstances(Name type) {
        return this.select(Triple.node(type)).map(t -> t.mapName(Name::firstPart));
    }

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
