package no.hvl.past.graph.trees;

import no.hvl.past.attributes.ErrorValue;
import no.hvl.past.names.Name;

import java.util.*;
import java.util.stream.Stream;

public class JsonNode implements Node {

    private Branch parent;
    private final Name name;
    private final com.fasterxml.jackson.databind.JsonNode node;
    private List<Branch> cachedChildren;

    public JsonNode(Name name, com.fasterxml.jackson.databind.JsonNode node) {
        this.name = name;
        this.node = node;
        this.parent = null;
    }

    public void setParent(Branch parent) {
        this.parent = parent;
    }

    @Override
    public Name elementName() {
        if (node.isObject()) {
            return name;
        } else {
            if (node.isTextual()) {
                return Name.value(node.textValue());
            }
            if (node.isIntegralNumber()) {
                return Name.value(node.bigIntegerValue());
            }
            if (node.isFloatingPointNumber()) {
                return Name.value(node.doubleValue());
            }
            if (node.isBoolean()) {
                return node.booleanValue() ? Name.trueValue() : Name.falseValue();
            }
            return ErrorValue.INSTANCE;
        }
    }

    @Override
    public Optional<Branch> parentRelation() {
        return Optional.ofNullable(parent);
    }

    @Override
    public Stream<Branch> children() {
        if (cachedChildren == null) {
            cachedChildren = new ArrayList<>();
            if (node.isObject()) {
                Iterator<String> fieldNames = node.fieldNames();
                while (fieldNames.hasNext()) {
                    String field = fieldNames.next();
                    com.fasterxml.jackson.databind.JsonNode nested = node.get(field);
                    if (nested.isArray()) {
                        Iterator<com.fasterxml.jackson.databind.JsonNode> nestedIterator = nested.iterator();
                        int i = 0;
                        while (nestedIterator.hasNext()) {
                            JsonNode child = new JsonNode(Name.anonymousIdentifier(), node);
                            Branch.Impl branch = new Branch.Impl(this, field, child, i);
                            child.setParent(branch);
                            cachedChildren.add(branch);
                        }
                    } else {
                        JsonNode child = new JsonNode(Name.anonymousIdentifier(), node);
                        Branch.Impl branch = new Branch.Impl(this, field, child);
                        child.setParent(branch);
                        cachedChildren.add(branch);
                    }
                }
            }
        }
        return cachedChildren.stream();
    }
}
