package no.hvl.past.graph.operations;

import no.hvl.past.attributes.BuiltinOperations;
import no.hvl.past.attributes.OperationTerm;
import no.hvl.past.graph.AbstractGraphTest;
import no.hvl.past.graph.GraphError;
import no.hvl.past.graph.GraphMorphism;
import no.hvl.past.graph.Universe;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import org.junit.Test;


import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class GraphOperationsTest extends AbstractGraphTest {

    @Test
    public void testComposition() throws GraphError {
        GraphMorphism m0 = getContextCreatingBuilder() // nothing to compose really
                .edge("A", "f", "B")
                .edge("B'", "g", "C'")
                .node("A'")
                .node("C")
                .graph("G0")
                .codomain(Universe.TRIANGLE)
                .map(Name.identifier("A"), Universe.CHAIN_FST.getSource())
                .map(Name.identifier("A'"), Universe.CHAIN_FST.getSource())
                .map(Name.identifier("B'"), Universe.CHAIN_FST.getTarget())
                .map(Name.identifier("B"), Universe.CHAIN_FST.getTarget())
                .map(Name.identifier("C"), Universe.CHAIN_SND.getTarget())
                .map(Name.identifier("C"), Universe.CHAIN_SND.getTarget())
                .map(Name.identifier("f"), Universe.CHAIN_FST.getLabel())
                .map(Name.identifier("g"), Universe.CHAIN_SND.getLabel())
                .morphism("m0")
                .getResult(GraphMorphism.class);

        assertTrue(Compose.getInstance().isSatisfied(m0));
        GraphMorphism result0 = Compose.getInstance().execute(m0, getExecutionContext());
        assertStreamEquals(Collections.emptySet(), result0.preimage(Universe.TRIANGLE_HYP));

        GraphMorphism m1 = getContextCreatingBuilder() // simple happy flow
                .edge("A", "f", "B")
                .edge("B", "g", "C")
                .graph("G1")
                .codomain(Universe.TRIANGLE)
                .map(Name.identifier("A"), Universe.CHAIN_FST.getSource())
                .map(Name.identifier("B"), Universe.CHAIN_SND.getSource())
                .map(Name.identifier("C"), Universe.CHAIN_SND.getTarget())
                .map(Name.identifier("f"), Universe.CHAIN_FST.getLabel())
                .map(Name.identifier("g"), Universe.CHAIN_SND.getLabel())
                .morphism("m1")
                .getResult(GraphMorphism.class);

        assertFalse(Compose.getInstance().isSatisfied(m1));
        GraphMorphism result1 = Compose.getInstance().execute(m1, getExecutionContext());
        assertStreamEquals(Collections.singleton(Triple.edge(
                Name.identifier("A"),
                Name.identifier("f").composeSequentially(Name.identifier("g")),
                Name.identifier("C"))), result1.preimage(Universe.TRIANGLE_HYP));
        assertTrue(Compose.getInstance().isSatisfied(result1));

        GraphMorphism m2 = getContextCreatingBuilder()
                .edge("A", "f", "B")
                .edge("A'", "f'", "B")
                .edge("B", "g", "C")
                .edge("B", "g'", "C'")
                .edge("A", "h", "C")
                .edge("A'", "h'", "C'")
                .graph("G1")
                .codomain(Universe.TRIANGLE)
                .map(Name.identifier("A"), Universe.CHAIN_FST.getSource())
                .map(Name.identifier("A'"), Universe.CHAIN_FST.getSource())
                .map(Name.identifier("B"), Universe.CHAIN_SND.getSource())
                .map(Name.identifier("C"), Universe.CHAIN_SND.getTarget())
                .map(Name.identifier("C'"), Universe.CHAIN_SND.getTarget())
                .map(Name.identifier("f"), Universe.CHAIN_FST.getLabel())
                .map(Name.identifier("f'"), Universe.CHAIN_FST.getLabel())
                .map(Name.identifier("g"), Universe.CHAIN_SND.getLabel())
                .map(Name.identifier("g'"), Universe.CHAIN_SND.getLabel())
                .map(Name.identifier("h"), Universe.TRIANGLE_HYP.getLabel())
                .map(Name.identifier("h'"), Universe.TRIANGLE_HYP.getLabel())
                .morphism("m2")
                .getResult(GraphMorphism.class);

        assertFalse(Compose.getInstance().isExecutedCorrectly(m2));
        GraphMorphism result2 = Compose.getInstance().fix(m2, getExecutionContext());
        assertTrue(Compose.getInstance().isExecutedCorrectly(result2));
        addExpectedTriple(Triple.edge(Name.identifier("A"),
                                    Name.identifier("f").composeSequentially(Name.identifier("g")),
                                    Name.identifier("C")));
        addExpectedTriple(Triple.edge(Name.identifier("A"),
                Name.identifier("f").composeSequentially(Name.identifier("g'")),
                Name.identifier("C'")));
        addExpectedTriple(Triple.edge(Name.identifier("A'"),
                Name.identifier("f'").composeSequentially(Name.identifier("g")),
                Name.identifier("C")));
        addExpectedTriple(Triple.edge(Name.identifier("A'"),
                Name.identifier("f'").composeSequentially(Name.identifier("g'")),
                Name.identifier("C'")));
        assertStreamEquals(expected(), result2.allInstances(Universe.TRIANGLE_HYP));
    }


    @Test
    public void testBinAttrEval() throws GraphError {
        GraphMorphism m0 = getContextCreatingBuilder()
                .attribute("A", "firstname", Name.value("Ole"))
                .attribute("A", "lastname", Name.value("Nordmann"))
                .graph("G0")
                .codomain(Universe.SPAN)
                .map(Name.identifier("A"), Universe.SPAN_LEFT_LEG.getSource())
                .map(Name.identifier("firstname").prefixWith(Name.identifier("A")), Universe.SPAN_LEFT_LEG.getLabel())
                .map(Name.identifier("lastname").prefixWith(Name.identifier("A")), Universe.SPAN_RIGHT_LEG.getLabel())
                .map(Name.value("Ole"), Universe.SPAN_LEFT_LEG.getTarget())
                .map(Name.value("Nordmann"), Universe.SPAN_RIGHT_LEG.getTarget())
                .morphism("m0")
                .getResult(GraphMorphism.class);

        EvaluateBinaryAttributeOperation op = EvaluateBinaryAttributeOperation.getInstance(BuiltinOperations.Concatenation.getInstance());

        assertFalse(op.isSatisfied(m0));
        GraphMorphism result0 = op.execute(m0, getExecutionContext());
        List<Triple> m0Inst = result0.allInstances(Universe.multiSpanEdge(3)).collect(Collectors.toList());
        assertEquals(1, m0Inst.size());
        assertEquals(Name.value("OleNordmann"), m0Inst.get(0).getValue().get());
        assertTrue(op.isSatisfied(result0));


        GraphMorphism m1 = getContextCreatingBuilder()
                .attribute("A", "firstname", Name.value("Ole"))
                .graph("G1")
                .codomain(Universe.SPAN_3)
                .map(Name.identifier("A"), Universe.SPAN_LEFT_LEG.getSource())
                .map(Name.identifier("firstname").prefixWith(Name.identifier("A")), Universe.SPAN_LEFT_LEG.getLabel())
                .map(Name.value("Ole"), Universe.SPAN_LEFT_LEG.getTarget())
                .morphism("m1")
                .getResult(GraphMorphism.class);

        assertTrue(op.isSatisfied(m1));
        GraphMorphism result1 = op.execute(m1, getExecutionContext());
        assertStreamEquals(m1.domain().elements(), result1.domain().elements());

        GraphMorphism m3 = getContextCreatingBuilder()
                .attribute("A", "firstname", Name.value("Ole "))
                .attribute("B", "firstname", Name.value("Max "))
                .attribute("A", "lastname", Name.value("Nordmann"))
                .attribute("B", "lastname", Name.value("Mustermann"))
                .attribute("B", "fullname", Name.value("Ole Nordmann"))
                .attribute("A", "fullname", Name.value("Max Musterman"))
                .graph("G2")
                .codomain(Universe.SPAN_3)
                .map(Name.identifier("A"), Universe.SPAN_LEFT_LEG.getSource())
                .map(Name.identifier("B"), Universe.SPAN_LEFT_LEG.getSource())
                .map(Name.identifier("firstname").prefixWith(Name.identifier("A")), Universe.SPAN_LEFT_LEG.getLabel())
                .map(Name.identifier("firstname").prefixWith(Name.identifier("B")), Universe.SPAN_LEFT_LEG.getLabel())
                .map(Name.identifier("lastname").prefixWith(Name.identifier("A")), Universe.SPAN_RIGHT_LEG.getLabel())
                .map(Name.identifier("lastname").prefixWith(Name.identifier("B")), Universe.SPAN_RIGHT_LEG.getLabel())
                .map(Name.identifier("fullname").prefixWith(Name.identifier("A")), Universe.SPAN_3_EDGE.getLabel())
                .map(Name.identifier("fullname").prefixWith(Name.identifier("B")), Universe.SPAN_3_EDGE.getLabel())
                .morphism("m3")
                .getResult(GraphMorphism.class);

        assertFalse(op.isSatisfied(m3));
        GraphMorphism result3 = op.fix(m3, getExecutionContext());
        assertTrue(op.isSatisfied(result3));
        assertEquals(Name.value("Ole Nordmann"), result3.allOutgoingInstances(Universe.SPAN_3_EDGE, Name.identifier("A")).findFirst().get().getTarget());
        assertEquals(Name.value("Max Mustermann"), result3.allOutgoingInstances(Universe.SPAN_3_EDGE, Name.identifier("B")).findFirst().get().getTarget());


        // constants are special
        EvaluateBinaryAttributeOperation op2 = EvaluateBinaryAttributeOperation.getInstance(new OperationTerm.Const<>(Name.trueValue()));
        GraphMorphism m4 = getContextCreatingBuilder()
                .node("A1")
                .edge("A2", "f1", "B")
                .edge("A3", "f2", "B")
                .edge("A3", "g", "B")
                .graph("G4")
                .codomain(Universe.SPAN_3)
                .map(Name.identifier("A1"), Universe.SPAN_RIGHT_LEG.getSource())
                .map(Name.identifier("A2"), Universe.SPAN_RIGHT_LEG.getSource())
                .map(Name.identifier("A3"), Universe.SPAN_RIGHT_LEG.getSource())
                .map(Name.identifier("f1"), Universe.SPAN_LEFT_LEG.getLabel())
                .map(Name.identifier("f2"), Universe.SPAN_LEFT_LEG.getLabel())
                .map(Name.identifier("g"), Universe.SPAN_RIGHT_LEG.getLabel())
                .morphism("m4")
                .getResult(GraphMorphism.class);

        GraphMorphism result4 = op2.execute(m4, getExecutionContext());
        assertEquals(3, result4.allInstances(Universe.SPAN_3_EDGE).count());
    }


    @Test
    public void testMultiAttrEval() throws GraphError {
        OperationTerm term = new OperationTerm.Appl(
                BuiltinOperations.Multiplication.getInstance(),
                3,
                new OperationTerm[]{
                        new OperationTerm.Var(0),
                        new OperationTerm.Appl(
                                BuiltinOperations.Addition.getInstance(),
                                3,
                                new OperationTerm[]{
                                        new OperationTerm.Var(1),
                                        new OperationTerm.Var(2)
                                }
                        )
                }
        );
        EvaluateMultiAryAttributeOperation operation = EvaluateMultiAryAttributeOperation.getInstance(term);

        GraphMorphism mor = getContextCreatingBuilder()
                .edge(Name.identifier("A"), Name.identifier("f").index(0), Name.value(1))
                .edge(Name.identifier("A"), Name.identifier("f").index(1), Name.value(2))
                .edge(Name.identifier("B"), Name.identifier("g").index(42), Name.value(3))
                .edge(Name.identifier("B"), Name.identifier("g").index(1), Name.value(2))
                .edge(Name.identifier("B"), Name.identifier("g").index(23), Name.value(1))
                .graph("G")
                .codomain(Universe.SPAN)
                .map(Name.identifier("A"), Universe.SPAN_LEFT_LEG.getSource())
                .map(Name.identifier("B"), Universe.SPAN_LEFT_LEG.getSource())
                .map(Name.identifier("f").index(0), Universe.SPAN_LEFT_LEG.getLabel())
                .map(Name.identifier("f").index(1), Universe.SPAN_LEFT_LEG.getLabel())
                .map(Name.identifier("g").index(42), Universe.SPAN_LEFT_LEG.getLabel())
                .map(Name.identifier("g").index(1), Universe.SPAN_LEFT_LEG.getLabel())
                .map(Name.identifier("g").index(23), Universe.SPAN_LEFT_LEG.getLabel())
                .map(Name.value(1), Universe.SPAN_LEFT_LEG.getTarget())
                .map(Name.value(2), Universe.SPAN_LEFT_LEG.getTarget())
                .map(Name.value(3), Universe.SPAN_LEFT_LEG.getTarget())
                .morphism("M")
                .getResult(GraphMorphism.class);

        GraphMorphism result = operation.execute(mor, getExecutionContext());
        // should evaluate 2 * (3+1) on B
        assertEquals(Name.value(8), result.allInstances(Universe.SPAN_RIGHT_LEG).findFirst().get().getTarget());
    }

    @Test
    public void testInvert() throws GraphError {
        GraphMorphism m0 = getContextCreatingBuilder()
                .edge("A", "f", "B")
                .graph("G0")
                .codomain(Universe.CYCLE)
                .map(Name.identifier("A"), Universe.CYCLE_FWD.getSource())
                .map(Name.identifier("B"), Universe.CYCLE_FWD.getTarget())
                .map(Name.identifier("f"), Universe.CYCLE_FWD.getLabel())
                .morphism("M0")
                .getResult(GraphMorphism.class);



        assertFalse(Invert.getInstance().isSatisfied(m0));
        GraphMorphism result0 = Invert.getInstance().execute(m0, getExecutionContext());
        assertEquals(Triple.edge(Name.identifier("B"), Name.identifier("f").inverse(), Name.identifier("A")),
                result0.allInstances(Universe.CYCLE_BWD).findFirst().get());
        assertTrue(Invert.getInstance().isSatisfied(result0));

        GraphMorphism emtpty = getContextCreatingBuilder()
                .node("A")
                .node("B")
                .graph("G_EMPTY")
                .codomain(Universe.CYCLE)
                .map(Name.identifier("A"), Universe.CYCLE_FWD.getSource())
                .map(Name.identifier("B"), Universe.CYCLE_FWD.getTarget())
                .morphism("M1")
                .getResult(GraphMorphism.class);

        assertTrue(Invert.getInstance().isSatisfied(emtpty));
        GraphMorphism resultEmpty = Invert.getInstance().execute(emtpty, getExecutionContext());
        assertStreamEquals(emtpty.domain().elements(), resultEmpty.domain().elements());


        GraphMorphism m2 = getContextCreatingBuilder()
                .edge("A", "f", "B'")
                .edge("A'", "f'", "B")
                .edge("B", "f_inv", "A")
                .edge("B'", "f'_inv", "A'")
                .graph("G_fix")
                .codomain(Universe.CYCLE)
                .map(Name.identifier("A"), Universe.CYCLE_FWD.getSource())
                .map(Name.identifier("A'"), Universe.CYCLE_FWD.getSource())
                .map(Name.identifier("B"), Universe.CYCLE_FWD.getTarget())
                .map(Name.identifier("B'"), Universe.CYCLE_FWD.getTarget())
                .map(Name.identifier("f"), Universe.CYCLE_FWD.getLabel())
                .map(Name.identifier("f'"), Universe.CYCLE_FWD.getLabel())
                .map(Name.identifier("f_inv"), Universe.CYCLE_BWD.getLabel())
                .map(Name.identifier("f'_inv"), Universe.CYCLE_BWD.getLabel())
                .morphism("M1")
                .getResult(GraphMorphism.class);

        assertFalse(Invert.getInstance().isSatisfied(m2));
        GraphMorphism reesultFix = Invert.getInstance().fix(m2, getExecutionContext());
        assertEquals(Name.identifier("A'"), reesultFix.allOutgoingInstances(Universe.CYCLE_BWD, Name.identifier("B")).findFirst().get().getTarget());
        assertEquals(Name.identifier("A"), reesultFix.allOutgoingInstances(Universe.CYCLE_BWD, Name.identifier("B'")).findFirst().get().getTarget());


        GraphMorphism comlex = getContextCreatingBuilder()
                .edge("A1", "f1", "B1")
                .edge("A2", "f2", "B1")
                .edge("A3", "f3", "B2")
                .edge("A3", "f4", "B3")
                .edge("A4", "f5", "B3")
                .graph("G_cmplx")
                .codomain(Universe.CYCLE)
                .map(Name.identifier("A1"), Universe.CYCLE_FWD.getSource())
                .map(Name.identifier("A2"), Universe.CYCLE_FWD.getSource())
                .map(Name.identifier("A3"), Universe.CYCLE_FWD.getSource())
                .map(Name.identifier("A4"), Universe.CYCLE_FWD.getSource())
                .map(Name.identifier("A5"), Universe.CYCLE_FWD.getSource())
                .map(Name.identifier("B1"), Universe.CYCLE_FWD.getTarget())
                .map(Name.identifier("B2"), Universe.CYCLE_FWD.getTarget())
                .map(Name.identifier("B3"), Universe.CYCLE_FWD.getTarget())
                .map(Name.identifier("f1"), Universe.CYCLE_FWD.getLabel())
                .map(Name.identifier("f2"), Universe.CYCLE_FWD.getLabel())
                .map(Name.identifier("f3"), Universe.CYCLE_FWD.getLabel())
                .map(Name.identifier("f4"), Universe.CYCLE_FWD.getLabel())
                .map(Name.identifier("f5"), Universe.CYCLE_FWD.getLabel())
                .morphism("M4")
                .getResult(GraphMorphism.class);

        assertFalse(Invert.getInstance().isSatisfied(comlex));
        GraphMorphism resultComplex = Invert.getInstance().execute(comlex, getExecutionContext());
        assertEquals(2, resultComplex.allOutgoingInstances(Universe.CYCLE_BWD, Name.identifier("B1")).count());
        assertEquals(1, resultComplex.allOutgoingInstances(Universe.CYCLE_BWD, Name.identifier("B2")).count());
        assertEquals(2, resultComplex.allOutgoingInstances(Universe.CYCLE_BWD, Name.identifier("B3")).count());
        assertEquals(1, resultComplex.allIncomingInstances(Universe.CYCLE_BWD, Name.identifier("A1")).count());
        assertEquals(1, resultComplex.allIncomingInstances(Universe.CYCLE_BWD, Name.identifier("A2")).count());
        assertEquals(2, resultComplex.allIncomingInstances(Universe.CYCLE_BWD, Name.identifier("A3")).count());
        assertEquals(1, resultComplex.allIncomingInstances(Universe.CYCLE_BWD, Name.identifier("A4")).count());
        assertTrue(Invert.getInstance().isSatisfied(resultComplex));

    }

    @Test
    public void testInverse() throws GraphError {
        GraphMorphism m0 = getContextCreatingBuilder().domain(Universe.EMPTY).codomain(Universe.CYCLE).morphism("m0").getResult(GraphMorphism.class);
        GraphMorphism m1 = getContextCreatingBuilder() // happy case
                .edge("A", "f", "B")
                .edge("B", "g", "A")
                .graph("G1")
                .codomain(Universe.CYCLE)
                .map(Name.identifier("A"), Universe.CYCLE_FWD.getSource())
                .map(Name.identifier("B"), Universe.CYCLE_FWD.getTarget())
                .map(Name.identifier("f"), Universe.CYCLE_FWD.getLabel())
                .map(Name.identifier("g"), Universe.CYCLE_BWD.getLabel())
                .morphism("m1")
                .getResult(GraphMorphism.class);

        GraphMorphism m2 = getContextCreatingBuilder() // non valid
                .edge("A", "f", "B")
                .edge("B'", "g" ,"A")
                .graph("G2")
                .codomain(Universe.CYCLE)
                .map(Name.identifier("A"), Universe.CYCLE_FWD.getSource())
                .map(Name.identifier("B"), Universe.CYCLE_FWD.getTarget())
                .map(Name.identifier("B'"), Universe.CYCLE_FWD.getTarget())
                .map(Name.identifier("f"), Universe.CYCLE_FWD.getLabel())
                .map(Name.identifier("g"), Universe.CYCLE_BWD.getLabel())
                .morphism("m2")
                .getResult(GraphMorphism.class);


        GraphMorphism m3 = getContextCreatingBuilder() // set valued but valid
                .edge("A", "f", "B")
                .edge("A", "f'", "B'")
                .edge("B", "g", "A")
                .edge("B'", "g'", "A")
                .graph("G3")
                .codomain(Universe.CYCLE)
                .map(Name.identifier("A"), Universe.CYCLE_FWD.getSource())
                .map(Name.identifier("B"), Universe.CYCLE_FWD.getTarget())
                .map(Name.identifier("B'"), Universe.CYCLE_FWD.getTarget())
                .map(Name.identifier("f"), Universe.CYCLE_FWD.getLabel())
                .map(Name.identifier("f'"), Universe.CYCLE_FWD.getLabel())
                .map(Name.identifier("g"), Universe.CYCLE_BWD.getLabel())
                .map(Name.identifier("g'"), Universe.CYCLE_BWD.getLabel())
                .morphism("m3")
                .getResult(GraphMorphism.class);

        assertTrue(Invert.getInstance().isSatisfied(m0));
        assertTrue(Invert.getInstance().isSatisfied(m1));
        assertFalse(Invert.getInstance().isSatisfied(m2));
        assertTrue(Invert.getInstance().isSatisfied(m3));
    }

    @Test
    public void testProxy() throws GraphError {
        GraphMorphism m0 = getContextCreatingBuilder().domain(Universe.EMPTY).codomain(Universe.ARROW).morphism("m0").getResult(GraphMorphism.class);

        assertTrue(Proxy.getInstance().isSatisfied(m0));
        assertTrue(InverseProxy.getInstance().isSatisfied(m0));

        GraphMorphism m1 = getContextCreatingBuilder()
                .edge("A", "f", "B")
                .graph("G1")
                .codomain(Universe.ARROW)
                .map(Name.identifier("A"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("f"), Universe.ARROW_LBL_NAME)
                .map(Name.identifier("B"), Universe.ARROW_TRG_NAME)
                .morphism("m0")
                .getResult(GraphMorphism.class);

        assertTrue(Proxy.getInstance().isSatisfied(m1));
        assertTrue(InverseProxy.getInstance().isSatisfied(m1));


        GraphMorphism m2 = getContextCreatingBuilder()
                .node("A")
                .node("B")
                .graph("G2")
                .map(Name.identifier("A"), Universe.ARROW_SRC_NAME)
                .map(Name.identifier("B"), Universe.ARROW_SRC_NAME)
                .morphism("m2")
                .getResult(GraphMorphism.class);

        assertTrue(InverseProxy.getInstance().isExecutedCorrectly(m2));
        assertFalse(Proxy.getInstance().isExecutedCorrectly(m2));

        assertStreamEquals(m2.domain().elements(), InverseProxy.getInstance().execute(m2, getExecutionContext()).domain().elements());
        GraphMorphism resultM2 = Proxy.getInstance().execute(m2, getExecutionContext());
        assertTrue(Proxy.getInstance().isSatisfied(resultM2));
        assertTrue(InverseProxy.getInstance().isSatisfied(resultM2));
        assertEquals(2, resultM2.allInstances(Universe.ARROW_THE_ARROW).count());
        assertEquals(2, resultM2.allNodeInstances(Universe.ARROW_TRG_NAME).count());


        GraphMorphism m3 = getContextCreatingBuilder()
                .node("A")
                .node("B")
                .graph("G2")
                .map(Name.identifier("A"), Universe.ARROW_TRG_NAME)
                .map(Name.identifier("B"), Universe.ARROW_TRG_NAME)
                .morphism("m3")
                .getResult(GraphMorphism.class);

        assertTrue(Proxy.getInstance().isExecutedCorrectly(m3));
        assertFalse(InverseProxy.getInstance().isExecutedCorrectly(m3));
        GraphMorphism resultM3 = InverseProxy.getInstance().execute(m3, getExecutionContext());
        assertTrue(Proxy.getInstance().isSatisfied(resultM3));
        assertTrue(InverseProxy.getInstance().isSatisfied(resultM3));
        assertEquals(2, resultM3.allInstances(Universe.ARROW_THE_ARROW).count());
        assertEquals(2, resultM3.allNodeInstances(Universe.ARROW_TRG_NAME).count());
    }


    @Test
    public void testReflexiveClosure() throws GraphError {
        GraphMorphism m0 = getContextCreatingBuilder().domain(Universe.EMPTY).codomain(Universe.LOOP).morphism("m0").getResult(GraphMorphism.class);
        assertTrue(ReflexiveClosure.getInstance().isSatisfied(m0));

        GraphMorphism m1 = getContextCreatingBuilder()
                .node("A")
                .node("B")
                .graph("G1")
                .codomain(Universe.LOOP)
                .map(Name.identifier("A"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("B"), Universe.LOOP_THE_LOOP.getSource())
                .morphism("M1")
                .getResult(GraphMorphism.class);

        assertFalse(ReflexiveClosure.getInstance().isSatisfied(m1));
        GraphMorphism result1 = ReflexiveClosure.getInstance().execute(m1, getExecutionContext());
        assertTrue(ReflexiveClosure.getInstance().isSatisfied(result1));
        assertEquals(2, result1.allInstances(Universe.LOOP_THE_LOOP).count());


        GraphMorphism m2 = getContextCreatingBuilder()
                .edge("A", "id_A", "B")
                .edge("B", "id_B", "A")
                .graph("G2")
                .codomain(Universe.LOOP)
                .map(Name.identifier("A"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("B"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("id_A"), Universe.LOOP_THE_LOOP.getLabel())
                .map(Name.identifier("id_B"), Universe.LOOP_THE_LOOP.getLabel())
                .morphism("M2")
                .getResult(GraphMorphism.class);
        assertFalse(ReflexiveClosure.getInstance().isSatisfied(m2));
        GraphMorphism result2 = ReflexiveClosure.getInstance().fix(m2, getExecutionContext());
        assertTrue(ReflexiveClosure.getInstance().isSatisfied(result2));
        assertEquals(2, result1.allInstances(Universe.LOOP_THE_LOOP).count());
    }

    @Test
    public void testTransitiveClosure() throws GraphError {
        GraphMorphism m0 = getContextCreatingBuilder()
                .edge("A", "f", "B")
                .graph("G0")
                .codomain(Universe.LOOP)
                .map(Name.identifier("A"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("B"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("f"), Universe.LOOP_THE_LOOP.getLabel())
                .morphism("m0")
                .getResult(GraphMorphism.class);
        assertTrue(TransitiveClosure.getInstance().isSatisfied(m0));

        GraphMorphism m1 = getContextCreatingBuilder()
                .edge("A", "f", "B")
                .edge("B", "g", "C")
                .edge("C", "h", "D")
                .edge("D", "k", "E")
                .graph("G1")
                .codomain(Universe.LOOP)
                .map(Name.identifier("A"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("B"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("C"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("D"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("E"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("f"), Universe.LOOP_THE_LOOP.getLabel())
                .map(Name.identifier("g"), Universe.LOOP_THE_LOOP.getLabel())
                .map(Name.identifier("h"), Universe.LOOP_THE_LOOP.getLabel())
                .map(Name.identifier("k"), Universe.LOOP_THE_LOOP.getLabel())
                .morphism("m1")
                .getResult(GraphMorphism.class);
        assertFalse(TransitiveClosure.getInstance().isSatisfied(m1));
        GraphMorphism result1 = TransitiveClosure.getInstance().execute(m1, getExecutionContext());
        assertTrue(TransitiveClosure.getInstance().isSatisfied(result1));
        assertEquals(10, result1.allInstances(Universe.LOOP_THE_LOOP).count());

        GraphMorphism m2 = getContextCreatingBuilder()
                .edge("1", "10", "0")
                .edge("2", "21", "1")
                .edge("3", "31", "1")
                .edge("2", "20", "0")
                .edge("A", "f", "B")
                .graph(Name.identifier("G_2"))
                .codomain(Universe.LOOP)
                .map(Name.identifier("1"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("2"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("3"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("A"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("B"), Universe.LOOP_THE_LOOP.getSource())
                .map(Name.identifier("10"), Universe.LOOP_THE_LOOP.getLabel())
                .map(Name.identifier("21"), Universe.LOOP_THE_LOOP.getLabel())
                .map(Name.identifier("31"), Universe.LOOP_THE_LOOP.getLabel())
                .map(Name.identifier("20"), Universe.LOOP_THE_LOOP.getLabel())
                .map(Name.identifier("f"), Universe.LOOP_THE_LOOP.getLabel())
                .morphism("m2")
                .getResult(GraphMorphism.class);

        assertFalse(TransitiveClosure.getInstance().isSatisfied(m2));
        GraphMorphism result2 = TransitiveClosure.getInstance().fix(m2, getExecutionContext());
        assertTrue(TransitiveClosure.getInstance().isSatisfied(result2));
        assertEquals(m2.allInstances(Universe.LOOP_THE_LOOP).count() + 1, result2.allInstances(Universe.LOOP_THE_LOOP).count());
    }

    @Test
    public void testProduct() throws GraphError {
        GraphMorphism m0 = getContextCreatingBuilder()
                .node("A")
                .graph("G0")
                .codomain(Universe.SPAN)
                .map(Name.identifier("A"), Universe.SPAN_LEFT_LEG.getTarget())
                .morphism("m0")
                .getResult(GraphMorphism.class);

        assertTrue(NodeProduct.getInstance().isSatisfied(m0));

        GraphMorphism m1 = getContextCreatingBuilder()
                .node("A")
                .node("A'")
                .node("B")
                .node("B'")
                .graph("G1")
                .codomain(Universe.SPAN)
                .map(Name.identifier("A"), Universe.SPAN_LEFT_LEG.getTarget())
                .map(Name.identifier("A'"), Universe.SPAN_LEFT_LEG.getTarget())
                .map(Name.identifier("B"), Universe.SPAN_RIGHT_LEG.getTarget())
                .map(Name.identifier("B'"), Universe.SPAN_RIGHT_LEG.getTarget())
                .morphism("m1")
                .getResult(GraphMorphism.class);

        assertFalse(NodeProduct.getInstance().isSatisfied(m1));
        GraphMorphism result1 = NodeProduct.getInstance().execute(m1, getExecutionContext());
        assertTrue(NodeProduct.getInstance().isSatisfied(result1));
        addExpectedTriple(Triple.node(Name.identifier("A").pair(Name.identifier("B"))));
        addExpectedTriple(Triple.node(Name.identifier("A'").pair(Name.identifier("B"))));
        addExpectedTriple(Triple.node(Name.identifier("A'").pair(Name.identifier("B'"))));
        addExpectedTriple(Triple.node(Name.identifier("A").pair(Name.identifier("B'"))));
        assertStreamEquals(expected(), result1.allInstances(Universe.SPAN_LEFT_LEG.getSource()));
        assertEquals(Name.identifier("A"), result1.allOutgoingInstances(Universe.SPAN_LEFT_LEG, Name.identifier("A").pair(Name.identifier("B"))).findFirst().get().getTarget());
        assertEquals(Name.identifier("B"), result1.allOutgoingInstances(Universe.SPAN_RIGHT_LEG, Name.identifier("A").pair(Name.identifier("B"))).findFirst().get().getTarget());

        GraphMorphism m2 = getContextCreatingBuilder()
                .node("A")
                .node("A'")
                .node("B")
                .edge("C", "p_A", "A")
                .edge("C'", "p_A'", "A'")
                .graph("G2")
                .codomain(Universe.SPAN)
                .map(Name.identifier("A"), Universe.SPAN_LEFT_LEG.getTarget())
                .map(Name.identifier("A'"), Universe.SPAN_LEFT_LEG.getTarget())
                .map(Name.identifier("B"), Universe.SPAN_RIGHT_LEG.getTarget())
                .map(Name.identifier("C'"), Universe.SPAN_RIGHT_LEG.getSource())
                .map(Name.identifier("C"), Universe.SPAN_RIGHT_LEG.getSource())
                .map(Name.identifier("p_A"), Universe.SPAN_LEFT_LEG.getLabel())
                .map(Name.identifier("p_A'"), Universe.SPAN_LEFT_LEG.getLabel())
                .morphism("m2")
                .getResult(GraphMorphism.class);

        assertFalse(NodeProduct.getInstance().isSatisfied(m2));
        GraphMorphism result2 = NodeProduct.getInstance().fix(m2, getExecutionContext());
        assertTrue(NodeProduct.getInstance().isSatisfied(result2));
    }

    @Test
    public void testCoproduct() throws GraphError {
        GraphMorphism m0 = getContextCreatingBuilder().domain(Universe.EMPTY).codomain(Universe.COSPAN).morphism("m0").getResult(GraphMorphism.class);
        assertTrue(NodeCoproduct.getInstance().isSatisfied(m0));

        GraphMorphism m1 = getContextCreatingBuilder()
                .node("A")
                .graph("G1")
                .codomain(Universe.COSPAN)
                .map(Name.identifier("A"), Universe.COSPAN_LEFT_LEG.getSource())
                .morphism("m1")
                .getResult(GraphMorphism.class);
        assertFalse(NodeCoproduct.getInstance().isSatisfied(m1));
        GraphMorphism result1 = NodeCoproduct.getInstance().execute(m1, getExecutionContext());
        assertTrue(NodeCoproduct.getInstance().isSatisfied(result1));
        assertEquals(1, result1.allInstances(Universe.COSPAN_LEFT_LEG).count());
        assertEquals(1, result1.allInstances(Universe.COSPAN_LEFT_LEG.getTarget()).count());


        GraphMorphism m2 = getContextCreatingBuilder()
                .node("1")
                .node("2")
                .node("3")
                .node("A")
                .node("B")
                .node("1A")
                .node("1B")
                .node("2A")
                .node("2B")
                .node("3A")
                .node("3B")
                .graph("G2")
                .codomain(Universe.COSPAN)
                .map(Name.identifier("1"), Universe.COSPAN_LEFT_LEG.getSource())
                .map(Name.identifier("2"), Universe.COSPAN_LEFT_LEG.getSource())
                .map(Name.identifier("3"), Universe.COSPAN_LEFT_LEG.getSource())
                .map(Name.identifier("A"), Universe.COSPAN_RIGHT_LEG.getSource())
                .map(Name.identifier("B"), Universe.COSPAN_RIGHT_LEG.getSource())
                .map(Name.identifier("1A"), Universe.COSPAN_RIGHT_LEG.getTarget())
                .map(Name.identifier("1B"), Universe.COSPAN_RIGHT_LEG.getTarget())
                .map(Name.identifier("2A"), Universe.COSPAN_RIGHT_LEG.getTarget())
                .map(Name.identifier("2B"), Universe.COSPAN_RIGHT_LEG.getTarget())
                .map(Name.identifier("3A"), Universe.COSPAN_RIGHT_LEG.getTarget())
                .map(Name.identifier("3B"), Universe.COSPAN_RIGHT_LEG.getTarget())
                .morphism("m2")
                .getResult(GraphMorphism.class);
        assertFalse(NodeCoproduct.getInstance().isSatisfied(m2));
        GraphMorphism result2 = NodeCoproduct.getInstance().fix(m2, getExecutionContext());
        assertTrue(NodeCoproduct.getInstance().isSatisfied(result2));
        assertEquals(3, result2.allInstances(Universe.COSPAN_LEFT_LEG).count());
        assertEquals(2, result2.allInstances(Universe.COSPAN_RIGHT_LEG).count());
    }


    @Test
    public void testProductUniversalProperty() throws GraphError {
        GraphMorphism m0 = getContextCreatingBuilder()
                .edge(Name.identifier("a").pair(Name.identifier("1")), Name.identifier("pi_A"), Name.identifier("a"))
                .edge(Name.identifier("a").pair(Name.identifier("1")), Name.identifier("pi_1"), Name.identifier("1"))
                .edge("X", "f", "1")
                .graph("G0")
                .codomain(Universe.PRODUCT_MEDIATOR_DIAGRAM)
                .map(Name.identifier("a").pair(Name.identifier("1")), Universe.SPAN_LEFT_LEG.getSource())
                .map(Name.identifier("pi_A"), Universe.SPAN_LEFT_LEG.getLabel())
                .map(Name.identifier("pi_1"), Universe.SPAN_RIGHT_LEG.getLabel())
                .map(Name.identifier("a"), Universe.SPAN_LEFT_LEG.getTarget())
                .map(Name.identifier("1"), Universe.SPAN_RIGHT_LEG.getTarget())
                .map(Name.identifier("f"), Universe.PRODUCT_RIGHT_COMPARATOR_EDGE.getLabel())
                .map(Name.identifier("X"), Universe.PRODUCT_RIGHT_COMPARATOR_EDGE.getSource())
                .morphism(Name.identifier("m0"))
                .getResult(GraphMorphism.class);

        assertTrue(ProductUniversalProp.getInstance().isSatisfied(m0));


        GraphMorphism m1 = getContextCreatingBuilder()
                .edge(Name.identifier("a").pair(Name.identifier("1")), Name.identifier("pi_A1"), Name.identifier("a"))
                .edge(Name.identifier("b").pair(Name.identifier("1")), Name.identifier("pi_B1"), Name.identifier("b"))
                .edge(Name.identifier("a").pair(Name.identifier("2")), Name.identifier("pi_A2"), Name.identifier("a"))
                .edge(Name.identifier("b").pair(Name.identifier("2")), Name.identifier("pi_B2"), Name.identifier("b"))
                .edge(Name.identifier("a").pair(Name.identifier("1")), Name.identifier("pi_1A"), Name.identifier("1"))
                .edge(Name.identifier("b").pair(Name.identifier("1")), Name.identifier("pi_1B"), Name.identifier("1"))
                .edge(Name.identifier("a").pair(Name.identifier("2")), Name.identifier("pi_2A"), Name.identifier("2"))
                .edge(Name.identifier("b").pair(Name.identifier("2")), Name.identifier("pi_2B"), Name.identifier("2"))
                .edge("X", "f", "a")
                .edge("X", "g", "b")
                .edge("X", "h", "1")
                .edge("Z", "i", "a")
                .edge("Y", "j", "2")
                .graph("G1")
                .codomain(Universe.PRODUCT_MEDIATOR_DIAGRAM)
                .map(Name.identifier("a").pair(Name.identifier("1")), Universe.SPAN_LEFT_LEG.getSource())
                .map(Name.identifier("a").pair(Name.identifier("2")), Universe.SPAN_LEFT_LEG.getSource())
                .map(Name.identifier("b").pair(Name.identifier("1")), Universe.SPAN_LEFT_LEG.getSource())
                .map(Name.identifier("b").pair(Name.identifier("2")), Universe.SPAN_LEFT_LEG.getSource())
                .map(Name.identifier("pi_A1"), Universe.SPAN_LEFT_LEG.getLabel())
                .map(Name.identifier("pi_B1"), Universe.SPAN_LEFT_LEG.getLabel())
                .map(Name.identifier("pi_A2"), Universe.SPAN_LEFT_LEG.getLabel())
                .map(Name.identifier("pi_B2"), Universe.SPAN_LEFT_LEG.getLabel())
                .map(Name.identifier("pi_1A"), Universe.SPAN_RIGHT_LEG.getLabel())
                .map(Name.identifier("pi_1B"), Universe.SPAN_RIGHT_LEG.getLabel())
                .map(Name.identifier("pi_2A"), Universe.SPAN_RIGHT_LEG.getLabel())
                .map(Name.identifier("pi_2B"), Universe.SPAN_RIGHT_LEG.getLabel())
                .map(Name.identifier("a"), Universe.SPAN_LEFT_LEG.getTarget())
                .map(Name.identifier("b"), Universe.SPAN_LEFT_LEG.getTarget())
                .map(Name.identifier("1"), Universe.SPAN_RIGHT_LEG.getTarget())
                .map(Name.identifier("2"), Universe.SPAN_RIGHT_LEG.getTarget())
                .map(Name.identifier("f"), Universe.PRODUCT_LEFT_COMPARATOR_EDGE.getLabel())
                .map(Name.identifier("g"), Universe.PRODUCT_LEFT_COMPARATOR_EDGE.getLabel())
                .map(Name.identifier("h"), Universe.PRODUCT_RIGHT_COMPARATOR_EDGE.getLabel())
                .map(Name.identifier("i"), Universe.PRODUCT_LEFT_COMPARATOR_EDGE.getLabel())
                .map(Name.identifier("j"), Universe.PRODUCT_RIGHT_COMPARATOR_EDGE.getSource())
                .map(Name.identifier("X"), Universe.PRODUCT_RIGHT_COMPARATOR_EDGE.getSource())
                .map(Name.identifier("Z"), Universe.PRODUCT_RIGHT_COMPARATOR_EDGE.getSource())
                .map(Name.identifier("Z"), Universe.PRODUCT_RIGHT_COMPARATOR_EDGE.getSource())
                .morphism(Name.identifier("m1"))
                .getResult(GraphMorphism.class);

        assertFalse(ProductUniversalProp.getInstance().isSatisfied(m1));
        GraphMorphism result1 = ProductUniversalProp.getInstance().execute(m1, getExecutionContext());
        assertTrue(ProductUniversalProp.getInstance().isSatisfied(result1));
        assertEquals(2, result1.allInstances(Universe.PRODUCT_MEDIATOR_EDGE).count());

    }

    @Test
    public void testCoproductUniversalProperty() throws GraphError {
        GraphMorphism m0 = getContextCreatingBuilder()
                .edge("A", "in_A", "A'")
                .edge("B", "in_B", "B'")
                .edge("1", "in_1", "1'")
                .graph("G_0")
                .codomain(Universe.COPRODUCT_MEDIATOR_DIAGRAM)
                .map(Name.identifier("A"), Universe.COSPAN_LEFT_LEG.getSource())
                .map(Name.identifier("B"), Universe.COSPAN_LEFT_LEG.getSource())
                .map(Name.identifier("in_A"), Universe.COSPAN_LEFT_LEG.getLabel())
                .map(Name.identifier("in_B"), Universe.COSPAN_LEFT_LEG.getLabel())
                .map(Name.identifier("A'"), Universe.COSPAN_LEFT_LEG.getTarget())
                .map(Name.identifier("B'"), Universe.COSPAN_LEFT_LEG.getTarget())
                .map(Name.identifier("1"), Universe.COSPAN_RIGHT_LEG.getSource())
                .map(Name.identifier("in_1"), Universe.COSPAN_RIGHT_LEG.getLabel())
                .map(Name.identifier("1'"), Universe.COSPAN_RIGHT_LEG.getTarget())
                .morphism("M_0")
                .getResult(GraphMorphism.class);

        assertTrue(CoproductUniversalProp.getInstance().isSatisfied(m0));


        GraphMorphism m1 = getContextCreatingBuilder()
                .edge("A", "in_A", "A'")
                .edge("B", "in_B", "B'")
                .edge("1", "in_1", "1'")
                .edge("A", "f", "X")
                .edge("1", "g", "X")
                .edge("1", "g'", "Y")
                .graph("G_1")
                .codomain(Universe.COPRODUCT_MEDIATOR_DIAGRAM)
                .map(Name.identifier("A"), Universe.COSPAN_LEFT_LEG.getSource())
                .map(Name.identifier("B"), Universe.COSPAN_LEFT_LEG.getSource())
                .map(Name.identifier("in_A"), Universe.COSPAN_LEFT_LEG.getLabel())
                .map(Name.identifier("in_B"), Universe.COSPAN_LEFT_LEG.getLabel())
                .map(Name.identifier("A'"), Universe.COSPAN_LEFT_LEG.getTarget())
                .map(Name.identifier("B'"), Universe.COSPAN_LEFT_LEG.getTarget())
                .map(Name.identifier("1"), Universe.COSPAN_RIGHT_LEG.getSource())
                .map(Name.identifier("in_1"), Universe.COSPAN_RIGHT_LEG.getLabel())
                .map(Name.identifier("1'"), Universe.COSPAN_RIGHT_LEG.getTarget())
                .map(Name.identifier("f"), Universe.COPRODUCT_LEFT_COMPARATOR_EDGE.getLabel())
                .map(Name.identifier("g"), Universe.COPRODUCT_RIGHT_COMPARATOR_EDGE.getLabel())
                .map(Name.identifier("g'"), Universe.COPRODUCT_RIGHT_COMPARATOR_EDGE.getLabel())
                .map(Name.identifier("X"), Universe.COPRODUCT_RIGHT_COMPARATOR_EDGE.getTarget())
                .map(Name.identifier("Y"), Universe.COPRODUCT_RIGHT_COMPARATOR_EDGE.getTarget())
                .morphism("M_1")
                .getResult(GraphMorphism.class);

        assertFalse(CoproductUniversalProp.getInstance().isSatisfied(m1));
        GraphMorphism result1 = CoproductUniversalProp.getInstance().execute(m1, getExecutionContext());
        assertTrue(CoproductUniversalProp.getInstance().isSatisfied(result1));
        assertEquals(3, result1.allInstances(Universe.CoPRODUCT_MEDIATOR_EDGE).count());

    }


    @Test
    public void testEqualizer() throws GraphError {
        GraphMorphism instance = getContextCreatingBuilder()
                .edge("x", "f", "1")
                .edge("x", "g", "1")
                .edge("y", "p", "1")
                .edge("y", "q", "2")
                .edge("z", "h", "2")
                .edge("z", "h'", "2")
                .edge("z", "k", "3")
                .edge("z", "i", "2")
                .edge("z", "j", "3")
                .graph("EQUALIZER_TEST_GRAPH")
                .codomain(Universe.EQUALIZER_DIAGRAM)
                .map(Name.identifier("x"), Universe.CELL_LHS.getSource())
                .map(Name.identifier("y"), Universe.CELL_LHS.getSource())
                .map(Name.identifier("z"), Universe.CELL_LHS.getSource())
                .map(Name.identifier("f"), Universe.CELL_LHS.getLabel())
                .map(Name.identifier("p"), Universe.CELL_LHS.getLabel())
                .map(Name.identifier("q"), Universe.CELL_RHS.getLabel())
                .map(Name.identifier("h"), Universe.CELL_LHS.getLabel())
                .map(Name.identifier("h'"), Universe.CELL_LHS.getLabel())
                .map(Name.identifier("k"), Universe.CELL_LHS.getLabel())
                .map(Name.identifier("g"), Universe.CELL_RHS.getLabel())
                .map(Name.identifier("r"), Universe.CELL_RHS.getLabel())
                .map(Name.identifier("i"), Universe.CELL_RHS.getLabel())
                .map(Name.identifier("j"), Universe.CELL_RHS.getLabel())
                .map(Name.identifier("1"), Universe.CELL_LHS.getTarget())
                .map(Name.identifier("2"), Universe.CELL_LHS.getTarget())
                .map(Name.identifier("3"), Universe.CELL_LHS.getTarget())
                .morphism("EQUALIZER_TEST_MORPH")
                .getResult(GraphMorphism.class);

        assertFalse(Equalizer.getInstance().isSatisfied(instance));
        GraphMorphism result = Equalizer.getInstance().execute(instance, getExecutionContext());
        assertTrue(Equalizer.getInstance().isSatisfied(result));
        Set<Name> resultSet = result.allInstances(Universe.EQUALIZER_MEDIATOR).map(Triple::getTarget).collect(Collectors.toSet());
        assertEquals(2, resultSet.size());
        assertTrue(resultSet.contains(Name.identifier("x")));
        assertTrue(resultSet.contains(Name.identifier("z")));
        assertFalse(resultSet.contains(Name.identifier("y")));
    }

    @Test
    public void testCoequalizer() throws GraphError {
        GraphMorphism instance = getContextCreatingBuilder()
                .edge("1", "1a", "a")
                .edge("2", "2a", "a")
                .edge("2", "2b", "b")
                .edge("3", "3b", "b")
                .edge("4", "4c", "c")
                .edge("4", "4c'", "c")
                .graph("COEQUALIZER_TEST_GRAPH")
                .codomain(Universe.COEQUALIZER_DIAGRAM)
                .map(Name.identifier("1"), Universe.CELL_LHS.getSource())
                .map(Name.identifier("2"), Universe.CELL_LHS.getSource())
                .map(Name.identifier("3"), Universe.CELL_LHS.getSource())
                .map(Name.identifier("4"), Universe.CELL_LHS.getSource())
                .map(Name.identifier("1a"), Universe.CELL_LHS.getLabel())
                .map(Name.identifier("4c"), Universe.CELL_LHS.getLabel())
                .map(Name.identifier("2a"), Universe.CELL_LHS.getLabel())
                .map(Name.identifier("2b"), Universe.CELL_RHS.getLabel())
                .map(Name.identifier("3b"), Universe.CELL_RHS.getLabel())
                .map(Name.identifier("4c'"), Universe.CELL_RHS.getLabel())
                .map(Name.identifier("a"), Universe.CELL_RHS.getTarget())
                .map(Name.identifier("b"), Universe.CELL_RHS.getTarget())
                .map(Name.identifier("c"), Universe.CELL_RHS.getTarget())
                .morphism("COEQUALIZER_TEST_MORPH")
                .getResult(GraphMorphism.class);

        assertFalse(Coequalizer.getInstance().isSatisfied(instance));
        GraphMorphism result = Coequalizer.getInstance().execute(instance, getExecutionContext());
        assertTrue(Coequalizer.getInstance().isSatisfied(result));
        assertEquals(2, result.allNodeInstances(Universe.COEQUALIZER_MEDIATOR.getTarget()).count());
        assertEquals(result.allOutgoingInstances(Universe.COEQUALIZER_MEDIATOR, Name.identifier("a")).findFirst().get().getTarget(), result.allOutgoingInstances(Universe.COEQUALIZER_MEDIATOR, Name.identifier("b")).findFirst().get().getTarget());
        assertNotEquals(result.allOutgoingInstances(Universe.COEQUALIZER_MEDIATOR, Name.identifier("a")).findFirst().get().getTarget(), result.allOutgoingInstances(Universe.COEQUALIZER_MEDIATOR, Name.identifier("c")).findFirst().get().getTarget());

    }

}
