package no.hvl.past.graph;

import no.hvl.past.names.Name;

import java.util.Map;
import java.util.Optional;

public class DynamicDiagramGraphInstantiation implements SketchMorphism {

    private static abstract class SourceState {

    }

    private static class Initial extends SourceState {

    }


    private final Name name;
    private final DiagrammaticGraph target;
    private final Graph impartialSource;
    private final Map<Name, Name> typings;
    private SourceState state;

    public DynamicDiagramGraphInstantiation(
            Name name,
            DiagrammaticGraph target,
            Graph impartialSource,
            Map<Name, Name> typings) {
        this.name = name;
        this.target = target;
        this.impartialSource = impartialSource;
        this.typings = typings;
    }

    private Sketch calculateSource() {
        return null; // TODO tomrrow
    }

    @Override
    public Sketch domain() {
        return null;
    }

    @Override
    public Sketch codomain() {
        return null;
    }

    @Override
    public Optional<Diagram> mapDiagram(Diagram diagram) {
        return Optional.empty();
    }

    @Override
    public Name getName() {
        return null;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public boolean verify() {
        return false;
    }
}
