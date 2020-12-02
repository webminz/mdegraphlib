package no.hvl.past.graph;

public class DiagrammaticGraphTest {
// TODO move these into the new sketch test

//    /**
//     * This test combines test for compose and composeIncident by applying
//     * it to the inheritance usecase, i.e. copying incoming edge targets to subtypes
//     * and outgoing edge source to subtypes.
//     */
//    @Test
//    public void testInheritance() {
//        GraphImpl typeGraph = new GraphImpl.Builder("TG")
//                .edge("C", "r", "C")
//                .edge("C", "e", "C")
//                .build();
//        DiagrammaticOperation op1 = new DiagrammaticOperation.Builder(typeGraph, Predefined.ComposeIncident.getInstance())
//                .bind(Name.variable("0"), Name.identifier("C"))
//                .bind(Name.variable("1"), Name.identifier("r"))
//                .bind(Name.variable("2"), Name.identifier("C"))
//                .bind(Name.variable("3"), Name.identifier("e"))
//                .bind(Name.variable("4"), Name.identifier("C"))
//                .bind(Name.variable("5"), Name.identifier("r"))
//                .builld();
//        DiagrammaticOperation op2 = new DiagrammaticOperation.Builder(typeGraph, Predefined.Compose.getInstance())
//                .bind(Name.variable("0"), Name.identifier("C"))
//                .bind(Name.variable("1"), Name.identifier("e"))
//                .bind(Name.variable("2"), Name.identifier("C"))
//                .bind(Name.variable("3"), Name.identifier("r"))
//                .bind(Name.variable("4"), Name.identifier("C"))
//                .bind(Name.variable("5"), Name.identifier("r"))
//                .builld();
//        GraphImpl testGraph = new GraphImpl.Builder("G")
//                .edge(Name.identifier("B"), Name.identifier("B").superType(Name.identifier("A")), Name.identifier("A"))
//                .edge(Name.identifier("C"), Name.identifier("C").superType(Name.identifier("A")), Name.identifier("A"))
//                .edge("A", "i", "int")
//                .edge("B","s", "String")
//                .edge("A", "r1", "D")
//                .edge("E", "r2", "A")
//                .build();
//        GraphMorphismImpl typing = new GraphMorphismImpl.Builder("t_G", testGraph, typeGraph)
//                .map("A", "C")
//                .map("B", "C")
//                .map("C", "C")
//                .map("int", "C")
//                .map("String", "C")
//                .map("E", "C")
//                .map("D", "C")
//                .map("i", "r")
//                .map("s", "r")
//                .map("r1", "r")
//                .map("r2", "r")
//                .map(Name.identifier("B").superType(Name.identifier("A")), Name.identifier("e"))
//                .map(Name.identifier("C").superType(Name.identifier("A")), Name.identifier("e"))
//                .build();
//
//        DiagrammaticGraph dgraph = new DiagrammaticGraph(
//                typeGraph,
//                Collections.emptyList(),
//                Arrays.asList(op1,op2)
//        );
//        GraphMorphism result = dgraph.applyOperations(typing);
//
//        Set<Triple> allTriples = result.getDomain().elements().collect(Collectors.toSet());
//
//        Set<Triple> expected = new HashSet<>(testGraph.getElements());
//        expected.add(new Triple(Name.identifier("B"), Name.identifier("B").superType(Name.identifier("A")).composeSequentially(Name.identifier("i")), Name.identifier("int")));
//        expected.add(new Triple(Name.identifier("B"), Name.identifier("B").superType(Name.identifier("A")).composeSequentially(Name.identifier("r1")), Name.identifier("D")));
//        expected.add(new Triple(Name.identifier("C"), Name.identifier("C").superType(Name.identifier("A")).composeSequentially(Name.identifier("i")), Name.identifier("int")));
//        expected.add(new Triple(Name.identifier("C"),  Name.identifier("C").superType(Name.identifier("A")).composeSequentially(Name.identifier("r1")), Name.identifier("D")));
//        expected.add(new Triple(Name.identifier("E"), Name.identifier("r2").composeSequentially(Name.identifier("B").superType(Name.identifier("A")).inverse()), Name.identifier("B")));
//        expected.add(new Triple(Name.identifier("E"), Name.identifier("r2").composeSequentially(Name.identifier("C").superType(Name.identifier("A")).inverse()), Name.identifier("C")));
//        assertEquals(expected.stream().filter(Triple::isEddge).collect(Collectors.toSet()), allTriples);
//    }
//
//    @Test
//    public void testConstants() {
//        GraphImpl TG = new GraphImpl.Builder("TG")
//                .edge(Name.identifier("C"), Name.identifier("a"), Name.identifier("V"))
//                .build();
//
//        DiagrammaticOperation constantTrue = new DiagrammaticOperation.Builder(TG, Predefined.ConstantTrue.getInstance())
//                .bind(Name.variable("0"), Name.identifier("C"))
//                .bind(Name.variable("1"), Name.identifier("a"))
//                .bind(Name.variable("2"), Name.identifier("V"))
//                .builld();
//        DiagrammaticOperation constantFalse = new DiagrammaticOperation.Builder(TG, Predefined.ConstantFalse.getInstance())
//                .bind(Name.variable("0"), Name.identifier("C"))
//                .bind(Name.variable("1"), Name.identifier("a"))
//                .bind(Name.variable("2"), Name.identifier("V"))
//                .builld();
//        DiagrammaticOperation constantString = new DiagrammaticOperation.Builder(TG, Predefined.ConstantString.getInstance("Hello, World"))
//                .bind(Name.variable("0"), Name.identifier("C"))
//                .bind(Name.variable("1"), Name.identifier("a"))
//                .bind(Name.variable("2"), Name.identifier("V"))
//                .builld();
//        DiagrammaticOperation constantInt = new DiagrammaticOperation.Builder(TG, Predefined.ConstantInt.getInstance("23"))
//                .bind(Name.variable("0"), Name.identifier("C"))
//                .bind(Name.variable("1"), Name.identifier("a"))
//                .bind(Name.variable("2"), Name.identifier("V"))
//                .builld();
//        DiagrammaticOperation constantFloat = new DiagrammaticOperation.Builder(TG, Predefined.ConstantFloat.getInstance(3.1415))
//                .bind(Name.variable("0"), Name.identifier("C"))
//                .bind(Name.variable("1"), Name.identifier("a"))
//                .bind(Name.variable("2"), Name.identifier("V"))
//                .builld();
//        DiagrammaticGraph dgraph = new DiagrammaticGraph(
//                TG,
//                Collections.emptyList(),
//                Arrays.asList(constantTrue, constantFalse, constantString, constantInt, constantFloat)
//        );
//
//        GraphMorphism instance1 = new GraphMorphismImpl.TypedGrapBuilder(Name.identifier("inst0"), TG)
//                .typedNode(Name.value("V"), Name.identifier("V"))
//                .build();
//        GraphMorphism instance2 = new GraphMorphismImpl.TypedGrapBuilder(Name.identifier("inst1"), TG)
//                .typedNode(Name.identifier("c1"), Name.identifier("C"))
//                .build();
//        GraphMorphism instance3 = new GraphMorphismImpl.TypedGrapBuilder(Name.identifier("inst2"), TG)
//                .typedNode(Name.identifier("c1"), Name.identifier("C"))
//                .typedEdge(new Triple(
//                        Name.identifier("c2"),
//                        Name.identifier("a1"),
//                        Name.value(42)
//                ), new Triple(
//                        Name.identifier("C"),
//                        Name.identifier("a"),
//                        Name.identifier("V")
//                ))
//                .build();
//
//        Set<Triple> expected1 = instance1.getDomain().elements().collect(Collectors.toSet());
//        Set<Triple> expected2 = new HashSet<>(instance2.getDomain().elements().collect(Collectors.toSet()));
//        expected2.add(new Triple(
//                Name.identifier("c1"),
//                Name.identifier(
//                        Predefined.ConstantTrue.getInstance().name())
//                        .appliedTo(Name.identifier("c1").elementOf(instance2.getDomain().getName())),
//                Name.trueValue()
//        ));
//        expected2.add(new Triple(
//                Name.identifier("c1"),
//                Name.identifier(
//                        Predefined.ConstantFalse.getInstance().name())
//                        .appliedTo(Name.identifier("c1").elementOf(instance2.getDomain().getName())),
//                Name.falseValue()
//        ));
//        expected2.add(new Triple(
//                Name.identifier("c1"),
//                Name.identifier(
//                        Predefined.ConstantString.getInstance("Hello, World").name())
//                        .appliedTo(Name.identifier("c1").elementOf(instance2.getDomain().getName())),
//                Name.value("Hello, World")
//        ));
//        expected2.add(new Triple(
//                Name.identifier("c1"),
//                Name.identifier(
//                        Predefined.ConstantInt.getInstance("23").name())
//                        .appliedTo(Name.identifier("c1").elementOf(instance2.getDomain().getName())),
//                Name.value(23)
//        ));
//        expected2.add(new Triple(
//                Name.identifier("c1"),
//                Name.identifier(
//                        Predefined.ConstantFloat.getInstance(3.1415).name())
//                        .appliedTo(Name.identifier("c1").elementOf(instance2.getDomain().getName())),
//                Name.value(3.1415)
//        ));
//        expected2.add(Triple.fromNode(Name.value("Hello, World")));
//        expected2.add(Triple.fromNode(Name.value(23)));
//        expected2.add(Triple.fromNode(Name.trueValue()));
//        expected2.add(Triple.fromNode(Name.falseValue()));
//        expected2.add(Triple.fromNode(Name.value(3.1415)));
//
//        Set<Triple> expected3 = new HashSet<>(instance3.getDomain().elements().collect(Collectors.toSet()));
//        expected3.add(new Triple(
//                Name.identifier("c1"),
//                Name.identifier(
//                        Predefined.ConstantTrue.getInstance().name())
//                        .appliedTo(Name.identifier("c1").elementOf(instance3.getDomain().getName())),
//                Name.trueValue()
//        ));
//        expected3.add(new Triple(
//                Name.identifier("c1"),
//                Name.identifier(
//                        Predefined.ConstantFalse.getInstance().name())
//                        .appliedTo(Name.identifier("c1").elementOf(instance3.getDomain().getName())),
//                Name.falseValue()
//        ));
//        expected3.add(new Triple(
//                Name.identifier("c1"),
//                Name.identifier(
//                        Predefined.ConstantString.getInstance("Hello, World").name())
//                        .appliedTo(Name.identifier("c1").elementOf(instance3.getDomain().getName())),
//                Name.value("Hello, World")
//        ));
//        expected3.add(new Triple(
//                Name.identifier("c1"),
//                Name.identifier(
//                        Predefined.ConstantInt.getInstance("23").name())
//                        .appliedTo(Name.identifier("c1").elementOf(instance3.getDomain().getName())),
//                Name.value(23)
//        ));
//        expected3.add(new Triple(
//                Name.identifier("c1"),
//                Name.identifier(
//                        Predefined.ConstantFloat.getInstance(3.1415).name())
//                        .appliedTo(Name.identifier("c1").elementOf(instance3.getDomain().getName())),
//                Name.value(3.1415)
//        ));
//        expected3.add(new Triple(
//                Name.identifier("c2"),
//                Name.identifier(
//                        Predefined.ConstantTrue.getInstance().name())
//                        .appliedTo(Name.identifier("c2").elementOf(instance3.getDomain().getName())),
//                Name.trueValue()
//        ));
//        expected3.add(new Triple(
//                Name.identifier("c2"),
//                Name.identifier(
//                        Predefined.ConstantFalse.getInstance().name())
//                        .appliedTo(Name.identifier("c2").elementOf(instance3.getDomain().getName())),
//                Name.falseValue()
//        ));
//        expected3.add(new Triple(
//                Name.identifier("c2"),
//                Name.identifier(
//                        Predefined.ConstantString.getInstance("Hello, World").name())
//                        .appliedTo(Name.identifier("c2").elementOf(instance3.getDomain().getName())),
//                Name.value("Hello, World")
//        ));
//        expected3.add(new Triple(
//                Name.identifier("c2"),
//                Name.identifier(
//                        Predefined.ConstantInt.getInstance("23").name())
//                        .appliedTo(Name.identifier("c2").elementOf(instance3.getDomain().getName())),
//                Name.value(23)
//        ));
//        expected3.add(new Triple(
//                Name.identifier("c2"),
//                Name.identifier(
//                        Predefined.ConstantFloat.getInstance(3.1415).name())
//                        .appliedTo(Name.identifier("c2").elementOf(instance3.getDomain().getName())),
//                Name.value(3.1415)
//        ));
//        expected3.add(Triple.fromNode(Name.value("Hello, World")));
//        expected3.add(Triple.fromNode(Name.value(23)));
//        expected3.add(Triple.fromNode(Name.trueValue()));
//        expected3.add(Triple.fromNode(Name.falseValue()));
//        expected3.add(Triple.fromNode(Name.value(3.1415)));
//
//        Set<Triple> actual1 = dgraph.applyOperations(instance1).getDomain().elements().collect(Collectors.toSet());
//        Set<Triple> actual2 = dgraph.applyOperations(instance2).getDomain().elements().collect(Collectors.toSet());
//        Set<Triple> actual3 = dgraph.applyOperations(instance3).getDomain().elements().collect(Collectors.toSet());
//
//
//        assertEquals(expected1, actual1);
//        assertEquals(expected2, actual2);
//        assertEquals(expected3, actual3);
//
//    }

    // TODO add test for addition with multiple collection elements




}
