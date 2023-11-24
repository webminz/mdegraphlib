package no.hvl.past.graph.trees;

import no.hvl.past.graph.Graph;
import no.hvl.past.names.Name;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class GenericTreeLibrary {

    private static abstract class AbstractConfig {
        protected Charset encoding;
        private AbstractConfig() {
            this.encoding = StandardCharsets.UTF_8;
        }

        public void encoding(Charset charset) {
            this.encoding = charset;
        }

        public void encoding(String charset) {
            this.encoding = Charset.forName(charset);
        }


    }

    public static class AbstractReaderConfig extends AbstractConfig {

        protected Name rootName;
        protected Name treeName;
        protected Graph typing;
        private TreeTypeLibrary typingLib;

        AbstractReaderConfig() {
            super();
            this.rootName = Node.ROOT_NAME;
        }

        public AbstractReaderConfig rootName(Name rootName) {
            this.rootName = rootName;
            return this;
        }

        public AbstractReaderConfig treeName(Name treeName) {
            this.treeName = treeName;
            return this;
        }

        public AbstractReaderConfig typedOver(Graph typeGraph, TreeTypeLibrary typingLib) {
            this.typing = typeGraph;
            this.typingLib = typingLib;
            return this;
        }

    }

    public static class AbstractWriterConfig extends AbstractConfig {


    }

    // TODO V.1.0.1: POJO as Trees by interpreting Map objects and Java Classes via reflection

}
