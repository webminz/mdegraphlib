package no.hvl.past.util;

import java.util.Comparator;

/**
 * Provides a slight enhancement of Javas builitin comparator,
 * i.e. it actually allows to express partial orders, where elements
 * may not be comparable.
 */
public interface ProperComparator<E> extends Comparator<E> {

    enum CompareResult {

        EQUAL,

        LESS_THAN,

        BIGGER_THAN,

        INCOMPARABLE
    }


    CompareResult cmp(E lhs, E rhs);

    @Override
    default int compare(E o1, E o2){
        if (cmp(o1, o2) == CompareResult.EQUAL) {
            return 0;
        }
        if (cmp(o1, o2) == CompareResult.LESS_THAN) {
            return -1;
        }
        if (cmp(o1, o2) == CompareResult.BIGGER_THAN) {
            return 1;
        }
        // incomparable elements cannot really be expressed via Javas Comparator,
        // therefore we just return a really small value.
        return Integer.MIN_VALUE;
    }

    default boolean lessEq(E lhs, E rhs) {
        return cmp(lhs, rhs) == CompareResult.EQUAL || cmp(lhs, rhs) == CompareResult.LESS_THAN;
    }

    default boolean less(E lhs, E rhs) {
        return cmp(lhs, rhs) == CompareResult.LESS_THAN;
    }

    default boolean equal(E lhs, E rhs) {
        return cmp(lhs, rhs) == CompareResult.EQUAL;
    }

    default boolean incomparable(E lhs, E rhs) {
        return cmp(lhs, rhs) == CompareResult.INCOMPARABLE;
    }

    default boolean biggerEq(E lhs, E rhs) {
        return cmp(lhs, rhs) == CompareResult.BIGGER_THAN || cmp(lhs, rhs) == CompareResult.EQUAL;
    }

    default boolean bigger(E lhs, E rhs) {
        return cmp(lhs, rhs) == CompareResult.BIGGER_THAN;
    }

    static <S> ProperComparator<S> fromTotalOrder(Comparator<S> comparator) {
        return new ProperComparator<S>() {
            @Override
            public CompareResult cmp(S lhs, S rhs) {
                int res = comparator.compare(lhs, rhs);
                if (res == 0) {
                    return CompareResult.EQUAL;
                }
                if (comparator.compare(lhs, rhs) == comparator.compare(rhs, lhs)) {
                    return CompareResult.INCOMPARABLE;
                }
                if (res < 0) {
                    return CompareResult.LESS_THAN;
                } else {
                    return CompareResult.BIGGER_THAN;
                }
            }
        };
    }

}
