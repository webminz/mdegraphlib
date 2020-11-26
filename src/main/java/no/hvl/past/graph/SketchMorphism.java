package no.hvl.past.graph;

import java.util.Optional;

public interface SketchMorphism extends Element {

    Sketch domain();

    Sketch codomain();

    Optional<Diagram> mapDiagram(Diagram diagram);

    @Override
    default FrameworkElement elementType() {
        return FrameworkElement.SKETCH_MORPHISM;
    }
}
