package no.hvl.past.graph.trees;

import no.hvl.past.graph.Graph;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import no.hvl.past.names.Prefix;
import no.hvl.past.util.Pair;
import no.hvl.past.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.*;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

import java.util.stream.Collectors;

public class XmlLibrary {

    // Well-known schema uris
    private static final String XML_METADATA_INTERCHANGE_URL = "http://www.omg.org/XMI";
    private static final String XML_SCHEMA_INSTANCE_URL = "http://www.w3.org/2001/XMLSchema-instance";
    private static final String XML_SCHEMA_URL = "http://www.w3.org/2001/XMLSchema";


    private static final XmlNameReader DEFAULT_XML_NAME_READER = new XmlNameReader() {
        @Override
        public Name read(String name) {
            return Name.identifier(name);
        }

        @Override
        public Name read(String namespace, String prefix, String name) throws URISyntaxException {
            return Name.identifier(name).prefixWith(Name.uri(namespace));
        }

        @Override
        public Name read(Name namespaceUri, String name) {
            return Name.identifier(name).prefixWith(namespaceUri);
        }
    };

    private final XmlNameWriter DEFAULT_XML_NAME_WRITER = n -> {
        if (n instanceof Prefix) {
            Prefix prefixedName = (Prefix) n;
            return new Pair<>(Optional.of(prefixedName.printRaw()), prefixedName.secondPart().printRaw());
        } else {
            return new Pair<>(Optional.empty(), n.printRaw());
        }
    };

    // public inner classes

    public static class XmlNs {
        private final String uri;
        private final String preferredPrefix;

        public XmlNs(String uri) {
            this.uri = uri;
            this.preferredPrefix = null;
        }

        public XmlNs(String uri, String preferredPrefix) {
            this.uri = uri;
            this.preferredPrefix = preferredPrefix;
        }

        public String getUri() {
            return uri;
        }

        public Optional<String> getPreferredPrefix() {
            return Optional.ofNullable(preferredPrefix);
        }

        public Name getUriAsName() {
            return Name.uri(uri);
        }
    }

    public static class XmlTyping {
        private final XmlNs mainNamespace;
        private final List<XmlNs> auxliaryNamespaces;
        private final Graph typeGraph;
        private final TreeTypeLibrary typeLibrary;

        public XmlTyping(XmlNs mainNamespace, List<XmlNs> auxliarySchemas, Graph typeGraph, TreeTypeLibrary typeLibrary) {
            this.mainNamespace = mainNamespace;
            this.typeGraph = typeGraph;
            this.typeLibrary = typeLibrary;
            this.auxliaryNamespaces = auxliarySchemas;
        }

        public boolean matchesMain(String uri) {
            return this.mainNamespace.uri.equals(uri);
        }

        public boolean matchesAll(String mainNsUri, Collection<String> auxUris) {
            if (matchesMain(mainNsUri)) {
                for (String aux : auxUris) {
                    if (auxliaryNamespaces.stream().noneMatch(regAux -> regAux.uri.equals(aux))) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }

        public XmlNs getMainNamespace() {
            return mainNamespace;
        }

        public List<XmlNs> getAuxliaryNamespaces() {
            return auxliaryNamespaces;
        }

        public Graph getTypeGraph() {
            return typeGraph;
        }

        public TreeTypeLibrary getTypeLibrary() {
            return typeLibrary;
        }

        public Optional<Name> lookupUri(String namespaceURI) throws URISyntaxException {
            if (matchesMain(namespaceURI)) {
                return Optional.of(mainNamespace.getUriAsName());
            } else {
                return auxliaryNamespaces.stream()
                        .filter(regAux -> regAux.uri.equals(namespaceURI))
                        .findFirst()
                        .map(XmlNs::getUriAsName);
            }
        }
    }


    public interface XmlNameReader {

        Name read(String name);

        Name read(String namespace, String prefix, String name) throws URISyntaxException;

        Name read(Name namespaceUri, String name);

    }

    @FunctionalInterface
    public interface XmlNameWriter {

        Pair<Optional<String>, String> write(Name name);
    }


    public static class XmlReaderConfig {

        private Name treeName;
        private Charset charset;
        private InputStream source;
        private XmlNameReader nameReader;
        private boolean specialXMIandXSItreatment;
        private Name idKey;

        public XmlReaderConfig detectIDsViaKey(Name key) {
            this.idKey = key;
            return this;
        }

        public XmlReaderConfig enableSpecialXMIXSITreatment() {
            this.specialXMIandXSItreatment = true;
            return this;
        }

        public XmlReaderConfig disableSpecialXMIXSITreatment() {
            this.specialXMIandXSItreatment = false;
            return this;
        }

        public XmlReaderConfig() {
            this.treeName = Name.anonymousIdentifier();
            charset = StandardCharsets.UTF_8;
            nameReader = DEFAULT_XML_NAME_READER;
            this.specialXMIandXSItreatment = true;
        }

        public XmlReaderConfig charset(String charsetName) {
            this.charset = Charset.forName(charsetName);
            return this;
        }

        public XmlReaderConfig treeName(Name treeName) {
            this.treeName = treeName;
            return this;
        }

        public XmlReaderConfig nameReader(XmlNameReader xmlNameReader) {
            this.nameReader = xmlNameReader;
            return this;
        }

        public TreeEmitter read(String string) throws XMLStreamException {
            InputStream input =  new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8));
            return read(input);
        }

        public TreeEmitter read(File file) throws FileNotFoundException, XMLStreamException {
            this.treeName = Name.identifier(file.getAbsolutePath());
            return this.read(new FileInputStream(file));
        }

        public TreeEmitter read(InputStream inputStream) throws XMLStreamException {
            this.source = inputStream;
            return new XMLEventReaderToTreeCreator(treeName,
                    XmlLibrary.getInstance().xmlInputFactory.createXMLEventReader(
                            inputStream, charset.name()),
                    nameReader,
                    specialXMIandXSItreatment,
                    idKey);
        }

    }

    public static class XmlWriterConfig {
        private OutputStream target;
        private boolean useAttributesWherePossible;
        private XmlNameWriter nameWriter;
        private Charset charset;


        public XmlWriterConfig() {
            this.useAttributesWherePossible = true;
            this.nameWriter = instance.DEFAULT_XML_NAME_WRITER;
            this.charset = StandardCharsets.UTF_8;
        }

        public XmlWriterConfig charset(String charsetName) {
            this.charset = Charset.forName(charsetName);
            return this;
        }

        public XmlWriterConfig useAttributesWhenPossible(boolean yes) {
            this.useAttributesWherePossible = yes;
            return this;
        }

        public XmlWriterConfig nameWriter(XmlNameWriter writer) {
            this.nameWriter = writer;
            return this;
        }

        public TreeReceiver write(OutputStream outputStream) throws XMLStreamException {
            this.target = outputStream;
            return new ToXMLTreeReceiver(
                    XmlLibrary.getInstance().xmlOutputFactory.createXMLStreamWriter(outputStream, charset.name()),
                    nameWriter,
                    !useAttributesWherePossible
                    );
        }
    }

    // Private inner classes
    private static class XMLEventReaderToTreeCreator implements TreeEmitter {


        private final XMLEventReader xmlReader;
        private final XmlNameReader branchKeyCreator;
        private final Name treeName;
        private final boolean xmiXsiTreatment;
        private final Name idDetection;

        // read when exploring the start element
        private final Map<String, String> prefixToSchema;
        private String mainNamespace;
        private XmlTyping typeContext;

        // internal state
        private final Stack<QName> lastElementStack = new Stack<>();
        private boolean hasLooseEnds = false;
        private boolean couldExpectSimpleContent = false;
        private int expectedDepth = -1;
        private int actualDepth = -1;



        public XMLEventReaderToTreeCreator(
                Name treeName,
                XMLEventReader xmlReader,
                XmlNameReader branchKeyCreator,
                boolean xmiXsiTreatment,
                Name idDetection) {
            this.treeName = treeName;
            this.xmlReader = xmlReader;
            this.branchKeyCreator = branchKeyCreator;
            this.prefixToSchema = new HashMap<>();
            this.xmiXsiTreatment = xmiXsiTreatment;
            this.idDetection = idDetection;
        }

        void parse(TreeReceiver handler) throws Exception {
            XMLEvent xmlEvent = xmlReader.nextEvent();
            if (xmlEvent.getEventType() == XMLStreamConstants.START_DOCUMENT) {

                handler.startTree(treeName);

                // advance to the root element, skipping everything except START_ELEMENT
                do {
                    xmlEvent = xmlReader.nextEvent();
                } while (xmlReader.hasNext() && xmlEvent.getEventType() != XMLStreamConstants.START_ELEMENT);

                if (xmlEvent.getEventType() != XMLStreamConstants.START_ELEMENT) {
                    throw new IOException("Malformed XML given, was expecting at least one root element");
                }

                StartElement rootElement = xmlEvent.asStartElement();

                analyzeRootElement(rootElement, handler);
                expectedDepth = 0;
                actualDepth = 0;
                hasLooseEnds = false;
                couldExpectSimpleContent = !rootElement.getAttributes().hasNext();
                lastElementStack.push(rootElement.getName());

                while (xmlReader.hasNext()) {
                    xmlEvent = xmlReader.nextEvent();
                    switch (xmlEvent.getEventType()) {

                        case XMLStreamConstants.START_ELEMENT:
                            StartElement startElement = xmlEvent.asStartElement();
                            handleStartElement(startElement, handler);
                            break;

                        case XMLStreamConstants.CHARACTERS:
                            Characters characters = xmlEvent.asCharacters();
                            handleCharacters(characters, handler);
                            break;

                        case XMLStreamConstants.END_ELEMENT:
                            handleEndElement(xmlEvent.asEndElement(), handler);
                            break;

                        case XMLStreamConstants.END_DOCUMENT:
                            handleEndDocument(handler);
                            break;

                        default:
                            break;
                        // Skipping intermediate stuff

                    }
                }
            } else {
                throw new IOException("Input was not well-formed: Expected START_DOCUMENT (id=1) but was " + xmlEvent.getEventType());
            }
        }

        private void handleStartElement(StartElement startElement, TreeReceiver handler) throws Exception {
            expectedDepth++;
            if (hasLooseEnds) {
                handler.startComplexNode();
                hasLooseEnds = false;
                couldExpectSimpleContent = false;
            }
            if (expectedDepth > actualDepth) {
                handler.startBranch(translate(startElement.getName()), true);
                actualDepth++;

                lastElementStack.push(startElement.getName());
                hasLooseEnds = true;
                couldExpectSimpleContent = true;

            } else if  (expectedDepth == actualDepth) {
                if (startElement.getName().equals(lastElementStack.peek())) {
                    // still in the same branch
                    if (!couldExpectSimpleContent) {
                        handler.endComplexNode();
                        handler.startComplexNode();
                    }
                } else {
                    // we are starting a new branch
                    if (!couldExpectSimpleContent) {
                        handler.endComplexNode();
                    }
                    handler.endBranch();
                    lastElementStack.pop();
                    lastElementStack.push(startElement.getName());
                    handler.startBranch(translate(startElement.getName()), true);
                    hasLooseEnds = true;
                    couldExpectSimpleContent = true;
                }
            }

            // attribute treatment
            if (startElement.getAttributes().hasNext()) {
                if (hasLooseEnds) {
                    hasLooseEnds = false;
                    couldExpectSimpleContent = false;
                    handler.startComplexNode();

                }
                handleAttributes(startElement, handler);
            }
        }



        private void handleCharacters(Characters characters, TreeReceiver handler) throws Exception {
            if (couldExpectSimpleContent) {
                String cdata = characters.getData().trim();
                if (!cdata.isEmpty()) {
                    hasLooseEnds = false;
                    handler.valueLeaf(Name.value(cdata));
                }
            }

        }

        private void handleEndElement(EndElement element, TreeReceiver handler) throws Exception {
            if (expectedDepth < actualDepth) {
                if (hasLooseEnds && couldExpectSimpleContent) {
                    hasLooseEnds = false;
                    handler.emptyLeaf();
                }
                if (!couldExpectSimpleContent) {
                    handler.endComplexNode();
                }
                handler.endBranch();
                couldExpectSimpleContent = false;
                lastElementStack.pop();
                actualDepth--;
            }
            expectedDepth--;
            if (expectedDepth < 0) {
                // end is reached
                handler.endRoot();
            }
        }

        private void handleEndDocument(TreeReceiver handler) throws Exception {
            handler.endTree();
        }


        private Name translate(QName qName) throws URISyntaxException {
            if (qName.getNamespaceURI() != null && !qName.getNamespaceURI().isEmpty()) {
                if (typeContext != null) {
                    Optional<Name> uri = typeContext.lookupUri(qName.getNamespaceURI());
                    if (uri.isPresent()) {
                        return branchKeyCreator.read(uri.get(), qName.getLocalPart());
                    } else {
                        XmlLibrary.getInstance().logger.error("Unknown URI: '" + qName.getNamespaceURI() + "'");
                        return branchKeyCreator.read(qName.getLocalPart());
                    }
                } else {
                    return branchKeyCreator.read(qName.getNamespaceURI(), qName.getPrefix(), qName.getLocalPart());
                }

            } else {
                return branchKeyCreator.read(qName.getLocalPart());
            }
        }

        @SuppressWarnings("rawtypes")
        private void analyzeRootElement(StartElement rootElement, TreeReceiver handler) throws Exception {
            // register namespaces and see if there is a type graph for it

            Iterator namespaces = rootElement.getNamespaces();
            while (namespaces.hasNext()) {
                Namespace ns = (Namespace) namespaces.next();
                if (ns.getPrefix() != null) {
                    this.prefixToSchema.put(ns.getPrefix(), ns.getNamespaceURI());
                } else {
                    this.mainNamespace = ns.getNamespaceURI();
                }
            }

            if (rootElement.getName().getPrefix() != null) {
                this.mainNamespace = prefixToSchema.get(rootElement.getName().getPrefix());
            }

            if (this.mainNamespace != null) {
                Optional<XmlTyping> typ = XmlLibrary.getInstance().getTypeLibraryFor(mainNamespace, this.prefixToSchema.values().stream().filter(s -> !s.equals(this.mainNamespace))
                        .filter(s -> !s.equals(XML_METADATA_INTERCHANGE_URL))
                        .filter(s -> !s.equals(XML_SCHEMA_INSTANCE_URL)).collect(Collectors.toSet()));
                if (typ.isPresent()) {
                    this.typeContext = typ.get();
                    handler.treeType(typeContext.typeGraph, typeContext.typeLibrary);
                }
            }


            handler.startRoot(translate(rootElement.getName()));
            checkForIds(rootElement, handler);

            Iterator attributes = rootElement.getAttributes();
            while (attributes.hasNext()) {
                Attribute a = (Attribute) attributes.next();
                handleAttribute(handler, a);
            }

        }

        private void startBranch(StartElement startElement, TreeReceiver handler) throws Exception {
            handler.startBranch(translate(startElement.getName()), true); // TODO better with typing lib...
        }


        @SuppressWarnings("rawtypes")
        private void handleAttributes(StartElement element, TreeReceiver handler) throws Exception {
            checkForIds(element, handler);


            Iterator attributeIt = element.getAttributes();
            while (attributeIt.hasNext()) {
                Attribute att = (Attribute) attributeIt.next();
                handleAttribute(handler, att);
            }
        }

        private void checkForIds(StartElement element, TreeReceiver handler) throws Exception {
            if (idDetection != null || xmiXsiTreatment) {
                @SuppressWarnings("rawtypes") Iterator i = element.getAttributes();
                if (idDetection != null) {
                    while (i.hasNext()) {
                        Attribute att = (Attribute) i.next();
                        if (idDetection.equals(translate(att.getName()))) {
                            handler.nodeId(Name.identifier(att.getValue()));
                            break;
                        }
                    }
                } else {
                    while (i.hasNext()) {
                        Attribute att = (Attribute) i.next();
                        if (att.getName().getPrefix() != null && XML_METADATA_INTERCHANGE_URL.equals(this.prefixToSchema.get(att.getName().getPrefix()))) {
                            handleXMIAttribute(handler, att);
                        } else if (att.getName().getPrefix() != null && XML_SCHEMA_INSTANCE_URL.equals(this.prefixToSchema.get(att.getName().getPrefix()))) {
                            handleXSIAttribute(handler, att);
                        }
                    }
                }
            }
        }

        private void handleAttribute(TreeReceiver handler, Attribute att) throws Exception {
            if (att.getName().getPrefix() != null && !att.getName().getPrefix().isEmpty() && !this.prefixToSchema.containsKey(att.getName().getPrefix())) {
                throw new IOException("Unregistered XML namespace prefix: " + att.getName().getPrefix());
            } else if (idDetection != null && idDetection.equals(translate(att.getName()))) {
                return;
            } else if (att.getName().getPrefix() != null && XML_METADATA_INTERCHANGE_URL.equals(this.prefixToSchema.get(att.getName().getPrefix()))) {
                return;
            } else if (att.getName().getPrefix() != null && XML_SCHEMA_INSTANCE_URL.equals(this.prefixToSchema.get(att.getName().getPrefix()))) {
                return;
            } else {
                Name name = translate(att.getName());
                handler.startBranch(name, false);
                if (att.getValue().isEmpty()) {
                    handler.emptyLeaf();
                } else {
                    handler.valueLeaf(Name.value(att.getValue()));
                }
                handler.endBranch();
            }
        }

        private void handleXSIAttribute(TreeReceiver handler, Attribute att) throws Exception {
            if (att.getName().getLocalPart().equals("type")) {
                // TODO V.1.0.0: check for prefix
                handler.nodeType(Name.identifier(att.getValue()));
            }
        }

        private void handleXMIAttribute(TreeReceiver handler, Attribute att) throws Exception {
            if (att.getName().getLocalPart().equals("version")) {
                // ignore
            }
            if (att.getName().getLocalPart().equals("id")) {
                handler.nodeId(Name.identifier(att.getValue()));
            }
        }

        @Override
        public void emitEvents(TreeReceiver target) throws Exception {
            parse(target);
        }

        @Override
        public void reset() {

        }
    }




    private static class ToXMLTreeReceiver implements TreeReceiver {

        private boolean useCDATAInsteadOfAttributes = false;
        private final Stack<Name> currentTag = new Stack<>();
        private final XMLStreamWriter writer;
        private final XmlNameWriter toXMLnameTransformer;

        public ToXMLTreeReceiver(
                XMLStreamWriter writer,
                XmlNameWriter toXMLnameTransformer,
                boolean useCDATAInsteadOfAttributes) {
            this.writer = writer;
            this.toXMLnameTransformer = toXMLnameTransformer;
            this.useCDATAInsteadOfAttributes = useCDATAInsteadOfAttributes;
        }

        @Override
        public void startTree(Name treeName) throws XMLStreamException {
            writer.writeStartDocument();

        }

        @Override
        public void treeType(Graph typeGraph, TreeTypeLibrary mappingLibrary) throws XMLStreamException {
            Optional<XmlTyping> typing = XmlLibrary.getInstance().getNamespacesFor(typeGraph);
            if (typing.isPresent()) {
                Set<String> prefixesInUse = new HashSet<>();
                String mainPrefix = typing.get().mainNamespace.getPreferredPrefix().orElse(StringUtils.initials(typing.get().mainNamespace.getUri()));
                prefixesInUse.add(mainPrefix);
                writer.writeNamespace(mainPrefix, typing.get().mainNamespace.uri);
                for (XmlNs ns : typing.get().auxliaryNamespaces) {
                    String prfx = ns.getPreferredPrefix().orElse(StringUtils.initials(ns.getUri()));
                    if (prefixesInUse.contains(prfx)) {
                        prfx = prfx + "1";
                    }
                    prefixesInUse.add(prfx);
                    writer.writeNamespace(prfx, ns.uri);
                }
                writer.writeDefaultNamespace(typing.get().mainNamespace.uri);
            }
        }

        private void writeTag(Name name) throws XMLStreamException {
            Pair<Optional<String>, String> qname = toXMLnameTransformer.write(name);
            if (qname.getFirst().isPresent()) {
                writer.writeStartElement(qname.getFirst().get(), qname.getSecond());
            } else {
                writer.writeStartElement(qname.getSecond());
            }
        }

        @Override
        public void startRoot(Name rootName) throws Exception {
            writeTag(rootName);
        }

        @Override
        public void nodeId(Name id) throws IOException {

        }

        @Override
        public void nodeType(Name type) throws IOException {

        }


        @Override
        public void startBranch(Name key, boolean isCollection) throws IOException {
            this.currentTag.push(key);
        }

        @Override
        public void branchType(Triple type) throws Exception {

        }

        @Override
        public void endBranch() throws IOException {
            this.currentTag.pop();
        }

        @Override
        public void startComplexNode() throws XMLStreamException {
            writeTag(currentTag.peek());
        }

        @Override
        public void endComplexNode() throws XMLStreamException {
            writer.writeEndElement();

        }

        @Override
        public void emptyLeaf() throws IOException {
        }

        @Override
        public void valueLeaf(Name value) throws XMLStreamException {
            if (useCDATAInsteadOfAttributes) {
                writeTag(currentTag.peek());
                writer.writeCData(value.printRaw());
                writer.writeEndElement();
            } else {
                Pair<Optional<String>, String> qname = toXMLnameTransformer.write(currentTag.peek());
                if (qname.getFirst().isPresent()) {
                    writer.writeAttribute(qname.getFirst().get(), qname.getSecond(), value.printRaw());
                } else {
                    writer.writeAttribute(qname.getSecond(), value.printRaw());
                }
            }


        }

        @Override
        public void endRoot() throws XMLStreamException {
            writer.writeEndElement();
        }

        @Override
        public void endTree() throws XMLStreamException {
            writer.writeEndDocument();
        }


    }





    private static XmlLibrary instance;

    private final XMLInputFactory xmlInputFactory;
    private final XMLOutputFactory xmlOutputFactory;

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final List<XmlTyping> registeredXMLTypes = new ArrayList<>(); // TODO caches ?


    private XmlLibrary() {
        this.xmlInputFactory = XMLInputFactory.newFactory();
        this.xmlOutputFactory = XMLOutputFactory.newFactory();

    }


    private Optional<XmlTyping> getNamespacesFor(Graph typeGraph) {
        for (XmlTyping xmlTyping : registeredXMLTypes) {
            if (xmlTyping.getTypeGraph().equals(typeGraph)) {
                return Optional.of(xmlTyping);
            }
        }
        return Optional.empty();
    }

    Optional<XmlTyping> getTypeLibraryFor(
            String primarySchemaURL,
            Collection<String> secondarySchemaURLs) {
        for (XmlTyping xmlTyping : registeredXMLTypes) {
            if (xmlTyping.matchesAll(primarySchemaURL, secondarySchemaURLs)) {
                return Optional.of(xmlTyping);
            }
        }
        logger.info("Could not find a type graph for schema '" + primarySchemaURL + "'" +
                (secondarySchemaURLs.isEmpty() ? "!" : " (and supporting schemas: " +
                        StringUtils.fuseList(secondarySchemaURLs, schema -> "'" + schema + "'", ", ")));
        return Optional.empty();
    }

    public static XmlLibrary getInstance() {
        if (instance == null) {
            instance = new XmlLibrary();
        }
        return instance;
    }

    public XmlReaderConfig reader() {
        return new XmlReaderConfig();
    }

    public XmlWriterConfig writer() {
        return new XmlWriterConfig();
    }


    public void registerSchema(XmlNs mainNs, Graph typeGraph, TreeTypeLibrary treeTypeLibrary, XmlNs... auxNss) {
        List<XmlNs> aux = new ArrayList<>();
        Collections.addAll(aux, auxNss);
        this.registeredXMLTypes.add(new XmlTyping(mainNs, aux, typeGraph, treeTypeLibrary));
    }




}



