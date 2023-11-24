package no.hvl.past.graph.trees;

import no.hvl.past.attributes.DataTypeDescription;
import no.hvl.past.graph.Graph;
import no.hvl.past.graph.GraphMorphism;
import no.hvl.past.graph.TestWithGraphLib;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import no.hvl.past.names.URIName;
import no.hvl.past.util.Multiplicity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class RoundtripTests extends TestWithGraphLib {


    public static final String NS = "https://www.example.com/objects";
    public static final URIName NS_URI = Name.uri(NS);
    public static final Name O_ROOT = id("_2347");
    public static final Name O_C0 = id("_4589");
    public static final Name O_C0C0 = id("_3874");
    public static final Name O_C0C1 = id("_7823");
    public static final Name O_C1 = id("_0238");
    public static final Name O_C2 = id("_8764");
    private Graph typeGraph;
    private TreeTypeLibrary treeTypeLibrary;

    @BeforeEach
    public void setUp() {
        typeGraph = getContextCreatingBuilder()
                .node("O")
                .node("V")
                .edge("O", "r", "O")
                .edge("O", "c", "O")
                .edge("O", "a", "V")
                .graph("Typ")
                .getResult(Graph.class);

        treeTypeLibrary = new TreeTypeLibrary() {
            @Override
            public Name rootTyping() {
                return id("O");
            }

            @Override
            public Optional<Triple> childTyping(Name parentType, Name childBranchLabel) {
                if (childBranchLabel.equals(id("object"))) {
                    return Optional.of(Triple.node(id("O")));
                }
                if (childBranchLabel.equals(id("child").prefixWith(NS_URI))) {
                    return Optional.of(Triple.edge(id("O"), id("c"), id("O")));
                }
                if (childBranchLabel.equals(id("ref"))) {
                    return Optional.of(Triple.edge(id("O"), id("r"), id("O")));
                }
                if (childBranchLabel.equals(id("attribute").prefixWith(NS_URI))) {
                    return Optional.of(Triple.edge(id("O"), id("a"), id("V")));
                }
                return Optional.empty();
            }

            @Override
            public Optional<Multiplicity> branchMultiplicity(Name parentType, Name childBranchLabel) {
                return Optional.empty();
            }

            @Override
            public Optional<DataTypeDescription> branchDataType(Name parentType, Name childBranchLabel) {
                return Optional.empty();
            }
        };
        XmlLibrary.getInstance().registerSchema(new XmlLibrary.XmlNs(NS), typeGraph, treeTypeLibrary);

    }

    @Test
    public void testReading() throws Exception {
        FileInputStream inputStream = new FileInputStream("src/test/resources/trees/objects.xml");
        TreeEmitter.SimpleCollector actual = new TreeEmitter.SimpleCollector();
        TreeEmitter.SimpleCollector expected = new TreeEmitter.SimpleCollector();

        TreeReceiver.MapName refParser1 = new TreeReceiver.MapName(actual, id("ref")) {
            @Override
            public Name map(Name in) {
                return in.asID();
            }
        };

        XmlLibrary.getInstance().reader().treeName(id("import")).read(inputStream).emitEvents(refParser1);



        expected.startTree(id("import"));
        expected.treeType(typeGraph, treeTypeLibrary);
        expected.startRoot(id("object").prefixWith(NS_URI));
        expected.nodeId(O_ROOT);
        expected.startBranch(id("child").prefixWith(NS_URI), true);

            expected.startComplexNode();
            expected.nodeId(O_C0);
                expected.startBranch(id("child").prefixWith(NS_URI), true);
                    expected.startComplexNode();
                        expected.nodeId(O_C0C0);
                        expected.startBranch(id("ref"), false);
                            expected.valueLeaf(Name.identifier("_0238"));
                        expected.endBranch();
                        expected.startBranch(id("attribute").prefixWith(NS_URI), true);
                            expected.valueLeaf(Name.value("x"));
                        expected.endBranch();
                    expected.endComplexNode();
                    expected.startComplexNode();
                        expected.nodeId(O_C0C1);
                        expected.startBranch(id("ref"), false);
                            expected.valueLeaf(Name.identifier("_8764"));
                        expected.endBranch();
                        expected.startBranch(id("attribute").prefixWith(NS_URI), true);
                            expected.valueLeaf(Name.value("y"));
                        expected.endBranch();
                    expected.endComplexNode();
                expected.endBranch();
            expected.endComplexNode();

            expected.startComplexNode();
                    expected.nodeId(O_C1);
                    expected.startBranch(id("ref"), false);
                        expected.valueLeaf(Name.identifier("_8764"));
                    expected.endBranch();
                    expected.startBranch(id("attribute").prefixWith(NS_URI), true);
                        expected.valueLeaf(Name.value("z"));
                    expected.endBranch();
            expected.endComplexNode();

            expected.startComplexNode();
                expected.nodeId(O_C2);
                    expected.startBranch(id("attribute").prefixWith(NS_URI), true);
                        expected.valueLeaf(Name.value("z"));
                    expected.endBranch();
                expected.endComplexNode();

        expected.endBranch();
        expected.endRoot();
        expected.endTree();

        assertTrue(expected.equals(actual));
    }

    @Test
    public void testGraphReading() throws Exception {
        GraphMorphism expected = getContextCreatingBuilder()
                .node(O_ROOT)
                .node(O_C0)
                .edge(O_ROOT, id("child").prefixWith(NS_URI).index(0).prefixWith(O_ROOT), O_C0)
                .node(O_C0C0)
                .edge(O_C0, id("child").prefixWith(NS_URI).index(0).prefixWith(O_C0), O_C0C0)
                .node(O_C0C1)
                .edge(O_C0C0, id("ref").prefixWith(O_C0C0), O_C1)
                .node(Name.value("x"))
                .edge(O_C0C0, id("attribute").prefixWith(NS_URI).index(0).prefixWith(O_C0C0), Name.value("x"))
                .node(O_C1)
                .edge(O_C0, id("child").prefixWith(NS_URI).index(1).prefixWith(O_C0), O_C0C1)
                .node(O_C2)
                .node(Name.value("y"))
                .edge(O_C1, id("ref").prefixWith(O_C1), O_C2)
                .edge(O_C1, id("attribute").prefixWith(NS_URI).index(0).prefixWith(O_C1), Name.value("z"))
                .edge(O_ROOT, id("child").prefixWith(NS_URI).index(1).prefixWith(O_ROOT), O_C1)
                .edge(O_C0C1, id("ref").prefixWith(O_C0C1), O_C2)
                .node(Name.value("z"))
                .edge(O_C0C1, id("attribute").prefixWith(NS_URI).index(0).prefixWith(O_C0C1), Name.value("y"))
                .edge(O_ROOT, id("child").prefixWith(NS_URI).index(2).prefixWith(O_ROOT), O_C2)
                .edge(O_C2, id("attribute").prefixWith(NS_URI).index(0).prefixWith(O_C2), Name.value("z"))
                .graph(id("import").absolute())
                .codomain(typeGraph)
                .map(O_ROOT, id("O"))
                .map(O_C0, id("O"))
                .map(O_C0C0, id("O"))
                .map(O_C0C1, id("O"))
                .map(O_C2, id("O"))
                .map(O_C1, id("O"))
                .map(Name.value("x"), id("V"))
                .map(Name.value("y"), id("V"))
                .map(Name.value("z"), id("V"))
                .map(id("child").prefixWith(NS_URI).index(0).prefixWith(O_ROOT), id("c"))
                .map(id("child").prefixWith(NS_URI).index(0).prefixWith(O_C0), id("c"))
                .map(id("child").prefixWith(NS_URI).index(1).prefixWith(O_C0), id("c"))
                .map(id("child").prefixWith(NS_URI).index(1).prefixWith(O_ROOT), id("c"))
                .map(id("child").prefixWith(NS_URI).index(2).prefixWith(O_ROOT), id("c"))
                .map(id("ref").prefixWith(O_C0C0), id("r"))
                .map(id("ref").prefixWith(O_C1), id("r"))
                .map(id("ref").prefixWith(O_C0C1), id("r"))
                .map(id("attribute").prefixWith(NS_URI).index(0).prefixWith(O_C0), id("a"))
                .map(id("attribute").prefixWith(NS_URI).index(0).prefixWith(O_C1), id("a"))
                .map(id("attribute").prefixWith(NS_URI).index(0).prefixWith(O_C0C1), id("a"))
                .map(id("attribute").prefixWith(NS_URI).index(0).prefixWith(O_C2), id("a"))
                .map(id("attribute").prefixWith(NS_URI).index(0).prefixWith(O_C0C0), id("a"))
                .morphism(id("import"))
                .getResult(GraphMorphism.class);

        FileInputStream inputStream = new FileInputStream("src/test/resources/trees/objects.xml");
        ToGraphTreeReceiver receiver = new ToGraphTreeReceiver(getUniverse());
        TreeReceiver.MapName refParser = new TreeReceiver.MapName(receiver, id("ref")) {
            @Override
            public Name map(Name in) {
                return in.asID();
            }
        };

        XmlLibrary.getInstance().reader().treeName(id("import")).read(inputStream).emitEvents(refParser);

        GraphMorphism actual = receiver.getTypedGraphResult();

        assertMorphismsEqual(expected, actual);
    }


}
