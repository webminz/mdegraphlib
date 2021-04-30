package no.hvl.past.graph.plotting.dm;

import java.io.IOException;
import java.io.Writer;

public abstract class PlotElement {

    public abstract void writePlantUML(Writer writer) throws IOException;

}
