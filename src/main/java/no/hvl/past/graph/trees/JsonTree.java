package no.hvl.past.graph.trees;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.hvl.past.attributes.ErrorValue;
import no.hvl.past.names.Name;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class JsonTree implements Tree {

    private final Name treeName;
    private final Node root;

    private JsonTree(Name treeName, Node root) {
        this.treeName = treeName;
        this.root = root;
    }

    @Override
    public no.hvl.past.graph.trees.Node root() {
        return root;
    }

    @Override
    public Name getName() {
        return treeName;
    }

    public void serializeToJson(OutputStream outputStream) throws IOException {
        JsonGenerator generator = new JsonFactory().createGenerator(outputStream);
        this.root.serialize(generator);

    }

    /**
     * Basically simple a wrapper around a Jackson {@link JsonNode}.
     */
    public static class Node implements no.hvl.past.graph.trees.Node {

        private final Name name;
        private final JsonNode jsonNode;
        private final Name parent;

        public Node(Name name, JsonNode jsonNode) {
            this.name = name;
            this.jsonNode = jsonNode;
            this.parent = null;
        }

        public Node(Name name, JsonNode jsonNode, Name parent) {
            this.name = name;
            this.jsonNode = jsonNode;
            this.parent = parent;
        }

        @Override
        public Name elementName() {
            return name;
        }

        @Override
        public Optional<Name> parentName() {
            return Optional.ofNullable(parent);
        }

        @Override
        public Optional<Name> attribute(Name attributeName) {
            return Optional.empty();
        }

        @Override
        public Stream<Name> attributeNames() {
            return Stream.empty();
        }

        @Override
        public Stream<no.hvl.past.graph.trees.Node> children(Name childBranchName) {
            Iterator<String> stringIterator = jsonNode.fieldNames();
            while (stringIterator.hasNext()) {
                String next = stringIterator.next();
                if (Name.identifier(next).equals(childBranchName)) {
                    JsonNode child = this.jsonNode.get(next);
                    if (child.isArray()) {
                        List<no.hvl.past.graph.trees.Node> children = new ArrayList<>();
                        Iterator<JsonNode> iterator = child.iterator();
                        int i = 0;
                        while (iterator.hasNext()) {
                            JsonNode listItem = iterator.next();
                            children.add(makeNode(listItem, i, childBranchName, this));
                            i++;
                        }
                        return children.stream();
                    } else {
                        Stream.of(makeNode(child, -1,childBranchName,  this));
                    }
                }
            }
            return Stream.empty();
        }

        private Node makeNode(JsonNode jsonNode, int idx,Name branchName, Node parent) {
            if (jsonNode.isObject()) {
                if (idx >= 0) {
                    return new Node(branchName.index(idx).childOf(parent.name), jsonNode, parent.name);
                } else {
                    return new Node(branchName.childOf(parent.name), jsonNode, parent.name);
                }
            } else {
                Name name;
                if (jsonNode.isIntegralNumber()) {
                    name = Name.value(jsonNode.longValue());
                } else if (jsonNode.isTextual()) {
                    name = Name.value(jsonNode.textValue());
                } else if (jsonNode.isBoolean()) {
                    name = jsonNode.booleanValue() ? Name.trueValue() : Name.falseValue();
                } else if (jsonNode.isFloatingPointNumber()) {
                    name = Name.value(jsonNode.doubleValue());
                } else if (jsonNode.isNull()) {
                    name = ErrorValue.INSTANCE;
                } else {
                    name = Name.identifier(jsonNode.toString());
                }
                return new Node(name, jsonNode, parent.name);
            }
        }

        @Override
        public Stream<Name> childBranchNames() {
            if (!jsonNode.isObject()) {
                return Stream.empty();
            }
            List<Name> childs = new ArrayList<>();
            Iterator<String> iterator = jsonNode.fieldNames();
            while (iterator.hasNext()) {
                childs.add(Name.identifier(iterator.next()));
            }
            return childs.stream();
        }

        public void serialize(JsonGenerator generator) throws IOException {
            generator.writeTree(jsonNode);
        }
    }

    public static JsonTree fromJsonObject(Name treeName, Name rootObjectName, JsonNode jsonNode) {
        Node node = new Node(rootObjectName, jsonNode);
        return new JsonTree(treeName, node);
    }

    public static Tree fromInputStream(Name treeName, Name rootObjectName, InputStream inputStream) throws IOException {
        // TODO maybe build it directly from the stream if thats faster
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(inputStream);
        return fromJsonObject(treeName, rootObjectName, jsonNode);
    }




}
