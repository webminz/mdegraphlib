package no.hvl.past.graph.trees;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import no.hvl.past.graph.Graph;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class NormalTree implements Tree {

    private Node root;
    private Name name;
    private Graph type;

    private final LoadingCache<Name, Optional<Node>> nodeCache;
    private final LoadingCache<Name, Optional<Name>> mappingCache;

    public NormalTree(Node root, Name name) {
        this.root = root;
        this.name = name;
        this.nodeCache = CacheBuilder.newBuilder().build(new CacheLoader<Name, Optional<Node>>() {
            @Override
            public Optional<Node> load(Name key) {
                return root.byName(key);
            }
        });
        this.mappingCache = CacheBuilder.newBuilder().build(new CacheLoader<Name, Optional<Name>>() {
            @Override
            public Optional<Name> load(Name key) throws Exception {
                return root.byName(name).flatMap(Node::nodeType);
            }
        });
    }


    protected void setRoot(Node root) {
        this.root = root;
    }

    protected void setName(Name name) {
        this.name = name;
    }

    protected void setType(Graph type) {
        this.type = type;
    }

    @Override
    public Optional<Node> findNodeById(Name elementId) {
        return nodeCache.getUnchecked(elementId);
    }

    @Override
    public Optional<Name> map(Name name) {
        return mappingCache.getUnchecked(name);
    }

    @Override
    public Node root() {
        return root;
    }

    @Override
    public Optional<Graph> type() {
        return Optional.ofNullable(type);
    }

    @Override
    public Name getName() {
        return name;
    }



}
