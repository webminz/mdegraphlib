package no.hvl.past.graph.trees;


import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import no.hvl.past.graph.Graph;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.graph.expressions.EvaluatableExpression;
import no.hvl.past.names.Name;
import no.hvl.past.names.NamePath;
import no.hvl.past.util.Pair;
import no.hvl.past.util.StringUtils;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;


/**
 * A {@link TreeCollectCursor} provides an advanced machanism
 * to coordinate a collection multiple {@link TreeEvent} streams simultaneously
 * and emit them as a single stream.
 * It can receive events an several channels and provides a {@link TreeEmitter}
 * interface.
 * {@link TreeCollectCursor}s are being constructed from a {@link no.hvl.past.graph.trees.CursorConfig}.
 *
 */
public abstract class TreeCollectCursor implements TreeEmitter {



    static final class Channel {

        private final String channel;
        private final String errorStream;
        private final Stack<TreeCollectCursor> cursorHandlerStack = new Stack<>();
        private final TreeReceiver internalHandler;

        public Channel(CursorConfig.ChannelConfiguration configuration, TreeCollectCursor currentCursor) {
            this.channel = configuration.getChannel();
            this.errorStream = configuration.errorStream();
            this.cursorHandlerStack.push(currentCursor);
            TreeReceiver validatorHandler = createInternalValidatorHandler();
            TreeReceiver regularHandler = createRegularHandler();
            TreeReceiver.Multiplexer mxer = new TreeReceiver.Multiplexer(Arrays.asList(validatorHandler, regularHandler));
            TreeReceiver.MapName mapper = configuration.getRenamer(mxer);
            this.internalHandler = configuration.getFilter(mapper);
        }

        private TreeReceiver createRegularHandler() {
            return new TreeReceiver() {
                @Override
                public void startTree(Name treeName) throws Exception {
                }

                @Override
                public void treeType(Graph typeGraph, TreeTypeLibrary mappingLibrary) throws Exception {
                }

                @Override
                public void startRoot(Name rootName) throws Exception {
                    try {
                        currentCursor().startRoot(channel, rootName);
                    } catch (Exception e) {
                        currentCursor().registerError(errorStream, currentCursor().currentPath() , e);
                    }
                }

                @Override
                public void startBranch(Name key, boolean isCollection) throws Exception {
                    try {
                        currentCursor().startBranch(channel, key, isCollection);
                    } catch (Exception e) {
                        currentCursor().registerError(errorStream, currentCursor().currentPath() , e);
                    }
                }

                @Override
                public void branchType(Triple type) throws Exception {
                    try {
                        currentCursor().branchType(channel, type);
                    } catch (Exception e) {
                        currentCursor().registerError(errorStream, currentCursor().currentPath() , e);
                    }
                }

                @Override
                public void endBranch() throws Exception {
                    try {
                        currentCursor().endBranch(channel);
                    } catch (Exception e) {
                        currentCursor().registerError(errorStream, currentCursor().currentPath() , e);
                    }
                }

                @Override
                public void startComplexNode() throws Exception {
                    try {
                        currentCursor().startComplexNode(channel);
                    } catch (Exception e) {
                        currentCursor().registerError(errorStream, currentCursor().currentPath() , e);
                    }
                }

                @Override
                public void nodeId(Name nodeId) throws Exception {
                    try {
                        currentCursor().nodeId(channel, nodeId);
                    } catch (Exception e) {
                        currentCursor().registerError(errorStream, currentCursor().currentPath() , e);
                    }
                }

                @Override
                public void nodeType(Name type) throws Exception {
                    try {
                        currentCursor().nodeType(channel, type);
                    } catch (Exception e) {
                        currentCursor().registerError(errorStream, currentCursor().currentPath() , e);
                    }
                }

                @Override
                public void endComplexNode() throws Exception {
                    try {
                        currentCursor().endComplexNode(channel);
                    } catch (Exception e) {
                        currentCursor().registerError(errorStream, currentCursor().currentPath() , e);
                    }
                }

                @Override
                public void emptyLeaf() throws Exception {
                    try {
                        currentCursor().emptyLeaf(channel);
                    } catch (Exception e) {
                        currentCursor().registerError(errorStream, currentCursor().currentPath() , e);
                    }
                }

                @Override
                public void valueLeaf(Name value) throws Exception {
                    try {
                        currentCursor().valueLeaf(channel, value);
                    } catch (Exception e) {
                        currentCursor().registerError(errorStream, currentCursor().currentPath() , e);
                    }
                }

                @Override
                public void endRoot() throws Exception {
                    try {
                        currentCursor().endRoot(channel);
                    } catch (Exception e) {
                        currentCursor().registerError(errorStream, currentCursor().currentPath() , e);
                    }
                }

                @Override
                public void endTree() throws Exception {
                }
            };
        }

        private TreeReceiver createInternalValidatorHandler() {
            return new TreeReceiver() {
                @Override
                public void startTree(Name treeName) throws Exception {
                }

                @Override
                public void treeType(Graph typeGraph, TreeTypeLibrary mappingLibrary) throws Exception {
                }

                @Override
                public void startRoot(Name rootName) throws Exception {
                    currentCursor().getValidator().ifPresent(treeValidator -> treeValidator.startRoot(rootName));
                }

                @Override
                public void startBranch(Name key, boolean isCollection) throws Exception {
                    currentCursor().getValidator().ifPresent(treeValidator -> treeValidator.startBranch(key, isCollection));

                }

                @Override
                public void branchType(Triple type) throws Exception {
                    currentCursor().getValidator().ifPresent(treeValidator -> treeValidator.branchType(type));

                }

                @Override
                public void endBranch() throws Exception {
                    currentCursor().getValidator().ifPresent(treeValidator -> treeValidator.endBranch());

                }

                @Override
                public void startComplexNode() throws Exception {
                    currentCursor().getValidator().ifPresent(treeValidator -> treeValidator.startComplexNode());

                }

                @Override
                public void nodeId(Name nodeId) throws Exception {
                    currentCursor().getValidator().ifPresent(treeValidator -> treeValidator.nodeId(nodeId));

                }

                @Override
                public void nodeType(Name type) throws Exception {
                    currentCursor().getValidator().ifPresent(treeValidator -> treeValidator.nodeType(type));

                }

                @Override
                public void endComplexNode() throws Exception {
                    currentCursor().getValidator().ifPresent(treeValidator -> treeValidator.endComplexNode());

                }

                @Override
                public void emptyLeaf() throws Exception {
                    currentCursor().getValidator().ifPresent(treeValidator -> treeValidator.emptyLeaf());

                }

                @Override
                public void valueLeaf(Name value) throws Exception {
                    currentCursor().getValidator().ifPresent(treeValidator -> treeValidator.valueLeaf(value));

                }

                @Override
                public void endRoot() throws Exception {
                    currentCursor().getValidator().ifPresent(treeValidator -> treeValidator.endRoot());

                }

                @Override
                public void endTree() throws Exception {
                    currentCursor().getValidator().ifPresent(treeValidator -> treeValidator.endTree());

                }
            };
        }


        public void newHandler(TreeCollectCursor cursor) {
            cursorHandlerStack.push(cursor);
        }

        public void handBack() {
            cursorHandlerStack.pop();
        }

        private TreeCollectCursor currentCursor() {
            return cursorHandlerStack.peek();
        }

        TreeReceiver getHandler() {
            return internalHandler;
        }


    }


    static class Cache implements Iterable<List<Pair<String, TreeEmitter>>> {

        private final Map<Long, SimpleCollector> collectorsMap = new HashMap<>();
        private final Map<Long, String> channels = new HashMap<>();
        private final Map<Long, TreeReceiver.ExpressionEvaluator> keyEvaluator = new HashMap<>();
        private final Map<String, EvaluatableExpression> keyExpressions;
        //private Map<Long, Map<Name, TreeReceiver.ExpressionEvaluator>> otherEvaluators;
        private final Multimap<Name, Long> evaluatedKeys = MultimapBuilder.hashKeys().arrayListValues().build();
        private final Multimap<Long, Name> evaluatedKeysReverse = MultimapBuilder.hashKeys().arrayListValues().build();
        private long counter = 0;

        public Cache(Map<String, EvaluatableExpression> keyExpressions) {
            this.keyExpressions = keyExpressions;
        }

        long pullNumber(String channel) {
            long result = counter++;
            channels.put(result, channel);
            collectorsMap.put(result, new SimpleCollector());
            EvaluatableExpression expression = keyExpressions.get(channel);
            if (expression != null) {
                keyEvaluator.put(result, new TreeReceiver.ExpressionEvaluator(expression));
            }
            return result;
        }

        void registerEvent(long number, TreeEvent event) throws Exception {
            event.accept(collectorsMap.get(number));
            if (keyEvaluator.containsKey(number)) {
                event.accept(keyEvaluator.get(number));
            }

        }

        void endNumber(long number) {
            if (keyEvaluator.containsKey(number)) {
                for (Name key : keyEvaluator.get(number).evaluate()) {
                    evaluatedKeys.put(key, number);
                    evaluatedKeysReverse.put(number, key);
                }
            }
        }


        void reset() {
            this.collectorsMap.clear();
            this.channels.clear();
            keyEvaluator.clear();
            evaluatedKeysReverse.clear();
            evaluatedKeys.clear();
            this.counter = 0;
        }


        @Override
        public Iterator<List<Pair<String, TreeEmitter>>> iterator() {
            return new Iterator<List<Pair<String, TreeEmitter>>>() {

                private long current = 0;
                private final Set<Long> alreadySeen = new HashSet<>();

                private long advance(long startAt, Set<Long> alreadySeen) {
                    if (alreadySeen.contains(startAt)) {
                        return advance(startAt + 1L, alreadySeen);
                    } else {
                        return startAt;
                    }
                }

                @Override
                public boolean hasNext() {
                    return current < counter && !alreadySeen.contains(current);
                }

                @Override
                public List<Pair<String, TreeEmitter>> next() {
                    List<Pair<String, TreeEmitter>> result = new ArrayList<>();

                    boolean converged = false;
                    LinkedHashSet<Long> toAdd = new LinkedHashSet<>();
                    toAdd.add(current);

                    while (!converged) {
                        converged = true;
                        LinkedHashSet<Long> nextRound = new LinkedHashSet<>();
                        Iterator<Long> ticketIterator = toAdd.iterator();
                        while (ticketIterator.hasNext()) {
                            Long ticket = ticketIterator.next();

                            String channel = channels.get(ticket);
                            channels.remove(current);
                            SimpleCollector collector = collectorsMap.get(ticket);
                            collectorsMap.remove(current);
                            result.add(new Pair<>(channel, collector));

                            for (Name key : evaluatedKeysReverse.get(ticket)) {
                                for (Long otherTicket : evaluatedKeys.get(key)) {
                                    if (!alreadySeen.contains(otherTicket) && !toAdd.contains(otherTicket)) {
                                        nextRound.add(otherTicket);
                                        converged = false;
                                    }
                                }
                            }

                            alreadySeen.add(ticket);
                            ticketIterator.remove();
                        }
                        toAdd = nextRound;
                    }

                    current = advance(current + 1, alreadySeen);
                    return result;
                }
            };
        }


    }


    public static class GlobalMergingCursor extends TreeCollectCursor {

        private final Cache cache;
        private final Map<String, AtomicInteger> depthCounters = new HashMap<>();
        private final LinkedHashMap<Name, CursorConfig.NestedCursorConfig> objectConfig;
        private final Set<String> openChannels = new HashSet<>();
        private final List<OneObjectCollector> objects = new ArrayList<>();
        private final Map<String, Long> ticketNumbers = new HashMap<>();

        public GlobalMergingCursor(TreeCollectCursor parent, Cache cache, LinkedHashMap<Name, CursorConfig.NestedCursorConfig> objectConfig, Set<String> channels) {
            super(parent);
            this.cache = cache;
            this.objectConfig = objectConfig;
            for (String channel : channels) {
                depthCounters.put(channel, new AtomicInteger(-1));
                openChannels.add(channel);
                parent.updateChannelListener(channel, this);
            }
        }

        void activate(Set<String> channels) {
            for (String channel : channels) {
                if (depthCounters.containsKey(channel)) {
                    depthCounters.get(channel).incrementAndGet();
                }
            }
            this.openChannels.retainAll(channels);
        }

        @Override
        protected boolean isCollection() {
            return true;
        }

        @Override
        public void emitEvents(TreeReceiver target) throws Exception {
            for (OneObjectCollector ob : objects) {
                ob.emitEvents(target);
            }
        }

        @Override
        public Set<String> unclosedChannels() {
            return openChannels;
        }

        @Override
        public void reset() {
            // ???
        }

        @Override
        protected void startBranch(String channel, Name key, boolean isCollection) throws Exception {
            int depth = depthCounters.get(channel).incrementAndGet();
            if (depth > 0 && this.ticketNumbers.containsKey(channel)) {
                Long ticketNumber = this.ticketNumbers.get(channel);
                this.cache.registerEvent(ticketNumber, TreeEvent.startBranch(key, isCollection));
            }
        }

        @Override
        protected void startComplexNode(String channel) throws Exception {
            int depth = depthCounters.get(channel).get();
            if (depth == 0) {
                long number = this.cache.pullNumber(channel);
                this.ticketNumbers.put(channel, number);
            } else if (depth > 0 && this.ticketNumbers.containsKey(channel)) {
                Long ticketNumber = this.ticketNumbers.get(channel);
                this.cache.registerEvent(ticketNumber, TreeEvent.startComplexNode());
            }
        }

        @Override
        protected void valueLeaf(String channel, Name value) throws Exception {
            if (ticketNumbers.containsKey(channel)) {
                this.cache.registerEvent(ticketNumbers.get(channel), TreeEvent.valueLeaf(value));
            }
        }

        @Override
        protected void emptyLeaf(String channel) throws Exception {
            if (ticketNumbers.containsKey(channel)) {
                this.cache.registerEvent(ticketNumbers.get(channel), TreeEvent.emptyLeaf());
            }
        }

        @Override
        protected void endBranch(String channel) throws Exception {
            int depth = depthCounters.get(channel).decrementAndGet();
            if (depth >= 0 && ticketNumbers.containsKey(channel)) {
                this.cache.registerEvent(ticketNumbers.get(channel), TreeEvent.endBranch());
            }
            if (depth < 0) {
                openChannels.remove(channel);
                if (getParent() != null) {
                    getParent().handBack(channel);
                }
            }
            if (openChannels.isEmpty()) {
                internalHandOver();
//                for (String chnl : this.depthCounters.keySet()) {
//                    if (getParent() != null) {
//                        getParent().handBack(chnl);
//                    }
//                }
            }
        }

        @Override
        protected void endComplexNode(String channel) throws Exception {
            int depth = depthCounters.get(channel).get();
            if (depth == 0 && ticketNumbers.containsKey(channel)) {
                Long ticketNumber = ticketNumbers.get(channel);
                ticketNumbers.remove(channel);
                this.cache.endNumber(ticketNumber);
            } else if (ticketNumbers.containsKey(channel)) {
                this.cache.registerEvent(ticketNumbers.get(channel), TreeEvent.endComplexNode());
            }
        }

        // is called as soon as all open channels are closed
        // an open channel is closed when its depth gets negative (again)
        private void internalHandOver() throws Exception {
            for (List<Pair<String, TreeEmitter>> cached : cache) {
                OneObjectCollector object = new OneObjectCollector(this);
                Set<String> channels = new HashSet<>(this.depthCounters.keySet());
                objects.add(object);
                for (Name feature : objectConfig.keySet()) {
                    TreeCollectCursor subCursor = objectConfig.get(feature).createSubCursor(object);
                    if (subCursor instanceof GlobalMergingCursor) {
                        // not so nice...
                        ((GlobalMergingCursor) subCursor).activate(cached.stream().map(Pair::getFirst).collect(Collectors.toSet()));
                    }
                    object.addFeatureHandler(feature, subCursor);
                }
                for (Pair<String, TreeEmitter> collected : cached) {
                    channels.remove(collected.getFirst());
                    this.updateChannelListener(collected.getFirst(), object);
                    TreeReceiver shifter = getReceiverForChannel(collected.getFirst());
                    // start complex node
                    collected.getSecond().emitEvents(shifter);
                    shifter.endComplexNode();

                }
                for (String stillOpen : channels) {
                    object.thatsIt(stillOpen);
                }
            }
            cache.reset();
        }
    }



    public static class ConcatenatingObjectsCollector extends TreeCollectCursor {

        private final LinkedHashMap<Name, CursorConfig.NestedCursorConfig> childrenConfig;
        private final List<OneObjectCollector> objects = new ArrayList<>();
        private Semaphore semaphore = new Semaphore(0);
        private final Set<String> waitsForChannels = new HashSet<>();


        public ConcatenatingObjectsCollector(TreeCollectCursor parent, LinkedHashMap<Name, CursorConfig.NestedCursorConfig> childrenConfig, Set<String> waitsForChannels) {
            super(parent);
            this.childrenConfig = childrenConfig;
            this.waitsForChannels.addAll(waitsForChannels);
        }

        @Override
        public Set<String> unclosedChannels() {
            return waitsForChannels;
        }

        @Override
        protected void endBranch(String channel) throws Exception {
            waitsForChannels.remove(channel);
            if (getParent() != null) {
                getParent().handBack(channel);
            }
            if (waitsForChannels.isEmpty()) {
                semaphore = new Semaphore(objects.size());
            }
        }

        @Override
        protected void startComplexNode(String channel) throws Exception {
            OneObjectCollector ob = new OneObjectCollector(this);
            for (Name branch : childrenConfig.keySet()) {
                ob.addFeatureHandler(branch, childrenConfig.get(branch).createSubCursor(ob));
            }
            updateChannelListener(channel, ob);
            objects.add(ob);
        }

        @Override
        protected boolean isCollection() {
            return true;
        }

        @Override
        public void emitEvents(TreeReceiver target) throws Exception {
            for (OneObjectCollector collector : objects) {
                collector.emitEvents(target);
            }
        }

        @Override
        public void reset() {

        }
    }


    public static class OneObjectCollector extends TreeCollectCursor {

        private final LinkedHashMap<Name, TreeCollectCursor> featureMap = new LinkedHashMap<>();
        private Semaphore semaphore = new Semaphore(0);

        public void addFeatureHandler(Name branch, TreeCollectCursor handler) {
            featureMap.put(branch, handler);
        }

        public OneObjectCollector(TreeCollectCursor parent) {
            super(parent);
        }

        @Override
        protected boolean isCollection() {
            return false;
        }

        @Override
        public void emitEvents(TreeReceiver target) throws Exception {
            target.startComplexNode();
            for (Name branchKey : featureMap.keySet()) {
                target.startBranch(branchKey, featureMap.get(branchKey).isCollection());
                featureMap.get(branchKey).emitEvents(target);
                target.endBranch();
            }

            target.endComplexNode();
        }

        @Override
        public void reset() {
            for (Name key : featureMap.keySet()) {
                featureMap.get(key).reset();
            }
            this.semaphore = new Semaphore(0);
        }

        @Override
        protected void startBranch(String channel, Name key, boolean isCollection) throws Exception {
            if (featureMap.containsKey(key)) {
                updateChannelListener(channel, featureMap.get(key));
            }
        }

        @Override
        protected void endComplexNode(String channel) throws Exception {
            if (getParent() != null) {
                getParent().handBack(channel);
            }
            semaphore.release();
        }


        public void thatsIt(String channel) throws Exception {
            for (Name prop : this.featureMap.keySet()) {
                TreeCollectCursor subCursor = this.featureMap.get(prop);
                if (subCursor.unclosedChannels().contains(channel)) {
                    subCursor.endBranch(channel);
                }
            }
            // notify all sub
        }
    }



    // TODO make safe against concurrent events from separate channels
    public static class LeafCollectCursor extends TreeCollectCursor {


        private final SimpleCollector collector = new SimpleCollector();

        private final boolean isCollection;
        private final boolean isCompacting;

        private int depth = 0;


        public LeafCollectCursor(TreeCollectCursor parent,
                                 boolean isCollection,
                                 boolean isCompacting) {
            super(parent);
            this.isCollection = isCollection;
            this.isCompacting = isCompacting;
        }


        @Override
        protected boolean isCollection() {
            return isCollection;
        }

        @Override
        public void emitEvents(TreeReceiver target) throws Exception {
            if (!isCollection && collector.getCollectedEvents().isEmpty()) {
                target.emptyLeaf();
            } else {
                collector.emitEvents(target);
            }
        }

        @Override
        public void reset() {
            collector.reset();
            this.depth = 0;
        }


        @Override
        public void startBranch(String channel, Name key, boolean isCollection) throws Exception {
            collector.startBranch(key, isCollection);
        }

        @Override
        public void branchType(String channel,Triple type) throws Exception {
            collector.branchType(type);

        }

        @Override
        public void endBranch(String channel) throws Exception {
            if (depth == 0) {
                handBack(channel);
            } else {
                collector.endBranch();
            }
        }

        @Override
        public void startComplexNode(String channel) throws Exception {
            depth++;
            collector.startComplexNode();


        }

        @Override
        public void nodeId(String channel, Name nodeId) throws Exception {
            collector.nodeId(nodeId);

        }

        @Override
        public void nodeType(String channel, Name type) throws Exception {
            collector.nodeType(type);
        }

        @Override
        public void endComplexNode(String channel) throws Exception {
            depth--;
            collector.endComplexNode();

        }

        @Override
        public void emptyLeaf(String channel) throws Exception {
            collector.emptyLeaf();
        }

        @Override
        public void valueLeaf(String channel, Name value) throws Exception {
            if (!isCompacting || collector.getCollectedEvents().isEmpty() || !collector.getCollectedEvents().contains(TreeEvent.valueLeaf(value)) ) {
                collector.valueLeaf(value);
            }
        }

    }


    public static final class RootCursor extends TreeCollectCursor {

        private Name treeName = Name.anonymousIdentifier();
        private Name rootName = Name.anonymousIdentifier();

        private Name rootType;
        private Pair<Graph, TreeTypeLibrary> treeType;

        private final boolean emitStartTreeEvent;
        private final LinkedHashMap<Name, TreeCollectCursor> fields = new LinkedHashMap<>();

        private final Set<String> activeChannels = new HashSet<>();

        public RootCursor(boolean emitStartTreeEvent) {
            super(null);
            this.emitStartTreeEvent = emitStartTreeEvent;
        }

        public RootCursor setRootName(Name rootName) {
            this.rootName = rootName;
            return this;
        }

        public RootCursor registerCollectedFiled(Name fieldName, TreeCollectCursor cursor) {
            this.fields.put(fieldName, cursor);
            return this;
        }

        public RootCursor setTreeName(Name treeName) {
            this.treeName = treeName;
            return this;
        }

        public RootCursor setRootType(Name rootType) {
            this.rootType = rootType;
            return this;
        }

        public RootCursor setTreeType(Pair<Graph, TreeTypeLibrary> treeType) {
            this.treeType = treeType;
            return this;
        }

        @Override
        protected boolean isCollection() {
            return false;
        }

        @Override
        public void emitEvents(TreeReceiver target) throws Exception {
            if (emitStartTreeEvent) {
                target.startTree(treeName);
                if (treeType != null) {
                    target.treeType(treeType.getFirst(), treeType.getSecond());
                }
            }
            target.startRoot(rootName);
            if (rootType != null) {
                target.nodeType(rootType);
            }
            for (Name key : fields.keySet()) {
                target.startBranch(key, fields.get(key).isCollection());
                fields.get(key).emitEvents(target);
                target.endBranch();
            }
            target.endRoot();
            if (emitStartTreeEvent) {
                target.endTree();
            }
        }

        @Override
        public void reset() {

        }

        @Override
        protected void startBranch(String channel, Name key, boolean isCollection) throws Exception {
            if (activeChannels.contains(channel)) {
                if (this.fields.containsKey(key)) {
                    updateChannelListener(channel, fields.get(key));
                }
            }
        }

        @Override
        protected void startRoot(String channel, Name rootName) {
            this.activeChannels.add(channel);
        }

        @Override
        protected void endRoot(String channel) {
            this.activeChannels.remove(channel);
        }


    }


    public static class ConstantCursor extends TreeCollectCursor {

        private final TreeEmitter.ConstantEmitter emitter;


        public ConstantCursor(TreeCollectCursor parent, List<TreeEvent> constantEvents) {
            super(parent);
            this.emitter = new TreeEmitter.ConstantEmitter(constantEvents);
        }


        @Override
        protected boolean isCollection() {
            return true; // TODO
        }

        @Override
        public void emitEvents(TreeReceiver target) throws Exception {
            emitter.emitEvents(target);
        }

        @Override
        public void reset() {
            emitter.reset();
        }
    }

    public static class ErrorCursor extends TreeCollectCursor {

        private final Name messageName;
        private final Name locationName;
        private final Name stackTraceName;
        private final int stackStraceDepth;

        private final SimpleCollector simpleCollector = new SimpleCollector();

        public ErrorCursor(TreeCollectCursor parent, String errorStreamName, Name messageName, Name locationName, Name stackTraceName, int stackStraceDepth) {
            super(parent);
            this.messageName = messageName;
            this.locationName = locationName;
            this.stackTraceName = stackTraceName;
            this.stackStraceDepth = stackStraceDepth;

            parent.getErrorEventBus().put(errorStreamName, pair -> handle(pair.getFirst(), pair.getSecond()));
        }



        @Override
        protected boolean isCollection() {
            return true;
        }

        @Override
        public void emitEvents(TreeReceiver target) throws Exception {
            simpleCollector.emitEvents(target);
        }

        @Override
        public void reset() {
            simpleCollector.reset();
        }



        private void handle(NamePath location, Exception e) {
                if (messageName == null) {
                    simpleCollector.valueLeaf(Name.value(e.getMessage()));
                } else {
                    simpleCollector.startComplexNode();
                    simpleCollector.startBranch(messageName, false);
                    simpleCollector.valueLeaf(Name.value(e.getMessage()));
                    simpleCollector.endBranch();
                    if (locationName != null) {
                        simpleCollector.startBranch(locationName, true);
                        for (String lpart : location.segmentString()) {
                            if (StringUtils.isNumber(lpart)) {
                                simpleCollector.valueLeaf(Name.value(Long.parseLong(lpart)));
                            } else {
                                simpleCollector.valueLeaf(Name.value(lpart));
                            }
                        }
                        simpleCollector.endBranch();
                    }
                    if (stackTraceName != null) {
                        simpleCollector.startBranch(stackTraceName, true);
                        int depth = Math.min(stackStraceDepth, e.getStackTrace().length);
                        for (int i = 0; i < depth; i++) {
                            simpleCollector.valueLeaf(Name.value(e.getStackTrace()[i].toString()));
                        }
                        simpleCollector.endBranch();
                    }
                    simpleCollector.endComplexNode();
                }
        }
    }

    private enum State {
        CREATED,
        COLLECTING,
        COLLECTED,
        EMITTED
    }

    // Start main class



    private final Map<String, Channel> channels = new HashMap<>();
    private final Multimap<String, Consumer<Pair<NamePath, Exception>>> errorEventBus = MultimapBuilder.hashKeys().arrayListValues().build();
    private final TreeCollectCursor parent;
    private State state;
    private TreeValidator validator;


    protected final Optional<TreeValidator> getValidator() {
        return Optional.ofNullable(validator);
    }


    //  Constructor
    protected TreeCollectCursor(TreeCollectCursor parent) {
        this.parent = parent;
        this.state = State.CREATED;
    }


    // Public API

    public TreeReceiver getReceiverForChannel(String channel) {
        if (getParent() == null) {
            if (channels.containsKey(channel)) {
                return channels.get(channel).getHandler();
            } else {
                return new TreeReceiver.Discard();
            }
        } else {
            return getParent().getReceiverForChannel(channel);
        }

    }

    private NamePath currentPath() {
        // TODO
        return null;
    }

    // TODO make more elegant
    public Set<String> unclosedChannels() {
        return Collections.emptySet();
    }

    public TreeCollectCursor registerChannel(CursorConfig.ChannelConfiguration channel) {
        if (getParent() == null) {
            this.channels.put(channel.getChannel(), new Channel(channel, this));
        } else {
            getParent().registerChannel(channel);
        }
        return this;
    }

    // Private API

    final Multimap<String, Consumer<Pair<NamePath, Exception>>> getErrorEventBus() {
        if (getParent() == null) {
            return this.errorEventBus;
        } else {
            return getParent().getErrorEventBus();
        }
    }

    protected TreeCollectCursor getParent() {
        return parent;
    }


    final void updateChannelListener(String channel, TreeCollectCursor cursor) {
        if (parent != null) {
            parent.updateChannelListener(channel, cursor);
        } else {
            this.channels.get(channel).newHandler(cursor);
        }
    }

    protected final void handBack(String channel) {
        if (parent != null) {
            parent.handBack(channel);
        } else {
            if (channels.containsKey(channel)) {
                channels.get(channel).handBack();
            }
        }
    }

    private Set<String> getRegisteredChannels() {
        if (getParent() == null) {
            return channels.keySet();
        } else {
            return getParent().getRegisteredChannels();
        }
    }

    void registerValidator(TreeValidator validator) {
        this.validator = validator;
        for (String channel : getRegisteredChannels()) {
            validator.registerErrorSink(pair -> registerError(channel, pair.getFirst(), pair.getSecond()));
        }
    }

    protected final void registerError(String channel, NamePath location, Exception e) {
        if (getParent() == null) {
            for (Consumer<Pair<NamePath, Exception>> c : errorEventBus.get(channels.get(channel).errorStream)) {
                c.accept(new Pair<>(location, e));
            }
        } else {
            getParent().registerError(channel, location, e);
        }
    }

    protected abstract boolean isCollection();

    protected void startBranch(String channel, Name key, boolean isCollection) throws Exception {
    }
    protected void branchType(String channel, Triple type) throws Exception {
    }
    protected void endBranch(String channel) throws Exception {
    }
    protected void startComplexNode(String channel) throws Exception {
    }
    protected void nodeId(String channel, Name nodeId) throws Exception {
    }
    protected void nodeType(String channel, Name type) throws Exception {
    }
    protected void endComplexNode(String channel) throws Exception {
    }
    protected void emptyLeaf(String channel) throws Exception {
    }
    protected void valueLeaf(String channel, Name value) throws Exception {
    }

    protected void startRoot(String channel, Name rootName) {}

    protected void endRoot(String channel) {
    }

}
