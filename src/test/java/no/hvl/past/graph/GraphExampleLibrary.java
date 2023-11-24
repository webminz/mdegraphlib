package no.hvl.past.graph;

import no.hvl.past.graph.operations.Invert;
import no.hvl.past.graph.predicates.*;
import no.hvl.past.names.Name;

public class GraphExampleLibrary  {

    public static GraphExampleLibrary INSTANCE = new GraphExampleLibrary();


    public Graph PersonsAndJobs;
    public Sketch PersonsAndJobsSketch;
    public Graph PatientsAndObservations;
    public Sketch PatientsAndObservationsSketch;
    public Sketch Families;
    public Sketch Persons;


    public void initialize(GraphBuilders contextCreatingBuilder) throws GraphError {


        this.PersonsAndJobs = contextCreatingBuilder
                .node("Person")
                .node("Job")
                .node("CommunicationChannel")
                .node("PostalAddress")
                .node("Email")
                .node("Gender")
                .node("Repository")

                .edge("Repository", "persons", "Person")
                .edge("Repository", "channels", "CommunicationChannel")
                .edge("Repository", "jobs", "Job")
                .edge("PostalAddress", "street", "String")
                .edge("PostalAddress", "city", "String")
                .edge("PostalAddress", "zip", "String")
                .edge("Email", "address", "String")
                .edge("Person", "name", "String")
                .edge("Person", "age", "Int")
                .edge("Person", "gender", "Gender")
                .edge("Person", "worksAt", "Job")
                .edge("Job", "position", "String")
                .edge("Job", "employer", "String")
                .edge("CommunicationChannel", "owner", "Person")
                .edge("Person", "contacts", "CommunicationChannel")
                .map("PostalAddress", "CommunicationChannel")
                .map("Email", "CommunicationChannel")
                .graph(Name.identifier("PersonsAndJobs"))
                .getResult(Graph.class);


        this.PersonsAndJobsSketch = contextCreatingBuilder.clear().importGraph(PersonsAndJobs)
                .graph(Name.identifier("PersonsAndJobsSketch").absolute())
                .startDiagram(StringDT.getInstance())
                .map(Universe.ONE_NODE_THE_NODE, Name.identifier("String"))
                .endDiagram(Name.identifier("d1"))
                .startDiagram(IntDT.getInstance())
                .map(Universe.ONE_NODE_THE_NODE, Name.identifier("Int"))
                .endDiagram(Name.identifier("d2"))
                .startDiagram(EnumValue.getInstance(Name.identifier("MALE"), Name.identifier("FEMALE")))
                .map(Universe.ONE_NODE_THE_NODE, Name.identifier("Gender"))
                .endDiagram(Name.identifier("d3"))
                .startDiagram(TargetMultiplicity.getInstance(0, 1))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Person"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("worksAt"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("Job"))
                .endDiagram(Name.identifier("d4"))
                .startDiagram(TargetMultiplicity.getInstance(1, -1))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("CommunicationChannel"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("owner"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("Person"))
                .endDiagram(Name.identifier("d5"))
                .startDiagram(Invert.getInstance())
                .map(Universe.CYCLE_FWD.getSource(), Name.identifier("CommunicationChannel"))
                .map(Universe.CYCLE_FWD.getTarget(), Name.identifier("Person"))
                .map(Universe.CYCLE_FWD.getLabel(), Name.identifier("owner"))
                .map(Universe.CYCLE_BWD.getLabel(), Name.identifier("contacts"))
                .endDiagram(Name.identifier("commChannels"))
                .startDiagram(Acyclicity.getInstance())
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Repository"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("persons"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("Person"))
                .endDiagram(Name.identifier("d7"))
                .startDiagram(Acyclicity.getInstance())
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Repository"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("channels"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("CommunicationChannel"))
                .endDiagram(Name.identifier("d8"))
                .startDiagram(Acyclicity.getInstance())
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Repository"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("jobs"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("Job"))
                .endDiagram(Name.identifier("d9"))
                .startDiagram(SourceMultiplicity.getInstance(0, 1))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Repository"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("persons"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("Person"))
                .endDiagram(Name.identifier("d10"))
                .startDiagram(SourceMultiplicity.getInstance(0, 1))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Repository"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("channels"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("CommunicationChannel"))
                .endDiagram(Name.identifier("d11"))
                .startDiagram(SourceMultiplicity.getInstance(0, 1))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Repository"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("jobs"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("Job"))
                .endDiagram(Name.identifier("d12"))

                .sketch("PersonsAndJobsSketch")
                .getResult(Sketch.class);

        this.PatientsAndObservations = contextCreatingBuilder.clear()
                .node("Patient")
                .node("Observation")
                .node("Quantity")
                .node("CodeableConcept")
                .node("JournalSystem")
                .node("Address")

                .edge("JournalSystem", "patients", "Patient")
                .edge("JournalSystem", "observations", "Observation")
                .edge("Patient", "name", "String")
                .edge("Patient", "address", "Address")
                .edge("Patient", "patientId", "Long")
                .edge("Observation", "observationId", "Long")
                .edge("Observation", "patient", "Patient")
                .edge("Observation", "effectiveDateTime", "DateTime")
                .edge("Observation", "coding", "CodeableConcept")
                .edge("Observation", "measurement", "Quantity")
                .edge("Quantity", "value", "Double")
                .edge("Quantity", "unit", "CodeableConcept")
                .edge("CodeableConcept", "codeSystem", "URI")
                .edge("CodeableConcept", "code", "String")
                .edge("Address", "street", "String")
                .edge("Address", "city", "String")
                .edge("Address", "postCode", "String")

                .graph(Name.identifier("PatientsAndObservations"))
                .getResult(Graph.class);

        this.PatientsAndObservationsSketch = contextCreatingBuilder.clear().importGraph(PatientsAndObservations)
                .graph(Name.identifier("PatientsAndObservationsSketch").absolute())
                .startDiagram(StringDT.getInstance())
                .map(Universe.ONE_NODE_THE_NODE, Name.identifier("String"))
                .endDiagram(Name.anonymousIdentifier())

                .startDiagram(IntDT.getInstance())
                .map(Universe.ONE_NODE_THE_NODE, Name.identifier("Long"))
                .endDiagram(Name.anonymousIdentifier())

                .startDiagram(FloatDT.getInstance())
                .map(Universe.ONE_NODE_THE_NODE, Name.identifier("Double"))
                .endDiagram(Name.anonymousIdentifier())

                .startDiagram(DataTypePredicate.getInstance())
                .map(Universe.ONE_NODE_THE_NODE, Name.identifier("DateTime"))
                .endDiagram(Name.anonymousIdentifier())

                .startDiagram(DataTypePredicate.getInstance())
                .map(Universe.ONE_NODE_THE_NODE, Name.identifier("URI"))
                .endDiagram(Name.anonymousIdentifier())

                .startDiagram(TargetMultiplicity.getInstance(1, 1))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Observation"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("patient"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("Patient"))
                .endDiagram(Name.anonymousIdentifier())

                .startDiagram(TargetMultiplicity.getInstance(1, 1))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Observation"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("observationId"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("Long"))
                .endDiagram(Name.anonymousIdentifier())

                .startDiagram(TargetMultiplicity.getInstance(1, -1))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Observation"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("coding"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("CodeableConcept"))
                .endDiagram(Name.anonymousIdentifier())

                .startDiagram(TargetMultiplicity.getInstance(1, 1))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Observation"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("measurement"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("Quantity"))
                .endDiagram(Name.anonymousIdentifier())

                .startDiagram(TargetMultiplicity.getInstance(1, 1))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Patient"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("patientId"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("Long"))
                .endDiagram(Name.anonymousIdentifier())

                .startDiagram(TargetMultiplicity.getInstance(0, 1))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Patient"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("address"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("Address"))
                .endDiagram(Name.anonymousIdentifier())

                .startDiagram(TargetMultiplicity.getInstance(2, -1))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Patient"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("name"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("String"))
                .endDiagram(Name.anonymousIdentifier())

                .startDiagram(TargetMultiplicity.getInstance(1, 1))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("CodeableConcept"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("code"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("String"))
                .endDiagram(Name.anonymousIdentifier())

                .startDiagram(TargetMultiplicity.getInstance(1, 1))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Quantity"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("value"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("Double"))
                .endDiagram(Name.anonymousIdentifier())

                .startDiagram(TargetMultiplicity.getInstance(0, 1))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Quantity"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("unit"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("CodeableConcept"))
                .endDiagram(Name.anonymousIdentifier())

                .startDiagram(Acyclicity.getInstance())
                .map(Universe.ARROW_SRC_NAME, Name.identifier("JournalSystem"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("patients"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("Patient"))
                .endDiagram(Name.anonymousIdentifier())

                .startDiagram(Acyclicity.getInstance())
                .map(Universe.ARROW_SRC_NAME, Name.identifier("JournalSystem"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("observations"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("Observation"))
                .endDiagram(Name.anonymousIdentifier())

                .startDiagram(Acyclicity.getInstance())
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Observation"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("measurement"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("Quantity"))
                .endDiagram(Name.anonymousIdentifier())

                .startDiagram(Acyclicity.getInstance())
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Observation"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("coding"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("CodeableConcept"))
                .endDiagram(Name.anonymousIdentifier())

                .startDiagram(SourceMultiplicity.getInstance(1,1))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("JournalSystem"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("patients"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("Patient"))
                .endDiagram(Name.anonymousIdentifier())

                .startDiagram(SourceMultiplicity.getInstance(1,1))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("JournalSystem"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("observations"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("Observation"))
                .endDiagram(Name.anonymousIdentifier())

                .startDiagram(SourceMultiplicity.getInstance(1,1))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Observation"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("measurement"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("Quantity"))
                .endDiagram(Name.anonymousIdentifier())

                .startDiagram(SourceMultiplicity.getInstance(1,1))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Observation"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("coding"))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("CodeableConcept"))
                .endDiagram(Name.anonymousIdentifier())
                .sketch(Name.identifier("PatientsAndObservationsSketch"))
                .getResult(Sketch.class);


        contextCreatingBuilder.clear();

        Families = contextCreatingBuilder
                .edgePrefixWithOwner("FamilyRegister", "families", "Family")
                .edgePrefixWithOwner("Family", "familiesInverse", "FamilyRegister")
                .edgePrefixWithOwner("Family", "name", "String")
                .edgePrefixWithOwner("Family", "father", "FamilyMember")
                .edgePrefixWithOwner("Family", "mother", "FamilyMember")
                .edgePrefixWithOwner("Family", "sons", "FamilyMember")
                .edgePrefixWithOwner("Family", "daughters", "FamilyMember")
                .edgePrefixWithOwner("FamilyMember", "name", "String")
                .edgePrefixWithOwner("FamilyMember", "fatherInverse", "Family")
                .edgePrefixWithOwner("FamilyMember", "motherInverse", "Family")
                .edgePrefixWithOwner("FamilyMember", "sonsInverse", "Family")
                .edgePrefixWithOwner("FamilyMember", "daughtersInverse", "Family")
                .graph(Name.identifier("Families").absolute())
                .startDiagram(StringDT.getInstance())
                .map(Universe.ONE_NODE_THE_NODE, Name.identifier("String"))
                .endDiagram(Name.anonymousIdentifier())
                .startDiagram(TargetMultiplicity.getInstance(1, 1))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Family"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("name").prefixWith(Name.identifier("Family")))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("String"))
                .endDiagram(Name.anonymousIdentifier())
                .startDiagram(TargetMultiplicity.getInstance(0, 1))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("FamilyMember"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("name").prefixWith(Name.identifier("FamilyMember")))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("String"))
                .endDiagram(Name.anonymousIdentifier())
                .startDiagram(TargetMultiplicity.getInstance(0, 1))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Family"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("father").prefixWith(Name.identifier("Family")))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("FamilyMember"))
                .endDiagram(Name.anonymousIdentifier())
                .startDiagram(TargetMultiplicity.getInstance(0, 1))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Family"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("mother").prefixWith(Name.identifier("Family")))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("FamilyMember"))
                .endDiagram(Name.anonymousIdentifier())
                .startDiagram(SourceMultiplicity.getInstance(0, 1))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Family"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("father").prefixWith(Name.identifier("Family")))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("FamilyMember"))
                .endDiagram(Name.anonymousIdentifier())
                .startDiagram(Acyclicity.getInstance())
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Family"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("father").prefixWith(Name.identifier("Family")))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("FamilyMember"))
                .endDiagram(Name.anonymousIdentifier())
                .startDiagram(SourceMultiplicity.getInstance(0, 1))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Family"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("mother").prefixWith(Name.identifier("Family")))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("FamilyMember"))
                .endDiagram(Name.anonymousIdentifier())
                .startDiagram(Acyclicity.getInstance())
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Family"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("mother").prefixWith(Name.identifier("Family")))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("FamilyMember"))
                .endDiagram(Name.anonymousIdentifier())
                .startDiagram(SourceMultiplicity.getInstance(0, 1))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Family"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("sons").prefixWith(Name.identifier("Family")))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("FamilyMember"))
                .endDiagram(Name.anonymousIdentifier())
                .startDiagram(Acyclicity.getInstance())
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Family"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("sons").prefixWith(Name.identifier("Family")))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("FamilyMember"))
                .endDiagram(Name.anonymousIdentifier())
                .startDiagram(SourceMultiplicity.getInstance(0, 1))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Family"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("daughters").prefixWith(Name.identifier("Family")))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("FamilyMember"))
                .endDiagram(Name.anonymousIdentifier())
                .startDiagram(Acyclicity.getInstance())
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Family"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("daughters").prefixWith(Name.identifier("Family")))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("FamilyMember"))
                .endDiagram(Name.anonymousIdentifier())
                .startDiagram(SourceMultiplicity.getInstance(0, 1))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("FamilyRegister"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("families").prefixWith(Name.identifier("FamilyRegister")))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("Family"))
                .endDiagram(Name.anonymousIdentifier())
                .startDiagram(Acyclicity.getInstance())
                .map(Universe.ARROW_SRC_NAME, Name.identifier("FamilyRegister"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("families").prefixWith(Name.identifier("FamilyRegister")))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("Family"))
                .endDiagram(Name.anonymousIdentifier())
                .startDiagram(Invert.getInstance())
                .map(Universe.CYCLE_FWD.getSource(), Name.identifier("FamilyRegister"))
                .map(Universe.CYCLE_FWD.getTarget(), Name.identifier("Family"))
                .map(Universe.CYCLE_FWD.getLabel(), Name.identifier("families").prefixWith(Name.identifier("FamilyRegister")))
                .map(Universe.CYCLE_BWD.getLabel(), Name.identifier("familiesInverse").prefixWith(Name.identifier("Family")))
                .endDiagram(Name.anonymousIdentifier())
                .startDiagram(Invert.getInstance())
                .map(Universe.CYCLE_FWD.getSource(), Name.identifier("Family"))
                .map(Universe.CYCLE_FWD.getTarget(), Name.identifier("FamilyMember"))
                .map(Universe.CYCLE_FWD.getLabel(), Name.identifier("father").prefixWith(Name.identifier("Family")))
                .map(Universe.CYCLE_BWD.getLabel(), Name.identifier("fatherInverse").prefixWith(Name.identifier("FamilyMember")))
                .endDiagram(Name.anonymousIdentifier())
                .startDiagram(Invert.getInstance())
                .map(Universe.CYCLE_FWD.getSource(), Name.identifier("Family"))
                .map(Universe.CYCLE_FWD.getTarget(), Name.identifier("FamilyMember"))
                .map(Universe.CYCLE_FWD.getLabel(), Name.identifier("mother").prefixWith(Name.identifier("Family")))
                .map(Universe.CYCLE_BWD.getLabel(), Name.identifier("motherInverse").prefixWith(Name.identifier("FamilyMember")))
                .endDiagram(Name.anonymousIdentifier())
                .startDiagram(Invert.getInstance())
                .map(Universe.CYCLE_FWD.getSource(), Name.identifier("Family"))
                .map(Universe.CYCLE_FWD.getTarget(), Name.identifier("FamilyMember"))
                .map(Universe.CYCLE_FWD.getLabel(), Name.identifier("sons").prefixWith(Name.identifier("Family")))
                .map(Universe.CYCLE_BWD.getLabel(), Name.identifier("sonsInverse").prefixWith(Name.identifier("FamilyMember")))
                .endDiagram(Name.anonymousIdentifier())
                .startDiagram(Invert.getInstance())
                .map(Universe.CYCLE_FWD.getSource(), Name.identifier("Family"))
                .map(Universe.CYCLE_FWD.getTarget(), Name.identifier("FamilyMember"))
                .map(Universe.CYCLE_FWD.getLabel(), Name.identifier("daughters").prefixWith(Name.identifier("Family")))
                .map(Universe.CYCLE_BWD.getLabel(), Name.identifier("daughtersInverse").prefixWith(Name.identifier("FamilyMember")))
                .endDiagram(Name.anonymousIdentifier())
                .sketch(Name.identifier("Families"))
                .getResult(Sketch.class);


        Persons = contextCreatingBuilder
                .edgePrefixWithOwner("PersonRegister", "persons", "Person")
                .edgePrefixWithOwner("Person", "name", "String")
                .edgePrefixWithOwner("Person", "personsInverse", "PersonRegister")
                .node("Male")
                .node("Female")
                .map("Male", "Person")
                .map("Female", "Person")
                .graph(Name.identifier("Persons").absolute())
                .startDiagram(StringDT.getInstance())
                .map(Universe.ONE_NODE_THE_NODE, Name.identifier("String"))
                .endDiagram(Name.anonymousIdentifier())
                .startDiagram(TargetMultiplicity.getInstance(1, 1))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("Person"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("name").prefixWith(Name.identifier("Person")))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("String"))
                .endDiagram(Name.anonymousIdentifier())
                .startDiagram(SourceMultiplicity.getInstance(0, 1))
                .map(Universe.ARROW_SRC_NAME, Name.identifier("PersonRegister"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("persons").prefixWith(Name.identifier("PersonRegister")))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("Person"))
                .endDiagram(Name.anonymousIdentifier())
                .startDiagram(Acyclicity.getInstance())
                .map(Universe.ARROW_SRC_NAME, Name.identifier("PersonRegister"))
                .map(Universe.ARROW_LBL_NAME, Name.identifier("persons").prefixWith(Name.identifier("PersonRegister")))
                .map(Universe.ARROW_TRG_NAME, Name.identifier("Person"))
                .endDiagram(Name.anonymousIdentifier())
                .startDiagram(Invert.getInstance())
                .map(Universe.CYCLE_FWD.getSource(), Name.identifier("PersonRegister"))
                .map(Universe.CYCLE_FWD.getTarget(), Name.identifier("Person"))
                .map(Universe.CYCLE_FWD.getLabel(), Name.identifier("persons").prefixWith(Name.identifier("PersonRegister")))
                .map(Universe.CYCLE_BWD.getLabel(), Name.identifier("personsInverse").prefixWith(Name.identifier("Person")))
                .endDiagram(Name.anonymousIdentifier())
                .sketch(Name.identifier("Persons"))
                .getResult(Sketch.class);

    }





}
