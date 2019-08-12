package no.hvl.past.graph.techspace;

import no.hvl.past.graph.modification.Diff;

public interface IncrementalUpdateSupportingTechnologicalSpace extends TechnologicalSpace {

    void applyUpdate(String location, Diff difference);


}
