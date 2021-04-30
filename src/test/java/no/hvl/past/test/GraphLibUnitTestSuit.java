package no.hvl.past.test;

import no.hvl.past.graph.GraphTest;
import no.hvl.past.graph.MorphismTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(
        Suite.class
)
@Suite.SuiteClasses({
        GraphTest.class,
        MorphismTest.class
})
public class GraphLibUnitTestSuit {
}
