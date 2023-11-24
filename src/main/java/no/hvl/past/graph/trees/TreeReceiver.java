package no.hvl.past.graph.trees;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import no.hvl.past.graph.Graph;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.graph.expressions.EvaluatableExpression;
import no.hvl.past.names.AnonymousIdentifier;
import no.hvl.past.names.Name;
import no.hvl.past.names.PrintingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public interface TreeReceiver {

    void startTree(Name treeName) throws Exception;

    void treeType(Graph typeGraph, TreeTypeLibrary mappingLibrary) throws Exception;

    void startRoot(Name rootName) throws Exception;

    void startBranch(Name key, boolean isCollection) throws Exception;

    void branchType(Triple type) throws Exception;

    void endBranch() throws Exception;

    void startComplexNode() throws Exception;

    void nodeId(Name nodeId) throws Exception;

    void nodeType(Name type) throws Exception;

    void endComplexNode() throws Exception;

    void emptyLeaf() throws Exception;

    void valueLeaf(Name value) throws Exception;

    void endRoot() throws Exception;

    void endTree() throws Exception;


    abstract class ToEvent implements TreeReceiver {

        public abstract void handle(TreeEvent event);

        @Override
        public void startTree(Name treeName)  {
            handle(TreeEvent.startTree(treeName));

        }

        @Override
        public void treeType(Graph typeGraph, TreeTypeLibrary mappingLibrary) {
            handle(TreeEvent.treeType(typeGraph, mappingLibrary));
        }

        @Override
        public void startRoot(Name rootName)  {
            handle(TreeEvent.startRoot(rootName));

        }

        @Override
        public void startBranch(Name key, boolean isCollection)  {
            handle(TreeEvent.startBranch(key, isCollection));
        }

        @Override
        public void branchType(Triple type) {
            handle(TreeEvent.branchType(type));
        }

        @Override
        public void endBranch() {
            handle(TreeEvent.endBranch());
        }

        @Override
        public void startComplexNode() {
            handle(TreeEvent.startComplexNode());
        }

        @Override
        public void nodeId(Name nodeId)  {
            handle(TreeEvent.nodeId(nodeId));
        }

        @Override
        public void nodeType(Name type)  {
           handle(TreeEvent.nodeType(type));
        }

        @Override
        public void endComplexNode()  {
            handle(TreeEvent.endComplexNode());
        }

        @Override
        public void emptyLeaf()  {
            handle(TreeEvent.emptyLeaf());
        }

        @Override
        public void valueLeaf(Name value)  {
            handle(TreeEvent.valueLeaf(value));
        }

        @Override
        public void endRoot()  {
            handle(TreeEvent.endRoot());
        }

        @Override
        public void endTree() {
            handle(TreeEvent.endTree());
        }

    }


    /**
     * A Tree Receiver that simply ignores all the events it receives.
     *
     */
    class Discard implements TreeReceiver {

        @Override
        public void startTree(Name treeName) {
        }

        @Override
        public void treeType(Graph typeGraph, TreeTypeLibrary mappingLibrary) {
        }

        @Override
        public void startRoot(Name rootName) {
        }

        @Override
        public void startBranch(Name key, boolean isCollection)  {
        }

        @Override
        public void branchType(Triple type) {
        }

        @Override
        public void endBranch() {
        }

        @Override
        public void startComplexNode() {
        }

        @Override
        public void nodeId(Name nodeId) {
        }

        @Override
        public void nodeType(Name type) {
        }

        @Override
        public void endComplexNode() {
        }

        @Override
        public void emptyLeaf() {
        }

        @Override
        public void valueLeaf(Name value) {
        }

        @Override
        public void endRoot() {
        }

        @Override
        public void endTree() {
        }
    }


    class LoggingTreeReceiver extends ToEvent {

        private final Logger logger;
        private final Level logLevel;

        public LoggingTreeReceiver(Level logLevel, String loggerName) {
            this.logLevel = logLevel;
            this.logger = LoggerFactory.getLogger(loggerName);
        }



        @Override
        public void handle(TreeEvent event) {
            logger.atLevel(logLevel).log(event.toString());
        }
    }


    class ToStringTreeReceiver implements TreeReceiver {

        private final StringBuilder stringBuilder;

        private static final String INDENDATION_VIZZ = "    ";
        private static final String CHILD_VIZZ = "└── ";
        private final Stack<Name> currentStack = new Stack<>();
        private final Stack<Integer> idxStack = new Stack<>();


        private void newLine() {
            stringBuilder.append("\n");

        }

        public ToStringTreeReceiver(StringBuilder stringBuilder) {
            this.stringBuilder = stringBuilder;
        }

        @Override
        public void startTree(Name treeName) {

        }

        @Override
        public void treeType(Graph typeGraph, TreeTypeLibrary mappingLibrary) {

        }

        @Override
        public void startRoot(Name rootName) {
            stringBuilder.append("/");
            if (!(rootName.equals(Node.ROOT_NAME) || rootName instanceof AnonymousIdentifier)) {
                stringBuilder.append(" <");
                rootName.print(PrintingStrategy.DETAILED);
                stringBuilder.append(">");
            }
            idxStack.push(-1);
        }

        @Override
        public void nodeId(Name id) {

        }

        @Override
        public void nodeType(Name type) {

        }


        @Override
        public void startBranch(Name key, boolean isCollection) {
            indent();
            stringBuilder.append(CHILD_VIZZ);
            stringBuilder.append(key);
            currentStack.push(key);
            idxStack.push(isCollection ? 0 : -1);
            if (isCollection) {
                newLine();
            }
        }

        private void indent() {
            for (int i = 0; i < currentStack.size(); i++) {
                stringBuilder.append(INDENDATION_VIZZ);
            }
        }

        @Override
        public void branchType(Triple type) {

        }

        @Override
        public void endBranch() {
            currentStack.pop();
            idxStack.pop();
        }

        @Override
        public void startComplexNode() {
            if (idxStack.peek() >= 0) {
                indent();
                int i = idxStack.pop();
                stringBuilder.append(i);
                stringBuilder.append(": ");
                idxStack.push(i + 1);
            }
            newLine();
        }

        @Override
        public void endComplexNode() {
        }

        @Override
        public void emptyLeaf() {
            if (idxStack.peek() >= 0) {
                indent();
                int i = idxStack.pop();
                stringBuilder.append(i);
                idxStack.push(i + 1);
            }
            stringBuilder.append(": NULL");
            newLine();
        }

        @Override
        public void valueLeaf(Name value) {
            if (idxStack.peek() >= 0) {
                indent();
                int i = idxStack.pop();
                stringBuilder.append(i);
                idxStack.push(i + 1);
            }
            stringBuilder.append(": ");
            stringBuilder.append(value.print(PrintingStrategy.DETAILED));
            newLine();
        }

        @Override
        public void endRoot() throws IOException {

        }

        @Override
        public void endTree() throws Exception {

        }
    }

    class Multiplexer implements TreeReceiver {
        private final List<TreeReceiver> toDistribute;

        public Multiplexer(List<TreeReceiver> toDistribute) {
            this.toDistribute = toDistribute;
        }

        @Override
        public void startTree(Name treeName) throws Exception {
            for (TreeReceiver creator : toDistribute) {
                creator.startTree(treeName);
            }
        }

        @Override
        public void treeType(Graph typeGraph, TreeTypeLibrary mappingLibrary) throws Exception {
            for (TreeReceiver creator : toDistribute) {
                creator.treeType(typeGraph, mappingLibrary);
            }
        }

        @Override
        public void startRoot(Name rootName) throws Exception {
            for (TreeReceiver creator : toDistribute) {
                creator.startRoot(rootName);
            }
        }

        @Override
        public void nodeId(Name id) throws Exception {
            for (TreeReceiver creator : toDistribute) {
                creator.nodeId(id);
            }
        }

        @Override
        public void nodeType(Name type) throws Exception {
            for (TreeReceiver creator : toDistribute) {
                creator.nodeType(type);
            }
        }

        @Override
        public void startBranch(Name key, boolean isCollection) throws Exception {
            for (TreeReceiver creator : toDistribute) {
                creator.startBranch(key, isCollection);
            }
        }

        @Override
        public void branchType(Triple type) throws Exception {
            for (TreeReceiver creator : toDistribute) {
                creator.branchType(type);
            }
        }

        @Override
        public void endBranch() throws Exception {
            for (TreeReceiver creator : toDistribute) {
                creator.endBranch();
            }
        }

        @Override
        public void startComplexNode() throws Exception {
            for (TreeReceiver creator : toDistribute) {
                creator.startComplexNode();
            }
        }

        @Override
        public void endComplexNode() throws Exception {
            for (TreeReceiver creator : toDistribute) {
                creator.endComplexNode();
            }
        }

        @Override
        public void emptyLeaf() throws Exception {
            for (TreeReceiver creator : toDistribute) {
                creator.emptyLeaf();
            }
        }

        @Override
        public void valueLeaf(Name value) throws Exception {
            for (TreeReceiver creator : toDistribute) {
                creator.valueLeaf(value);
            }
        }

        @Override
        public void endRoot() throws Exception {
            for (TreeReceiver creator : toDistribute) {
                creator.endRoot();
            }
        }

        @Override
        public void endTree() throws Exception {
            for (TreeReceiver creator : toDistribute) {
                creator.endTree();
            }
        }
    }

    abstract class AbstractFilter implements TreeReceiver {

        private enum State {
            IGNORING,
            SWITCHING,
            ACCEPTING
        }

        private final Name listeningBranchTag;
        private boolean mustBeActivated;
        private boolean isSilentFirst;
        private boolean allowTopLevelEvents;
        private int depth = -1;
        private final TreeReceiver wrapped;
        private State state;


        public AbstractFilter(Name listeningBranchTag, boolean active, boolean silent, boolean allowTopLevelEvents, TreeReceiver wrapped) {
            this.listeningBranchTag = listeningBranchTag;
            this.mustBeActivated = active;
            this.isSilentFirst = silent;
            this.state = active ? State.ACCEPTING : State.IGNORING;
            this.wrapped = wrapped;
            this.allowTopLevelEvents = allowTopLevelEvents;
        }


        @Override
        public void startTree(Name treeName) throws Exception {
            if (allowTopLevelEvents) {
                wrapped.startTree(treeName);
            }
        }

        @Override
        public void treeType(Graph typeGraph, TreeTypeLibrary mappingLibrary) throws Exception {
            if (allowTopLevelEvents) {
                wrapped.treeType(typeGraph, mappingLibrary);
            }
        }

        @Override
        public void startRoot(Name rootName) throws Exception {
            if (allowTopLevelEvents) {
                wrapped.startRoot(rootName);
            }
        }

        @Override
        public void startBranch(Name key, boolean isCollection) throws Exception {
            if (this.state.equals(State.SWITCHING)) {
                this.state = State.ACCEPTING;
                wrapped.startBranch(key, isCollection);
            } else {
                if (state.equals(State.ACCEPTING)) {
                    if (key.equals(listeningBranchTag)) {
                        this.state = State.IGNORING;
                        depth = 0;
                    } else {
                        wrapped.startBranch(key, isCollection);
                    }
                } else {
                    if (key.equals(listeningBranchTag)) {
                        depth = 0;
                        if (isSilentFirst) {
                            this.state = State.SWITCHING;
                        } else {
                            this.state = State.ACCEPTING;
                            wrapped.startBranch(key,isCollection);
                        }
                    }
                }
            }
        }

        @Override
        public void endBranch() throws Exception {
            if (depth == 0) {
                depth--;
                if (state.equals(State.ACCEPTING)) {
                    if (!isSilentFirst) {
                        wrapped.endBranch();
                    }
                    this.state = State.IGNORING;
                } else {
                    state = State.ACCEPTING;
                }
            } else {
                if (this.state.equals(State.ACCEPTING)) {
                    wrapped.endBranch();
                }
            }
        }

        @Override
        public void branchType(Triple type) throws Exception {
            if (state.equals(State.ACCEPTING)) {
                wrapped.branchType(type);
            }
        }



        @Override
        public void startComplexNode() throws Exception {
            if (depth >= 0) {
                depth++;
            }
            if (this.state.equals(State.ACCEPTING)) {
                wrapped.startComplexNode();
            }
        }

        @Override
        public void nodeId(Name nodeId) throws Exception {
            if (this.state.equals(State.ACCEPTING)) {
                wrapped.nodeId(nodeId);
            }
        }

        @Override
        public void nodeType(Name type) throws Exception {
            if (this.state.equals(State.ACCEPTING)) {
                wrapped.nodeType(type);
            }
        }

        @Override
        public void endComplexNode() throws Exception {
            if (depth > 0) {
                depth--;
            }
            if (this.state.equals(State.ACCEPTING) && (!isSilentFirst || depth != 0)) {
                wrapped.endComplexNode();
            }
        }

        @Override
        public void emptyLeaf() throws Exception {
            if (this.state.equals(State.ACCEPTING)) {
                wrapped.emptyLeaf();
            }
        }

        @Override
        public void valueLeaf(Name value) throws Exception {
            if (this.state.equals(State.ACCEPTING)) {
                wrapped.valueLeaf(value);
            }
        }

        @Override
        public void endRoot() throws Exception {
            if (allowTopLevelEvents) {
                wrapped.endRoot();
            }
        }

        @Override
        public void endTree() throws Exception {
            if (allowTopLevelEvents) {
                wrapped.endTree();
            }
        }

    }

    class ActivationFilter extends AbstractFilter {

        public ActivationFilter(Name listeningBranchTag, boolean silent, boolean allowTopLevelEvents, TreeReceiver wrapped) {
            super(listeningBranchTag, false, silent, allowTopLevelEvents, wrapped);
        }
    }

    class DeactivationFilter extends AbstractFilter {

        public DeactivationFilter(Name listeningBranchTag, boolean allowTopLevelEvents, TreeReceiver wrapped) {
            super(listeningBranchTag, true, true, allowTopLevelEvents, wrapped);
        }
    }

    class ExpressionEvaluator implements TreeReceiver {

        private final EvaluatableExpression expression;
        private final Multimap<Name, Name> parameterMap =  MultimapBuilder.hashKeys().arrayListValues().build();
        private Name active = null;

        public ExpressionEvaluator(EvaluatableExpression expression) {
            this.expression = expression;
            for (Name key : expression.requiredFeatures()) {
                parameterMap.putAll(key, Collections.emptyList());
            }
        }

        @Override
        public void startTree(Name treeName) throws Exception {

        }

        @Override
        public void treeType(Graph typeGraph, TreeTypeLibrary mappingLibrary) throws Exception {

        }

        @Override
        public void startRoot(Name rootName) throws Exception {

        }

        @Override
        public void startBranch(Name key, boolean isCollection) throws Exception {
            if (expression.requiredFeatures().contains(key)) {
                active = key;
            }
        }

        @Override
        public void branchType(Triple type) throws Exception {

        }

        @Override
        public void endBranch() throws Exception {
            active = null;
        }

        @Override
        public void startComplexNode() throws Exception {

        }

        @Override
        public void nodeId(Name nodeId) throws Exception {

        }

        @Override
        public void nodeType(Name type) throws Exception {

        }

        @Override
        public void endComplexNode() throws Exception {

        }

        @Override
        public void emptyLeaf() throws Exception {

        }

        @Override
        public void valueLeaf(Name value) throws Exception {
            if (active != null) {
                parameterMap.put(active, value);
            }
        }

        @Override
        public void endRoot() throws Exception {

        }

        @Override
        public void endTree() throws Exception {

        }

        public Collection<Name> evaluate() {
            return expression.evaluate(parameterMap);
        }
    }

    abstract class MapName implements TreeReceiver {

        private final TreeReceiver wrapped;
        private final Name activationTag;
        private boolean isActive;
        private int depth = -1;

        public MapName(TreeReceiver wrapped) {
            this.wrapped = wrapped;
            this.activationTag = null;
            this.isActive = true;
        }

        public MapName(TreeReceiver wrapped, Name activationTag) {
            this.wrapped = wrapped;
            this.activationTag = activationTag;
            this.isActive = false;
        }

        public abstract Name map(Name in);

        @Override
        public void startTree(Name treeName) throws Exception {
            wrapped.startTree(isActive? map(treeName) : treeName);
        }

        @Override
        public void treeType(Graph typeGraph, TreeTypeLibrary mappingLibrary) throws Exception {
            wrapped.treeType(typeGraph, mappingLibrary);
        }

        @Override
        public void startRoot(Name rootName) throws Exception {
            wrapped.startRoot(isActive ? map(rootName) : rootName);
        }

        @Override
        public void startBranch(Name key, boolean isCollection) throws Exception {
            if (activationTag != null) {
                if (key.equals(activationTag)) {
                    isActive = true;
                    depth = 0;
                }
            }
            wrapped.startBranch(isActive ? map(key) : key, isCollection);
        }

        @Override
        public void branchType(Triple type) throws Exception {
            wrapped.branchType(isActive ? type.mapName(this::map) : type);
        }

        @Override
        public void endBranch() throws Exception {
            wrapped.endBranch();
            if (activationTag != null) {
                if (depth == 0 && isActive) {
                    isActive = false;
                }
            }
        }

        @Override
        public void startComplexNode() throws Exception {
            wrapped.startComplexNode();
            if (activationTag != null && isActive) {
                depth++;
            }
        }

        @Override
        public void nodeId(Name nodeId) throws Exception {
            wrapped.nodeId(isActive ? map(nodeId) : nodeId);
        }

        @Override
        public void nodeType(Name type) throws Exception {
            wrapped.nodeType(isActive ? map(type) : type);
        }

        @Override
        public void endComplexNode() throws Exception {
            wrapped.endComplexNode();
            if (activationTag != null && isActive) {
                depth--;
            }
        }

        @Override
        public void emptyLeaf() throws Exception {
            wrapped.emptyLeaf();
        }

        @Override
        public void valueLeaf(Name value) throws Exception {
            if (isActive) {
                wrapped.valueLeaf(map(value));
            } else {
                wrapped.valueLeaf(value);
            }
        }

        @Override
        public void endRoot() throws Exception {
            wrapped.endRoot();
        }

        @Override
        public void endTree() throws Exception {
            wrapped.endTree();
        }
    }


}
