package no.hvl.past.graph.trees;

import com.google.common.base.Objects;

import no.hvl.past.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * A source of {@link TreeEvent}s.
 */
public interface TreeEmitter {

    /**
     * Emits all available event into the provided receiver.
     */
    void emitEvents(TreeReceiver target) throws Exception;

    void reset();


    class ConstantEmitter implements TreeEmitter {

        private final List<TreeEvent> emitCycle = new ArrayList<>();
        private int idx = 0;

        public ConstantEmitter(TreeEvent event) {
            emitCycle.add(event);
        }

        public ConstantEmitter(List<TreeEvent> emitSequence) {
            emitCycle.addAll(emitSequence);
        }


        @Override
        public void emitEvents(TreeReceiver target) throws Exception {
            for (TreeEvent event : emitCycle) {
                event.accept(target);
            }
        }

        @Override
        public void reset() {
            this.idx = 0;
        }
    }


    class SimpleCollector extends TreeReceiver.ToEvent implements TreeEmitter {

        private final List<TreeEvent> collectedEvents;
        private Semaphore semaphore;

        public SimpleCollector() {
            this.collectedEvents = new ArrayList<>();
            this.semaphore = new Semaphore(0);
        }

        public List<TreeEvent> getCollectedEvents() {
            return collectedEvents;
        }


        @Override
        public void emitEvents(TreeReceiver target) throws Exception {
            for (TreeEvent event : this.collectedEvents) {
                event.accept(target);
            }
        }

        @Override
        public void reset() {
            this.collectedEvents.clear();
            this.semaphore = new Semaphore(0);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SimpleCollector that = (SimpleCollector) o;
            return Objects.equal(collectedEvents, that.collectedEvents);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(collectedEvents);
        }

        @Override
        public String toString() {
            return StringUtils.fuseList(collectedEvents.stream().map(java.util.Objects::toString), "\n");
        }

        @Override
        public void handle(TreeEvent event) {
            collectedEvents.add(event);
        }
    }
}
