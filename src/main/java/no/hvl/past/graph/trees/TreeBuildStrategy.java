package no.hvl.past.graph.trees;

import no.hvl.past.attributes.BoolValue;
import no.hvl.past.attributes.ErrorValue;
import no.hvl.past.attributes.FloatValue;
import no.hvl.past.attributes.IntegerValue;
import no.hvl.past.graph.Graph;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

import java.math.BigInteger;
import java.util.Optional;

public class TreeBuildStrategy {

    private Name treeName;

    protected Name getTreeName() {
        return treeName;
    }

    public TreeBuildStrategy(Name treeName) {
        this.treeName = treeName;
    }

    public TreeBuildStrategy() {
        this.treeName = Name.anonymousIdentifier();
    }

    public Tree tree(Node root) {
        return new Tree.Impl(root, treeName);
    }

    public Node.Builder root() {
        return new Node.Builder();
    }

    public Node.Builder objectChild(Node.Builder parent, String fieldName) {
        return parent.beginChild(fieldName, makeOID(parent, fieldName));
    }

    protected void reportError(Node.Builder parent, String field, String message) {
        // Per default simply ignored
    }

    protected Name makeOID(Node.Builder parent, String field) {
        return Name.anonymousIdentifier();
    }

    public void simpleChild(Node.Builder parent, String fieldName, String content) {
        parent.attribute(fieldName, Name.value(content));
    }

    public void simpleChild(Node.Builder parent, String fieldName, Long integerContent) {
        parent.attribute(fieldName, Name.value(integerContent));
    }

    public void simpleChild(Node.Builder parent, String fieldName, boolean boolContent) {
        parent.attribute(fieldName, boolContent ? Name.trueValue() : Name.falseValue());
    }

    public void simpleChild(Node.Builder parent, String fieldName, BigInteger bigIntegerContent) {
        parent.attribute(fieldName, Name.value(bigIntegerContent));
    }

    public void simpleChild(Node.Builder parent, String fieldName, double floatingPointContent) {
        parent.attribute(fieldName, Name.value(floatingPointContent));
    }

    public void simpleChildNullValue(Node.Builder parent, String fieldName) {
        parent.attribute(fieldName, ErrorValue.INSTANCE);
    }

    public void simpleChild(Node.Builder parent, String namespace, String name, String value) {
        parent.attribute(namespace + ":" + name, Name.value(value));
    }

    public static abstract class TypedStrategy extends TreeBuildStrategy {

        public TypedStrategy() {
            super();
        }

        public abstract Graph getSchemaGraph();

        public abstract Optional<Name> rootType(String label);

        public abstract Optional<Triple> lookupType(Name parentType, String field);

        public abstract boolean isStringType(Name typeName);

        public abstract boolean isBoolType(Name typeName);

        public abstract boolean isFloatType(Name typeName);

        public abstract boolean isIntType(Name typeName);

        public abstract boolean isEnumType(Name typeName);

        public Optional<Triple> inverseOf(Triple edge) {
            return Optional.empty();
        }

        @Override
        public Tree tree(Node root) {
            if (root.children().count() == 1) {
                //noinspection OptionalGetWithoutIsPresent
                return new TypedTree.Impl((TypedNode) root.children().findFirst().get().child(), getTreeName(), getSchemaGraph());
            }
            return new TypedTree.Impl((TypedNode) root, getTreeName(), getSchemaGraph());
        }

        @Override
        public Node.Builder root() {
            return new TypedNode.Builder(Node.ROOT_NAME, TypedNode.BUNDLE_TYPE);
        }

        @Override
        public Node.Builder objectChild(Node.Builder parent, String fieldName) {
            if (parent.elementName.equals(Node.ROOT_NAME)) {
                Optional<Name> name = rootType(fieldName);
                if (name.isPresent()) {
                    return ((TypedNode.Builder) parent).beginChild(
                            fieldName,
                            makeOID(parent, fieldName),
                            Triple.edge(TypedNode.BUNDLE_TYPE, name.get().prefixWith(TypedNode.BUNDLE_TYPE), name.get()));
                }
            } else if (parent instanceof TypedNode.Builder) {
                Name parentType = ((TypedNode.Builder) parent).getType();
                Optional<Triple> edgeTyping = lookupType(parentType, fieldName);
                if (edgeTyping.isPresent()) {
                    Optional<Triple> inverseTriple = inverseOf(edgeTyping.get());
                    if (inverseTriple.isPresent()) {
                        return ((TypedNode.Builder) parent).beginChild(
                                fieldName,
                                makeOID(parent, fieldName),
                                edgeTyping.get(),
                                inverseTriple.get());
                    } else {
                        return ((TypedNode.Builder) parent).beginChild(
                                fieldName,
                                makeOID(parent, fieldName),
                                edgeTyping.get());
                    }

                }
            }
            return super.objectChild(parent, fieldName);
        }

        @Override
        public void simpleChild(Node.Builder parent, String namespace, String name, String value) {
            if (parent instanceof TypedNode.Builder) {
               simpleChild(parent, name, value);
            } else {
                super.simpleChild(parent, namespace, name, value);
            }
        }

        @Override
        public void simpleChild(Node.Builder parent, String fieldName, String content) {
            if (parent instanceof TypedNode.Builder) {
                Name parentType = ((TypedNode.Builder) parent).getType();
                Optional<Triple> edgeTyping = lookupType(parentType, fieldName);
                if (edgeTyping.isPresent()) {
                    handleSimpleValue((TypedNode.Builder) parent, fieldName, content, edgeTyping.get());
                    return;
                }
            }
            super.simpleChild(parent,fieldName,content);

        }

        protected void handleSimpleValue(TypedNode.Builder parent, String fieldName, String content, Triple edgeTyping) {
            Name value = null;
            if (isBoolType(edgeTyping.getTarget())) {
                value = BoolValue.tryParse(Name.value(content));
            }
            if (isIntType(edgeTyping.getTarget())) {
                value = IntegerValue.tryParse(Name.value(content));
            }
            if (isFloatType(edgeTyping.getTarget())) {
                value = FloatValue.tryParse(Name.value(content));
            }
            if (isEnumType(edgeTyping.getTarget())) {
                value = Name.identifier(content);
            }
            if (isStringType(edgeTyping.getTarget())) {
                value = Name.value(content);
            }
            if (value != null) {
                parent.attribute(fieldName, value, edgeTyping);
            }
            reportError(parent, fieldName, uninterpretedAttributeMsg(parent, fieldName, content, edgeTyping));
        }

        private String uninterpretedAttributeMsg(TypedNode.Builder parent, String fieldName, String content, Triple edgeTyping) {
            return "Field at " + parent.elementName + "." + fieldName + " with content '" + content + " could not be interpreted as an instance of " + edgeTyping;
        }

        @Override
        public void simpleChild(Node.Builder parent, String fieldName, Long integerContent) {
            if (parent instanceof TypedNode.Builder) {
                Name parentType = ((TypedNode.Builder) parent).getType();
                Optional<Triple> edgeTyping = lookupType(parentType, fieldName);
                if (edgeTyping.isPresent()) {
                    Name value = null;
                    if (isFloatType(edgeTyping.get().getTarget())) {
                        value = Name.value(integerContent).toFloat();
                    }
                    if (isIntType(edgeTyping.get().getTarget())) {
                        value = Name.value(integerContent);
                    }
                    if (value != null) {
                        ((TypedNode.Builder) parent).attribute(fieldName, value, edgeTyping.get());
                        return;
                    }
                }
            }
            super.simpleChild(parent,fieldName,integerContent);
        }

        @Override
        public void simpleChild(Node.Builder parent, String fieldName, boolean boolContent) {
            if (parent instanceof TypedNode.Builder) {
                Name parentType = ((TypedNode.Builder) parent).getType();
                Optional<Triple> edgeTyping = lookupType(parentType, fieldName);
                if (edgeTyping.isPresent()) {
                    Name value = null;
                    if (isBoolType(edgeTyping.get().getTarget())) {
                        value = boolContent ? Name.trueValue() : Name.falseValue();
                    }
                    if (value != null) {
                        ((TypedNode.Builder) parent).attribute(fieldName, value, edgeTyping.get());
                        return;
                    }
                }
            }
            super.simpleChild(parent,fieldName,boolContent);
        }

        @Override
        public void simpleChild(Node.Builder parent, String fieldName, BigInteger bigIntegerContent) {
            if (parent instanceof TypedNode.Builder) {
                Name parentType = ((TypedNode.Builder) parent).getType();
                Optional<Triple> edgeTyping = lookupType(parentType, fieldName);
                if (edgeTyping.isPresent()) {
                    Name value = null;
                    if (isFloatType(edgeTyping.get().getTarget())) {
                        value = Name.value(bigIntegerContent).toFloat();
                    }
                    if (isIntType(edgeTyping.get().getTarget())) {
                        value = Name.value(bigIntegerContent);
                    }
                    if (value != null) {
                        ((TypedNode.Builder) parent).attribute(fieldName, value, edgeTyping.get());
                        return;
                    }
                }
            }
            super.simpleChild(parent,fieldName,bigIntegerContent);
        }

        @Override
        public void simpleChild(Node.Builder parent, String fieldName, double floatingPointContent) {
            if (parent instanceof TypedNode.Builder) {
                Name parentType = ((TypedNode.Builder) parent).getType();
                Optional<Triple> edgeTyping = lookupType(parentType, fieldName);
                if (edgeTyping.isPresent()) {
                    Name value = null;
                    if (isFloatType(edgeTyping.get().getTarget())) {
                        value = Name.value(floatingPointContent);
                    }
                    if (value != null) {
                        ((TypedNode.Builder) parent).attribute(fieldName, value, edgeTyping.get());
                        return;
                    }
                }
            }
            super.simpleChild(parent,fieldName,floatingPointContent);
        }

        @Override
        public void simpleChildNullValue(Node.Builder parent, String fieldName) {
            if (parent instanceof TypedNode.Builder) {
                Name parentType = ((TypedNode.Builder) parent).getType();
                Optional<Triple> edgeTyping = lookupType(parentType, fieldName);
                if (edgeTyping.isPresent()) {
                    ((TypedNode.Builder) parent).attribute(fieldName, ErrorValue.INSTANCE, edgeTyping.get());
                    return;
                }
            }
            super.simpleChildNullValue(parent,fieldName);
        }
    }


}
