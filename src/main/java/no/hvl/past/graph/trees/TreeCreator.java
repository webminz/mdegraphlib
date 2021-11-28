package no.hvl.past.graph.trees;

import com.fasterxml.jackson.core.JsonGenerator;
import no.hvl.past.attributes.BoolValue;
import no.hvl.past.attributes.FloatValue;
import no.hvl.past.attributes.IntegerValue;
import no.hvl.past.attributes.StringValue;
import no.hvl.past.names.Name;
import no.hvl.past.util.StringUtils;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Stack;

public interface TreeCreator {

    void mkRoot() throws IOException;

    void mkForrest() throws IOException;

    void startBranch(String key, boolean isCollection) throws IOException;

    void endBranch() throws IOException;

    void startComplexChild() throws IOException;

    void endComplexChild() throws IOException;

    void empty() throws IOException;

    void simpleChild(Name value) throws IOException;

    void endRoot() throws IOException;

    void endForrest() throws IOException;

    public static class JsonGeneratorTreeCreator implements TreeCreator {

        private final JsonGenerator generator;
        private final PrintStream debugLogger;
        private int nestingLevel = 0;
        private Stack<Boolean> currentIsCollection = new Stack<>();

        public JsonGeneratorTreeCreator(JsonGenerator generator, PrintStream debugLogger) {
            this.generator = generator;
            this.debugLogger = debugLogger;
        }

        public JsonGeneratorTreeCreator(JsonGenerator generator) {
            this.generator = generator;
            this.debugLogger = null;
        }

        @Override
        public void mkRoot() throws IOException {
            generator.writeStartObject();
            if (debugLogger != null) {
                debugLogger.println("$STARTROOT:");
                debugLogger.println("{");
            }
            nestingLevel++;
        }

        @Override
        public void mkForrest() throws IOException {
            generator.writeStartArray();
            if (debugLogger != null) {
                debugLogger.println("$STARTFORREST:");
                debugLogger.println("[");
            }
        }

        @Override
        public void startBranch(String key, boolean isCollection) throws IOException {
            generator.writeFieldName(key);
            if (isCollection) {
                generator.writeStartArray();
            }
            this.currentIsCollection.push(isCollection);
            if (debugLogger != null) {
                debugLogger.print(StringUtils.produceIndentation(nestingLevel) + key + " : " + (isCollection ? "[" : ""));
            }
            nestingLevel++;

        }

        @Override
        public void endBranch() throws IOException {
            if (currentIsCollection.peek()) {
                if (debugLogger != null) {
                    debugLogger.println(StringUtils.produceIndentation(nestingLevel) + "]");
                }
                generator.writeEndArray();
            }
            currentIsCollection.pop();
            nestingLevel--;
        }

        @Override
        public void startComplexChild() throws IOException {
            generator.writeStartObject();
            if (debugLogger != null) {
                debugLogger.println(StringUtils.produceIndentation(nestingLevel) + "{");
            }
        }

        @Override
        public void endComplexChild() throws IOException {
            generator.writeEndObject();
            if (debugLogger != null) {
                debugLogger.println(StringUtils.produceIndentation(nestingLevel) + "}");
            }
        }

        @Override
        public void empty() throws IOException {
            if (!currentIsCollection.peek()) {
                generator.writeNull();
                if (debugLogger != null) {
                    debugLogger.println("null");
                }
            }
        }

        @Override
        public void simpleChild(Name value) throws IOException {
            if (value.isValue()) {
                if (value instanceof IntegerValue) {
                    generator.writeNumber(((IntegerValue) value).getIntegerValue());
                } else if (value instanceof BoolValue) {
                    generator.writeBoolean(((BoolValue) value).isTrue());
                } else if (value instanceof FloatValue) {
                    generator.writeNumber(((FloatValue) value).getFloatValue());
                } else if (value instanceof StringValue) {
                    generator.writeString(((StringValue) value).getStringValue());
                } else {
                    generator.writeString(value.printRaw());
                }
            } else {
                generator.writeString(value.printRaw());
            }
            if (debugLogger != null) {
                debugLogger.println(value.printRaw());
            }
        }

        @Override
        public void endRoot() throws IOException {
            generator.writeEndObject();
            if (debugLogger != null) {
                debugLogger.println("}");
                debugLogger.println("$ENDROOT:");
            }
        }

        @Override
        public void endForrest() throws IOException {
            generator.writeEndArray();
            if (debugLogger != null) {
                debugLogger.println("]");
                debugLogger.println("$ENDFORREST:");
            }
        }
    }





}
