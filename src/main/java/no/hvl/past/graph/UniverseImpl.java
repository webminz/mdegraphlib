package no.hvl.past.graph;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

import java.util.*;
import java.util.stream.Stream;

/**
 * Represents the mathematical universe containing all elements, i.e.
 * the category of directed multigraphs.
 */
public class UniverseImpl implements Graph, Universe {

    public static final Universe EMPTY = new Universe() {
        @Override
        public Optional<Element> getElement(Name element) {
            return Optional.empty();
        }

        @Override
        public Optional<FrameworkElement> getTypeOfElement(Name element) {
            return Optional.empty();
        }

        @Override
        public Stream<Triple> elements() {
            return Stream.empty();
        }

        @Override
        public Name getName() {
            return UNIVERSE;
        }

        @Override
        public boolean isInfinite() {
            return true;
        }

        @Override
        public void register(Element element) {
        }
    };

    private final Map<Name, Element> globalRegistry;
    private final Universe next;

    public UniverseImpl(Universe next) {
        this.globalRegistry = new HashMap<>();
        this.next = next;
    }

    @Override
    public Stream<Triple> elements() {
        return null; // TODO
    }

    @Override
    public boolean contains(Triple triple) {
        return false; // TODO
    }

    @Override
    public boolean mentions(Name name) {
        return this.globalRegistry.containsKey(name);
    }

    @Override
    public Name getName() {
        return UNIVERSE;
    }

    @Override
    public boolean isInfinite() {
        return true;
    }

    @Override
    public Optional<Element> getElement(Name element) {
        return null; //TODO
    }

    @Override
    public Optional<FrameworkElement> getTypeOfElement(Name element) {
        return null; //TODO
    }

    @Override
    public void register(Element element) {
        // TODO
    }
}
