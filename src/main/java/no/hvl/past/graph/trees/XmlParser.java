package no.hvl.past.graph.trees;

import no.hvl.past.graph.Sketch;
import no.hvl.past.names.Name;
import no.hvl.past.util.FileSystemUtils;
import org.w3c.dom.Document;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Stack;

public class XmlParser {

    public static final String XML_TEXT_CONTENT_FIELD_NAME = "text";
    private final XMLInputFactory factory;

    public XmlParser() {
        this.factory = XMLInputFactory.newFactory();
    }

    public Tree parse(File source, Name resultName) throws XMLStreamException, IOException {
        XMLEventReader reader = factory.createXMLEventReader(new FileInputStream(source));
        return parse(reader, new TreeBuildStrategy(resultName));
    }

    public Tree parse(String source, Name resultName) throws XMLStreamException, IOException {
        XMLEventReader reader = factory.createXMLEventReader(new ByteArrayInputStream(source.getBytes(StandardCharsets.UTF_8)));
        return parse(reader, new TreeBuildStrategy(resultName));
    }

    public Tree parse(InputStream source, Name resultName) throws XMLStreamException, IOException {
        XMLEventReader reader = factory.createXMLEventReader(source);
        return parse(reader, new TreeBuildStrategy(resultName));
    }



    public Tree parse(Document rootNode, Name resultName) {
        return null; // TODO
    }

    public TypedTree parse(File source, TreeBuildStrategy buildStrategy) throws IOException, XMLStreamException {
        XMLEventReader reader = factory.createXMLEventReader(new FileInputStream(source));
        return (TypedTree) parse(reader, buildStrategy);
    }


    public TypedTree parse(String source, TreeBuildStrategy buildStrategy) throws XMLStreamException, IOException {
        XMLEventReader reader = factory.createXMLEventReader(new ByteArrayInputStream(source.getBytes(StandardCharsets.UTF_8)));
        return (TypedTree) parse(reader, buildStrategy);
    }

    public TypedTree parse(InputStream source, TreeBuildStrategy buildStrategy) throws XMLStreamException, IOException {
        XMLEventReader reader = factory.createXMLEventReader(source);
        return (TypedTree) parse(reader, buildStrategy); // TODO
    }

    public TypedTree parseTyped(Document source, Name resultName, Sketch schema, Name rootType) {
        return null; // TODO
    }


    private static Tree parse(XMLEventReader xmlReader, TreeBuildStrategy buildStrategy) throws XMLStreamException, IOException {
        Stack<Node.Builder> builderStack = new Stack<>();
        XMLEvent xmlEvent = xmlReader.nextEvent();

        if (xmlEvent.getEventType() == XMLStreamConstants.START_DOCUMENT) {
            Node.Builder root = buildStrategy.root();
            builderStack.push(root);
            while (xmlReader.hasNext()) {
                xmlEvent = xmlReader.nextEvent();
                switch (xmlEvent.getEventType()) {
                    case XMLStreamConstants.START_ELEMENT:
                        StartElement startElement = xmlEvent.asStartElement();

                        builderStack.push(buildStrategy.objectChild(builderStack.peek(), startElement.getName().getLocalPart()));

                        Iterator attributeIt = startElement.getAttributes();
                        while (attributeIt.hasNext()) {
                            Attribute att = (Attribute) attributeIt.next();
                            if (att.getName().getNamespaceURI() != null && !att.getName().getNamespaceURI().isEmpty()) {
                                buildStrategy.simpleChild(builderStack.peek(),att.getName().getNamespaceURI() ,att.getName().getLocalPart(),att.getValue());
                            } else {
                                buildStrategy.simpleChild(builderStack.peek(),att.getName().getLocalPart(),att.getValue());
                            }
                        }

                        break;
                    case XMLStreamConstants.CHARACTERS:
                        Characters characters = xmlEvent.asCharacters();
                        if (!characters.getData().trim().isEmpty()) {
                            buildStrategy.simpleChild(builderStack.peek(), XML_TEXT_CONTENT_FIELD_NAME, characters.getData());
                        }
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        builderStack.pop();
                        break;
                    case XMLStreamConstants.END_DOCUMENT:
                        break;
                        // Everything else is just skipped
                    default:

                }
            }
            return buildStrategy.tree(root.build());
        }
        throw new IOException("Is not well-formed XML");
    }


//    public static Tree readFromFile(Name containerName, String fileName) throws FileNotFoundException, XMLStreamException {
//        File xmlFile = FileSystemUtils.getInstance().file(fileName);
//        XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
//        FileInputStream fis = new FileInputStream(xmlFile);
//        XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(fis);
//
//        NodeImpl.Builder rootBuilder = new NodeImpl.Builder(ROOT_NAME, 0).setRoot();
//        XMLParseNode parseNode = new XMLParseNode(null, rootBuilder, xmlEventReader);
//        while (xmlEventReader.hasNext()) {
//            parseNode.proceed();
//        }
//        return new TreeImpl(containerName, parseNode.result);
//    }


//    static class XMLParseNode {
//        private final XMLParseNode parent;
//        private final NodeImpl.Builder builder;
//        private final XMLEventReader reader;
//        private Node result;
//
//        public XMLParseNode(XMLParseNode parent, NodeImpl.Builder builder, XMLEventReader reader) {
//            this.parent = parent;
//            this.builder = builder;
//            this.reader = reader;
//        }
//
//        public void proceed() throws XMLStreamException {
//            if (reader.hasNext()) {
//                XMLEvent xmlEvent = reader.nextEvent();
//                switch (xmlEvent.getEventType()) {
//                    case XMLStreamConstants.START_ELEMENT:
//                        StartElement startElement = xmlEvent.asStartElement();
//
//                        NodeImpl.Builder child = this.builder.addChild(translate(startElement.getName()));
//
//                        Iterator attributeIt = startElement.getAttributes();
//                        while (attributeIt.hasNext()) {
//                            Attribute att = (Attribute) attributeIt.next();
//                            child.addAttribute(translate(att.getName()), Name.value(att.getValue()));
//                        }
//
//                        XMLParseNode childNode = new XMLParseNode(this, child, reader);
//                        childNode.proceed();
//
//                        break;
//                    case XMLStreamConstants.CHARACTERS:
//                        Characters characters = xmlEvent.asCharacters();
//                        if (!characters.getData().trim().isEmpty()) {
//                            builder.name(Name.value(characters.getData()));
//                        }
//                        this.proceed();
//                    case XMLStreamConstants.END_ELEMENT:
//                        finishChild();
//                        break;
//                    case XMLStreamConstants.END_DOCUMENT:
//                        finishAll();
//                        break;
//                    default:
//                        this.proceed();// skip
//                }
//            }
//        }
//
//        private Name translate(QName qualifiedName) {
//            Name result;
//            if (qualifiedName.getPrefix() != null && !qualifiedName.getPrefix().isEmpty()) {
//                result = Name.identifier(qualifiedName.getLocalPart()).prefixWith(Name.identifier(qualifiedName.getPrefix()));
//            } else {
//                result = Name.identifier(qualifiedName.getLocalPart());
//            }
//            return result;
//        }
//
//        public void finishChild() throws XMLStreamException {
//            if (parent != null) {
//                parent.proceed();
//            } else {
//                this.proceed();
//            }
//        }
//
//        public void finishAll() {
//            if (parent != null) {
//                parent.finishAll();
//            } else {
//                this.result =  builder.build(null);
//            }
//        }
//    }



}
