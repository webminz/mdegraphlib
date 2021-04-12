package no.hvl.past.graph.trees;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import no.hvl.past.attributes.ErrorValue;
import no.hvl.past.graph.Sketch;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

import java.io.*;
import java.util.Optional;
import java.util.function.BiFunction;

public class JsonParser {

    private final JsonFactory jsonFactory;

    public JsonParser(JsonFactory jsonFactory) {
        this.jsonFactory = jsonFactory;
    }

    public Tree parse(File source, Name resultName) throws IOException {
        com.fasterxml.jackson.core.JsonParser parser = jsonFactory.createParser(source);
        return parse(parser, new TreeBuildStrategy(resultName));
    }

    public Tree parse(String source, Name resultName) throws IOException {
        com.fasterxml.jackson.core.JsonParser parser = jsonFactory.createParser(source);
        return parse(parser, new TreeBuildStrategy(resultName));
    }

    public Tree parse(InputStream source, Name resultName) throws IOException {
        com.fasterxml.jackson.core.JsonParser parser = jsonFactory.createParser(source);
        return parse(parser, new TreeBuildStrategy(resultName));
    }

    public Tree parse(JsonNode rootNode, Name resultName) {
        return null; // TODO
    }

    public TypedTree parse(String source, TreeBuildStrategy strategy) throws IOException {
        com.fasterxml.jackson.core.JsonParser parser = jsonFactory.createParser(source);
        return (TypedTree) parse(parser, strategy);
    }

    public TypedTree parse(InputStream source,  TreeBuildStrategy strategy) throws IOException {
        com.fasterxml.jackson.core.JsonParser parser = jsonFactory.createParser(source);
        return (TypedTree) parse(parser, strategy);
    }

    public TypedTree parse(File source, TreeBuildStrategy strategy) throws IOException {
        com.fasterxml.jackson.core.JsonParser parser = jsonFactory.createParser(source);
        return (TypedTree) parse(parser, strategy);
    }

    public TypedTree parseTyped(
            JsonNode source,
            Name resultName, Sketch schema, Name rootType, BiFunction<Name, String, Optional<Triple>> nameLookup) {
        return null; // TODO
    }

    private static Tree parse(com.fasterxml.jackson.core.JsonParser parser, TreeBuildStrategy strategy) throws IOException {
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

    private static void processObjectContent(com.fasterxml.jackson.core.JsonParser parser, Node.Builder b, TreeBuildStrategy strategy) throws IOException {
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

    private static void processBaseType(com.fasterxml.jackson.core.JsonParser parser, Node.Builder b, String field, JsonToken value, TreeBuildStrategy strategy) throws IOException {
        if (value == JsonToken.VALUE_STRING) {
            strategy.simpleChild(b, field, parser.getValueAsString()); // only with typing we can be more intelligent
        } else if (value == JsonToken.VALUE_NUMBER_INT) {
            strategy.simpleChild(b, field, parser.getLongValue()); // or big int???
        } else if (value == JsonToken.VALUE_TRUE) {
            strategy.simpleChild(b, field, true);
        } else if (value == JsonToken.VALUE_FALSE) {
            strategy.simpleChild(b, field, false);
        } else if (value == JsonToken.VALUE_NUMBER_FLOAT) {
            strategy.simpleChild(b, field, parser.getDoubleValue());
        } else if (value == JsonToken.VALUE_NULL) {
            strategy.simpleChildNullValue(b, field);
        }
    }

    private static void processListEntries(String field, com.fasterxml.jackson.core.JsonParser parser, Node.Builder b, TreeBuildStrategy strategy) throws IOException {
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
