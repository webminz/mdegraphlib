package no.hvl.past.searching;


import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

import no.hvl.past.util.CSPSolver;
import no.hvl.past.util.ShouldNotHappenException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Searches for matches between two graphs by translating
 * it into a CSP and using the basic builtin solver.
 */
public class GraphMatcher {

    private static abstract class GraphMatchingVar implements CSPSolver.Variable<Triple> {
        private final Name name;
        private final Set<Triple> candidates;

        GraphMatchingVar(Name name, Set<Triple> candidates) {
            this.name = name;
            this.candidates = candidates;
        }

        @Override
        public Set<Triple> possibleValues() {
            return candidates;
        }

        public Name getName() {
            return name;
        }
    }

    private static class NodeVar extends GraphMatchingVar {
        NodeVar(Name name, Set<Triple> candidates) {
            super(name, candidates);
        }
    }

    private static class EdgeVar extends GraphMatchingVar {
        EdgeVar(Name name, Set<Triple> candidates) {
            super(name, candidates);
        }
    }

    private static abstract class IncidenceConstraint implements CSPSolver.BinaryConstraint<Triple> {
        private final EdgeVar edgeVar;
        private final NodeVar nodeVar;
        final Graph targetGraph;

        IncidenceConstraint(EdgeVar edgeVar, NodeVar nodeVar, Graph targetGraph) {
            this.edgeVar = edgeVar;
            this.nodeVar = nodeVar;
            this.targetGraph = targetGraph;
        }

        @Override
        public CSPSolver.Variable<Triple> lhs() {
            return edgeVar;
        }

        @Override
        public CSPSolver.Variable<Triple> rhs() {
            return nodeVar;
        }


    }

    private static class SrcIncidence extends IncidenceConstraint {

        SrcIncidence(EdgeVar edgeVar, NodeVar nodeVar, Graph targetGraph) {
            super(edgeVar, nodeVar, targetGraph);
        }

        @Override
        public boolean satisfied(Triple lhs, Triple rhs) {
            return lhs.getSource().equals(rhs.getLabel());
        }
    }

    private static class TrgIncidence extends IncidenceConstraint {
        TrgIncidence(EdgeVar edgeVar, NodeVar nodeVar, Graph targetGraph) {
            super(edgeVar, nodeVar, targetGraph);
        }

        @Override
        public boolean satisfied(Triple lhs, Triple rhs) {
            return lhs.getTarget().equals(rhs.getLabel());
        }
    }

    private CSPSolver<Triple, GraphMatchingVar> turnIntoCSPProblem(GraphMorphism morphism) {
        Map<Name, GraphMatchingVar> variables = new HashMap<>();
        Set<CSPSolver.BinaryConstraint<Triple>> constraints = new HashSet<>();
        Set<CSPSolver.AllDiffConstraint<Triple>> allDiffConstraints = new HashSet<>();
        morphism.codomain().elements().filter(Triple::isNode).forEach(t ->
                variables.put(t.getLabel(), new NodeVar(t.getLabel(), morphism.preimage(t).map(target -> target.mapName(Name::secondPart)).collect(Collectors.toSet()))));
        morphism.codomain().elements().filter(Triple::isEddge).forEach(t -> {
            NodeVar sourceVar = (NodeVar) variables.get(t.getSource());
            NodeVar targetVar = (NodeVar) variables.get(t.getTarget());
            EdgeVar edgeVar = new EdgeVar(t.getLabel(), morphism.preimage(t).map(target -> target.mapName(Name::secondPart)).collect(Collectors.toSet()));
            constraints.add(new SrcIncidence(edgeVar, sourceVar, morphism.domain()));
            constraints.add(new TrgIncidence(edgeVar, targetVar, morphism.domain()));
            variables.put(t.getLabel(), edgeVar);
        });
        if (injectiveOnly) {
            allDiffConstraints.add(new CSPSolver.AllDiffConstraint<>(variables.values().stream().filter(v -> v instanceof NodeVar).collect(Collectors.toList())));
            allDiffConstraints.add(new CSPSolver.AllDiffConstraint<>(variables.values().stream().filter(v -> v instanceof EdgeVar).collect(Collectors.toList())));
        }
        return CSPSolver.createProblem(new HashSet<>(variables.values()), constraints, allDiffConstraints);
    }

    private CSPSolver<Triple, GraphMatchingVar> turnIntoCSPProblem(Graph pattern, Graph in) {
        Map<Name, GraphMatchingVar> variables = new HashMap<>();
        Set<CSPSolver.BinaryConstraint<Triple>> constraints = new HashSet<>();
        Set<CSPSolver.AllDiffConstraint<Triple>> allDiffConstraints = new HashSet<>();
        pattern.elements().filter(Triple::isNode).forEach(t ->
                variables.put(t.getLabel(), new NodeVar(t.getLabel(), in.elements().filter(Triple::isNode).collect(Collectors.toSet()))));
        pattern.elements().filter(Triple::isEddge).forEach(t -> {
            NodeVar sourceVar = (NodeVar) variables.get(t.getSource());
            NodeVar targetVar = (NodeVar) variables.get(t.getTarget());
            EdgeVar edgeVar = new EdgeVar(t.getLabel(), in.elements().filter(Triple::isEddge).collect(Collectors.toSet()));
            constraints.add(new SrcIncidence(edgeVar, sourceVar, in));
            constraints.add(new TrgIncidence(edgeVar, targetVar, in));
            variables.put(t.getLabel(), edgeVar);
        });
        if (injectiveOnly) {
            allDiffConstraints.add(new CSPSolver.AllDiffConstraint<>(variables.values().stream().filter(v -> v instanceof NodeVar).collect(Collectors.toList())));
            allDiffConstraints.add(new CSPSolver.AllDiffConstraint<>(variables.values().stream().filter(v -> v instanceof EdgeVar).collect(Collectors.toList())));
        }
        return CSPSolver.createProblem(new HashSet<>(variables.values()), constraints, allDiffConstraints);
    }

    private GraphMorphism turnIntoSolution(Map<GraphMatchingVar, Triple> cspSolution, Graph domain, Graph codomain) {
        GraphBuilders builder = new GraphBuilders().domain(domain).codomain(codomain);
        cspSolution.entrySet().stream().forEach(kv -> builder.map(kv.getKey().name, kv.getValue().getLabel()));
        try {
            return builder.morphism(nextName()).getResult(GraphMorphism.class);
        } catch (GraphError graphError) {
            throw new ShouldNotHappenException(getClass(), "turnIntoSolution", graphError);
        }
    }

    private Name nextName() {
        return Name.identifier("match" + (counter++));
    }

    private final boolean injectiveOnly;
    private int counter = 0;

    public GraphMatcher(boolean injectiveOnly) {
        this.injectiveOnly = injectiveOnly;
    }

    public Set<GraphMorphism> allMatches(Graph patternGraph, Graph hostGraph) {
        return turnIntoCSPProblem(patternGraph, hostGraph)
                .backtrackAllSolutions()
                .stream()
                .map(m -> this.turnIntoSolution(m, patternGraph, hostGraph)).collect(Collectors.toSet());
    }

    public GraphMorphism randomMatch(Graph patternGraph, Graph hostGraph) {
        return turnIntoSolution(this.turnIntoCSPProblem(patternGraph, hostGraph).backTrackOneSolution(), patternGraph, hostGraph);
    }

    public boolean existMatch(Graph patternGraph, Graph hostGraph) {
        return !this.turnIntoCSPProblem(patternGraph, hostGraph).backTrackOneSolution().isEmpty();
    }

    public List<GraphMorphism> allTypedMatches(GraphMorphism typedPatternGraph, GraphMorphism typedHostGraph) {
        if (!typedHostGraph.codomain().equals(typedPatternGraph.codomain())) {
            return Collections.emptyList();
        }
        GraphMorphism relevant = typedPatternGraph.pullback(typedHostGraph, typedHostGraph.domain().getName()).getFirst();
        return turnIntoCSPProblem(relevant)
                .backtrackAllSolutions()
                .stream()
                .map(m -> this.turnIntoSolution(m, typedPatternGraph.domain(), typedHostGraph.domain()))
                .collect(Collectors.toList());
    }



}
