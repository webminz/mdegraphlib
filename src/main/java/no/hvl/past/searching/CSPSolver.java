package no.hvl.past.searching;

import no.hvl.past.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class CSPSolver<D, V extends CSPSolver.Variable<D>> {

    private static class VariableAssignment<D, V extends  Variable<D>> {
        private final V variable;
        private final List<D> possibleValues;
        private final List<BinaryConstraint<D>> subjectTo;

        private VariableAssignment(V variable, List<D> possibleValues, List<BinaryConstraint<D>> subjectTo) {
            this.variable = variable;
            this.possibleValues = possibleValues;
            this.subjectTo = subjectTo;
        }

        /**
         * Performs a choice for this variable assignment, i.e. it picks the value that has been considered best.
         */
        private void assign(D value) {
            this.possibleValues.removeIf(x -> !x.equals(value));
        }

        /**
         * Return true if this CSP problem is unresolvable, i.e. the domain of possible value has no solution anymore.
         */
        private boolean isUnresolvable() {
            return possibleValues.isEmpty();
        }

        /**
         * Returns true if this variable has been determined.
         */
        private boolean isFinished() {
            return possibleValues.size() == 1;
        }

        /**
         * Provides the degree of freedom for this variable, i.e. the number of choices we hav to assign it.
         */
        private int freenesDegree() {
            return possibleValues.size();
        }

        private Optional<Pair<Variable<D>, D>> getAssignment() {
            if (isFinished()) {
                return Optional.of(new Pair<>(this.variable, this.possibleValues.get(0)));
            }
            return Optional.empty();
        }

        private VariableAssignment<D, V> deepCopy() {
            return new VariableAssignment<>(variable, new ArrayList<>(this.possibleValues), subjectTo);
        }


        public boolean revise(BinaryConstraint<D> current, List<D> possibleValues) {
            boolean revised = false;
            if (current.lhs().equals(this.variable)) {
                Iterator<D> thisIterator = this.possibleValues.iterator();
                while (thisIterator.hasNext()){
                    D thisValue = thisIterator.next();
                    boolean hasFriend = false;
                    for (D other : possibleValues) {
                        if (current.satisfied(thisValue, other)) {
                            hasFriend = true;
                        }
                    }
                    if (!hasFriend) {
                        thisIterator.remove();
                        revised = true;
                    }
                }
            } else {
                Iterator<D> thisIterator = this.possibleValues.iterator();
                while (thisIterator.hasNext()){
                    D thisValue = thisIterator.next();
                    boolean hasFriend = false;
                    for (D other : possibleValues) {
                        if (current.satisfied(other, thisValue)) {
                            hasFriend = true;
                        }
                    }
                    if (!hasFriend) {
                        thisIterator.remove();
                        revised = true;
                    }
                }
            }
            return revised;
        }
    }

    public interface Variable<D> {

        Set<D> possibleValues();

    }

    public interface BinaryConstraint<D> {

        Variable<D> lhs();

        Variable<D> rhs();

        boolean satisfied(D lhs, D rhs);

        default boolean satisfiable(Set<D> lhsValue, Set<D> rhsValues) {
            for (D lhs : lhsValue) {
                for (D rhs : rhsValues) {
                    if (satisfied(lhs, rhs)) {
                        return true;
                    }
                }
            }
            return false;
        }

    }

    public static class AllDiffConstraint<D> {

        private final List<Variable<D>> variables;

        public AllDiffConstraint(List<Variable<D>> variables) {
            this.variables = variables;
        }

        boolean satisfied(List<D> values) {
            Set<D> check = new HashSet<>();
            for (D value : values) {
                if (check.contains(value)) {
                    return false;
                }
                check.add(value);
            }
            return true;
        }
    }

    private final Map<? extends Variable<D>, VariableAssignment<D, V>> assignments;
    private final Set<AllDiffConstraint<D>> allDiffConstraints;

    private CSPSolver(Map<V, VariableAssignment<D, V>> assignments, Set<AllDiffConstraint<D>> allDiffConstraints) {
        this.assignments = assignments;
        this.allDiffConstraints = allDiffConstraints;
    }

    private CSPSolver<D, V> deepCopy() {
        Map<V, VariableAssignment<D,V>> result = new HashMap<>();
        for (Map.Entry<? extends Variable<D>, VariableAssignment<D, V>> entr : this.assignments.entrySet()) {
            result.put(entr.getValue().variable, entr.getValue().deepCopy());
        }
        return new CSPSolver<>(result, this.allDiffConstraints);
    }

    private Set<CSPSolver<D, V>> splitIsolatedProblems() {
        Set<VariableAssignment<D, V>> isolated = this.assignments.values().stream()
                .filter(a -> a.subjectTo.isEmpty()).collect(Collectors.toSet());
        isolated.forEach(a -> this.assignments.remove(a.variable));
        return isolated.stream().map(a -> new CSPSolver<>(Collections.singletonMap(a.variable, a), allDiffConstraints)).collect(Collectors.toSet());
    }


    public Set<Map<V, D>> backtrackAllSolutions() {
        if (isUnresolvable()) {
            return Collections.emptySet();
        }
        if (isComplete()) {
            if (isValid()) {
                return Collections.singleton(this.assignments.values()
                        .stream()
                        .map(v -> new Pair<>(v.variable, v.possibleValues.get(0)))
                        .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
            } else {
                return Collections.emptySet();
            }
        }

        if (canSplitInSubproblem()) {
            Set<CSPSolver<D, V>> isolatedProblems = splitIsolatedProblems();
            Set<Map<V, D>> thisSolutions = this.backtrackAllSolutions();
            return combineMany(thisSolutions, isolatedProblems);
        }

        VariableAssignment<D, V> var = findMostRestricted();
        Set<Map<V, D>> result = new HashSet<>();
        for (D value : var.possibleValues) {
            if (isConsistent(var, value)) {
                CSPSolver<D, V> child = this.deepCopy();
                child.assign(var, value);
                if (!child.inference(var)) {
                    result.addAll(child.backtrackAllSolutions());
                }
            }
        }

        return result;

    }



    public Map<V, D> backTrackOneSolution() {
        if (isUnresolvable()) {
            return Collections.emptyMap();
        }
        if (isComplete()) {
            if (isValid()) {
                return this.assignments.values()
                        .stream()
                        .map(v -> new Pair<>(v.variable, v.possibleValues.get(0)))
                        .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
            } else {
                return Collections.emptyMap();
            }
        }

        if (canSplitInSubproblem()) {
            Set<CSPSolver<D, V>> isolatedProblems = splitIsolatedProblems();
            Map<V, D> thisSolution = this.backTrackOneSolution();
            if (!thisSolution.isEmpty()) {
                return combineOne(thisSolution, isolatedProblems);
            } else {
                return thisSolution;
            }
        }

        VariableAssignment<D, V> var = findMostRestricted();
        for (D value : var.possibleValues) {
            if (isConsistent(var, value)) {
                CSPSolver<D, V> child = this.deepCopy();
                child.assign(var, value);
                if (!child.inference(var)) {
                    Map<V, D> result = child.backTrackOneSolution();
                    if (!result.isEmpty()) {
                        return result;
                    }
                }
            }
        }
        return Collections.emptyMap();
    }

    private boolean canSplitInSubproblem() {
        return this.assignments.size() > 1 && assignments.values().stream().anyMatch(a -> a.subjectTo.isEmpty() && allDiffConstraints.stream().noneMatch(cons -> cons.variables.contains(a.variable)));
    }

    private Set<Map<V,D>> combineMany(Set<Map<V,D>> thisSolutions, Set<CSPSolver<D,V>> isolatedProblems) {
        Set<Map<V, D>> result = new HashSet<>(thisSolutions);
        for (CSPSolver<D, V> solver : isolatedProblems) {
            Set<Map<V, D>> next = new HashSet<>();
            for (Map<V, D> nested : solver.backtrackAllSolutions()) {
                if (!nested.isEmpty()) {
                    for (Map<V, D> current : result) {
                        Map<V, D> nextMap = new HashMap<>();
                        nextMap.putAll(current);
                        nextMap.putAll(nested);
                        next.add(nextMap);
                    }
                }
            }
            result = next;
        }
        return result;

    }

    private Map<V,D> combineOne(Map<V,D> thisSolution, Set<CSPSolver<D,V>> isolatedProblems) {
        for (CSPSolver<D, V> solver : isolatedProblems) {
            thisSolution.putAll(solver.backTrackOneSolution());
        }
        return thisSolution;
    }

    /**
     * Performs the AC-3 algorithm to perform inference.
     * Returns true if an inconsistency is found.
     * @param var
     * @return
     */
    private boolean inference(VariableAssignment<D, V> var) {
        List<Pair<Variable<D>, BinaryConstraint<D>>> queue = var.subjectTo.stream().map(c -> new Pair<Variable<D>, BinaryConstraint<D>>(var.variable, c)).collect(Collectors.toList());

        // when there are all-diff constraints we can reduce the value sets even quicker.
        for (AllDiffConstraint<D> allDiffConstraint : allDiffConstraints) {
            D assigned = this.assignments.get(var.variable).possibleValues.get(0);
            for (VariableAssignment<D, V> ass : allDiffConstraint.variables.stream().filter(v -> !v.equals(var.variable)).map(this.assignments::get).collect(Collectors.toSet())) {
                if (ass.possibleValues.remove(assigned)) {
            //        ass.subjectTo.stream().filter(cons -> !cons.lhs().equals(var.variable) && !cons.rhs().equals(var.variable)).forEach(c -> queue.add(new Pair<>(ass.variable, c)));
                }
            }
        }

        // AC-3
        while (!queue.isEmpty()) {
            Pair<Variable<D>, BinaryConstraint<D>> current = queue.get(0);
            queue.remove(0);
            boolean revised = false;
            if (current.getSecond().lhs().equals(current.getFirst())) {
                revised = this.assignments.get(current.getSecond().rhs()).revise(current.getSecond(),this.assignments.get(current.getSecond().lhs()).possibleValues);
                if (revised) {
                    if (this.assignments.get(current.getSecond().rhs()).isUnresolvable()) {
                        return true;
                    }
                    this.assignments.get(current.getSecond().rhs()).subjectTo.stream().filter(c -> !c.equals(current.getSecond())).forEach(c -> queue.add(new Pair<>(current.getSecond().rhs(), c)));
                }

            } else {
                revised = this.assignments.get(current.getSecond().lhs()).revise(current.getSecond(), this.assignments.get(current.getSecond().rhs()).possibleValues);
                if (revised) {
                    if (this.assignments.get(current.getSecond().lhs()).isUnresolvable()) {
                        return true;
                    }
                }
                this.assignments.get(current.getSecond().lhs()).subjectTo.stream().filter(c -> !c.equals(current.getSecond())).forEach(c -> new Pair<>(current.getSecond().lhs(),c));
            }
        }
        return false;
    }

    private void assign(VariableAssignment<D, V> var, D value) {
        this.assignments.get(var.variable).assign(value);
    }

    private boolean isUnresolvable() {
        return this.assignments.entrySet().stream().anyMatch(kv -> kv.getValue().isUnresolvable());
    }

    private boolean isConsistent(VariableAssignment<D, V> var, D value) {
        for (BinaryConstraint<D> constraint : var.subjectTo) {
            if (constraint.lhs().equals(var.variable)) {
                if (!constraint.satisfiable(Collections.singleton(value), new HashSet<>(this.assignments.get(constraint.rhs()).possibleValues))) {
                    return false;
                }
            } else {
                if (!constraint.satisfiable(new HashSet<>(this.assignments.get(constraint.lhs()).possibleValues), Collections.singleton(value))) {
                    return false;
                }
            }
        }
        return true;
    }

    private VariableAssignment<D, V> findMostRestricted() {
        VariableAssignment<D, V> result = null;
        int current = Integer.MAX_VALUE;
        for (VariableAssignment<D, V> ass : this.assignments.values()) {
            if (ass.freenesDegree() > 1 && ass.freenesDegree() < current) {
                result = ass;
                current = ass.freenesDegree();
            }
        }
        return result;
    }

    private boolean isComplete() {
        return this.assignments.entrySet().stream().allMatch(kv -> kv.getValue().isFinished());
    }

    private boolean isValid() {
        for (AllDiffConstraint<D> diffConstraint : allDiffConstraints) {
            if (!diffConstraint.satisfied(diffConstraint.variables.stream().map(this.assignments::get).map(a -> a.possibleValues.get(0)).collect(Collectors.toList()))) {
                return false;
            }
        }
        return true;
    }

    public static <D, V extends  Variable<D>> CSPSolver<D, V> createProblem(Set<V> variables, Set<BinaryConstraint<D>> constraints) {
        Map<V, VariableAssignment<D, V>> assignments = new HashMap<>();
        for (V variable : variables) {
            assignments.put(variable, new VariableAssignment<>(
                    variable,
                    new ArrayList<>(variable.possibleValues()),
                    constraints.stream().filter(c -> c.lhs().equals(variable) || c.rhs().equals(variable)).collect(Collectors.toList())));
        }
        return new CSPSolver<>(assignments, Collections.emptySet());
    }

    public static <D, V extends  Variable<D>> CSPSolver<D, V> createProblem(Set<V> variables, Set<BinaryConstraint<D>> constraints, Set<AllDiffConstraint<D>> diffConstraints) {
        Map<V, VariableAssignment<D, V>> assignments = new HashMap<>();
        for (V variable : variables) {
            assignments.put(variable, new VariableAssignment<>(
                    variable,
                    new ArrayList<>(variable.possibleValues()),
                    constraints.stream().filter(c -> c.lhs().equals(variable) || c.rhs().equals(variable)).collect(Collectors.toList())));
        }
        return new CSPSolver<>(assignments, diffConstraints);
    }



}
