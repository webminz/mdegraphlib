package no.hvl.past.keys;

import com.fasterxml.jackson.databind.JsonNode;
import no.hvl.past.graph.Graph;
import no.hvl.past.graph.GraphMorphism;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.graph.trees.ChildrenRelation;
import no.hvl.past.graph.trees.Node;
import no.hvl.past.names.Name;
import no.hvl.past.names.PrintingStrategy;
import org.w3c.dom.Element;

import java.util.*;

public class AttributeBasedKey implements Key {

    private final Graph carrier;
    private final Triple attributeEdge;
    private final Name targetType;

    public AttributeBasedKey(Graph carrier, Triple attributeEdge, Name targetType) {
        this.carrier = carrier;
        this.attributeEdge = attributeEdge;
        this.targetType = targetType;
    }

    @Override
    public Graph container() {
        return carrier;
    }


    @Override
    public Name targetType() {
        return targetType;
    }

    @Override
    public List<Triple> requiredProperties() {
        return Collections.singletonList(attributeEdge);
    }

    @Override
    public Name evaluate(Name element, GraphMorphism typedContainer) throws KeyNotEvaluated {
        Optional<Triple> result = typedContainer.domain().outgoing(element).filter(t -> typedContainer.apply(t).map(attributeEdge::equals).orElse(false)).findFirst();
        if (result.isPresent()) {
            return result.get().getTarget();
        }
        throw new KeyNotEvaluated();
    }

    @Override
    public Name evaluate(Object element) throws KeyNotEvaluated {
        if (element instanceof Map) {
            return turnToKey(((Map) element).get(attributeEdge.getLabel().print(PrintingStrategy.IGNORE_PREFIX)));
        }

        if (element instanceof Node) {

            Optional<Name> attribute = ((Node) element).childrenByKey(attributeEdge.getLabel()).findFirst().map(c -> c.child().elementName());
            if (attribute.isPresent()) {
                return attribute.get();
            }
            // TODO find child
        }

        if (element instanceof JsonNode) {
            JsonNode node = (JsonNode) element;
            if (node.isObject() && node.has(attributeEdge.getLabel().print(PrintingStrategy.IGNORE_PREFIX))) {
                return turnToKey(node.get(attributeEdge.getLabel().print(PrintingStrategy.IGNORE_PREFIX)));
            }
        }

        if (element instanceof Element) {
            Element xml = (Element) element;
            if (xml.hasAttribute(attributeEdge.getLabel().print(PrintingStrategy.IGNORE_PREFIX))) {
                return turnToKey(xml.getAttribute(attributeEdge.getLabel().print(PrintingStrategy.IGNORE_PREFIX)));
            }
            // TODO find child
        }

        return reflectionCall(element);
    }

    private Name reflectionCall(Object element) throws KeyNotEvaluated {
        // TODO use reflection to call a method or getter with the right name
        throw new KeyNotEvaluated();
    }

    private Name turnToKey(Object o) throws KeyNotEvaluated {
        if (o instanceof Name) {
            return (Name) o;
        }

        if (o instanceof String) {
            return Name.value((String)o);
        }
        if (o instanceof JsonNode) {
            JsonNode json = (JsonNode) o;
            if (json.isTextual()) {
                return Name.value(json.textValue());
            } else if (json.isIntegralNumber()) {
                return Name.value(json.longValue());
            } else if (json.isFloatingPointNumber()) {
                return Name.value(json.doubleValue());
            } else if (json.isBoolean()) {
                return json.booleanValue() ? Name.trueValue() : Name.falseValue();
            } else {
                throw new KeyNotEvaluated();
            }
        }

        return Name.identifier(o.toString());
    }

    @Override
    public Name getName() {
        return attributeEdge.getLabel().prefixWith(attributeEdge.getSource());
    }


    @Override
    public int hashCode() {
        return Objects.hash(targetType, attributeEdge);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AttributeBasedKey) {
            AttributeBasedKey k = (AttributeBasedKey) obj;
            return this.targetType.equals(k.targetType) && this.attributeEdge.equals(k.attributeEdge);
        }
        return false;
    }
}
