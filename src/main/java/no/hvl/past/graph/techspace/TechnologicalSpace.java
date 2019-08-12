package no.hvl.past.graph.techspace;

import no.hvl.past.graph.AbstractMorphism;
import no.hvl.past.graph.Graph;
import no.hvl.past.graph.OutputPort;
import no.hvl.past.graph.operations.DiagrammaticGraph;
import no.hvl.past.util.Pair;

import java.util.List;
import java.util.Optional;

/**
 * Represents a concrete technological Space, from which
 * models can be imported and models can be written to.
 * This interface handles all parsing and pretty printing.
 */
public interface TechnologicalSpace {

    /**
     * The unique name of this Technological Space that can be used for identification and lookup.
     */
    String name();

    /**
     * The name of the technological space of which this technological space is a realization.
     * Might be empty if there is no superordinate technological space used for this implementation
     * or it might be recursive, e.g. MOF.
     */
    Optional<String> specifiedBy();

    /**
     * A list of technological spaces that represent realizations of this technological space.
     */
    List<String> realizedBy();

    /**
     *
     * Loads a given resource with this technological space such that it is available
     * for processing with this framework.
     * Provides a pair of the abstract typing morphism and the graph of the metamodel.
     */
    Pair<AbstractMorphism, DiagrammaticGraph> load(String location) throws UnsupportedException;

    /**
     * Provides an output port for the given resource location
     * that can be used to write out a graph in this technological space.
     */
    OutputPort<?> write(String location);

}
