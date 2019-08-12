package no.hvl.past.graph;

import no.hvl.past.graph.names.Name;

import no.hvl.past.graph.names.Prefix;
import no.hvl.past.graph.names.PrintingStrategy;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class MultispanTest {


    /**
     * A naming strategy used throughout the test. Ignores prefixes and concatenates names.
     * Uses some standard symbols for the individual operations.
     */
    PrintingStrategy NAMING_STRATEGY = new PrintingStrategy() {
        @Override
        public String empty() {
            return "";
        }

        @Override
        public String sequentialComposition(String fst, String snd) {
            return fst + ";" + snd;
        }

        @Override
        public String coproduct(String fst, String snd) {
            return fst + "+" + snd;
        }

        @Override
        public String pullback(String applicant, String target) {
            return applicant + "*(" + target + ")";
        }


        @Override
        public String merge(Collection<String> transformedNames) {
            StringBuilder result = new StringBuilder();
            result.append('(');
            Iterator<String> iterator = transformedNames.iterator();
            while (iterator.hasNext()) {
                result.append(iterator.next());
                if (iterator.hasNext()) {
                    result.append(", ");
                }
            }
            result.append(')');
            return result.toString();
        }

        @Override
        public String transform(Name n, String prefix) {
            return n.toString();
        }

        @Override
        public String typedBy(String element, String type) {
            return element + " : " + type;
        }
    };

    @Test
    public void testColimit() throws GraphError {
        Graph patients = new Graph.Builder("patients")
                .edge("Patient", "patientNo", "Number")
                .edge("Patient", "assigned", "Bed")
                .edge("Bed", "stationNo", "Number")
                .edge("Observation", "of", "Patient")
                .edge("Observation", "recordedAt", "Timestamp")
                .edge("Observation", "details", "String")
                .build();

        Graph appointments = new Graph.Builder("appointments")
                .edge("Patient", "id", "Number")
                .edge("Appointment", "patient", "Patient")
                .edge("Appointment", "begin", "Timestamp")
                .edge("Appointment", "end", "Timestamp")
                .edge("Appointment", "doctor", "Doctor")
                .edge("Doctor", "worksAtStationNo", "Number")
                .edge("Doctor", "shiftStarts", "Time")
                .edge("Doctor", "shiftEnds", "Time")
                .build();

        Graph bloodTests = new Graph.Builder("bloodtests")
                .edge("Subject", "patientId", "Number")
                .edge("BloodSample", "takenFrom", "Subject")
                .edge("BloodSample", "takenAt", "Timestamp")
                .edge("BloodSample", "isCritical", "Bool")
                .edge("CholesterolTest", "CholesterolTest.basedOn", "BloodSample")
                .edge("CholesterolTest", "totalCholesterol", "Number")
                .edge("CholesterolTest", "hdlCholesterol", "Number")
                .edge("GlucoseTest", "GlucoseTest.basedOn", "BloodSample")
                .edge("GlucoseTest", "glucoseLevel", "Number")
                .build();

        Graph common = new Graph.Builder("commonalitites")
                .edge("Pat", "id", "Number")
                .edge("Obs", "of", "Pat")
                .edge("Obs", "recorded", "Timestamp")
                .build();

        Morphism m1 = new Morphism.Builder("m1", common, patients)
                .map("Pat", "Patient")
                .map("id", "patientNo")
                .map("Obs", "Observation")
                .map("of", "of")
                .map("recorded", "recordedAt")
                .map("Number", "Number")
                .map("Timestamp", "Timestamp")
                .build();

        Morphism m2 = new Morphism.Builder("m2", common, appointments)
                .map("Pat", "Patient")
                .map("id", "id")
                .map("Number", "Number")
                .map("Timestamp", "Timestamp")
                .build();

        Morphism m3 = new Morphism.Builder("m3", common, bloodTests)
                .map("Pat", "Subject")
                .map("id", "patientId")
                .map("Obs", "BloodSample")
                .map("of", "takenFrom")
                .map("recorded", "takenAt")
                .map("Number", "Number")
                .map("Timestamp", "Timestamp")
                .build();

        Multispan multimodel = Multispan.create(Name.identifier("multimodel"),
                common,
                Arrays.asList(patients, appointments, bloodTests),
                Arrays.asList(m1, m2, m3));


        AbstractGraph colimit = multimodel.colimit(Name.identifier("M+"), NamingStrategy.givePrecedenceToPrefix(common.getName()));

        Set<Triple> expected = new HashSet<>();
        expected.add(Triple.fromNode(Name.identifier("Bed").prefix(patients.getName())));
        expected.add(Triple.fromNode(Name.identifier("Obs").prefix(common.getName())));
        expected.add(Triple.fromNode(Name.identifier("Appointment").prefix(appointments.getName())));
        expected.add(Triple.fromNode(Name.identifier("Doctor").prefix(appointments.getName())));
        expected.add(Triple.fromNode(Name.identifier("Pat").prefix(common.getName())));
        expected.add(Triple.fromNode(Name.identifier("CholesterolTest").prefix(bloodTests.getName())));
        expected.add(Triple.fromNode(Name.identifier("GlucoseTest").prefix(bloodTests.getName())));
        expected.add(Triple.fromNode(Name.identifier("Number").prefix(common.getName())));
        expected.add(Triple.fromNode(Name.identifier("Timestamp").prefix(common.getName())));
        expected.add(Triple.fromNode(Name.identifier("Bool").prefix(bloodTests.getName())));
        expected.add(Triple.fromNode(Name.identifier("Time").prefix(appointments.getName())));
        expected.add(Triple.fromNode(Name.identifier("String").prefix(patients.getName())));



        expected.add(new Triple(
                Name.identifier("Obs").prefix(common.getName()),
                Name.identifier("recorded").prefix(common.getName()),
                Name.identifier("Timestamp").prefix(common.getName())));
        expected.add(new Triple(
                Name.identifier("Obs").prefix(common.getName()),
                Name.identifier("of").prefix(common.getName()),
                Name.identifier("Pat").prefix(common.getName())));
        expected.add(new Triple(
                Name.identifier("Pat").prefix(common.getName()),
                Name.identifier("id").prefix(common.getName()),
                Name.identifier("Number").prefix(common.getName())));
        expected.add(new Triple(
                Name.identifier("Obs").prefix(common.getName()),
                Name.identifier("isCritical").prefix(bloodTests.getName()),
                Name.identifier("Bool").prefix(bloodTests.getName())));
        expected.add(new Triple(
                Name.identifier("Obs").prefix(common.getName()),
                Name.identifier("details").prefix(patients.getName()),
                Name.identifier("String").prefix(patients.getName())));
        expected.add(new Triple(
                Name.identifier("GlucoseTest").prefix(bloodTests.getName()),
                Name.identifier("glucoseLevel").prefix(bloodTests.getName()),
                Name.identifier("Number").prefix(common.getName())));
        expected.add(new Triple(
                Name.identifier("GlucoseTest").prefix(bloodTests.getName()),
                Name.identifier("GlucoseTest.basedOn").prefix(bloodTests.getName()),
                Name.identifier("Obs").prefix(common.getName())));
        expected.add(new Triple(
                Name.identifier("CholesterolTest").prefix(bloodTests.getName()),
                Name.identifier("CholesterolTest.basedOn").prefix(bloodTests.getName()),
                Name.identifier("Obs").prefix(common.getName())));
        expected.add(new Triple(
                Name.identifier("CholesterolTest").prefix(bloodTests.getName()),
                Name.identifier("totalCholesterol").prefix(bloodTests.getName()),
                Name.identifier("Number").prefix(common.getName())));
        expected.add(new Triple(
                Name.identifier("CholesterolTest").prefix(bloodTests.getName()),
                Name.identifier("hdlCholesterol").prefix(bloodTests.getName()),
                Name.identifier("Number").prefix(common.getName())));
        expected.add(new Triple(
                Name.identifier("Appointment").prefix(appointments.getName()),
                Name.identifier("patient").prefix(appointments.getName()),
                Name.identifier("Pat").prefix(common.getName())));
        expected.add(new Triple(
                Name.identifier("Appointment").prefix(appointments.getName()),
                Name.identifier("begin").prefix(appointments.getName()),
                Name.identifier("Timestamp").prefix(common.getName())));
        expected.add(new Triple(
                Name.identifier("Appointment").prefix(appointments.getName()),
                Name.identifier("end").prefix(appointments.getName()),
                Name.identifier("Timestamp").prefix(common.getName())));
        expected.add(new Triple(
                Name.identifier("Appointment").prefix(appointments.getName()),
                Name.identifier("doctor").prefix(appointments.getName()),
                Name.identifier("Doctor").prefix(appointments.getName())));
        expected.add(new Triple(
                Name.identifier("Doctor").prefix(appointments.getName()),
                Name.identifier("worksAtStationNo").prefix(appointments.getName()),
                Name.identifier("Number").prefix(common.getName())));
        expected.add(new Triple(
                Name.identifier("Doctor").prefix(appointments.getName()),
                Name.identifier("shiftStarts").prefix(appointments.getName()),
                Name.identifier("Time").prefix(appointments.getName())));
        expected.add(new Triple(
                Name.identifier("Doctor").prefix(appointments.getName()),
                Name.identifier("shiftEnds").prefix(appointments.getName()),
                Name.identifier("Time").prefix(appointments.getName())));
        expected.add(new Triple(
                Name.identifier("Bed").prefix(patients.getName()),
                Name.identifier("stationNo").prefix(patients.getName()),
                Name.identifier("Number").prefix(common.getName())));
        expected.add(new Triple(
                Name.identifier("Pat").prefix(common.getName()),
                Name.identifier("assigned").prefix(patients.getName()),
                Name.identifier("Bed").prefix(patients.getName())));




        assertEquals(expected, StreamSupport.stream(colimit.spliterator(), false).collect(Collectors.toSet()));
    }

    public void testGrothendieckConstruction() throws GraphError {
        Graph patients = new Graph.Builder("patients")
                .edge("Patient", "patientNo", "Number")
                .edge("Patient", "assigned", "Bed")
                .edge("Bed", "stationNo", "Number")
                .edge("Observation", "of", "Patient")
                .edge("Observation", "recordedAt", "Timestamp")
                .edge("Observation", "details", "String")
                .build();

        Graph appointments = new Graph.Builder("patients")
                .edge("Patient", "id", "Number")
                .edge("Appointment", "patient", "Patient")
                .edge("Appointment", "begin", "Timestamp")
                .edge("Appointment", "end", "Timestamp")
                .edge("Appointment", "doctor", "Doctor")
                .edge("Doctor", "worksAtStationNo", "Number")
                .edge("Doctor", "shiftStarts", "Time")
                .edge("Doctor", "shiftEnds", "Time")
                .build();

        Graph bloodTests = new Graph.Builder("patients")
                .edge("Subject", "patientId", "Number")
                .edge("BloodSample", "takenFrom", "Subject")
                .edge("BloodSample", "takenAt", "Timestamp")
                .edge("BloodSample", "isCritical", "Bool")
                .edge("CholesterolTest", "CholesterolTest.basedOn", "BloodSample")
                .edge("CholesterolTest", "totalCholesterol", "Number")
                .edge("CholesterolTest", "hdlCholesterol", "Number")
                .edge("GlucoseTest", "GlucoseTest.basedOn", "BloodSample")
                .edge("GlucoseTest", "glucoseLevel", "Number")
                .build();

        Graph common = new Graph.Builder("commonalitites")
                .edge("Pat", "id", "Number")
                .edge("Obs", "of", "Pat")
                .edge("Obs", "recorded", "Timestamp")
                .build();

        Morphism m1 = new Morphism.Builder("m1", common, patients)
                .map("Pat", "Patient")
                .map("id", "patientNo")
                .map("Obs", "Observation")
                .map("of", "of")
                .map("recorded", "recordedAt")
                .map("Number", "Number")
                .map("Timestamp", "Timestamp")
                .build();

        Morphism m2 = new Morphism.Builder("m2", common, appointments)
                .map("Pat", "Patient")
                .map("id", "id")
                .map("Number", "Number")
                .build();

        Morphism m3 = new Morphism.Builder("m3", common, bloodTests)
                .map("Pat", "Subject")
                .map("id", "patientId")
                .map("Obs", "BloodSample")
                .map("of", "takenFrom")
                .map("recorded", "takenAt")
                .map("Number", "Number")
                .map("Timestamp", "Timestamp")
                .build();

        Multispan multimodel = Multispan.create(Name.identifier("multimodel"),
                common,
                Arrays.asList(patients, appointments, bloodTests),
                Arrays.asList(m1, m2, m3));
    }

    @Test
    public void testViolationIllDefinedSpan() {
        Graph apex = new Graph.Builder("0").build();
        Graph fst = new Graph.Builder("1").build();
        Graph snd = new Graph.Builder("2").build();

        Morphism a = new Morphism.Builder("m1", apex, fst).build();
        Morphism b = new Morphism.Builder("b", snd, apex).build();

        try {
            Multispan.create(Name.identifier("MM"), apex, Arrays.asList(fst, snd), Arrays.asList(a, b));
            fail();
        } catch (GraphError error) {
            Set<Triple> wrong = new HashSet<>();
            wrong.add(Triple.fromNode(snd.getName()));
            wrong.add(new Triple(snd.getName(), b.getName(), apex.getName()));
            assertEquals(GraphError.ERROR_TYPE.ILL_FORMED, error.getErrorType());
            assertEquals(wrong, error.getAffected());
        }

    }

}
