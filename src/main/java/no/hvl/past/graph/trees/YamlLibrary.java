package no.hvl.past.graph.trees;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;
import no.hvl.past.graph.Graph;
import no.hvl.past.names.Name;
import no.hvl.past.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class YamlLibrary {

    private static YamlLibrary instance;

    private final YAMLFactory factory = new YAMLFactory();

    public static class ReaderConfig extends JsonLibrary.AbstractJsonReaderConfig {

        ReaderConfig() {
            super();
        }

        @Override
        public ReaderConfig enableBundleWrapper(Name bundleAttributeName) {
            super.enableBundleWrapper(bundleAttributeName);
            return this;
        }

        @Override
        public ReaderConfig detectTypeByAtt(Name typeAttribute) {
            super.detectTypeByAtt(typeAttribute);
            return this;
        }

        @Override
        public ReaderConfig detectObjectIDByAtt(Name idAttribute) {
            super.detectObjectIDByAtt(idAttribute);
            return this;
        }

        @Override
        public ReaderConfig disableBundleWrapper() {
            super.disableBundleWrapper();
            return this;
        }

        @Override
        public ReaderConfig nameParsingStrategy(Function<String, Name> strategy) {
            super.nameParsingStrategy(strategy);
            return this;
        }

        @Override
        public ReaderConfig rootName(Name rootName) {
            super.rootName(rootName);
            return this;
        }

        @Override
        public ReaderConfig typedOver(Graph typeGraph, TreeTypeLibrary typingLib) {
            super.typedOver(typeGraph, typingLib);
            return this;
        }

        @Override
        public ReaderConfig treeName(Name treeName) {
            super.treeName(treeName);
            return this;
        }

        public TreeEmitter read(File file) throws IOException {
            if (this.treeName == null) {
                this.treeName = Name.uri(file.toURI().toASCIIString());
            }
            return read(new FileInputStream(file));
        }

        public TreeEmitter read(InputStream inputStream) throws IOException {
            YAMLParser parser = YamlLibrary.getInstance().factory.createParser(inputStream);
            return new Reader(
                    this.idAttribute,
                    this.typeAttribute,
                    this.parsingStrategy,
                    null,
                    this.rootName,
                    this.bundleAttributeName,
                    parser,
                    this.treeName);

        }

    }


    private static class Reader extends JsonLibrary.AbstractJsonReader implements TreeEmitter {


        Reader(Name idAttribute,
               Name typeAttribute,
               Function<String, Name> parsingStrategy,
               Pair<Graph, TreeTypeLibrary> typing,
               Name rootName,
               Name bundleAttributeName,
               JsonParser parser,
               Name treeName) {
            super(idAttribute, typeAttribute, parsingStrategy, typing, rootName, bundleAttributeName, parser, treeName);
        }

        @Override
        public void emitEvents(TreeReceiver target) throws Exception {
            SimpleCollector collector = new SimpleCollector();
            super.parse(collector, bundleAttributeName != null);
            long roots = collector.getCollectedEvents()
                    .stream()
                    .filter(e -> e instanceof TreeEvent.StartRoot)
                    .count();
            target.startTree(treeName);
            if (typing != null) {
                target.treeType(typing.getFirst(), typing.getSecond());
            }
            if (roots <= 1) {
                collector.emitEvents(target);
            } else {
                // Special treatment for the --- separator in YAML
                target.startRoot(Node.ROOT_NAME);
                target.startBranch(bundleAttributeName, true);
                List<TreeEvent> toProcess = new ArrayList<>(collector.getCollectedEvents());
                while (!toProcess.isEmpty()) {
                    toProcess.remove(0);
                    target.startComplexNode();
                    int endIdx = toProcess.indexOf(TreeEvent.endRoot());
                    if (endIdx < 0) {
                        endIdx = toProcess.size();
                    }
                    for (int i = 0; i < endIdx; i++) {
                        toProcess.get(0).accept(target);
                        toProcess.remove(0);
                    }
                    if (!toProcess.isEmpty()) {
                        toProcess.remove(0);
                    }
                    target.endComplexNode();
                }
                target.endBranch();
                target.endRoot();
            }
            target.endTree();

        }

        @Override
        public void reset() {

        }
    }


    public static class WriterConfig extends JsonLibrary.AbstractJsonWriterConfig {

        public WriterConfig printingStrategy(Function<Name, String> strategy) {
            this.printingStrategy = strategy;
            return this;
        }


        public TreeReceiver write(OutputStream outputStream) throws IOException {
            YAMLGenerator generator = YamlLibrary.getInstance().factory.createGenerator(outputStream);
            return new Writer(generator, this.printingStrategy);
        }

    }

    private static class Writer extends JsonLibrary.AbstractJsonWriter  {

        Writer(JsonGenerator generator, Function<Name, String> toStringStrategy) {
            super(generator, toStringStrategy);
        }
    }

    public WriterConfig writer() {
        return new WriterConfig();
    }


    public ReaderConfig reader() {
        return new ReaderConfig();
    }



    public static YamlLibrary getInstance() {
        if (instance == null) {
            instance = new YamlLibrary();
        }
        return instance;
    }
}
