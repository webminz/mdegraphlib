package no.hvl.past.graph;

import no.hvl.past.names.Name;

import java.util.stream.Stream;

public abstract class MonicMorphism extends AbstractModification {


    public MonicMorphism(
            Name name,
            Graph base,
            Name resultName,
            boolean codomainDerived) {
        super(name, base, resultName, codomainDerived);
    }

    @Override
    public boolean isInjective() {
        return true;
    }


}
