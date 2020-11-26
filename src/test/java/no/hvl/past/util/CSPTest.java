package no.hvl.past.util;

import com.google.common.collect.Sets;
import org.junit.Test;


import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class CSPTest {

    private enum Color {
        RED,
        GREEN,
        BLUE
    }

    private static class ColoredState implements CSPSolver.Variable<Color> {

        private final String name;

        private ColoredState(String name) {
            this.name = name;
        }

        @Override
        public Set<Color> possibleValues() {
            return Sets.newHashSet(Color.RED, Color.GREEN, Color.BLUE);
        }

        @Override
        public String toString() {
            return  name;
        }
    }

    private static class AdjacentStates implements CSPSolver.BinaryConstraint<Color> {
        private final ColoredState left;
        private final ColoredState right;

        public AdjacentStates(ColoredState left, ColoredState right) {
            this.left = left;
            this.right = right;
        }


        @Override
        public CSPSolver.Variable<Color> lhs() {
            return left;
        }

        @Override
        public CSPSolver.Variable<Color> rhs() {
            return right;
        }

        @Override
        public boolean satisfied(Color lhs, Color rhs) {
            return !lhs.equals(rhs);
        }
    }

    @Test
    public void testSimpleDistinctColoring() {
        ColoredState t = new ColoredState("Tasmania");
        ColoredState v = new ColoredState("Victoria");
        ColoredState nsw = new ColoredState("New South Wales");
        AdjacentStates c1 = new AdjacentStates(v, nsw);
        Set<Map<ColoredState, Color>> allSolutions = CSPSolver.createProblem(Sets.newHashSet(t, v, nsw), Sets.newHashSet(c1), Collections.singleton(new CSPSolver.AllDiffConstraint<Color>(Arrays.asList(nsw, t, v)))).backtrackAllSolutions();
        assertEquals(6, allSolutions.size());
    }

    @Test
    public void testAustraliaMapColoring() {
        ColoredState t = new ColoredState("Tasmania");
        ColoredState v = new ColoredState("Victoria");
        ColoredState nsw = new ColoredState("New South Wales");
        ColoredState q = new ColoredState("Queensland");
        ColoredState sa = new ColoredState("South Australia");
        ColoredState nt = new ColoredState("Northern Territory");
        ColoredState wa = new ColoredState("Western Australia");
        AdjacentStates c1 = new AdjacentStates(v, nsw);
        AdjacentStates c2 = new AdjacentStates(v, sa);
        AdjacentStates c3 = new AdjacentStates(nsw, sa);
        AdjacentStates c4 = new AdjacentStates(nsw, q);
        AdjacentStates c5 = new AdjacentStates(sa, q);
        AdjacentStates c6 = new AdjacentStates(nt, q);
        AdjacentStates c7 = new AdjacentStates(nt,sa);
        AdjacentStates c8 = new AdjacentStates(nt, wa);
        AdjacentStates c9 = new AdjacentStates(wa, sa);

        Map<ColoredState, Color> result = CSPSolver.createProblem(Sets.newHashSet(t, v, nsw, q, sa, nt, wa), Sets.newHashSet(c1, c2, c3, c4, c5, c6, c7, c8, c9)).backTrackOneSolution();
        assertFalse(result.isEmpty()); // There is a solution

        Set<Map<ColoredState, Color>> allSolutions = CSPSolver.createProblem(Sets.newHashSet(t, v, nsw, q, sa, nt, wa), Sets.newHashSet(c1, c2, c3, c4, c5, c6, c7, c8, c9)).backtrackAllSolutions();
        assertFalse(allSolutions.isEmpty());
        assertTrue(allSolutions.contains(result)); // The previously found solution is part of all solutions.
        assertEquals(18, allSolutions.size()); // There are 18 solutions.

    }
}
