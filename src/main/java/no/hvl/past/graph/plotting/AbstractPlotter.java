package no.hvl.past.graph.plotting;

import no.hvl.past.names.PrintingStrategy;
import no.hvl.past.util.StringUtils;

import java.io.BufferedWriter;
import java.io.IOException;

public abstract class AbstractPlotter {

    private final int indent;
    private final PrintingStrategy strategy;
    private String lineColor;
    private String fillColor;
    private String textColor;

    AbstractPlotter(int indent, PrintingStrategy strategy) {
        this.indent = indent;
        this.strategy = strategy;
        this.lineColor = "black";
        this.fillColor = "white";
        this.textColor = "black";
    }

    public PrintingStrategy getStrategy() {
        return strategy;
    }

    void newLine(BufferedWriter writer) throws IOException {
        writer.newLine();
        writer.append(StringUtils.produceIndentation(indent));
    }

    int getIndent() {
        return indent;
    }

    public abstract void serialize(BufferedWriter writer) throws IOException;


    public String getLineColor() {
        return lineColor;
    }

    public void setLineColor(String lineColor) {
        this.lineColor = lineColor;
    }

    public String getFillColor() {
        return fillColor;
    }

    public void setFillColor(String fillColor) {
        this.fillColor = fillColor;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }
}
