package no.hvl.past.plotting;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractDOTWriter {

    private final Map<String, String> properties;
    private final int indent;

    public AbstractDOTWriter(int indent) {
        this.indent = indent;
        this.properties = new HashMap<>();
    }

    public void setProperty(String key, String value) {
        this.properties.put(key, value);
    }

    public void newLine(BufferedWriter writer) throws IOException {
        writer.newLine();
        switch (indent) {
            case 1:
                writer.append("   ");
                break;
            case 2:
                writer.append("      ");
                break;
            case 3:
                writer.append("         ");
                break;
            case 4:
                writer.append("            ");
                break;
            case 0:
            default:
                break;
        }
    }

    public void appendProperties(BufferedWriter writer) throws IOException {
        for (Map.Entry<String, String> property : this.properties.entrySet()) {
            writer.append(property.getKey());
            writer.append("=\"");
            writer.append(property.getValue());
            writer.append("\" ");
        }
    }

    public int getIndent() {
        return indent;
    }

    public abstract void serialize(BufferedWriter writer) throws IOException;



}
