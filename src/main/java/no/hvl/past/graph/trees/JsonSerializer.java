package no.hvl.past.graph.trees;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import no.hvl.past.attributes.ErrorValue;
import no.hvl.past.attributes.FloatValue;
import no.hvl.past.attributes.IntegerValue;
import no.hvl.past.attributes.StringValue;
import no.hvl.past.names.Name;
import no.hvl.past.names.PrintingStrategy;
import no.hvl.past.names.Value;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JsonSerializer {

    private final JsonFactory factory;

    public JsonSerializer(JsonFactory factory) {
        this.factory = factory;
    }

    public String serialize(Tree tree, Function<Name, String> displayNames) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        JsonGenerator generator = this.factory.createGenerator(bos);
        serialize(generator, tree, displayNames);
        return bos.toString("UTF-8");
    }

    public void serialize(File target, Tree tree, Function<Name, String> displayNames) throws IOException {
        JsonGenerator generator = this.factory.createGenerator(target, JsonEncoding.UTF8);
        serialize(generator, tree, displayNames);
    }

    public void serialize(OutputStream target, Tree tree, Function<Name, String> displayNames) throws IOException {
        JsonGenerator generator = this.factory.createGenerator(target);
        serialize(generator, tree, displayNames);
    }

    private static void serialize(JsonGenerator generator, Tree instance, Function<Name, String> displayNames) throws IOException {
        serialize(generator, instance.root(), displayNames);
    }

    private static void serialize(JsonGenerator generator, Node node, Function<Name, String> displaynames) throws IOException {
        if (node.isLeaf()) {
            if (node.elementName().isValue()) {
                printValue(generator, (Value) node.elementName());
            }
        } else {
            generator.writeStartObject();
            Name currentKey = Name.anonymousIdentifier();
            boolean startList = false;
            for (ChildrenRelation child : node.children().collect(Collectors.toList())) {
                if (!child.key().equals(currentKey)) {
                    if (startList) {
                        generator.writeEndArray();
                        startList = false;
                    }
                    generator.writeFieldName(displaynames.apply(child.key()));
                    if (child.isCollection()) {
                        generator.writeStartArray();
                        startList = true;
                    }
                    currentKey = child.key();
                    serialize(generator, child.child(), displaynames);
                }
            }
            generator.writeEndObject();
        }
    }

    private static void printValue(JsonGenerator generator, Value elementName) throws IOException {
        if (elementName instanceof IntegerValue) {
            generator.writeNumber(((IntegerValue)elementName).getIntegerValue());
        } else if (elementName instanceof FloatValue) {
            generator.writeNumber(((FloatValue)elementName).getFloatValue());
        } else if (elementName instanceof StringValue) {
            generator.writeString(((StringValue) elementName).getStringValue());
        } else if (elementName.equals(ErrorValue.INSTANCE)) {
            generator.writeNull();
        } else {
            generator.writeString(elementName.print(PrintingStrategy.IGNORE_PREFIX));
        }
    }

}
