package no.hvl.past.graph;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import no.hvl.past.searching.StateSpace;
import no.hvl.past.util.*;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A straightforward concrete implementation of an AbstractGraph.
 * It is simply comprised of set of all triples.
 */
public class GraphImpl implements StateSpace<Name, Triple>, Graph, Iterable<Triple> {

    private final Name name;
    private final Map<Name, Triple> elements;

    public GraphImpl(Name name, Set<Triple> elements) {
        this.name = name;
        this.elements = new HashMap<>();
        for (Triple t : elements) {
            this.elements.put(t.getLabel(), t);
        }
    }

    @Override
    public Optional<Triple> get(Name label) {
        if (!elements.containsKey(label)) {
            return Optional.empty();
        }
        return Optional.of(this.elements.get(label));
    }

    @Override
    public Name getName() {
        return name;
    }

    @Override
    public Stream<Triple> elements() {
        return this.elements.values().stream();
    }

    @Override
    public boolean mentions(Name name) {
        return this.elements.containsKey(name);
    }

    @Override
    public boolean contains(Triple triple) {
        return this.elements.containsValue(triple);
    }

    @Override
    public Iterator<Triple> iterator() {
        return elements.values().iterator();
    }

    @Override
    public Spliterator<Triple> spliterator() {
        return this.elements.values().spliterator();
    }

    public Set<Triple> getElements() {
        return elements().collect(Collectors.toSet());
    }

    public Set<Triple> getEdges() {
        return edges().collect(Collectors.toSet());
    }

    public Set<Name> getNodes() {
        return nodes().collect(Collectors.toSet());
    }


    // Constructions


    @Override
    public GraphImpl unprefix() {
        return new GraphImpl(name, elements().map(t -> t.unprefix(getName())).collect(Collectors.toSet()));
    }

    @Override
    public GraphImpl prefix() {
        return new GraphImpl(name, elements().map(t -> t.prefix(getName())).collect(Collectors.toSet()));
    }

    @Override
    public Graph sum(Graph other) {
        if (other instanceof GraphImpl) {
            GraphImpl otherGraph = (GraphImpl) other;
            Set<Triple> elements = new HashSet<>();
            elements.addAll(this.prefix().elements.values());
            elements.addAll(otherGraph.prefix().elements.values());
            return new GraphImpl(this.name.sum(otherGraph.name), elements);
        }
        return new GraphUnion(Arrays.asList(this, other), this.getName().sum(other.getName()));
    }

    @Override
    public Graph multiSum(List<Graph> others) {
        if (others.stream().allMatch(g -> g instanceof GraphImpl)) {
            Set<Triple> elements = new HashSet<>();
            elements.addAll(this.prefix().elements.values());
            for (Graph g : others) {
                GraphImpl graph = (GraphImpl) g;
                elements.addAll(graph.prefix().elements.values());
            }
            return new GraphImpl(this.name.mergeWith(others.stream().map(Graph::getName).collect(Collectors.toList())), elements);
        }
        List<Graph> all = new ArrayList<>();
        all.add(this);
        all.addAll(others);
        return new GraphUnion(all, this.getName().mergeWith(others.stream().map(Graph::getName).collect(Collectors.toList())));
    }

    @Override
    public boolean isInfinite() {
        return false;
    }


    // Factory methods

    public static GraphImpl create(Name name, Set<Triple> elements) throws GraphError {
        GraphImpl result = new GraphImpl(name, elements);
        Set<Triple> dangling = result.danglingEdges().collect(Collectors.toSet());
        if (!dangling.isEmpty()) {
            GraphError ex = new GraphError();
            for (Triple d : dangling) {
                ex.addError(new GraphError.DanglingEdge(d, true, true));
            }
            throw ex;

        }
        return result;
    }

    private static <E> GraphImpl hasseDiagrammInternal(
            Name resultGraphName,
            Collection<E> elements,
            BiPredicate<E, E> canAddEdge,
            Function<E, Name> nameGiver,
            boolean withTransitive) {
        Set<Triple> preResult = new HashSet<>();
        for (E e1 : elements) {
            for (E e2 : elements) {
                if (canAddEdge.test(e1, e2)) {
                    preResult.add(new Triple(nameGiver.apply(e1), nameGiver.apply(e1).subTypeOf(nameGiver.apply(e2)), nameGiver.apply(e2)));
                }
            }
        }
        Set<Triple> result = new HashSet<>();
        elements.stream().map(e -> Triple.node(nameGiver.apply(e))).forEach(result::add);
        if (withTransitive) {
            result.addAll(preResult);
        } else {
            preResult.stream()
                    .filter(t ->
                            elements.stream()
                                    .map(e -> nameGiver.apply(e))
                                    .filter(n ->!n.equals(t.getSource()))
                                    .filter(n ->!n.equals(t.getTarget()))
                                    .filter(n -> preResult.stream().filter(t2 -> !t2.equals(t)).anyMatch(t2 -> t2.getSource().equals(t.getSource()) && t2.getTarget().equals(n)))
                                    .noneMatch(n -> preResult.stream().filter(t2 -> !t2.equals(t)).anyMatch(t2 -> t2.getSource().equals(n) && t2.getTarget().equals(t.getTarget())))
                    )
                    .forEach(result::add);
        }
        return new GraphImpl(resultGraphName, result);
    }

    public static <E extends Comparable<E>> GraphImpl hasseDiagramm(
            Name name,
            Collection<E> elements,
            Function<E, Name> nameGiver,
            boolean withTransitive) {
        return hasseDiagrammInternal(name, elements, (e1, e2) -> e1.compareTo(e2) < 0, nameGiver, withTransitive);
    }

    public static <E> GraphImpl hasseDiagramm(
            Name graphName,
            Collection<E> elements,
            ProperComparator<E> comparator,
            Function<E, Name> nameGiver,
            boolean withTransitive) {
        return hasseDiagrammInternal(graphName, elements, (e1, e2) -> comparator.cmp(e1, e2).equals(ProperComparator.CompareResult.LESS_THAN), nameGiver, withTransitive);
    }

    public static GraphImpl materialize(Graph graph) {
        Name name = graph.getName();
        Set<Triple> elements = new HashSet<>();
        graph.elements().forEach(elements::add);
        return new GraphImpl(name, elements);
    }

}
