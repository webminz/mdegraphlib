package no.hvl.past.util;



import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Provides Javas missing means to work with Iterables.
 */
public class IterationUtils {

    /**
     * Aka sequential composition.
     */
    public static  <X> Iterator<X> join(Iterator<X> first, Iterator<X> second) {
        return new Iterator<X>() {
            @Override
            public boolean hasNext() {
                return first.hasNext() || second.hasNext();
            }

            @Override
            public X next() {
                if (first.hasNext()) {
                    return first.next();
                }
                if (second.hasNext()) {
                    return second.next();
                }
                return null;
            }
        };
    }

    /**
     * Applies fmap to the Iterator.
     */
    public static <X,Y> Iterator<X> mapIterator(Iterator<Y> base, Function<Y, X> function) {
        return new Iterator<X>() {
            @Override
            public boolean hasNext() {
                return base.hasNext();
            }

            @Override
            public X next() {
                return function.apply(base.next());
            }
        };
    }

    /**
     * Filters out some elements from the base iterator, if they not satisfy the predicate.
     */
    public static <X> Iterator<X> filter(Iterator<X> base, Predicate<X> keepIf) {
        return new Iterator<X>() {

            private X lookahead = lookupNext();

            private X lookupNext() {
                if (base.hasNext()) {
                    X n = base.next();
                    if (keepIf.test(n)) {
                        return n;
                    }
                    return lookupNext();
                }
                return null;
            }

            @Override
            public boolean hasNext() {
                return lookahead != null;
            }

            @Override
            public X next() {
                X result = lookahead;
                lookahead = lookupNext();
                return result;
            }

        };
    }

    /**
     * Basically flat map for iterators.
     * If every element in the base iteration gives rise to a new collection
     * that can be iterated, all results are combined into one big iteration.
     */
    public static <X, Y> Iterator<X> flatMapIterator(Iterator<Y> base, Function<Y, Iterable<X>> generator) {
        return new Iterator<X>() {

            X lokkahead = lookupNext();

            private X lookupNext() {
                if (nested != null) {
                    if (nested.hasNext()) {
                        return nested.next();
                    }
                    nested = null;
                    return lookupNext();
                }
                if (base.hasNext()) {
                    nested = generator.apply(base.next()).iterator();
                    return lookupNext();
                }
                return null;
            }

            Iterator<X> nested;

            @Override
            public boolean hasNext() {
                return lokkahead != null;
            }

            @Override
            public X next() {
                X result = lokkahead;
                lokkahead = lookupNext();
                return result;
            }
        };
    }

    /**
     * Total utility class, therefore private constructor.
     */
    private IterationUtils() {
    }

}
