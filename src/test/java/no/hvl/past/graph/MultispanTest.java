package no.hvl.past.graph;

import com.google.common.collect.Sets;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static no.hvl.past.graph.ExampleGraphs.COMMONALITIES;
import static no.hvl.past.graph.ExampleGraphs.PATIENTS;
import static no.hvl.past.graph.ExampleGraphs.APPOINTMENTS;
import static no.hvl.past.graph.ExampleGraphs.BLOODTESTS;

import static org.junit.Assert.*;

public class MultispanTest extends AbstractTest {

//    @Test
//    public void testColimit() throws GraphError {
//
//
//        Multispan multimodel = Multispan.create(Name.identifier("multimodel"),
//                ExampleGraphs.PATIENTS_APPOINTMENTS_BLOODTEST_COMMONALITY_GRAPH,
//                Arrays.asList(ExampleGraphs.PATIENTS_GRAPH, ExampleGraphs.APPOINTMENTS_GRAPH, ExampleGraphs.BLOODTESTS_GRAPH),
//                Arrays.asList(ExampleGraphs.PATIENTS_PROJECTION, ExampleGraphs.PATIENTS_PROJECTION, ExampleGraphs.BLOODTEST_PROJECTION));
//
//
//        Graph colimit = multimodel.colimit(Name.identifier("M+"));
//
//        // TODO use correct names
//        Name mergedPat = Name.merge(Sets.newHashSet(pid(PATIENTS, "Patient"), pid(APPOINTMENTS, "Patient"), pid(BLOODTESTS, "Subject"), pid(COMMONALITIES, "Pat")));
//        Name mergedNumber = Name.merge(Sets.newHashSet(pid(COMMONALITIES, "Number"), pid(PATIENTS, "Number"), pid(APPOINTMENTS, "Number"), pid(BLOODTESTS, "Number")));
//        Name mergedObs = Name.merge(Sets.newHashSet(pid(COMMONALITIES, "Obs"), pid(PATIENTS, "Observation"), pid(BLOODTESTS, "BloodSample")));
//        Name mergedId = Name.merge(Sets.newHashSet(pid(PATIENTS, "patientNo"), pid(APPOINTMENTS, "id"), pid(BLOODTESTS, "patientId"), pid(COMMONALITIES, "id")));
//        Name mergeOf = Name.merge(Sets.newHashSet(pid(PATIENTS, "of"), pid(BLOODTESTS, "takenFrom"), pid(COMMONALITIES, "of")));
//        Name mergeTimestamp = Name.merge(Sets.newHashSet(pid(PATIENTS, "Timestamp"), pid(APPOINTMENTS, "Timestamp") ,pid(BLOODTESTS, "Timestamp"), pid(COMMONALITIES, "Timestamp")));
//        Name mergeTakenAt = Name.merge(Sets.newHashSet(pid(PATIENTS, "recordedAt"), pid(BLOODTESTS, "takenAt"), pid(COMMONALITIES, "recorded")));
//
//
//
//        GraphImpl expected = new GraphBuilder("expected")
//                .edge(mergedPat, mergedId, mergedNumber)
//                .edge(mergedPat, pid(PATIENTS, "assigned"), pid(PATIENTS, "Bed"))
//                .edge(pid(PATIENTS, "Bed"), pid(PATIENTS, "stationNo"), mergedNumber)
//                .edge(mergedObs, mergeOf, mergedPat)
//                .edge(mergedObs, mergeTakenAt, mergeTimestamp)
//                .edge(mergedObs, pid(PATIENTS, "details"), pid(PATIENTS, "String"))
//                .edge(pid(APPOINTMENTS, "Appointment"), pid(APPOINTMENTS, "patient"), mergedPat)
//                .edge(pid(APPOINTMENTS, "Appointment"), pid(APPOINTMENTS, "begin"), mergeTimestamp)
//                .edge(pid(APPOINTMENTS, "Appointment"), pid(APPOINTMENTS, "end"), mergeTimestamp)
//                .edge(pid(APPOINTMENTS, "Appointment"), pid(APPOINTMENTS, "doctor"), pid(APPOINTMENTS, "Doctor"))
//                .edge(pid(APPOINTMENTS, "Doctor"), pid(APPOINTMENTS, "worksAtStationNo"),mergedNumber)
//                .edge(pid(APPOINTMENTS, "Doctor"), pid(APPOINTMENTS, "shiftStarts"), pid(APPOINTMENTS, "Time"))
//                .edge(pid(APPOINTMENTS, "Doctor"), pid(APPOINTMENTS, "shiftEnds"), pid(APPOINTMENTS, "Time"))
//                .edge(mergedObs, pid(BLOODTESTS, "isCritical"), pid(BLOODTESTS, "Bool"))
//                .edge(pid(BLOODTESTS, "CholesterolTest"), pid(BLOODTESTS, "CholesterolTest.basedOn"), mergedObs)
//                .edge(pid(BLOODTESTS, "CholesterolTest"), pid(BLOODTESTS, "totalCholesterol"), mergedNumber)
//                .edge(pid(BLOODTESTS, "CholesterolTest"), pid(BLOODTESTS, "hdlCholesterol"), mergedNumber)
//                .edge(pid(BLOODTESTS, "GlucoseTest"), pid(BLOODTESTS, "GlucoseTest.basedOn"), mergedObs)
//                .edge(pid(BLOODTESTS, "GlucoseTest"), pid(BLOODTESTS, "glucoseLevel"), mergedNumber)
//                .build();
//
//        Set<Triple> unexpected1 = colimit.elements().filter(t -> !expected.contains(t)).collect(Collectors.toSet());
//
//        assertTrue(unexpected1.isEmpty());
//
//        assertEquals(expected.getElements(), colimit.elements().collect(Collectors.toSet()));
//    }
//
//    @Test
//    public void testInternalize() throws GraphError {
//
//
//        Multispan multimodel = Multispan.create(Name.identifier("multimodel"),
//                ExampleGraphs.PATIENTS_APPOINTMENTS_BLOODTEST_COMMONALITY_GRAPH,
//                Arrays.asList(ExampleGraphs.PATIENTS_GRAPH, ExampleGraphs.APPOINTMENTS_GRAPH, ExampleGraphs.BLOODTESTS_GRAPH),
//                Arrays.asList(ExampleGraphs.PATIENTS_PROJECTION, ExampleGraphs.APPOINTMENTS_PROJECTION, ExampleGraphs.BLOODTEST_PROJECTION));
//
//        Graph result = multimodel.internalize(multimodel.getName());
//
//        GraphImpl expected = new GraphBuilder(Name.identifier("multimodel"))
//                .edge(pid("patients", "Patient"), pid("patients", "patientNo"), pid("patients", "Number"))
//                .edge(pid("patients", "Patient"), pid("patients", "assigned"), pid("patients", "Bed"))
//                .edge(pid("patients", "Bed"), pid("patients", "stationNo"), pid("patients", "Number"))
//                .edge(pid("patients", "Observation"), pid("patients", "of"), pid("patients", "Patient"))
//                .edge(pid("patients", "Observation"), pid("patients", "recordedAt"), pid("patients", "Timestamp"))
//                .edge(pid("patients", "Observation"), pid("patients", "details"), pid("patients", "String"))
//                .edge(pid("appointments", "Patient"), pid("appointments", "id"), pid("appointments", "Number"))
//                .edge(pid("appointments", "Appointment"), pid("appointments", "patient"), pid("appointments", "Patient"))
//                .edge(pid("appointments", "Appointment"), pid("appointments", "begin"), pid("appointments", "Timestamp"))
//                .edge(pid("appointments", "Appointment"), pid("appointments", "end"), pid("appointments", "Timestamp"))
//                .edge(pid("appointments", "Appointment"), pid("appointments", "doctor"), pid("appointments", "Doctor"))
//                .edge(pid("appointments", "Doctor"), pid("appointments", "worksAtStationNo"), pid("appointments", "Number"))
//                .edge(pid("appointments", "Doctor"), pid("appointments", "shiftStarts"), pid("appointments", "Time"))
//                .edge(pid("appointments", "Doctor"), pid("appointments", "shiftEnds"), pid("appointments", "Time"))
//                .edge(pid("bloodtests", "Subject"), pid("bloodtests", "patientId"), pid("bloodtests", "Number"))
//                .edge(pid("bloodtests", "BloodSample"), pid("bloodtests", "takenFrom"), pid("bloodtests", "Subject"))
//                .edge(pid("bloodtests", "BloodSample"), pid("bloodtests", "takenAt"), pid("bloodtests", "Timestamp"))
//                .edge(pid("bloodtests", "BloodSample"), pid("bloodtests", "isCritical"), pid("bloodtests", "Bool"))
//                .edge(pid("bloodtests", "CholesterolTest"), pid("bloodtests", "CholesterolTest.basedOn"), pid("bloodtests", "BloodSample"))
//                .edge(pid("bloodtests", "CholesterolTest"), pid("bloodtests", "totalCholesterol"), pid("bloodtests", "Number"))
//                .edge(pid("bloodtests", "CholesterolTest"), pid("bloodtests", "hdlCholesterol"), pid("bloodtests", "Number"))
//                .edge(pid("bloodtests", "GlucoseTest"), pid("bloodtests", "GlucoseTest.basedOn"), pid("bloodtests", "BloodSample"))
//                .edge(pid("bloodtests", "GlucoseTest"), pid("bloodtests", "glucoseLevel"), pid("bloodtests", "Number"))
//                .edge(pid("commonalities", "Pat"), pid("commonalities", "id"), pid("commonalities", "Number"))
//                .edge(pid("commonalities", "Obs"), pid("commonalities", "of"), pid("commonalities", "Pat"))
//                .edge(pid("commonalities", "Obs"), pid("commonalities", "recorded"), pid("commonalities", "Timestamp"))
//                .edge(pid("commonalities", "Number"), pid("Number", "m1").prefixWith(id("commonalities")), pid("patients", "Number"))
//                .edge(pid("commonalities", "Number"), pid("Number", "m2").prefixWith(id("commonalities")), pid("appointments", "Number"))
//                .edge(pid("commonalities", "Number"), pid("Number", "m3").prefixWith(id("commonalities")), pid("bloodtests", "Number"))
//                .edge(pid("commonalities", "Timestamp"), pid("Timestamp", "m1").prefixWith(id("commonalities")), pid("patients", "Timestamp"))
//                .edge(pid("commonalities", "Timestamp"), pid("Timestamp", "m3").prefixWith(id("commonalities")), pid("bloodtests", "Timestamp"))
//                .edge(pid("commonalities", "Pat"), pid("Pat", "m1").prefixWith(id("commonalities")), pid("patients", "Patient"))
//                .edge(pid("commonalities", "Pat"), pid("Pat", "m2").prefixWith(id("commonalities")), pid("appointments", "Patient"))
//                .edge(pid("commonalities", "Pat"), pid("Pat", "m3").prefixWith(id("commonalities")), pid("bloodtests", "Subject"))
//                .edge(pid("commonalities", "Obs"), pid("Obs", "m1").prefixWith(id("commonalities")), pid("patients", "Observation"))
//                .edge(pid("commonalities", "Obs"), pid("Obs", "m3").prefixWith(id("commonalities")), pid("bloodtests", "BloodSample"))
//                .build();
//        assertEquals(expected.getElements(), result.elements().collect(Collectors.toSet()));
//    }
//
//
//    @Test
//    public void testNonVanKampenExample() throws GraphError {
//        GraphImpl L = new GraphBuilder("L")
//                .node("x").node("z")
//                .node("y").node("w")
//                .build();
//
//        GraphImpl R = new GraphBuilder("R")
//                .node("xy").node("zw")
//                .build();
//
//        GraphImpl A = new GraphBuilder("A")
//                .node("xz")
//                .node("yw")
//                .build();
//
//        GraphMorphismImpl a = new GraphMorphismImpl.Builder("a", L, A)
//                .map("x", "xz")
//                .map("y", "yw")
//                .map("z", "xz")
//                .map("w", "yw")
//                .build();
//
//        GraphMorphismImpl r = new GraphMorphismImpl.Builder("r", L, R)
//                .map("x", "xy")
//                .map("y", "xy")
//                .map("z", "zw")
//                .map("w", "zw")
//                .build();
//
//        GraphMorphism tau = new GraphMorphismImpl.TypedGrapBuilder(id("IA"), A)
//                .typedNode(id("12:xz"), id("xz"))
//                .typedNode(id("21:xz"), id("xz"))
//                .typedNode(id("1:yw"), id("yw"))
//                .typedNode(id("2:yw"), id("yw"))
//                .build();
//
//        GraphMorphism beta = new GraphMorphismImpl.TypedGrapBuilder(id("IR"), R)
//                .typedNode(id("1:xy"), id("xy"))
//                .typedNode(id("2:xy"), id("xy"))
//                .typedNode(id("1:zw"), id("zw"))
//                .typedNode(id("2:zw"), id("zw"))
//                .build();
//
//        Multispan typeSpane = Multispan.create(id("T"), L, Arrays.asList(R, A), Arrays.asList(a, r));
//
//        Graph colim = typeSpane.colimit(id("S"));
//        assertEquals(1, colim.elements().count());
//
//        Multispan leftRear = tau.pullback(id("leftRear"), a, id("I"));
//        Multispan rightRear = beta.pullback(id("rightRear"), r, id("I"));
//
//        assertEquals(8, leftRear.getApex().elements().count());
//        assertEquals(8, rightRear.getApex().elements().count());
//
//
//    }
//
//
//    @Test
//    public void testStructuralConsistency() {
//        // TODO write a good testcase
//    }
//
//    @Test
//    public void testViolationIllDefinedSpan() {
//        GraphImpl apex = new GraphBuilder("0").build();
//        GraphImpl fst = new GraphBuilder("1").build();
//        GraphImpl snd = new GraphBuilder("2").build();
//
//        GraphMorphismImpl a = new GraphMorphismImpl.Builder("m1", apex, fst).build();
//        GraphMorphismImpl b = new GraphMorphismImpl.Builder("b", snd, apex).build();
//
//        try {
//            Multispan.create(Name.identifier("MM"), apex, Arrays.asList(fst, snd), Arrays.asList(a, b));
//            fail();
//        } catch (GraphError error) {
//            Set<Triple> wrong = new HashSet<>();
//            wrong.add(Triple.node(snd.getName()));
//            wrong.add(new Triple(snd.getName(), b.getName(), apex.getName()));
//            assertEquals(GraphError.ERROR_TYPE.ILL_FORMED, error.getErrorType());
//            assertEquals(wrong, error.getAffected());
//        }
//
//    }

}
