package no.hvl.past.graph.matching;


import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import no.hvl.past.searching.CSPSolver;

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

        morphism.codomain()
                .nodes()
                .forEach(n -> variables.put(n, new NodeVar(
                        n,
                        morphism.allNodeInstances(n)
                                .map(Triple::node)
                                .collect(Collectors.toSet()))));

        morphism.codomain()
                .edges()
                .forEach(t -> {
                    NodeVar sourceVar = (NodeVar) variables.get(t.getSource());
                    NodeVar targetVar = (NodeVar) variables.get(t.getTarget());

                    EdgeVar edgeVar = new EdgeVar(
                            t.getLabel(),
                            morphism.allInstances(t)
                                    .collect(Collectors.toSet()));

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

    private GraphMorphism turnIntoSolution(Map<GraphMatchingVar, Triple> cspSolution, Graph domain, Graph codomain) throws GraphError {
        GraphBuilders builder = new GraphBuilders(executionContext.universe(), false, true)
                .domain(domain)
                .codomain(codomain)
                .setRegisterResults(false);
        cspSolution.entrySet().stream().forEach(kv -> builder.map(kv.getKey().name, kv.getValue().getLabel()));
        return builder.morphism(nextName()).getResult(GraphMorphism.class);

    }

    private Name nextName() {
        return Name.identifier("match" + (counter++));
    }

    private final boolean injectiveOnly;
    private int counter = 0; // TODO replace better with something in the execution context
    private final ExecutionContext executionContext;

    public GraphMatcher(ExecutionContext executionContext, boolean injectiveOnly) {
        this.injectiveOnly = injectiveOnly;
        this.executionContext = executionContext;
    }

    public Set<GraphMorphism> allMatches(Graph patternGraph, Graph hostGraph) throws GraphError {
        Set<GraphMorphism> result = new HashSet<>();
        for (Map<GraphMatchingVar, Triple> btResult : turnIntoCSPProblem(patternGraph, hostGraph)
                .backtrackAllSolutions()) {
            result.add(turnIntoSolution(btResult, patternGraph, hostGraph));
        }
        return result;
    }

    public GraphMorphism randomMatch(Graph patternGraph, Graph hostGraph) throws GraphError {
        return turnIntoSolution(this.turnIntoCSPProblem(patternGraph, hostGraph).backTrackOneSolution(), patternGraph, hostGraph);
    }

    public boolean existMatch(Graph patternGraph, Graph hostGraph) {
        return !this.turnIntoCSPProblem(patternGraph, hostGraph).backTrackOneSolution().isEmpty();
    }

    public List<GraphMorphism> allTypedMatches(GraphMorphism typedPatternGraph, GraphMorphism typedHostGraph) throws GraphError {
        if (!typedHostGraph.codomain().equals(typedPatternGraph.codomain())) {
            return Collections.emptyList();
        }
        GraphMorphism relevant = typedPatternGraph.pullback(typedHostGraph).getFirst();

        List<GraphMorphism> result = new ArrayList<>();

        for (Map<GraphMatchingVar, Triple> btResult : turnIntoCSPProblem(relevant).backtrackAllSolutions()) {
            result.add(turnIntoSolution(btResult, typedHostGraph.domain(), typedHostGraph.domain()));
        }
        return result;

    }



}
