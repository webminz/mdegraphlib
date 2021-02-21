package no.hvl.past.graph.trees;

import no.hvl.past.graph.Graph;
import no.hvl.past.names.Name;
import no.hvl.past.util.FileSystemUtils;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;


public class XmlTree implements Tree {

    // TODO a representation based on DOM

    private static final Name ROOT_NAME = Name.identifier("document");

    static class XMLParseNode {
        private final XMLParseNode parent;
        private final NodeImpl.Builder builder;
        private final XMLEventReader reader;
        private Node result;

        public XMLParseNode(XMLParseNode parent, NodeImpl.Builder builder, XMLEventReader reader) {
            this.parent = parent;
            this.builder = builder;
            this.reader = reader;
        }

        public void proceed() throws XMLStreamException {
            if (reader.hasNext()) {
                XMLEvent xmlEvent = reader.nextEvent();
                switch (xmlEvent.getEventType()) {
                    case XMLStreamConstants.START_ELEMENT:
                        StartElement startElement = xmlEvent.asStartElement();

                        NodeImpl.Builder child = this.builder.addChild(translate(startElement.getName()));

                        Iterator attributeIt = startElement.getAttributes();
                        while (attributeIt.hasNext()) {
                            Attribute att = (Attribute) attributeIt.next();
                            child.addAttribute(translate(att.getName()), Name.value(att.getValue()));
                        }

                        XMLParseNode childNode = new XMLParseNode(this, child, reader);
                        childNode.proceed();

                        break;
                    case XMLStreamConstants.CHARACTERS:
                        Characters characters = xmlEvent.asCharacters();
                        if (!characters.getData().trim().isEmpty()) {
                            builder.name(Name.value(characters.getData()));
                        }
                        this.proceed();
                    case XMLStreamConstants.END_ELEMENT:
                        finishChild();
                        break;
                    case XMLStreamConstants.END_DOCUMENT:
                        finishAll();
                        break;
                    default:
                        this.proceed();// skip
                }
            }
        }

        private Name translate(QName qualifiedName) {
            Name result;
            if (qualifiedName.getPrefix() != null && !qualifiedName.getPrefix().isEmpty()) {
                result = Name.identifier(qualifiedName.getLocalPart()).prefixWith(Name.identifier(qualifiedName.getPrefix()));
            } else {
                result = Name.identifier(qualifiedName.getLocalPart());
            }
            return result;
        }

        public void finishChild() throws XMLStreamException {
            if (parent != null) {
                parent.proceed();
            } else {
                this.proceed();
            }
        }

        public void finishAll() {
            if (parent != null) {
                parent.finishAll();
            } else {
                this.result =  builder.build(null);
            }
        }
    }


    @Override
    public Node root() {
        return null;
    }


    @Override
    public Name getName() {
        return null;
    }

    public static Tree readFromFile(Name containerName, String fileName) throws FileNotFoundException, XMLStreamException {
        File xmlFile = FileSystemUtils.getInstance().file(fileName);
        XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
        FileInputStream fis = new FileInputStream(xmlFile);
        XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(fis);

        NodeImpl.Builder rootBuilder = new NodeImpl.Builder(ROOT_NAME, 0).setRoot();
        XMLParseNode parseNode = new XMLParseNode(null, rootBuilder, xmlEventReader);
        while (xmlEventReader.hasNext()) {
            parseNode.proceed();
        }
        return new TreeImpl(containerName, parseNode.result);
    }



}
