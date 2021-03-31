package no.hvl.past.graph.trees;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import no.hvl.past.attributes.ErrorValue;
import no.hvl.past.graph.Sketch;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.graph.predicates.BoolDT;
import no.hvl.past.graph.predicates.FloatDT;
import no.hvl.past.graph.predicates.IntDT;
import no.hvl.past.graph.predicates.StringDT;
import no.hvl.past.names.Identifier;
import no.hvl.past.names.Name;
import no.hvl.past.names.Value;
import org.checkerframework.checker.nullness.Opt;

import java.io.*;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public class JsonParser {

    // TODO make better namings
    private interface BuildStrategy {

        Tree tree(Node root);

        Node.Builder root();

        Node.Builder objectChild(Node.Builder parent, String fieldName);

        void stringChild(Node.Builder parent, String fieldName, String content);

        void valueChild(Node.Builder parent, String fieldName, Value value);
    }

    private static class TypedStrategy implements BuildStrategy {

        private Map<String, Long> counterMap = new HashMap<>();
        private final Name resultName;
        private final Sketch schema;
        private final BiFunction<Name, String, Optional<Triple>> lookup;
        private Name currentType;

        public long getCounterFor(String key) {
            if (!this.counterMap.containsKey(key)) {
                this.counterMap.put(key, 0L);
            }
            long result = this.counterMap.get(key);
            this.counterMap.put(key, result + 1);
            return result;
        }

        public TypedStrategy(Name resultName, Sketch schema, BiFunction<Name, String, Optional<Triple>> lookup, Name root) {
            this.resultName = resultName;
            this.schema = schema;
            this.lookup = lookup;
            this.currentType = root;
        }

        @Override
        public Tree tree(Node root) {
            return new TypedTree.Impl((TypedNode) root, resultName, schema.carrier());
        }

        @Override
        public Node.Builder root() {
            return new TypedNode.Builder(resultName, currentType);
        }

        @Override
        public Node.Builder objectChild(Node.Builder parent, String fieldName) {
                TypedNode.Builder parentBuilder = (TypedNode.Builder) parent;
                Name type = parentBuilder.getType();
                Optional<Triple> typeTriple = lookup.apply(type, fieldName);
                if (typeTriple.isPresent()) {
                    return parentBuilder.beginChild(Name.identifier(fieldName), Name.identifier(fieldName).index(getCounterFor(fieldName)).childOf(parentBuilder.elementName), typeTriple.get());
                } else {
                    return parentBuilder.beginChild(Name.identifier(fieldName),Name.identifier(fieldName).index(getCounterFor(fieldName)).childOf(parentBuilder.elementName), null);
                }
        }

        @Override
        public void stringChild(Node.Builder parent, String fieldName, String content) {
                TypedNode.Builder parentBuilder = (TypedNode.Builder) parent;
                Name type = parentBuilder.getType();
                Optional<Triple> typeTriple = lookup.apply(type, fieldName);
                if (typeTriple.isPresent()) {
                    if (schema.diagramsOn(Triple.node(typeTriple.get().getTarget())).anyMatch(d -> d.label() instanceof IntDT)) {
                        parentBuilder.attribute(Name.identifier(fieldName), Name.value(new BigInteger(content)), typeTriple.get());
                    } else if (schema.diagramsOn(Triple.node(typeTriple.get().getTarget())).anyMatch(d -> d.label() instanceof FloatDT)) {
                        parentBuilder.attribute(Name.identifier(fieldName), Name.value(Double.parseDouble(content)), typeTriple.get());
                    } else if (schema.diagramsOn(Triple.node(typeTriple.get().getTarget())).anyMatch(d -> d.label() instanceof BoolDT)) {
                        parentBuilder.attribute(Name.identifier(fieldName), Boolean.parseBoolean(content) ? Name.trueValue() : Name.falseValue(), typeTriple.get());
                    } else if (schema.diagramsOn(Triple.node(typeTriple.get().getTarget())).anyMatch(d -> d.label() instanceof StringDT)) {
                        parentBuilder.attribute(Name.identifier(fieldName), Name.value(content), typeTriple.get());
                    } else {
                        parentBuilder.attribute(Name.identifier(fieldName), Name.identifier(content), typeTriple.get());
                    }
                } else {
                    parentBuilder.attribute(Name.identifier(fieldName), Name.identifier(content), null);
                }
        }

        @Override
        public void valueChild(Node.Builder parent, String fieldName, Value value) {
                TypedNode.Builder parentBuilder = (TypedNode.Builder) parent;
                Name type = parentBuilder.getType();
                Optional<Triple> typeTriple = lookup.apply(type, fieldName);
                if (typeTriple.isPresent()) {
                    parentBuilder.attribute(Name.identifier(fieldName), value, typeTriple.get());
                } else {
                    parentBuilder.attribute(Name.identifier(fieldName), value, null);
                }
        }
    }


    private static class UntypedStrategy implements BuildStrategy {

        private final Name resultName;

        public UntypedStrategy(Name resultName) {
            this.resultName = resultName;
        }

        @Override
        public Tree tree(Node root) {
            return new Tree.Impl(root, resultName);
        }

        @Override
        public Node.Builder root() {
            return new Node.Builder();
        }

        @Override
        public Node.Builder objectChild(Node.Builder parent, String fieldName) {
            return parent.beginChild(Name.identifier(fieldName), Name.anonymousIdentifier());
        }

        @Override
        public void stringChild(Node.Builder parent, String fieldName, String content) {
            parent.attribute(Name.identifier(fieldName), Name.identifier(content));
        }

        @Override
        public void valueChild(Node.Builder parent, String fieldName, Value value) {
            parent.attribute(Name.identifier(fieldName), value);
        }
    }

    private final JsonFactory jsonFactory;

    public JsonParser(JsonFactory jsonFactory) {
        this.jsonFactory = jsonFactory;
    }

    public Tree parse(File source, Name resultName) throws IOException {
        com.fasterxml.jackson.core.JsonParser parser = jsonFactory.createParser(source);
        return parse(parser, new UntypedStrategy(resultName));
    }

    public Tree parse(String source, Name resultName) throws IOException {
        com.fasterxml.jackson.core.JsonParser parser = jsonFactory.createParser(source);
        return parse(parser, new UntypedStrategy(resultName));
    }

    public Tree parse(InputStream source, Name resultName) throws IOException {
        com.fasterxml.jackson.core.JsonParser parser = jsonFactory.createParser(source);
        return parse(parser, new UntypedStrategy(resultName));
    }

    public Tree parse(JsonNode rootNode, Name resultName) {
        return null; // TODO
    }

    public TypedTree parseTyped(String source, Name resultName, Sketch schema, Name rootType, BiFunction<Name, String, Optional<Triple>> nameLookup) throws IOException {
        com.fasterxml.jackson.core.JsonParser parser = jsonFactory.createParser(source);
        BuildStrategy strategy = new TypedStrategy(resultName, schema, nameLookup, rootType);
        return (TypedTree) parse(parser, strategy);
    }

    public TypedTree parseTyped(InputStream source, Name resultName, Sketch schema, Name rootType, BiFunction<Name, String, Optional<Triple>> nameLookup) throws IOException {
        com.fasterxml.jackson.core.JsonParser parser = jsonFactory.createParser(source);
        BuildStrategy strategy = new TypedStrategy(resultName, schema, nameLookup, rootType);
        return (TypedTree) parse(parser, strategy);
    }

    public TypedTree parseTyped(File source, Name resultName, Sketch schema, Name rootType, BiFunction<Name, String, Optional<Triple>> nameLookup) throws IOException {
        com.fasterxml.jackson.core.JsonParser parser = jsonFactory.createParser(source);
        BuildStrategy strategy = new TypedStrategy(resultName, schema, nameLookup, rootType);
        return (TypedTree) parse(parser, strategy);
    }

    public TypedTree parseTyped(
            JsonNode source,
            Name resultName, Sketch schema, Name rootType, BiFunction<Name, String, Optional<Triple>> nameLookup) {
        return null; // TODO
    }

    private static Tree parse(com.fasterxml.jackson.core.JsonParser parser, BuildStrategy strategy) throws IOException {
        JsonToken jsonToken = parser.nextToken();
        Node.Builder b = strategy.root();
        if (jsonToken == JsonToken.START_OBJECT) {
            processObjectContent(parser, b, strategy);
            Node root = b.build();
            return strategy.tree(root);
        } else if (jsonToken == JsonToken.START_ARRAY) {
            processListEntries("root",parser,b, strategy);
            Node root = b.build();
            return strategy.tree(root);
        } else {
            processBaseType(parser, b, "root", jsonToken, strategy);
            Node root = b.build();
            return strategy.tree(root);
        }
    }

    private static void processObjectContent(com.fasterxml.jackson.core.JsonParser parser, Node.Builder b, BuildStrategy strategy) throws IOException {
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.currentName();
            JsonToken value = parser.nextToken();
            if (value == JsonToken.START_OBJECT) {
                Node.Builder child = strategy.objectChild(b, fieldName);
                processObjectContent(parser, child, strategy);
            } else if (value == JsonToken.START_ARRAY) {
                processListEntries(fieldName, parser, b, strategy);
            } else {
                processBaseType(parser, b, fieldName, value, strategy);
            }
        }
    }

    private static void processBaseType(com.fasterxml.jackson.core.JsonParser parser, Node.Builder b, String field, JsonToken value, BuildStrategy strategy) throws IOException {
        if (value == JsonToken.VALUE_STRING) {
            strategy.stringChild(b, field, parser.getValueAsString()); // only with typing we can be more intelligent
        } else if (value == JsonToken.VALUE_NUMBER_INT) {
            strategy.valueChild(b, field, Name.value(parser.getLongValue())); // or big int???
        } else if (value == JsonToken.VALUE_TRUE) {
            strategy.valueChild(b, field, Name.trueValue());
        } else if (value == JsonToken.VALUE_FALSE) {
            strategy.valueChild(b, field, Name.falseValue());
        } else if (value == JsonToken.VALUE_NUMBER_FLOAT) {
            strategy.valueChild(b, field, Name.value(parser.getDoubleValue()));
        } else if (value == JsonToken.VALUE_NULL) {
            strategy.valueChild(b, field, ErrorValue.INSTANCE);
        }
    }

    private static void processListEntries(String field, com.fasterxml.jackson.core.JsonParser parser, Node.Builder b, BuildStrategy strategy) throws IOException {
        JsonToken currentToken;
        while ((currentToken = parser.nextToken()) != JsonToken.END_ARRAY) {
            if (currentToken == JsonToken.START_ARRAY) {
                processListEntries(field, parser, b, strategy);
            } else if (currentToken == JsonToken.START_OBJECT) {
                Node.Builder child = strategy.objectChild(b, field);
                processObjectContent(parser, child, strategy);
            } else {
                processBaseType(parser, b, field, currentToken, strategy);
            }
        }
    }

//    /**
//     * Basically simple a wrapper around a Jackson {@link JsonNode}.
//     */
//    public static class NodeWrapper implements no.hvl.past.graph.trees.Node {
//
//        private final Name name;
//        private final JsonNode jsonNode;
//        private final Name parent;
//
//        public NodeWrapper(Name name, JsonNode jsonNode) {
//            this.name = name;
//            this.jsonNode = jsonNode;
//            this.parent = null;
//        }
//
//        public Node(Name name, JsonNode jsonNode, Name parent) {
//            this.name = name;
//            this.jsonNode = jsonNode;
//            this.parent = parent;
//        }
//
//        @Override
//        public Name elementName() {
//            return name;
//        }
//
//        @Override
//        public Optional<Name> parentName() {
//            return Optional.ofNullable(parent);
//        }
//
//        @Override
//        public Optional<Name> attribute(Name attributeName) {
//            return Optional.empty();
//        }
//
//        @Override
//        public Stream<Name> attributeNames() {
//            return Stream.empty();
//        }
//
//        @Override
//        public Stream<no.hvl.past.graph.trees.Node> children(Name childBranchName) {
//            Iterator<String> stringIterator = jsonNode.fieldNames();
//            while (stringIterator.hasNext()) {
//                String next = stringIterator.next();
//                if (Name.identifier(next).equals(childBranchName)) {
//                    JsonNode child = this.jsonNode.get(next);
//                    if (child.isArray()) {
//                        List<no.hvl.past.graph.trees.Node> children = new ArrayList<>();
//                        Iterator<JsonNode> iterator = child.iterator();
//                        int i = 0;
//                        while (iterator.hasNext()) {
//                            JsonNode listItem = iterator.next();
//                            children.add(makeNode(listItem, i, childBranchName, this));
//                            i++;
//                        }
//                        return children.stream();
//                    } else {
//                        Stream.of(makeNode(child, -1,childBranchName,  this));
//                    }
//                }
//            }
//            return Stream.empty();
//        }
//
//        private Node makeNode(JsonNode jsonNode, int idx,Name branchName, Node parent) {
//            if (jsonNode.isObject()) {
//                if (idx >= 0) {
//                    return new Node(branchName.index(idx).childOf(parent.name), jsonNode, parent.name);
//                } else {
//                    return new Node(branchName.childOf(parent.name), jsonNode, parent.name);
//                }
//            } else {
//                Name name;
//                if (jsonNode.isIntegralNumber()) {
//                    name = Name.value(jsonNode.longValue());
//                } else if (jsonNode.isTextual()) {
//                    name = Name.value(jsonNode.textValue());
//                } else if (jsonNode.isBoolean()) {
//                    name = jsonNode.booleanValue() ? Name.trueValue() : Name.falseValue();
//                } else if (jsonNode.isFloatingPointNumber()) {
//                    name = Name.value(jsonNode.doubleValue());
//                } else if (jsonNode.isNull()) {
//                    name = ErrorValue.INSTANCE;
//                } else {
//                    name = Name.identifier(jsonNode.toString());
//                }
//                return new Node(name, jsonNode, parent.name);
//            }
//        }
//
//        @Override
//        public Stream<Name> childBranchNames() {
//            if (!jsonNode.isObject()) {
//                return Stream.empty();
//            }
//            List<Name> childs = new ArrayList<>();
//            Iterator<String> iterator = jsonNode.fieldNames();
//            while (iterator.hasNext()) {
//                childs.add(Name.identifier(iterator.next()));
//            }
//            return childs.stream();
//        }
//
//        public void serialize(JsonGenerator generator) throws IOException {
//            generator.writeTree(jsonNode);
//        }
//    }


}
