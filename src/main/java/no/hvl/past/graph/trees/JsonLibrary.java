package no.hvl.past.graph.trees;

import com.fasterxml.jackson.core.*;
import no.hvl.past.attributes.*;
import no.hvl.past.graph.Graph;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import no.hvl.past.util.Pair;

import java.io.*;
import java.util.*;
import java.util.function.Function;

public class JsonLibrary {

    abstract static class AbstractJsonReaderConfig extends GenericTreeLibrary.AbstractReaderConfig {

        AbstractJsonReaderConfig() {
            this.bundleAttributeName = Name.identifier("data");
            this.parsingStrategy = Name::identifier;
        }

        protected Name idAttribute;
        protected Name typeAttribute;
        protected Function<String, Name> parsingStrategy;
        protected Name bundleAttributeName;

        public AbstractJsonReaderConfig detectObjectIDByAtt(Name idAttribute) {
            this.idAttribute = idAttribute;
            return this;
        }

        public AbstractJsonReaderConfig detectTypeByAtt(Name typeAttribute) {
            this.typeAttribute = typeAttribute;
            return this;
        }

        public AbstractJsonReaderConfig nameParsingStrategy(Function<String, Name> strategy) {
            this.parsingStrategy = strategy;
            return this;
        }

        public AbstractJsonReaderConfig disableBundleWrapper() {
            this.bundleAttributeName = null;
            return this;
        }

        public AbstractJsonReaderConfig enableBundleWrapper(Name bundleAttributeName) {
            this.bundleAttributeName = bundleAttributeName;
            return this;
        }

    }

    public static class ReaderConfig extends AbstractJsonReaderConfig {

        public ReaderConfig() {
            super();
        }

        @Override
        public ReaderConfig detectObjectIDByAtt(Name idAttribute) {
            super.detectObjectIDByAtt(idAttribute);
            return this;
        }

        @Override
        public ReaderConfig detectTypeByAtt(Name typeAttribute) {
            super.detectTypeByAtt(typeAttribute);
            return this;
        }

        @Override
        public ReaderConfig disableBundleWrapper() {
            super.disableBundleWrapper();
            return this;
        }

        @Override
        public ReaderConfig enableBundleWrapper(Name bundleAttributeName) {
            super.enableBundleWrapper(bundleAttributeName);
            return this;
        }

        @Override
        public ReaderConfig nameParsingStrategy(Function<String, Name> strategy) {
            super.nameParsingStrategy(strategy);
            return this;
        }

        @Override
        public ReaderConfig treeName(Name treeName) {
            super.treeName(treeName);
            return this;
        }

        @Override
        public ReaderConfig rootName(Name rootName) {
            super.rootName(rootName);
            return this;
        }


        public TreeEmitter input(File file) throws IOException {
            this.treeName(Name.uri(file.toURI().toASCIIString()));
            return input(new FileInputStream(file));
        }

        public TreeEmitter input(String input) throws IOException {
            return input(new ByteArrayInputStream(input.getBytes(this.encoding)));
        }

        public TreeEmitter input(InputStream inputStream) throws IOException {
            if (treeName == null) {
                this.treeName(Name.anonymousIdentifier());
            }
            return new Reader(idAttribute, typeAttribute, parsingStrategy,
                    rootName, bundleAttributeName,
                    // TODO V.1.0.0: consider charset
                    JsonLibrary.getInstance().jsonFactory.createParser(inputStream),
                    treeName, null);
        }
    }


    static abstract class AbstractJsonReader {

        protected final Name idAttribute;
        protected final Name typeAttribute;
        protected final Function<String, Name> parsingStrategy;
        protected final Pair<Graph, TreeTypeLibrary> typing;
        protected final Name rootName;
        protected final Name bundleAttributeName;
        protected final JsonParser parser;
        protected final Name treeName;

        AbstractJsonReader(
                Name idAttribute,
                Name typeAttribute,
                Function<String, Name> parsingStrategy,
                Pair<Graph, TreeTypeLibrary> typing,
                Name rootName,
                Name bundleAttributeName,
                JsonParser parser,
                Name treeName) {
            this.idAttribute = idAttribute;
            this.typeAttribute = typeAttribute;
            this.parsingStrategy = parsingStrategy;
            this.typing = typing;
            this.rootName = rootName;
            this.bundleAttributeName = bundleAttributeName;
            this.parser = parser;
            this.treeName = treeName;
        }

        protected void parse(TreeReceiver creator, boolean proceedAfterwards) throws Exception {
            JsonToken jsonToken = parser.nextToken();
            if (jsonToken == null) {
                return;
            }
            if (jsonToken == JsonToken.START_ARRAY) {
                if (bundleAttributeName != null) {
                    creator.startRoot(rootName);
                    creator.startBranch(bundleAttributeName, true);
                    processListEntries(creator);
                    creator.endBranch();
                    creator.endRoot();
                } else {
                    throw new Exception("Malformed JSON! Cannot have an array as root!");
                }
            } else if (jsonToken == JsonToken.START_OBJECT) {
                creator.startRoot(rootName);
                processObjectContent(creator);
                creator.endRoot();
            } else {
                if (bundleAttributeName != null) {
                    creator.startRoot(rootName);
                    creator.startBranch(bundleAttributeName, false);
                    processBaseType(jsonToken, creator);
                    creator.endBranch();
                    creator.endRoot();
                } else {
                    throw new Exception("Malformed JSON! Cannot have a value as root!");
                }
            }
            if (proceedAfterwards) {
                this.parse(creator, true);
            }

        }

        private void processObjectContent(TreeReceiver creator) throws Exception {
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = parser.currentName();
                Name branch = parsingStrategy.apply(fieldName);
                JsonToken value = parser.nextToken();
                if (branch.equals(idAttribute)) {
                    creator.nodeId(Name.identifier(parser.getValueAsString()));
                } else if (branch.equals(typeAttribute)) {
                    creator.nodeType(parsingStrategy.apply(parser.getValueAsString()));
                } else if (value == JsonToken.START_OBJECT) {
                    creator.startBranch(branch, false);
                    creator.startComplexNode();
                    processObjectContent(creator);
                    creator.endComplexNode();
                    creator.endBranch();

                } else if (value == JsonToken.START_ARRAY) {
                    creator.startBranch(branch, true);
                    processListEntries(creator);
                    creator.endBranch();

                } else {
                    creator.startBranch(branch, false);
                    processBaseType(value, creator);
                    creator.endBranch();
                }
            }
        }

        private void processBaseType(JsonToken value, TreeReceiver creator) throws Exception {
            if (value == JsonToken.VALUE_STRING) {
                creator.valueLeaf(Name.value(parser.getValueAsString()));
            } else if (value == JsonToken.VALUE_NUMBER_INT) {
                creator.valueLeaf(Name.value(parser.getLongValue()));
            } else if (value == JsonToken.VALUE_TRUE) {
                creator.valueLeaf(Name.trueValue());
            } else if (value == JsonToken.VALUE_FALSE) {
                creator.valueLeaf(Name.trueValue());
            } else if (value == JsonToken.VALUE_NUMBER_FLOAT) {
                creator.valueLeaf(Name.value(parser.getDoubleValue())); // only with typing we can be more intelligent
            } else if (value == JsonToken.VALUE_NULL) {
                creator.emptyLeaf();
            }
        }

        private void processListEntries(TreeReceiver creator) throws Exception {
            // TODO V.1.0.1: list flattening
            JsonToken currentToken;
            while ((currentToken = parser.nextToken()) != JsonToken.END_ARRAY) {
                if (currentToken == JsonToken.START_ARRAY) {
                    processListEntries(creator);
                } else if (currentToken == JsonToken.START_OBJECT) {
                    creator.startComplexNode();
                    processObjectContent(creator);
                    creator.endComplexNode();
                } else {
                    processBaseType(currentToken, creator);
                }
            }
        }
    }

    private static final class Reader extends AbstractJsonReader implements TreeEmitter {


        public Reader(Name idAttribute, Name typeAttribute, Function<String, Name> parsingStrategy,
                      Name rootName, Name bundleAttributeName, JsonParser parser, Name treeName,
                      Pair<Graph, TreeTypeLibrary> typing) {
            super(idAttribute, typeAttribute, parsingStrategy, typing, rootName, bundleAttributeName, parser, treeName);
        }

        @Override
        public void emitEvents(TreeReceiver target) throws Exception {
            target.startTree(treeName);
            if (typing != null) {
                target.treeType(typing.getFirst(), typing.getSecond());
            }
            parse(target, false);
            target.endTree();
        }


        @Override
        public void reset() {

        }
    }

    static abstract class AbstractJsonWriterConfig extends GenericTreeLibrary.AbstractWriterConfig {

        public AbstractJsonWriterConfig() {
            this.printingStrategy = Name::printRaw;
        }

        protected Function<Name, String> printingStrategy;
    }

    public static class WriterConfig extends AbstractJsonWriterConfig {
        private boolean prettyPrint;

        private WriterConfig() {
            super();
            this.prettyPrint = false;
        }

        public WriterConfig enablePrettyPrinting() {
            this.prettyPrint = true;
            return this;
        }

        public WriterConfig disablePrettyPrinting() {
            this.prettyPrint = false;
            return this;
        }

        public WriterConfig setWritingStrategy(Function<Name, String> printingStrategy) {
            this.printingStrategy = printingStrategy;
            return this;
        }

        public TreeReceiver write(File file) throws IOException {
            FileOutputStream fos = new FileOutputStream(file);
            return write(fos);

        }

        public TreeReceiver write(OutputStream outputStream) throws IOException {
            JsonGenerator generator = JsonLibrary.getInstance().jsonFactory.createGenerator(outputStream);
            return new Writer(generator, prettyPrint, printingStrategy);
        }

    }


    static abstract class AbstractJsonWriter implements TreeReceiver {
        protected final JsonGenerator generator;
        protected final Function<Name, String> toStringStrategy;
        private final Stack<Boolean> currentIsCollection = new Stack<>();

        AbstractJsonWriter(JsonGenerator generator, Function<Name, String> toStringStrategy) {
            this.generator = generator;
            this.toStringStrategy = toStringStrategy;
        }

        @Override
        public void startTree(Name treeName) throws Exception {

        }

        @Override
        public void treeType(Graph typeGraph, TreeTypeLibrary mappingLibrary) {

        }

        @Override
        public void startRoot(Name rootName) throws IOException {
            generator.writeStartObject();
        }

        @Override
        public void nodeId(Name nodeId) throws IOException {

        }

        @Override
        public void nodeType(Name type) throws IOException {

        }

        @Override
        public void startBranch(Name key, boolean isCollection) throws IOException {
            generator.writeFieldName(toStringStrategy.apply(key));
            if (isCollection) {
                generator.writeStartArray();
            }
            this.currentIsCollection.push(isCollection);

        }

        @Override
        public void branchType(Triple type) throws Exception {

        }

        @Override
        public void endBranch() throws IOException {
            if (currentIsCollection.peek()) {
                generator.writeEndArray();
            }
            currentIsCollection.pop();
        }

        @Override
        public void startComplexNode() throws IOException {
            generator.writeStartObject();
        }

        @Override
        public void endComplexNode() throws IOException {
            generator.writeEndObject();
        }

        @Override
        public void emptyLeaf() throws IOException {
            if (!currentIsCollection.peek()) {
                generator.writeNull();
            }
        }

        @Override
        public void valueLeaf(Name value) throws IOException {
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
        }

        @Override
        public void endRoot() throws IOException {
            generator.writeEndObject();
        }

        @Override
        public void endTree() throws Exception {
            generator.flush();
            generator.close();
        }
    }


    private static class Writer extends AbstractJsonWriter implements TreeReceiver {

        public Writer(JsonGenerator generator, boolean isPrettyPrint, Function<Name, String> toStringStrategy) {
            super(generator, toStringStrategy);
            if (isPrettyPrint) {
                this.generator.useDefaultPrettyPrinter();
            }
        }


    }


    private static JsonLibrary instance;

    private final JsonFactory jsonFactory;


    private JsonLibrary() {
        this.jsonFactory = new JsonFactoryBuilder().build();
    }

    public JsonFactory getJsonFactory() {
        return jsonFactory;
    }

    public static JsonLibrary getInstance() {
        if (instance == null) {
            instance = new JsonLibrary();
        }
        return instance;
    }

    public ReaderConfig reader() {
        return new ReaderConfig();
    }

    public WriterConfig writer() {
        return new WriterConfig();
    }



}
