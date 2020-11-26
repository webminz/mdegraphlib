package no.hvl.past.graph;

import com.google.common.collect.Sets;
import com.google.common.graph.GraphBuilder;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public interface Universe extends Graph {

    /**
     * Retrieves the respective concrete element by its name;
     */
    Optional<Element> getElement(Name element);

    /**
     * Returns the type of the respective concrete element;
     */
    Optional<FrameworkElement> getTypeOfElement(Name element);

    /**
     * Registers a new element in this universe.
     */
    void register(Element element);

    Name UNIVERSE = Name.identifier("UNIVERSE");

    /**
     * A graph with no elements, i.e. the initial object in the category of graphs.
     */
    GraphImpl EMPTY = new GraphImpl(Name.identifier("EMPTY"),
            Collections.emptySet());
    /**
     * The name of the only node in the graph ONE_NODE.
     */
    Name ONE_NODE_THE_NODE = Name.identifier("0");
    /**
     * A graph with a single node, i.e. the terminal object in the category of sets.
     */
    GraphImpl ONE_NODE = new GraphImpl(Name.identifier("NODE"), Collections.singleton(Triple.node(ONE_NODE_THE_NODE)));

    /**
     * A graph with two nodes, i.e. the discrete diagram for products and coproducts.
     */
    GraphImpl PAIR = new GraphImpl(
            Name.identifier("PAIR"),
            Sets.newHashSet(
                    Triple.node(Name.identifier("0")),
                    Triple.node(Name.identifier("1"))
            ));


    Triple LOOP_THE_LOOP = Triple.edge(Name.identifier("0"), Name.identifier("00"), Name.identifier("0"));

    /**
     * A graph with a single node and a single arrow that is a loop, i.e.
     * the terminal object in the category of graphs.
     */
    GraphImpl LOOP = new GraphImpl(Name.identifier("LOOP"),
            Sets.newHashSet(
                    Triple.node(LOOP_THE_LOOP.getSource()),
                    Triple.node(LOOP_THE_LOOP.getTarget()),
                    LOOP_THE_LOOP
            ));



    Name ARROW_SRC_NAME = Name.identifier("0");
    Name ARROW_LBL_NAME = Name.identifier("01");
    Name ARROW_TRG_NAME = Name.identifier("1");
    Triple ARROW_THE_ARROW = Triple.edge(ARROW_SRC_NAME, ARROW_LBL_NAME, ARROW_TRG_NAME);

    /**
     * A graph with two nodes and an edge connecting them a.k.a as the "walking arrow".
     * It is also the characteristic diagram for the category of graphs since all other graphs can
     * be created by compositions (and renamings) of this diagram.
     */
    GraphImpl ARROW = new GraphImpl(Name.identifier("2"),
            Sets.newHashSet(
                    Triple.node(ARROW_SRC_NAME),
                    Triple.node(ARROW_TRG_NAME),
                    ARROW_THE_ARROW
            ));

    Triple CELL_LHS = Triple.edge(Name.identifier("0"), Name.identifier("010"), Name.identifier("1"));
    Triple CELL_RHS = Triple.edge(Name.identifier("0"), Name.identifier("011"), Name.identifier("1"));

    /**
     * A graph with two nodes and two arrows, which are parallel to each other,
     * i.e. the arity of the equality predicate.
     */
    GraphImpl CELL = new GraphImpl(Name.identifier("CELL"),
            Sets.newHashSet(
                    Triple.node(CELL_LHS.getSource()),
                    Triple.node(CELL_RHS.getTarget()),
                    CELL_LHS,
                    CELL_RHS
            ));

    Triple CYCLE_FWD = Triple.edge(Name.identifier("0"), Name.identifier("01"), Name.identifier("1"));
    Triple CYCLE_BWD = Triple.edge(Name.identifier("1"), Name.identifier("10"), Name.identifier("0"));

    /**
     * A graph with two nodes and two arrows which go in opposite directions,
     * i.e. a cycle of the length two.
     */
    GraphImpl CYCLE = new GraphImpl(Name.identifier("CYCLE"),
            Sets.newHashSet(
                    Triple.node(CYCLE_FWD.getSource()),
                    Triple.node(CYCLE_FWD.getTarget()),
                    CYCLE_FWD,
                    CYCLE_BWD
            ));

    Triple CHAIN_FST = Triple.edge(Name.identifier("0"), Name.identifier("01"), Name.identifier("1"));
    Triple CHAIN_SND = Triple.edge(Name.identifier("1"), Name.identifier("12"), Name.identifier("2"));
    /**
     * A graph comprising two edges forming a chain.
     */
    Graph CHAIN = new GraphImpl(Name.identifier("CHAIN"),
            Sets.newHashSet(
                    Triple.node(CHAIN_FST.getSource()),
                    Triple.node(CHAIN_FST.getTarget()),
                    Triple.node(CHAIN_SND.getTarget()),
                    CHAIN_FST,
                    CHAIN_SND
            ));

    Triple TRIANGLE_HYP = Triple.edge(Name.identifier("0"), Name.identifier("02"), Name.identifier("2"));

    /**
     * A graph with three nodes and three arrows forming a triangle,
     * i.e. the diagram of the composition predicate.
     */
    GraphImpl TRIANGLE = new GraphImpl(Name.identifier("TRIANGLE"),
            Sets.newHashSet(
                    Triple.node(CHAIN_FST.getSource()),
                    Triple.node(CHAIN_FST.getTarget()),
                    Triple.node(CHAIN_SND.getTarget()),
                    CHAIN_FST,
                    CHAIN_SND,
                    TRIANGLE_HYP
            )
    );

    Triple SPAN_LEFT_LEG = Triple.edge(Name.identifier("0"), Name.identifier("01"), Name.identifier("1"));
    Triple SPAN_RIGHT_LEG = Triple.edge(Name.identifier("0"), Name.identifier("02"), Name.identifier("2"));
    /**
     * A graph with three nodes and two arrows forming a span (pair of arrows with the same source),
     * i.e. the input diagram for binary pushout.
     */
    GraphImpl SPAN = new GraphImpl(Name.identifier("SPAN"),
            Sets.newHashSet(
                    Triple.node(SPAN_LEFT_LEG.getSource()),
                    Triple.node(SPAN_LEFT_LEG.getTarget()),
                    Triple.node(SPAN_RIGHT_LEG.getTarget()),
                    SPAN_LEFT_LEG,
                    SPAN_RIGHT_LEG
            )
    );

    Triple COSPAN_LEFT_LEG = Triple.edge(Name.identifier("1"), Name.identifier("10"), Name.identifier("0"));
    Triple COSPAN_RIGHT_LEG = Triple.edge(Name.identifier("2"), Name.identifier("20"), Name.identifier("0"));
    /**
     * A graph with three nodes and two arrows forming a cospan (pair of arrows with the same target),
     * i.e. the input diahram for a binary pullback.
     */
    GraphImpl COSPAN = new GraphImpl(Name.identifier("COSPAN"),
            Sets.newHashSet(
                    Triple.node(COSPAN_LEFT_LEG.getSource()),
                    Triple.node(COSPAN_LEFT_LEG.getTarget()),
                    Triple.node(COSPAN_RIGHT_LEG.getSource()),
                    COSPAN_LEFT_LEG,
                    COSPAN_RIGHT_LEG
            )
    );

    Triple INCIDENCE_TRIANGLE_HYP = Triple.edge(Name.identifier("1"), Name.identifier("12"), Name.identifier("2"));


    Graph INCIDENCE_TRIANGLE = new GraphImpl(Name.identifier("INCIDENCE_TRIANGLE"),
            Sets.newHashSet(
                    Triple.node(COSPAN_LEFT_LEG.getSource()),
                    Triple.node(COSPAN_LEFT_LEG.getTarget()),
                    Triple.node(COSPAN_RIGHT_LEG.getSource()),
                    COSPAN_LEFT_LEG,
                    COSPAN_RIGHT_LEG,
                    INCIDENCE_TRIANGLE_HYP
            )
    );

    /**
     * A graph with four nodes and four edges forming a square containing two parallel paths,
     * i.e. the shape of pushouts/pullbacks.
     */
    GraphImpl SQUARE = new GraphImpl(Name.identifier("SQUARE"),
            Sets.newHashSet(
                    Triple.node(Name.identifier("0")),
                    Triple.node(Name.identifier("1")),
                    Triple.node(Name.identifier("2")),
                    Triple.node(Name.identifier("3")),
                    Triple.edge(Name.identifier("0"), Name.identifier("01"), Name.identifier("1")),
                    Triple.edge(Name.identifier("0"), Name.identifier("02"), Name.identifier("2")),
                    Triple.edge(Name.identifier("1"), Name.identifier("13"), Name.identifier("3")),
                    Triple.edge(Name.identifier("2"), Name.identifier("23"), Name.identifier("3"))
            )
    );


}
