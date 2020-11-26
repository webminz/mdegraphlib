package no.hvl.past.util;

import com.google.common.collect.Streams;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.sun.xml.internal.ws.util.StreamUtils;
import oracle.jvm.hotspot.jfr.Producer;

import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Provides some powerful utility methods for working with Streams
 * that are currently missing in the Java standard library.
 */
public class StreamExtensions {

    /**
     * Creates an ordered stream of integers for the given parameters.
     * The first parameter specifies the first element of the stream.
     * The second parameter specifies the last element of the stream.
     * The third parameter specifies the difference between each element.
     */
    public static IntStream iterate(int start, int end, int stepSize) {
        return StreamSupport.intStream(new Spliterator.OfInt() {
            private int current = start;

            private boolean tooBig() {
                return (current > end);
            }

            private void advance() {
                current = current + stepSize;
            }

            @Override
            public OfInt trySplit() {
                return null;
            }

            @Override
            public boolean tryAdvance(IntConsumer action) {
                if (tooBig()) {
                    return false;
                } else {
                    action.accept(current);
                    advance();
                    return true;
                }
            }

            @Override
            public long estimateSize() {
                return (end-start) / stepSize;
            }

            @Override
            public int characteristics() {
                return SIZED;
            }
        }, false);
    }

    /**
     * Creates an ordered infinite Stream of integer with the given parameters.
     * The first argument specifies the first element of the stream.
     * The second argument specifies the delta between each element.
     */
    public static IntStream iterate(int start, int stepSize) {
        return StreamSupport.intStream(new Spliterator.OfInt() {
            private int current = start;

            private void advance() {
                current = current + stepSize;
            }

            @Override
            public OfInt trySplit() {
                return null;
            }

            @Override
            public boolean tryAdvance(IntConsumer action) {
                    action.accept(current);
                    advance();
                    return true;
            }

            @Override
            public long estimateSize() {
                return Long.MAX_VALUE;
            }

            @Override
            public int characteristics() {
                return 0;
            }
        }, false);
    }

    /**
     * Translates between java.lang.Option and Streams.
     */
    public static <X> Stream<X> optToStream(Optional<X> in) {
        if (in.isPresent()) {
            return Stream.of(in.get());
        } else {
            return Stream.empty();
        }
    }

    public static <A, B, R> Stream<R> cartesianProduct(Supplier<Stream<A>> left, Supplier<Stream<B>> right, BiFunction<A, B, R> function) {
        return left.get().flatMap(a -> right.get().map(b -> function.apply(a, b)));
    }


    public static <R, P> Stream<R> variablePipleline(List<P> parameter, Stream<R> input, BiFunction<R, P, Stream<R>> streamTransformation) {
        if (parameter.isEmpty()) {
            return input;
        } else {
            Stream<R> next = input.flatMap(element -> streamTransformation.apply(element, parameter.get(0)));
            parameter.remove(0);
            return variablePipleline(parameter, next, streamTransformation);
        }
    }

    public static <R> Stream<R> variablePipeline(Supplier<Boolean> endCondition, Stream<R> input, Function<R, Stream<R>> streamTransformation) {
        if (endCondition.get()) {
            return input;
        } else {
            Stream<R> next = input.flatMap(streamTransformation::apply);
            return variablePipeline(endCondition, next, streamTransformation);
        }
    }
}
