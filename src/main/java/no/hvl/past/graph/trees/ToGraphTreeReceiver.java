package no.hvl.past.graph.trees;

import no.hvl.past.graph.Graph;
import no.hvl.past.graph.GraphBuilders;
import no.hvl.past.graph.GraphMorphism;
import no.hvl.past.graph.Universe;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.graph.elements.Tuple;
import no.hvl.past.names.Name;
import no.hvl.past.names.NamePath;

import java.util.*;

public class ToGraphTreeReceiver implements TreeReceiver {


    private final GraphBuilders builder;
    private final Map<NamePath, Name> nodeTypingMap;
    private final Map<NamePath, Name> edgeTypingMap;
    private final Map<Name, Name> valueTypingMap;
    private final LinkedHashMap<NamePath, Name> nodeIdMap;
    private final List<Tuple> triplesToCreate;
    private final Stack<Triple> currentTypeTripleStax;
    private final Stack<Long> currentIdStax;
    private Name name;
    private Graph typeGraph;
    private TreeTypeLibrary typeLibrary;
    private NamePath current;


    public ToGraphTreeReceiver(Universe universe) {
        this.builder = new GraphBuilders(universe, true, false);
        this.nodeTypingMap = new HashMap<>();
        this.edgeTypingMap = new HashMap<>();
        this.valueTypingMap = new HashMap<>();
        this.nodeIdMap = new LinkedHashMap<>();
        this.currentTypeTripleStax = new Stack<>();
        this.currentIdStax = new Stack<>();
        this.triplesToCreate = new ArrayList<>();
    }



    @Override
    public void startTree(Name treeName) throws Exception {
        this.name = treeName;
    }

    @Override
    public void treeType(Graph typeGraph, TreeTypeLibrary mappingLibrary) throws Exception {
        this.typeGraph = typeGraph;
        this.typeLibrary = mappingLibrary;
    }

    @Override
    public void startRoot(Name rootName) throws Exception {
        current = new NamePath(Collections.singletonList(rootName));
        this.nodeIdMap.put(current, rootName);
        if (this.typeLibrary != null) {
            Name rootTyping = typeLibrary.rootTyping();
            this.nodeTypingMap.put(current, rootTyping);
            this.currentTypeTripleStax.push(Triple.node(rootTyping));
        }
    }

    @Override
    public void startBranch(Name key, boolean isCollection) throws Exception {
        if (isCollection) {
            this.currentIdStax.push(0L);
        } else {
            this.currentIdStax.push(-1L);
        }
        current = current.addChild(isCollection ? key.index(0) : key);
        if (typeLibrary != null && !currentTypeTripleStax.isEmpty()) {
            typeLibrary.childTyping(currentTypeTripleStax.peek().getTarget(), key).ifPresent(currentTypeTripleStax::push);
        }

    }

    @Override
    public void branchType(Triple type) throws Exception {
        if (!currentTypeTripleStax.isEmpty() && currentIdStax.size() == currentTypeTripleStax.size()) {
            currentTypeTripleStax.pop();
        }
        currentTypeTripleStax.push(type);
    }

    @Override
    public void endBranch() throws Exception {
        this.currentIdStax.pop();
        if (!currentTypeTripleStax.isEmpty()) {
            currentTypeTripleStax.pop();
        }
        current = current.parent();
    }

    @Override
    public void startComplexNode() throws Exception {
        this.nodeIdMap.put(current, Name.anonymousIdentifier());
        this.triplesToCreate.add(new Tuple(current, current));
        if (!currentTypeTripleStax.isEmpty()) {
            this.edgeTypingMap.put(current, currentTypeTripleStax.peek().getLabel());
            this.nodeTypingMap.put(current, currentTypeTripleStax.peek().getTarget());
        }
    }

    @Override
    public void nodeId(Name nodeId) throws Exception {
        this.nodeIdMap.put(current, nodeId);
    }

    @Override
    public void nodeType(Name type) throws Exception {
        currentTypeTripleStax.push(Triple.node(type));
    }

    @Override
    public void endComplexNode() throws Exception {
        if (!currentTypeTripleStax.isEmpty() && currentTypeTripleStax.peek().isNode()) {
            currentTypeTripleStax.pop();
        }

        Long top = currentIdStax.peek();
        if (top >= 0) {
            currentIdStax.pop();
            top++;
            currentIdStax.push(top);
        }
        current = current.next(top);

    }

    @Override
    public void emptyLeaf() throws Exception {
    }

    @Override
    public void valueLeaf(Name value) throws Exception {
        this.triplesToCreate.add(new Tuple(current, value));
        if (!currentTypeTripleStax.isEmpty()) {
            this.edgeTypingMap.put(current, currentTypeTripleStax.peek().getLabel());
            this.valueTypingMap.put(value, currentTypeTripleStax.peek().getTarget());
        }

        Long top = currentIdStax.peek();
        if (top >= 0) {
            currentIdStax.pop();
            top++;
            currentIdStax.push(top);
        }
        current = current.next(top);


    }

    @Override
    public void endRoot() throws Exception {

    }

    @Override
    public void endTree() throws Exception {
        // translate to graph
    }

    private void mkGraph() {
        for (NamePath paths : this.nodeIdMap.keySet()) {
            this.builder.node(nodeIdMap.get(paths));
        }
        for (Tuple t : triplesToCreate) {
            Name src = nodeIdMap.get(((NamePath) t.getDomain()).parent());
            Name lbl = ((NamePath) t.getDomain()).current().prefixWith(src);
            Name trg;
            if (t.getCodomain() instanceof NamePath) {
                trg = nodeIdMap.get((NamePath) t.getCodomain());
            } else {
                trg = t.getCodomain();
            }
            this.builder.edge(src, lbl, trg);
        }
    }

    private void mkMorph() {
        mkGraph();
        this.builder.graph(name.absolute());
        if (typeGraph != null) {
            this.builder.codomain(typeGraph);
        }
        for (NamePath node : this.nodeIdMap.keySet()) {
            if (nodeTypingMap.containsKey(node)) {
                builder.map(nodeIdMap.get(node), nodeTypingMap.get(node));
            }
        }
        for (Tuple t : this.triplesToCreate) {
            if (edgeTypingMap.containsKey((NamePath) t.getDomain())) {
                Name src = nodeIdMap.get(((NamePath) t.getDomain()).parent());
                Name lbl = ((NamePath) t.getDomain()).current().prefixWith(src);
                builder.map(lbl, edgeTypingMap.get((NamePath) t.getDomain()));
                if (valueTypingMap.containsKey(t.getCodomain())) {
                    builder.map(t.getCodomain(), valueTypingMap.get(t.getCodomain()));
                }
            }
        }
    }

    public Graph getGraphResult() {
        mkGraph();
        builder.graph(name);
        return builder.getResult(Graph.class);
    }

    public GraphMorphism getTypedGraphResult() {
        mkMorph();
        builder.morphism(name);
        return builder.getResult(GraphMorphism.class);
    }
}
