package no.hvl.past.graph;

import java.util.function.Function;

public class GraphEnvironment<E> {

    private E containee;
    // TODO list of errors
    // TODO execution context
    // TODO universe

    public <F extends Element> GraphEnvironment<F> executeWithContext(Function<ExecutionContext,F> f) {
        return null;
    }

    public <F extends Element> GraphEnvironment<F> fmap(Function<E,F> f) {
        return null;  // TODO
    }

    public <F extends Element> GraphEnvironment<F> bind(Function<E,GraphEnvironment<F>> f) {
        return null; // TODO
    }

    public E counit() throws Exception { // FIXME proper exception
        return null; // TODO
    }

    public static <E extends Element> GraphEnvironment<E> join(GraphEnvironment<GraphEnvironment<E>> boxed) {
        return null; // TODO
    }

    public static <E extends Element> GraphEnvironment<E> unit(E e) {
        return null;  // TODO
    }

    public static GraphEnvironment<Void> empty() {
        return new GraphEnvironment<>();
    }

}
