package no.hvl.past.graph.trees;

import no.hvl.past.attributes.BoolValue;
import no.hvl.past.attributes.ErrorValue;
import no.hvl.past.attributes.FloatValue;
import no.hvl.past.attributes.IntegerValue;
import no.hvl.past.graph.Diagram;
import no.hvl.past.graph.Graph;
import no.hvl.past.graph.Sketch;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.graph.predicates.*;
import no.hvl.past.names.AnonymousIdentifier;
import no.hvl.past.names.Name;
import no.hvl.past.names.Value;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.Optional;
import java.util.function.BiFunction;

// TODO make better namings
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
        return parent.beginChild(Name.identifier(fieldName), makeOID(parent, fieldName));
    }

    @NotNull
    protected Name makeOID(Node.Builder parent, String field) {
        return Name.anonymousIdentifier();
    }

    public void simpleChild(Node.Builder parent, String fieldName, String content) {
        parent.attribute(Name.identifier(fieldName), Name.value(content));
    }

    public void simpleChild(Node.Builder parent, String fieldName, Long integerContent) {
        parent.attribute(Name.identifier(fieldName), Name.value(integerContent));

    }

    public void simpleChild(Node.Builder parent, String fieldName, boolean boolContent) {
        parent.attribute(Name.identifier(fieldName), boolContent ? Name.trueValue() : Name.falseValue());

    }

    public void simpleChild(Node.Builder parent, String fieldName, BigInteger bigIntegerContent) {
        parent.attribute(Name.identifier(fieldName), Name.value(bigIntegerContent));

    }

    public void simpleChild(Node.Builder parent, String fieldName, double floatingPointContent) {
        parent.attribute(Name.identifier(fieldName), Name.value(floatingPointContent));

    }

    public void simpleChildNullValue(Node.Builder parent, String fieldName) {
        parent.attribute(Name.identifier(fieldName), ErrorValue.INSTANCE);
    }

    public void simpleChild(Node.Builder parent, String namespace, String name, String value) {
        parent.attribute(Name.identifier(name).prefixWith(Name.identifier(namespace)), Name.value(value));
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

        @Override
        public Tree tree(Node root) {
            if (root.children().count() == 1) {
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
                            Name.identifier(fieldName),
                            makeOID(parent, fieldName),
                            Triple.edge(TypedNode.BUNDLE_TYPE, name.get().prefixWith(TypedNode.BUNDLE_TYPE), name.get()));
                }
            } else if (parent instanceof TypedNode.Builder) {
                Name parentType = ((TypedNode.Builder) parent).getType();
                Optional<Triple> edgeTyping = lookupType(parentType, fieldName);
                if (edgeTyping.isPresent()) {
                    return ((TypedNode.Builder) parent).beginChild(
                            Name.identifier(fieldName),
                            makeOID(parent, fieldName),
                            edgeTyping.get());
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
                parent.attribute(Name.identifier(fieldName), value, edgeTyping);
            }
            // TODO error handling
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
                        ((TypedNode.Builder) parent).attribute(Name.identifier(fieldName), value, edgeTyping.get());
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
                        ((TypedNode.Builder) parent).attribute(Name.identifier(fieldName), value, edgeTyping.get());
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
                        ((TypedNode.Builder) parent).attribute(Name.identifier(fieldName), value, edgeTyping.get());
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
                        ((TypedNode.Builder) parent).attribute(Name.identifier(fieldName), value, edgeTyping.get());
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
                    ((TypedNode.Builder) parent).attribute(Name.identifier(fieldName), ErrorValue.INSTANCE, edgeTyping.get());
                    return;
                }
            }
            super.simpleChildNullValue(parent,fieldName);
        }


    }


}
