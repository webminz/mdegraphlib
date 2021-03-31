package no.hvl.past.graph;

import no.hvl.past.names.Identifier;
import no.hvl.past.names.Name;
import no.hvl.past.util.ShouldNotHappenException;

/**
 * Contains some more or less real example graphs and type graphs.
 */
public class ExampleGraphs extends AbstractGraphTest {

    // String constants
    static final String NAME = "name";
    static final String TYPE = "type";
    static final String STRING = "String";
    static final String TIMESTAMP = "Timestamp";
    static final String TRANSACTION = "Transaction";
    static final String ACCOUNT = "Account";
    static final String MONEY = "Money";
    static final String ACCOUNT_TYPE = "AccountType";
    static final String AMOUNT = "amount";
    static final String VALID = "valid";
    static final String CREDIT = "credit";
    static final String DEBIT = "debit";
    static final String PARENT = "parent";
    static final String DESCRIPTION = "description";
    static final String APPOINTMENT = "Appointment";
    static final String DOCTOR = "Doctor";
    static final String TIME = "Time";
    static final String ID = "id";
    static final String PATIENT_ATT = "patient";
    static final String BEGIN = "begin";
    static final String END = "end";
    static final String DOCTOR_ATT = "doctor";
    static final String WORKS_AT_STATION_NO = "worksAtStationNo";
    static final String SHIFT_STARTS = "shiftStarts";
    static final String SHIFT_ENDS = "shiftEnds";
    static final String BOOL = "Bool";
    static final String SUBJECT = "Subject";
    static final String BLOOD_SAMPLE = "BloodSample";
    static final String CHOLESTEROL_TEST = "CholesterolTest";
    static final String GLUCOSE_TEST = "GlucoseTest";
    static final String PATIENT_ID = "patientId";
    static final String TAKEN_FROM = "takenFrom";
    static final String TAKEN_AT = "takenAt";
    static final String IS_CRITICAL = "isCritical";
    static final String CHOLESTEROL_TEST_BASED_ON = "CholesterolTest.basedOn";
    static final String TOTAL_CHOLESTEROL = "totalCholesterol";
    static final String HDL_CHOLESTEROL = "hdlCholesterol";
    static final String GLUCOSE_TEST_BASED_ON = "GlucoseTest.basedOn";
    static final String GLUCOSE_LEVEL = "glucoseLevel";
    static final String PATIENT = "Patient";
    static final String OBSERVATION = "Observation";
    static final String NUMBER = "Number";
    static final String BED = "Bed";
    static final String PATIENT_NO = "patientNo";
    static final String ASSIGNED = "assigned";
    static final String STATION_NO = "stationNo";
    static final String OF = "of";
    static final String RECORDED_AT = "recordedAt";
    static final String DETAILS = "details";
    static final String PAT = "Pat";
    static final String OBS = "Obs";
    static final String RECORDED = "recorded";
    static final String PATIENTS = "patients";
    static final String APPOINTMENTS = "appointments";
    static final String BLOODTESTS = "bloodtests";
    static final String COMMONALITIES = "commonalities";
    static final String ACCOUNTING_INSTANCE = "instance";


    static final Graph ACCOUNTING_GRAPH = buildAccountingGraph();

    private static Graph buildAccountingGraph() {
        try {
            return new GraphBuilders(universe, true, true)
                    .edge(TRANSACTION, AMOUNT, MONEY)
                    .edge(TRANSACTION, VALID, TIMESTAMP)
                    .edge(TRANSACTION, CREDIT, ACCOUNT)
                    .edge(TRANSACTION, DEBIT, ACCOUNT)
                    .edge(TRANSACTION, PARENT, TRANSACTION)
                    .edge(TRANSACTION, DESCRIPTION, STRING)
                    .edge(ACCOUNT, NAME, STRING)
                    .edge(ACCOUNT, TYPE, ACCOUNT_TYPE)
                    .graph("ACCOUNTING")
                    .getResult(Graph.class);
        } catch (GraphError error) {
            throw new ShouldNotHappenException(ExampleGraphs.class, error.getMessage());
        }
    }

    static final GraphMorphism ACCOUNTING_INSTANCE_GRAPH = buildAccountingGraphInstance();

    private static GraphMorphism buildAccountingGraphInstance()  {
        try {
            return new GraphBuilders(universe, true, true)
                    .codomain(ACCOUNTING_GRAPH)
                    .typedEdge(t("Depreciation", "Depreciation.type", "Expenses"), t(ACCOUNT, TYPE, ACCOUNT_TYPE))
                    .typedEdge(t("Food", "Food.type", "Expenses"), t(ACCOUNT, TYPE, ACCOUNT_TYPE))
                    .typedEdge(t("Payroll", "Payroll.type", "Expenses"), t(ACCOUNT, TYPE, ACCOUNT_TYPE))
                    .typedEdge(t("Sales", "Sales.type", "Revenues"), t(ACCOUNT, TYPE, ACCOUNT_TYPE))
                    .typedEdge(t("Capital", "Capital.type", "CapitalT"), t(ACCOUNT, TYPE, ACCOUNT_TYPE))
                    .typedEdge(t("Customer 1", "Customer1.type", "Debtors"), t(ACCOUNT, TYPE, ACCOUNT_TYPE))
                    .typedEdge(t("Customer 2", "Customer2.type", "Debtors"), t(ACCOUNT, TYPE, ACCOUNT_TYPE))
                    .typedEdge(t("Bank", "Bank.type", "Assets"), t(ACCOUNT, TYPE, ACCOUNT_TYPE))
                    .typedEdge(t("Furniture", "Furniture.type", "Assets"), t(ACCOUNT, TYPE, ACCOUNT_TYPE))
                    .typedEdge(t("CreditCard", "CreditCard.type", "Liabilities"), t(ACCOUNT, TYPE, ACCOUNT_TYPE))
                    .typedEdge(t("T1", "T1.amount", "$5,000"), t(TRANSACTION, AMOUNT, MONEY))
                    .typedEdge(t("T1", "T1.credit", "Customer 1"), t(TRANSACTION, CREDIT, ACCOUNT))
                    .typedEdge(t("T1", "T1.debit", "Sales"), t(TRANSACTION, DEBIT, ACCOUNT))
                    .typedEdge(t("T2", "T2.amount", "$5,000"), t(TRANSACTION, AMOUNT, MONEY))
                    .typedEdge(t("T2", "T2.credit", "Customer 2"), t(TRANSACTION, CREDIT, ACCOUNT))
                    .typedEdge(t("T2", "T2.debit", "Sales"), t(TRANSACTION, DEBIT, ACCOUNT))
                    .typedEdge(t("T3", "T3.amount", "$5,000"), t(TRANSACTION, AMOUNT, MONEY))
                    .typedEdge(t("T3", "T3.credit", "Bank"), t(TRANSACTION, CREDIT, ACCOUNT))
                    .typedEdge(t("T3", "T3.debit", "Customer 1"), t(TRANSACTION, DEBIT, ACCOUNT))
                    .typedEdge(t("T4", "T4.amount", "$2,500"), t(TRANSACTION, AMOUNT, MONEY))
                    .typedEdge(t("T4", "T4.credit", "Bank"), t(TRANSACTION, CREDIT, ACCOUNT))
                    .typedEdge(t("T4", "T4.debit", "Customer 2"), t(TRANSACTION, DEBIT, ACCOUNT))
                    .typedEdge(t("T5", "T5.amount", "$25,000"), t(TRANSACTION, AMOUNT, MONEY))
                    .typedEdge(t("T5", "T5.credit", "Bank"), t(TRANSACTION, CREDIT, ACCOUNT))
                    .typedEdge(t("T5", "T5.debit", "Capital"), t(TRANSACTION, DEBIT, ACCOUNT))
                    .typedEdge(t("T6", "T6.amount", "$500"), t(TRANSACTION, AMOUNT, MONEY))
                    .typedEdge(t("T6", "T6.credit", "Furniture"), t(TRANSACTION, CREDIT, ACCOUNT))
                    .typedEdge(t("T6", "T6.debit", "Bank"), t(TRANSACTION, DEBIT, ACCOUNT))
                    .typedEdge(t("T7", "T7.amount", "$8,000"), t(TRANSACTION, AMOUNT, MONEY))
                    .typedEdge(t("T7", "T7.credit", "Payroll"), t(TRANSACTION, CREDIT, ACCOUNT))
                    .typedEdge(t("T7", "T7.debit", "Bank"), t(TRANSACTION, DEBIT, ACCOUNT))
                    .typedEdge(t("T8", "T8.amount", "$8,000"), t(TRANSACTION, AMOUNT, MONEY))
                    .typedEdge(t("T8", "T8.credit", "CreditCard"), t(TRANSACTION, CREDIT, ACCOUNT))
                    .typedEdge(t("T8", "T8.debit", "Bank"), t(TRANSACTION, DEBIT, ACCOUNT))
                    .typedEdge(t("T9", "T9.amount", "$125"), t(TRANSACTION, AMOUNT, MONEY))
                    .typedEdge(t("T9", "T9.credit", "Depreciation"), t(TRANSACTION, CREDIT, ACCOUNT))
                    .typedEdge(t("T9", "T9.debit", "Furniture"), t(TRANSACTION, DEBIT, ACCOUNT))
                    .typedEdge(t("T10", "T10.amount", "13"), t(TRANSACTION, AMOUNT, MONEY))
                    .typedEdge(t("T10", "T10.credit", "Food"), t(TRANSACTION, CREDIT, ACCOUNT))
                    .typedEdge(t("T10", "T10.debit", "CreditCard"), t(TRANSACTION, DEBIT, ACCOUNT))
                    .graph(ACCOUNTING_INSTANCE)
                    .morphism(Name.identifier(ACCOUNTING_INSTANCE).typeBy(ACCOUNTING_GRAPH.getName()))
                    .getResult(GraphMorphism.class);
        } catch (GraphError error) {
            throw new ShouldNotHappenException(ExampleGraphs.class, error.getMessage());
        }
    }


    static final Graph PATIENTS_GRAPH = buildPatientsModelGraph();

    private static Graph buildPatientsModelGraph()  {
        try {
            return new GraphBuilders(universe, true, true)
                    .edge(PATIENT, PATIENT_NO, NUMBER)
                    .edge(PATIENT, ASSIGNED, BED)
                    .edge(BED, STATION_NO, NUMBER)
                    .edge(OBSERVATION, OF, PATIENT)
                    .edge(OBSERVATION, RECORDED_AT, TIMESTAMP)
                    .edge(OBSERVATION, DETAILS, STRING)
                    .graph("patients")
                    .getResult(Graph.class);
        } catch (GraphError error) {
            throw new ShouldNotHappenException(ExampleGraphs.class, error.getMessage());
        }
    }


    static final Graph APPOINTMENTS_GRAPH = buildAppointmentsModelGraph();

    private static Graph buildAppointmentsModelGraph() {
        try {
            return new GraphBuilders(universe, true, true)
                    .edge(PATIENT, ID, NUMBER)
                    .edge(APPOINTMENT, PATIENT_ATT, PATIENT)
                    .edge(APPOINTMENT, BEGIN, TIMESTAMP)
                    .edge(APPOINTMENT, END, TIMESTAMP)
                    .edge(APPOINTMENT, DOCTOR_ATT, DOCTOR)
                    .edge(DOCTOR, WORKS_AT_STATION_NO, NUMBER)
                    .edge(DOCTOR, SHIFT_STARTS, TIME)
                    .edge(DOCTOR, SHIFT_ENDS, TIME)
                    .graph("appointments")
                    .getResult(Graph.class);
        } catch (GraphError error) {
            throw new ShouldNotHappenException(ExampleGraphs.class, error.getMessage());
        }
    }


    static final Graph BLOODTESTS_GRAPH = buildBloodTestModelGraph();

    private static Graph buildBloodTestModelGraph() {
        try {
            return new GraphBuilders(universe, true, true)
                    .edge(SUBJECT, PATIENT_ID, NUMBER)
                    .edge(BLOOD_SAMPLE, TAKEN_FROM, SUBJECT)
                    .edge(BLOOD_SAMPLE, TAKEN_AT, TIMESTAMP)
                    .edge(BLOOD_SAMPLE, IS_CRITICAL, BOOL)
                    .edge(CHOLESTEROL_TEST, CHOLESTEROL_TEST_BASED_ON, BLOOD_SAMPLE)
                    .edge(CHOLESTEROL_TEST, TOTAL_CHOLESTEROL, NUMBER)
                    .edge(CHOLESTEROL_TEST, HDL_CHOLESTEROL, NUMBER)
                    .edge(GLUCOSE_TEST, GLUCOSE_TEST_BASED_ON, BLOOD_SAMPLE)
                    .edge(GLUCOSE_TEST, GLUCOSE_LEVEL, NUMBER)
                    .graph("bloodtests")
                    .getResult(Graph.class);
        } catch (GraphError error) {
            throw new ShouldNotHappenException(ExampleGraphs.class, error.getMessage());
        }
    }


    private static Graph buildCommonalitiesGraph() {
        try {
            return new GraphBuilders(universe, true, true)
                    .edge(PAT, ID, NUMBER)
                    .edge(OBS, OF, PAT)
                    .edge(OBS, RECORDED, TIMESTAMP)
                    .graph("commonalities")
                    .getResult(Graph.class);
        } catch (GraphError error) {
            throw new ShouldNotHappenException(ExampleGraphs.class, error.getMessage());
        }
    }

    static final Graph PATIENTS_APPOINTMENTS_BLOODTEST_COMMONALITY_GRAPH = buildCommonalitiesGraph();

    static final GraphMorphism PATIENTS_PROJECTION = buildPatientProjectionMorphism();

    private static GraphMorphism buildPatientProjectionMorphism() {
        try {
            return new GraphBuilders(universe, true, true)
                    .domain(PATIENTS_APPOINTMENTS_BLOODTEST_COMMONALITY_GRAPH)
                    .codomain(PATIENTS_GRAPH)
                    .map(PAT, PATIENT)
                    .map(ID, PATIENT_NO)
                    .map(OBS, OBSERVATION)
                    .map(OF, OF)
                    .map(RECORDED, RECORDED_AT)
                    .map(NUMBER, NUMBER)
                    .map(TIMESTAMP, TIMESTAMP)
                    .morphism("m1")
                    .getResult(GraphMorphism.class);
        } catch (GraphError error) {
            throw new ShouldNotHappenException(ExampleGraphs.class, error.getMessage());
        }
    }


    static final GraphMorphism APPOINTMENTS_PROJECTION = buildAppointmentsProjectionMorphism();

    private static GraphMorphism buildAppointmentsProjectionMorphism() {
        try {
            return new GraphBuilders(universe, true, true)
                    .domain(PATIENTS_APPOINTMENTS_BLOODTEST_COMMONALITY_GRAPH)
                    .codomain(APPOINTMENTS_GRAPH)
                    .map(PAT, PATIENT)
                    .map(ID, ID)
                    .map(NUMBER, NUMBER)
                    .map(TIMESTAMP, TIMESTAMP)
                    .morphism("m2")
                    .getResult(GraphMorphism.class);
        } catch (GraphError error) {
            throw new ShouldNotHappenException(ExampleGraphs.class, error.getMessage());
        }
    }

    static final GraphMorphism BLOODTEST_PROJECTION = buildBloodtestProjectionMorphism();

    private static GraphMorphism buildBloodtestProjectionMorphism() {
        try {
            return new GraphBuilders(universe, true, true)
                    .domain(PATIENTS_APPOINTMENTS_BLOODTEST_COMMONALITY_GRAPH)
                    .codomain(BLOODTESTS_GRAPH)
                    .map(PAT, SUBJECT)
                    .map(ID, PATIENT_ID)
                    .map(OBS, BLOOD_SAMPLE)
                    .map(OF, TAKEN_FROM)
                    .map(RECORDED, TAKEN_AT)
                    .map(NUMBER, NUMBER)
                    .map(TIMESTAMP, TIMESTAMP)
                    .morphism("m3")
                    .getResult(GraphMorphism.class);
        } catch (GraphError error) {
            throw new ShouldNotHappenException(ExampleGraphs.class, error.getMessage());
        }
    }


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

    public static final Sketch OO_KERNEL = buildOOKernel();

    private static Sketch buildOOKernel() {
        try {
            return new GraphBuilders(universe, true, true)
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
                    .getResult(Sketch.class);
        } catch (GraphError error) {
            throw new ShouldNotHappenException(ExampleGraphs.class, error.getMessage());
        }

    }
}
