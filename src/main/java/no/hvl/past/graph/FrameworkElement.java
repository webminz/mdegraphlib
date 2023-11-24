package no.hvl.past.graph;

/**
 * An enum over all the constituents of the graph framework.
 */
public enum FrameworkElement {

    /**
     * A graph is a collection of nodes and edges expressing structured information.
     */
    GRAPH,

    /**
     * A graph morphism relates two graphs with each other by relating nodes and edges well-defined manner.
     */
    GRAPH_MORPHISM,

    /**
     * A label is basically just a name, that is in a one-to-one relationship with a specific graph (arity)
     * and probably has an underlying semantic interpretation.
     */
    LABEL,

    /**
     * A diagram is a pair comprising a graph morphism (binding) and a label subject to the condition
     * that the domain of the binding equal to the arity of the label.
     */
    DIAGRAM,

    /**
     * A sketch is a graph (carrier) together with a collection of diagrams subject to the condition
     * that the codomain of all diagrams is equal to the carrier.
     */
    SKETCH,

    /**
     * A sketch morphism relates two sketches with each other, i.e. it comprises a graph morphism
     * between the two carriers and provides a translation between each diagram (co-slice morphism).
     */
    SKETCH_MORPHISM,

    /**
     * A star a.k.a. multispan is a source (apex) sketch together with multiple target sketches connected
     * to the source by a sketch morphism.
     * Each star can be seen as structural representation of a multi-ary relationship between multiple sketches.
     *
     */
    STAR,

    /**
     * A star morphism relates two stars with each other, i.e. it comprises multiple
     * sketch morphisms (between the apexes and all sources) such that all morphism
     * squares fulfill the naturality condition.
     */
    STAR_MORPHISM


}
