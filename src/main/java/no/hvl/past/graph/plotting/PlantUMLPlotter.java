package no.hvl.past.graph.plotting;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import no.hvl.past.graph.GraphTheory;
import no.hvl.past.graph.Universe;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.graph.operations.Invert;
import no.hvl.past.graph.plotting.dm.*;
import no.hvl.past.graph.predicates.*;
import no.hvl.past.logic.FormulaLiteral;
import no.hvl.past.names.*;
import no.hvl.past.systems.ComprSys;
import no.hvl.past.systems.MessageArgument;
import no.hvl.past.systems.MessageType;
import no.hvl.past.systems.Sys;
import no.hvl.past.util.Pair;
import no.hvl.past.util.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;

public class PlantUMLPlotter {

    private FileFormat outputFormat;
    private boolean printDiagrams;


    public PlantUMLPlotter(String format, boolean printDiagrams) {
        this.printDiagrams = printDiagrams;
        switch (format) {
            case "SVG":
                this.outputFormat = FileFormat.SVG;
                break;
            case "PDF":
                this.outputFormat = FileFormat.PDF;
                break;
            case "EPS":
                this.outputFormat = FileFormat.EPS;
                break;
            case "PNG":
            default:
                this.outputFormat = FileFormat.PNG;
                break;
        }
    }

    // TODO for instances
    // public void plot(SysData instance, OutputStream outputStream, boolean includeMetamodel)


    public void plot(Sys system, OutputStream outputStream) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(bos);

        writeSystem(system, writer);

        writer.flush();
        writer.close();

        String plantUMLSpec = bos.toString("UTF-8");
        // TODO configurable from outside
        // System.out.println(plantUMLSpec);

        SourceStringReader sourceStringReader = new SourceStringReader(plantUMLSpec);
        sourceStringReader.outputImage(outputStream, new FileFormatOption(this.outputFormat));

    }

    private void writeSystem(Sys system, OutputStreamWriter writer) throws IOException {
        FilePlotElement file = new FilePlotElement();

        system.types().forEach(node -> {
            if (system instanceof ComprSys) {
                if (node.firstPart().equals(system.schema().getName())) {
                    ComprSys csys = (ComprSys) system;
                    if (!csys.isMerged(node.secondPart())) {
                        plotCommWitn(csys, file, node);
                    } else {
                        plotNode(system,file,node);
                    }
                } else {
                    plotNode(system,file,node);
                }
            } else {
                plotNode(system, file, node);
            }
        });
        Set<Triple> covered = new HashSet<>();


        system.links().forEach(t -> {
            if (system instanceof ComprSys) {
                ComprSys csys = (ComprSys) system;
                if (t.getLabel() instanceof BinaryCombinator) {
                    plotProjection(csys, file, file, covered, t);
                } else if (t.getLabel().firstPart().equals(system.schema().getName())) {
                    if (csys.isMerged(t.getLabel())) {
                        plotCommLink(csys, file, covered, t);
                    } else {
                        plotLink(system,file,covered,t);
                    }
                } else {
                    plotLink(system, file, covered, t);
                }
            } else {
                plotLink(system, file, covered, t);
            }
        });

        system.directSuperTypes().forEach(t -> {
            String from = mkReference(system, t.getDomain());
            String to = mkReference(system, t.getCodomain());
            LinkPlotElement linkPlotElement = file.addAnonEdge(from, to);
            linkPlotElement.setType(LinkPlotElement.LinkType.INHERITANCE);
        });


        if (system.messages().anyMatch(x -> true)) {
            NodePlotElement service = file.addNode(system.schema().getName().printRaw() + "Service");
            service.setType(NodePlotElement.NodeType.SERVICE);
            system.messages().forEach(messageType -> {
                plotMessage(system, service, messageType);
            });
        }

        if (printDiagrams) {
            system.schema().diagrams()
                    .filter(d -> !(d instanceof MessageType) && !(d instanceof MessageArgument) &&
                            !DataTypePredicate.getInstance().diagramIsOfType(d) &&
                            !TargetMultiplicity.class.isAssignableFrom(d.label().getClass()) &&
                            !SourceMultiplicity.class.isAssignableFrom(d.label().getClass()) &&
                            !Acyclicity.class.isAssignableFrom(d.label().getClass()) &&
                            !Ordered.class.isAssignableFrom(d.label().getClass()) &&
                            !Unique.class.isAssignableFrom(d.label().getClass()) &&
                            !Invert.class.isAssignableFrom(d.label().getClass()) &&
                            !AbstractType.class.isAssignableFrom(d.label().getClass()) &&
                            !Singleton.class.isAssignableFrom(d.label().getClass()))
                .forEach(d -> {
                    plotDiagram(system, file, d);
                });
        }


        file.writePlantUML(writer);

    }

    private void plotCommLink(ComprSys csys, FilePlotElement metamodel, Set<Triple> covered, Triple t) {
        LinkPlotElement linkPlotElement = metamodel.addNamedEdge(csys.displayName(t.getSource()), csys.displayName(t.getLabel()), csys.displayName(t.getTarget()));
        linkPlotElement.setSpecialStyling("#blue;line.dashed");

    }

    private void plotProjection(ComprSys system, FilePlotElement file, FilePlotElement metamodel, Set<Triple> covered, Triple t) {
        String name = system.displayName(t.getSource());
        if (file.getTraceLink(name)!= null) {
            file.getTraceLink(name).add(mkReference(system, t.getTarget()));
        }
    }

    private void plotCommWitn(ComprSys system, FilePlotElement file, Name node) {
        file.addTraceLink(system.displayName(node));
    }

    private void plotDiagram(Sys system, FilePlotElement metamodel, no.hvl.past.graph.Diagram d) {
        String content = "";
        Name name = d.getName();
        if (!(name instanceof AnonymousIdentifier)) {
            content = name.print(PrintingStrategy.DETAILED);
        }
        if (d.label() instanceof GraphTheory) {
            content += "\n(";
            content += ((GraphTheory) d.label()).getName().print(PrintingStrategy.DETAILED);
            content += ")";
        }
        if (d.binding().domain().equals(Universe.ONE_NODE)) {
            metamodel.addNote(mkReference(system, d.nodeBinding().get()),content, NotePlotElement.Location.TYPE);
        } else if (d.binding().domain().equals(Universe.ARROW)) {
            Triple triple = d.edgeBinding().get();
            if (system.isAttributeType(triple)) {
                metamodel.addNote(mkReference(system,triple.getSource()) + "::" + system.displayName(triple.getLabel()), content, NotePlotElement.Location.ATTRIBUTE);
            } else {
                metamodel.addNote(system.displayName(triple.getLabel()),content, NotePlotElement.Location.LINK);
            }
        } else {
            List<String> references = new ArrayList<>();
            d.binding().domain().nodes().forEach(n -> d.binding().map(n).map(system::displayName).ifPresent(references::add));
            metamodel.addMultiNote(content,references);
        }
    }

    private void plotMessage(Sys system, NodePlotElement service, MessageType messageType) {
        StringBuilder serviceMethod = new StringBuilder();
        serviceMethod.append(system.displayName(messageType.typeName()));
        serviceMethod.append('(');
        serviceMethod.append(StringUtils.fuseList(messageType.inputs(), arg -> {
            return system.displayName(arg.asEdge().getLabel()) +
                    " : " +
                    system.displayName(arg.type()) + makeMultIfNotOptional(system.getTargetMultiplicity(arg.asEdge()));
        }, ", "));
        serviceMethod.append(") : ");
        String returnType = StringUtils.fuseList(messageType.outputs(), arg -> {
            return system.displayName(arg.type()) + makeMultIfNotOptional(system.getTargetMultiplicity(arg.asEdge()));
        }, ", ");
        if (returnType.isEmpty()) {
            returnType = "void";
        }
        serviceMethod.append(returnType);
        service.addCompartment(serviceMethod.toString());
    }

    private String mkReference(Sys sys, Name name) {
        String result = "";
        Name current = name;
        if (sys instanceof ComprSys && name.hasPrefix(sys.schema().getName())) {
            result += sys.schema().getName().printRaw() + "::overlaps::";
            current = name.secondPart();
        }
        while (current instanceof Prefix) {
            result += current.firstPart().printRaw();
            result += "::";
            current = current.secondPart();
        }
        result += sys.displayName(name);
        return result;
    }

    private void plotLink(Sys system, FilePlotElement metamodel, Set<Triple> covered, Triple t) {
        if (!covered.contains(t)) {
            Optional<Triple> opp = system.getOppositeIfExists(t);
            String s = mkReference(system,t.getSource());
            String l = system.displayName(t.getLabel());
            String tr = mkReference(system, t.getTarget());
            LinkPlotElement linkPlotElement = metamodel.addNamedEdge(s, tr, l);
            boolean isComp = system.isComposition(t);
            if (opp.isPresent()) {
                covered.add(opp.get());
                String ol = system.displayName(opp.get().getLabel());
                if (isComp) {
                    linkPlotElement.setType(LinkPlotElement.LinkType.BICOMPOSITION);
                } else {
                    linkPlotElement.setType(LinkPlotElement.LinkType.BIREFERENCE);
                }
                String assocName = system.schema().diagramsOn(t).filter(d -> Invert.class.isAssignableFrom(d.label().getClass())).findFirst().map(d -> d.getName().printRaw()).orElse(l + ":" + ol);
                linkPlotElement.setLabel(assocName);
                String tM = makeMult(system.getTargetMultiplicity(t));
                String sM = makeMult(system.getTargetMultiplicity(opp.get()));
                linkPlotElement.setTrgLabel(l + "  " + tM);
                linkPlotElement.setSrcLabel(ol + "  " + sM);
            } else {
                Pair<Integer, Integer> targetMultiplicity = system.getTargetMultiplicity(t);
                linkPlotElement.setTrgLabel(makeMult(targetMultiplicity));
                Pair<Integer, Integer> sourceMultiplicity = system.getSourceMultiplicity(t);
                if (sourceMultiplicity.getLeft() != 0 || sourceMultiplicity.getRight() != 1) {
                    linkPlotElement.setSrcLabel(makeMult(sourceMultiplicity));
                }
                if (isComp) {
                    linkPlotElement.setType(LinkPlotElement.LinkType.COMPOSITION);
                } else if (system.isAggregation(t)) {
                    linkPlotElement.setType(LinkPlotElement.LinkType.AGGREGATION);
                }
            }
            covered.add(t);
        }
    }

    private void plotNode(Sys system, FilePlotElement file, Name node) {
        String name = mkReference(system,node);


        if (system.isEnumType(node)) {
            NodePlotElement enumPlot = file.addNode(name);
            enumPlot.setType(NodePlotElement.NodeType.ENUM);
            for (Name lit : system.enumLiterals(node)) {
                enumPlot.addCompartment(lit.printRaw());
            }
        } else if (!system.isSimpleTypeNode(node)) {
            NodePlotElement nodePlot = file.addNode(name);
            if (system.isAbstract(node)) {
                nodePlot.setType(NodePlotElement.NodeType.ABSTRACT_TYPE);
            } else if (system.isSingleton(node)) {
                nodePlot.setType(NodePlotElement.NodeType.SINGLETON_TYPE);
            }
            system.attributeFeatures(node).forEach(attEdge -> {
                String attName = system.displayName(attEdge.getLabel());
                String typName = system.displayName(attEdge.getTarget());
                NodePlotElement.Compartment attCompartment = nodePlot.addCompartment(attName, typName);
                Pair<Integer, Integer> targetMultiplicity = system.getTargetMultiplicity(attEdge);
                attCompartment.setPredicates(makeMultIfNotOptional(targetMultiplicity));
            });
        }
    }

    private GroupPlotElement getMetamodelSubpackage(GroupPlotElement groupPlotElement, Name secondPart) {
        if (secondPart instanceof Prefix) {
            Name p = secondPart.firstPart();
            String s = p.printRaw();
            if (groupPlotElement.getGroupByName(s) == null) {
                return getMetamodelSubpackage(groupPlotElement.addGroup(s), secondPart.secondPart());
            } else {
                return getMetamodelSubpackage(groupPlotElement.getGroupByName(s), secondPart.secondPart());
            }
        } else {
            return groupPlotElement;
        }
    }


    private String makeMultIfNotOptional(Pair<Integer, Integer> targetMultiplicity) {
        if (targetMultiplicity.getLeft() != 0 || targetMultiplicity.getRight() != 1) {
            return "[" + makeMult(targetMultiplicity) + "]";
        }
        return "";
    }

    @NotNull
    private String makeMult(Pair<Integer, Integer> targetMultiplicity) {
        return "" + (targetMultiplicity.getLeft() < 0 ? "*" : targetMultiplicity.getLeft()) + ".." + (targetMultiplicity.getRight() < 0 ? "*" : targetMultiplicity.getRight()) + "";
    }

}
