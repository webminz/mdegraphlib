package no.hvl.past.util;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * Provides some powerful utility methods for working with Streams
 * that are currently missing in the Java standard library.
 */
public class StreamExt<X> implements Stream<X>{

    private final Stream<X> wrapped;

    private StreamExt(Stream<X> wrapped) {
        this.wrapped = wrapped;
    }


    public static <Z> Z pickOne(Collection<Z> collection) throws NoSuchElementException {
        return collection.stream().findFirst().orElseThrow(NoSuchElementException::new);
    }

    public static <Z> Optional<Z> pickOneSafe(Collection<Z> collection) {
        return collection.stream().findFirst();
    }


    @SuppressWarnings("unchecked")
    public <Y> StreamExt<Y> filterByType(Class<Y> type) {
        return new StreamExt<>(wrapped.filter(x -> type.isAssignableFrom(x.getClass()))).map(x -> (Y) x);
    }

    public boolean isEmpty() {
        return wrapped.noneMatch(x -> true);
    }

    public boolean notEmpty() {
        return wrapped.anyMatch(x -> true);
    }

    public boolean allMatchAndNotEmpty(Predicate<X> predicate) {
        Holder<Boolean> notEmpty = new Holder<>();
        return wrapped.allMatch(x -> {
            notEmpty.set(true);
            return predicate.test(x);
        }) && notEmpty.hasValue() && notEmpty.unsafeGet();
    }

    public StreamExt<X> withDuplicateProperty(Function<X, ?> property) {
        Map<Object, X> seen = new HashMap<>();
        List<X> result = new ArrayList<>();
        wrapped.forEach(x -> {
            Object prpertyValue = property.apply(x);
            if (!seen.containsKey(prpertyValue)) {
                seen.put(prpertyValue,x);
            } else {
                X old = seen.get(prpertyValue);
                if (!result.contains(old)) {
                    result.add(old);
                }
                result.add(x);
            }
        });
        return new StreamExt<>(result.stream());
    }

    public StreamExt<X> duplicates() {
        Set<Object> seen = new HashSet<>();
        List<X> result = new ArrayList<>();
        wrapped.forEach(x -> {
            if (!seen.contains(x)) {
                seen.add(x);
            } else {
                result.add(x);
            }
        });
        return new StreamExt<>(result.stream());
    }

    public String fuse(String separator, Function<X, String> toString) {
        return StringUtils.fuseList(this, toString, separator);
    }



    public static boolean isUnique(Stream<?> stream) {
        Set<Object> seen = new HashSet<>();
        return stream.allMatch(x -> {
            if (!seen.contains(x)) {
                seen.add(x);
                return true;
            } else {
                return false;
            }
        });
    }

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

    @Override
    public StreamExt<X> filter(Predicate<? super X> predicate) {
        return new StreamExt<>(wrapped.filter(predicate));
    }

    @Override
    public <R> StreamExt<R> map(Function<? super X, ? extends R> mapper) {
        return new StreamExt<>(wrapped.map(mapper));
    }

    @Override
    public IntStream mapToInt(ToIntFunction<? super X> mapper) {
        return wrapped.mapToInt(mapper);
    }

    @Override
    public LongStream mapToLong(ToLongFunction<? super X> mapper) {
        return wrapped.mapToLong(mapper);
    }

    @Override
    public DoubleStream mapToDouble(ToDoubleFunction<? super X> mapper) {
        return wrapped.mapToDouble(mapper);
    }

    @Override
    public <R> StreamExt<R> flatMap(Function<? super X, ? extends Stream<? extends R>> mapper) {
        return new StreamExt<>(wrapped.flatMap(mapper));
    }

    @Override
    public IntStream flatMapToInt(Function<? super X, ? extends IntStream> mapper) {
        return wrapped.flatMapToInt(mapper);
    }

    @Override
    public LongStream flatMapToLong(Function<? super X, ? extends LongStream> mapper) {
        return wrapped.flatMapToLong(mapper);
    }

    @Override
    public DoubleStream flatMapToDouble(Function<? super X, ? extends DoubleStream> mapper) {
        return wrapped.flatMapToDouble(mapper);
    }

    @Override
    public StreamExt<X> distinct() {
        return new StreamExt<>(wrapped.distinct());
    }

    @Override
    public StreamExt<X> sorted() {
        return new StreamExt<>(wrapped.sorted());
    }

    @Override
    public StreamExt<X> sorted(Comparator<? super X> comparator) {
        return new StreamExt<>(wrapped.sorted(comparator));
    }

    @Override
    public StreamExt<X> peek(Consumer<? super X> action) {
        return new StreamExt<>(wrapped.peek(action));
    }

    @Override
    public StreamExt<X> limit(long maxSize) {
        return new StreamExt<>(wrapped.limit(maxSize));
    }

    @Override
    public StreamExt<X> skip(long n) {
        return new StreamExt<>(wrapped.skip(n));
    }

    @Override
    public void forEach(Consumer<? super X> action) {
        wrapped.forEach(action);

    }

    @Override
    public void forEachOrdered(Consumer<? super X> action) {
        wrapped.forEachOrdered(action);
    }

    @Override
    public Object[] toArray() {
        return wrapped.toArray();
    }

    @Override
    public <A> A[] toArray(IntFunction<A[]> generator) {
        return wrapped.toArray(generator);
    }

    @Override
    public X reduce(X identity, BinaryOperator<X> accumulator) {
        return wrapped.reduce(identity,accumulator);
    }

    @Override
    public Optional<X> reduce(BinaryOperator<X> accumulator) {
        return wrapped.reduce(accumulator);
    }

    @Override
    public <U> U reduce(U identity, BiFunction<U, ? super X, U> accumulator, BinaryOperator<U> combiner) {
        return wrapped.reduce(identity, accumulator, combiner);
    }

    @Override
    public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super X> accumulator, BiConsumer<R, R> combiner) {
        return wrapped.collect(supplier,accumulator, combiner);
    }

    @Override
    public <R, A> R collect(Collector<? super X, A, R> collector) {
        return wrapped.collect(collector);
    }

    @Override
    public Optional<X> min(Comparator<? super X> comparator) {
        return wrapped.min(comparator);
    }

    @Override
    public Optional<X> max(Comparator<? super X> comparator) {
        return wrapped.max(comparator);
    }

    @Override
    public long count() {
        return wrapped.count();
    }

    @Override
    public boolean anyMatch(Predicate<? super X> predicate) {
        return wrapped.anyMatch(predicate);
    }

    @Override
    public boolean allMatch(Predicate<? super X> predicate) {
        return wrapped.allMatch(predicate);
    }

    @Override
    public boolean noneMatch(Predicate<? super X> predicate) {
        return wrapped.noneMatch(predicate);
    }

    @Override
    public Optional<X> findFirst() {
        return wrapped.findFirst();
    }

    @Override
    public Optional<X> findAny() {
        return wrapped.findAny();
    }

    @Override
    public Iterator<X> iterator() {
        return wrapped.iterator();
    }

    @Override
    public Spliterator<X> spliterator() {
        return wrapped.spliterator();
    }

    @Override
    public boolean isParallel() {
        return wrapped.isParallel();
    }

    @Override
    public StreamExt<X> sequential() {
        return new StreamExt<>(wrapped.sequential());
    }

    @Override
    public StreamExt<X> parallel() {
        return new StreamExt<>(wrapped.parallel());
    }

    @Override
    public StreamExt<X> unordered() {
        return new StreamExt<>(wrapped.unordered());
    }

    @Override
    public StreamExt<X> onClose(Runnable closeHandler) {
        return new StreamExt<>(wrapped.onClose(closeHandler));
    }

    @Override
    public void close() {
        wrapped.close();
    }

    public static <X> StreamExt<X> stream(Stream<X> base) {
        return new StreamExt<>(base);
    }

    public static <X> StreamExt<X> stream(Collection<X> collection) {
        return new StreamExt<>(collection.stream());
    }

    public static <K, V> StreamExt<Map.Entry<K, V>> stream(Map<K, V> map) {
        return new StreamExt<>(map.entrySet().stream());
    }
}
