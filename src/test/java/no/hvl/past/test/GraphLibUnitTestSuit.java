package no.hvl.past.test;

import no.hvl.past.attributes.AttributesTest;
import no.hvl.past.graph.*;
import no.hvl.past.graph.matching.GraphMatchingTest;
import no.hvl.past.graph.operations.GraphOperationsTest;
import no.hvl.past.graph.predicates.GraphPredicateTest;
import no.hvl.past.names.NamesTest;
import no.hvl.past.names.PropositionalLogicTest;
import no.hvl.past.util.Multiplicity;
import no.hvl.past.util.MultiplicityTest;
import no.hvl.past.util.PartititionTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(
        Suite.class
)
@Suite.SuiteClasses({
        GraphTest.class,
        MorphismTest.class,
        InheritanceGraphTest.class,
        GraphBuildersTest.class,
        SketchTest.class,
        TripleTest.class,
        GraphPredicateTest.class,
        GraphMatchingTest.class,
        GraphOperationsTest.class,
        NamesTest.class,
        PropositionalLogicTest.class,
        AttributesTest.class,
        MultiplicityTest.class,
        PartititionTest.class
})
public class GraphLibUnitTestSuit {
}
