package no.hvl.past.graph;

import no.hvl.past.names.Identifier;
import no.hvl.past.names.Name;

public class SketchTest {

    private static final Identifier OOK_GRAPH_NAME = Name.identifier("OOKernel_graph");
    private static final Identifier OOK_NAME = Name.identifier("OOKernel");

    private static final Identifier TYPE_NAME = Name.identifier("Type");
    private static final Identifier VALUE_NAME = Name.identifier("Value");
    private static final Identifier LITERAL_NAME = Name.identifier("Literal");
    private static final Identifier ENUM_NAME = Name.identifier("EnumValue");
    private static final Identifier ENUM_INDEX_NAME = Name.identifier("value");
    private static final Identifier FLOAT_NAME = Name.identifier("FloatValue");
    private static final Identifier BOOL_NAME = Name.identifier("BoolValue");
    private static final Identifier STRING_NAME = Name.identifier("StringValue");
    private static final Identifier INT_NAME = Name.identifier("IntValue");
    private static final Identifier CUSTOM_NAME = Name.identifier("CustomValue");
    private static final Identifier EXTENDS_NAME = Name.identifier("extends");
    private static final Identifier SUPER_NAME = Name.identifier("super");
    private static final Identifier CONTAINMENT_NAME = Name.identifier("containment");
    private static final Identifier REFERENCE_NAME = Name.identifier("reference");
    private static final Identifier ATTRIBUTE_NAME = Name.identifier("attribute");

    public static final Sketch OO_KERNEL = new GraphBuilders()
            .node(TYPE_NAME)
            .node(VALUE_NAME)
            .node(LITERAL_NAME)
            .node(ENUM_NAME)
            .node(FLOAT_NAME)
            .node(BOOL_NAME)
            .node(STRING_NAME)
            .node(INT_NAME)
            .node(CUSTOM_NAME)
            .edge(ENUM_NAME, ENUM_NAME.subTypeOf(VALUE_NAME), VALUE_NAME)
            .edge(ENUM_NAME, ENUM_INDEX_NAME, LITERAL_NAME)
            .edge(FLOAT_NAME, FLOAT_NAME.subTypeOf(VALUE_NAME), VALUE_NAME)
            .edge(BOOL_NAME, BOOL_NAME.subTypeOf(VALUE_NAME), VALUE_NAME)
            .edge(STRING_NAME, STRING_NAME.subTypeOf(VALUE_NAME), VALUE_NAME)
            .edge(INT_NAME, INT_NAME.subTypeOf(VALUE_NAME), VALUE_NAME)
            .edge(CUSTOM_NAME, CUSTOM_NAME.subTypeOf(VALUE_NAME), VALUE_NAME)
            .edge(TYPE_NAME, ATTRIBUTE_NAME, VALUE_NAME)
            .edge(TYPE_NAME, REFERENCE_NAME, TYPE_NAME)
            .edge(TYPE_NAME, CONTAINMENT_NAME, TYPE_NAME)
            .edge(TYPE_NAME, EXTENDS_NAME, TYPE_NAME)
            .edge(TYPE_NAME, SUPER_NAME, TYPE_NAME)
            .edge(TYPE_NAME, SUPER_NAME.composeSequentially(REFERENCE_NAME), TYPE_NAME)
            .edge(TYPE_NAME, SUPER_NAME.composeSequentially(ATTRIBUTE_NAME), TYPE_NAME)
            .edge(TYPE_NAME, REFERENCE_NAME.downTypeAlong(SUPER_NAME), TYPE_NAME)
            .graph(OOK_GRAPH_NAME)
            // Diagrams
            .sketch(OOK_NAME)
            .getSketchResult();


}
