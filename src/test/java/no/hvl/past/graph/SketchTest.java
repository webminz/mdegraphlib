package no.hvl.past.graph;

import no.hvl.past.attributes.BuiltinOperations;
import no.hvl.past.attributes.OperationTerm;
import no.hvl.past.graph.predicates.AttributePredicate;
import no.hvl.past.graph.predicates.Injective;
import no.hvl.past.graph.predicates.StringDT;
import no.hvl.past.graph.operations.ReflexiveClosure;
import no.hvl.past.graph.operations.Compose;
import no.hvl.past.graph.operations.EvaluateMultiAryAttributeOperation;
import no.hvl.past.graph.operations.CoproductUniversalProp;
import no.hvl.past.graph.operations.Invert;

import no.hvl.past.graph.predicates.TargetMultiplicity;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SketchTest extends AbstractGraphTest {

    private Sketch sketch;
    private GraphMorphism instance;

    @Before
    public void setUp() throws GraphError {
        // ¬((length(arg0))<=3)
        OperationTerm.Appl term = new OperationTerm.Appl(
                BuiltinOperations.Negation.getInstance(),
                1,
                new OperationTerm[]{
                        new OperationTerm.Appl(
                                BuiltinOperations.LessOrEqual.getInstance(),
                                1,
                                new OperationTerm[]{
                                        new OperationTerm.Appl(
                                                BuiltinOperations.Length.getInstance(),
                                                1,
                                                new OperationTerm[]{
                                                        new OperationTerm.Var(0)
                                                }
                                        ),
                                        new OperationTerm.Const<>(Name.value(3))
                                }
                        )
                }
        );

        // (arg1++", ")++arg0
        OperationTerm.Appl concatOp = new OperationTerm.Appl(
                BuiltinOperations.Concatenation.getInstance(),
                2,
                new OperationTerm[] {
                       new OperationTerm.Appl(
                               BuiltinOperations.Concatenation.getInstance(),
                               2,
                               new OperationTerm[] {
                                       new OperationTerm.Var(1),
                                       new OperationTerm.Const<>(Name.value(", "))
                               }
                       ),
                        new OperationTerm.Var(0)
                }
        );


        this.sketch = getContextCreatingBuilder()
                // base graph
                .node("Contract")
                .node("Person")
                .edge("Person", "has", "Contract")
                .edge("Person", "firstname", "String")
                .edge("Person", "lastname", "String")
                // derived edges
                .edge("Contract", "person", "Person")
                .edge("Person", "name", "String")
                .edge("Person", "names", "String")
                .edge("Person", "this", "Person")
                .edge("Contract", "personName", "String")

                .graph(Name.identifier("RunningExample").absolute())
                // base predicates
                .startDiagram(StringDT.getInstance())
                .map(Universe.ONE_NODE_THE_NODE, Name.identifier("String"))
                .endDiagram(Name.identifier("d0"))

                .startDiagram(Injective.getInstance())
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Person"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("has"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("Contract"))
                .endDiagram(Name.identifier("d1"))

                .startDiagram(TargetMultiplicity.getInstance(1, 1))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Person"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("firstname"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("String"))
                .endDiagram(Name.identifier("d2"))

                .startDiagram(TargetMultiplicity.getInstance(1, 1))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Person"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("lastname"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("String"))
                .endDiagram(Name.identifier("d3"))

                // base operations
                .startDiagram(ReflexiveClosure.getInstance())
                .map(Universe.LOOP_THE_LOOP.getSource(), Name.identifier("Person"))
                .map(Universe.LOOP_THE_LOOP.getLabel(), Name.identifier("this"))
                .endDiagram(Name.identifier("d4"))

                .startDiagram(Invert.getInstance())
                .map(Universe.CYCLE_FWD.getSource(), Name.identifier("Person"))
                .map(Universe.CYCLE_FWD.getLabel(), Name.identifier("has"))
                .map(Universe.CYCLE_FWD.getTarget(), Name.identifier("Contract"))
                .map(Universe.CYCLE_BWD.getLabel(), Name.identifier("person"))
                .endDiagram(Name.identifier("d5"))

                // dependent operations

                .startDiagram(CoproductUniversalProp.getInstance())
                .map(Universe.COSPAN_LEFT_LEG.getSource(), Name.identifier("Person"))
                .map(Universe.COSPAN_LEFT_LEG.getTarget(), Name.identifier("Person"))
                .map(Universe.COSPAN_LEFT_LEG.getLabel(), Name.identifier("this"))
                .map(Universe.COSPAN_LEFT_LEG.getTarget(), Name.identifier("Person"))
                .map(Universe.COSPAN_RIGHT_LEG.getSource(), Name.identifier("Person"))
                .map(Universe.COSPAN_RIGHT_LEG.getLabel(), Name.identifier("this"))
                .map(Universe.COPRODUCT_LEFT_COMPARATOR_EDGE.getLabel(), Name.identifier("firstname"))
                .map(Universe.COPRODUCT_RIGHT_COMPARATOR_EDGE.getLabel(), Name.identifier("lastname"))
                .map(Universe.CoPRODUCT_MEDIATOR_EDGE.getLabel(), Name.identifier("names"))
                .map(Universe.CoPRODUCT_MEDIATOR_EDGE.getTarget(), Name.identifier("String"))
                .endDiagram(Name.identifier("d6"))

                .startDiagram(EvaluateMultiAryAttributeOperation.getInstance(concatOp))
                .map(Universe.SPAN_LEFT_LEG.getSource(), Name.identifier("Person"))
                .map(Universe.SPAN_LEFT_LEG.getLabel(), Name.identifier("names"))
                .map(Universe.SPAN_LEFT_LEG.getTarget(), Name.identifier("String"))
                .map(Universe.SPAN_RIGHT_LEG.getLabel(), Name.identifier("name"))
                .map(Universe.SPAN_RIGHT_LEG.getTarget(), Name.identifier("String"))
                .endDiagram(Name.identifier("d7"))

                .startDiagram(Compose.getInstance())
                .map(Universe.CHAIN_FST.getSource(), Name.identifier("Contract"))
                .map(Universe.CHAIN_FST.getLabel(), Name.identifier("person"))
                .map(Universe.CHAIN_FST.getTarget(), Name.identifier("Person"))
                .map(Universe.CHAIN_SND.getLabel(), Name.identifier("name"))
                .map(Universe.CHAIN_SND.getTarget(), Name.identifier("String"))
                .map(Universe.TRIANGLE_HYP.getLabel(), Name.identifier("personName"))
                .endDiagram(Name.identifier("d8"))

                // dependent predicate
                .startDiagram(AttributePredicate.getInstance(term))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Contract"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("personName"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("String"))
                .endDiagram(Name.identifier("d9"))

                .sketch("RunningExample")
                .getResult(Sketch.class);

        this.instance = getContextCreatingBuilder()
                .node(":c0")
                .node(":c1")
                .node(":c2")
                .node(":p1")
                .node(":p2")
                .attribute(":p1", ":firstname", Name.value("Are"))
                .attribute(":p1", ":lastname", Name.value("Skog"))
                .attribute(":p2", ":firstname", Name.value("Ole"))
                .attribute(":p2", ":lastname", Name.value("Nordmann"))
                .edge(":p1", ":has1", ":c1")
                .edge(":p1", ":has2", ":c2")
                .graph("Instance_domain")
                .codomain(this.sketch.carrier())
                .map(Name.identifier(":c0"), Name.identifier("Contract"))
                .map(Name.identifier(":c1"), Name.identifier("Contract"))
                .map(Name.identifier(":c2"), Name.identifier("Contract"))
                .map(Name.identifier(":p1"), Name.identifier("Person"))
                .map(Name.identifier(":p2"), Name.identifier("Person"))
                .map(Name.identifier(":has1"), Name.identifier("has"))
                .map(Name.identifier(":has2"), Name.identifier("has"))
                .map(Name.identifier(":firstname").prefixWith(Name.identifier(":p1")), Name.identifier("firstname"))
                .map(Name.identifier(":lastname").prefixWith(Name.identifier(":p1")), Name.identifier("lastname"))
                .map(Name.identifier(":firstname").prefixWith(Name.identifier(":p2")), Name.identifier("firstname"))
                .map(Name.identifier(":lastname").prefixWith(Name.identifier(":p2")), Name.identifier("lastname"))
                .map(Name.value("Are"), Name.identifier("String"))
                .map(Name.value("Ole"), Name.identifier("String"))
                .map(Name.value("Skog"), Name.identifier("String"))
                .map(Name.value("Nordmann"), Name.identifier("String"))
                .morphism("instance")
                .getResult(GraphMorphism.class);
    }


    @Test
    public void testDerivedAndGroundElements() throws GraphError {
        assertTrue(this.sketch.diagrams().allMatch(diagram -> diagram.verify()));
        assertTrue(this.sketch.diagramByName(Name.identifier("d2")).get().generatedElements().count() == 0);

        addExpectedTriple(Triple.edge(
                Name.identifier("Contract"),
                Name.identifier("person"),
                Name.identifier("Person")
        ));
        assertStreamEquals(expected(), this.sketch.diagramByName(Name.identifier("d5")).get().generatedElements());


        addExpectedTriple(Triple.edge(
                Name.identifier("Person"),
                Name.identifier("this"),
                Name.identifier("Person")
        ));
        assertStreamEquals(expected(), this.sketch.diagramByName(Name.identifier("d4")).get().generatedElements());

        addExpectedTriple(Triple.edge(
                Name.identifier("Person"),
                Name.identifier("names"),
                Name.identifier("String")
        ));
        assertStreamEquals(expected(), this.sketch.diagramByName(Name.identifier("d6")).get().generatedElements());


        addExpectedTriple(Triple.edge(
               Name.identifier("Person"),
               Name.identifier("name"),
               Name.identifier("String")
       ));
        assertStreamEquals(expected(), this.sketch.diagramByName(Name.identifier("d7")).get().generatedElements());

        addExpectedTriple(Triple.edge(
                Name.identifier("Contract"),
                Name.identifier("personName"),
                Name.identifier("String")
        ));
        assertStreamEquals(expected(), this.sketch.diagramByName(Name.identifier("d8")).get().generatedElements());


        addExpectedTriple(Triple.edge(
                Name.identifier("Contract"),
                Name.identifier("person"),
                Name.identifier("Person")
        ));
        addExpectedTriple(Triple.edge(
                Name.identifier("Person"),
                Name.identifier("this"),
                Name.identifier("Person")
        ));
        addExpectedTriple(Triple.edge(
                Name.identifier("Person"),
                Name.identifier("name"),
                Name.identifier("String")
        ));
        addExpectedTriple(Triple.edge(
                Name.identifier("Contract"),
                Name.identifier("personName"),
                Name.identifier("String")
        ));
        addExpectedTriple(Triple.edge(
                Name.identifier("Person"),
                Name.identifier("names"),
                Name.identifier("String")
        ));
        assertStreamEquals(expected(), this.sketch.derivedElements());

        Graph expected = getContextCreatingBuilder()
                // base graph
                .node("Contract")
                .node("Person")
                .edge("Person", "has", "Contract")
                .edge("Person", "firstname", "String")
                .edge("Person", "lastname", "String")
                .graph("EXPECTED")
                .getResult(Graph.class);
        assertStreamEquals(expected.elements(), this.sketch.groundElements());

        assertTrue(this.sketch.isDerived(
                Triple.edge(
                        Name.identifier("Person"),
                        Name.identifier("this"),
                        Name.identifier("Person")
                )
        ));
        assertFalse(this.sketch.isDerived(
                Triple.edge(
                        Name.identifier("Person"),
                        Name.identifier("firstname"),
                        Name.identifier("String")
                )
        ));

    }

    @Test
    public void testDiagramDependencies() {
        assertFalse(this.sketch.diagramByName(Name.identifier("d1")).get().directlyDependsOn(this.sketch.diagramByName(Name.identifier("d2")).get()));
        assertFalse(this.sketch.diagramByName(Name.identifier("d5")).get().directlyDependsOn(this.sketch.diagramByName(Name.identifier("d4")).get()));
        assertTrue(this.sketch.diagramByName(Name.identifier("d6")).get().directlyDependsOn(this.sketch.diagramByName(Name.identifier("d4")).get()));
        assertFalse(this.sketch.diagramByName(Name.identifier("d6")).get().directlyDependsOn(this.sketch.diagramByName(Name.identifier("d5")).get()));
        assertTrue(this.sketch.diagramByName(Name.identifier("d7")).get().directlyDependsOn(this.sketch.diagramByName(Name.identifier("d6")).get()));
        assertTrue(this.sketch.diagramByName(Name.identifier("d8")).get().directlyDependsOn(this.sketch.diagramByName(Name.identifier("d7")).get()));
        assertTrue(this.sketch.diagramByName(Name.identifier("d8")).get().directlyDependsOn(this.sketch.diagramByName(Name.identifier("d5")).get()));
        assertTrue(this.sketch.diagramByName(Name.identifier("d9")).get().directlyDependsOn(this.sketch.diagramByName(Name.identifier("d8")).get()));
    }

    @Test
    public void testBuildSketchMorphInstance() throws GraphError {
        assertFalse(this.sketch.isSatisfied(this.instance));
        GraphMorphism extended = this.sketch.extend(Name.identifier("instance++"), this.instance, getExecutionContext());
        // extended.domain().elements().forEach(t -> System.out.println(t.toString())); if you want to see the whole graph
        assertTrue(this.sketch.isSatisfied(extended));

        assertEquals(0, this.instance.allOutgoingInstances(Triple.edge(
                Name.identifier("Contract"),
                Name.identifier("personName"),
                Name.identifier("String")
        ),Name.identifier(":c1")).count());
        assertEquals(0, this.instance.allOutgoingInstances(Triple.edge(
                Name.identifier("Contract"),
                Name.identifier("personName"),
                Name.identifier("String")
        ),Name.identifier(":c2")).count());
        assertEquals(1, extended.allOutgoingInstances(Triple.edge(
                Name.identifier("Contract"),
                Name.identifier("personName"),
                Name.identifier("String")
        ),Name.identifier(":c1")).count());
        assertEquals(1, extended.allOutgoingInstances(Triple.edge(
                Name.identifier("Contract"),
                Name.identifier("personName"),
                Name.identifier("String")
        ),Name.identifier(":c2")).count());

        assertEquals(extended.allOutgoingInstances(Triple.edge(
                Name.identifier("Contract"),
                Name.identifier("personName"),
                Name.identifier("String")
        ),Name.identifier(":c2")).findFirst().map(Triple::getTarget), extended.allOutgoingInstances(Triple.edge(
                Name.identifier("Contract"),
                Name.identifier("personName"),
                Name.identifier("String")
        ),Name.identifier(":c1")).findFirst().map(Triple::getTarget));
        // TODO make the union of edges impose a certain order that is stable under pullbacks
    }


}
