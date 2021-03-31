package no.hvl.past.graph.trees;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class XMLTreeTest {

    @Test
    public void testParseTablesXML() throws FileNotFoundException, XMLStreamException {
//        Tree marks = XmlParser.readFromFile(Name.identifier("marks"), "src/test/resources/trees/marks.tables");
//        Node root = marks.root().children(Name.identifier("Table").prefixWith(Name.identifier("tables"))).findFirst().get();
//        assertEquals(2, root.attributeNames().count());
//        assertEquals(Optional.of(Name.value("Exam Results")), root.attribute(Name.identifier("name")));
//
//        List<Node> columnGroups = root.children(Name.identifier("columnGroups")).collect(Collectors.toList());
//        List<Node> rows = root.children(Name.identifier("rows")).collect(Collectors.toList());
//        assertEquals(4, columnGroups.size());
//        Node firstCG = columnGroups.get(0);
//        assertEquals(1, firstCG.childBranchNames().count());
//        assertEquals(1, firstCG.children(Name.identifier("columns")).count());
//        assertEquals(Name.value("Student No"), firstCG.children(Name.identifier("columns")).findFirst().get().attribute(Name.identifier("title")).get());
//
//        assertEquals(3, rows.size());
//        Name cellName = rows.get(1).children(Name.identifier("cells")).collect(Collectors.toList()).get(6).elementName();
//        Set<Name> valueSet = marks.outgoing(cellName).filter(Triple::isEddge).map(Triple::getTarget).collect(Collectors.toSet());
//        assertEquals(2, valueSet.size());
//        assertTrue(valueSet.contains(Name.value("A")));
//        assertTrue(valueSet.contains(Name.value("//@columnGroups.3/@columns.0")));
    }
}
