package no.hvl.past.util;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class SearchTest {

    private static class BlockPuzzleMove {
        private final BlockPuzzle current;
        private final int from;
        private final int to;

        private BlockPuzzleMove(BlockPuzzle current, int from, int to) {
            this.current = current;
            this.from = from;
            this.to = to;
        }

        private String direction() {
            if (from / current.cubeLength == to / current.cubeLength) {
                return from > to ? "LEFT" :"RIGHT";
            } else {
                return from > to ? "UP" : "DOWN";
            }

        }

        @Override
        public String toString() {
            return direction();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof BlockPuzzleMove) {
                BlockPuzzleMove other = (BlockPuzzleMove) obj;
                return this.from == other.from && this.to == other.to && this.current.equals(other.current);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return current.hashCode() ^ from ^ to;
        }
    }

    public static class BlockPuzzle implements StateSpace<BlockPuzzle, BlockPuzzleMove> {

        private final int[] numbers;
        private final int cubeLength;

        private BlockPuzzle(int[] numbers, int cubeLength) {
            this.numbers = numbers;
            this.cubeLength = cubeLength;
        }

        private int manhattanDistance(int currentIndex, int targetIndex) {
            if (currentIndex > targetIndex) {
                int vertical = currentIndex / cubeLength - targetIndex / cubeLength;
                int sameRowCurrent = currentIndex % cubeLength;
                int sameRowTarget = targetIndex % cubeLength;
                int horizontal = sameRowCurrent > sameRowTarget ? sameRowCurrent - sameRowTarget : sameRowTarget - sameRowCurrent;
                return vertical + horizontal;
            } else {
                return manhattanDistance(targetIndex, currentIndex);
            }
        }

        public int manhattanDistanceMetric() {
            int result = 0;
            for (int i = 0; i < numbers.length; i++) {
                if (numbers[i] != i) {
                    result += manhattanDistance(i, numbers[i]);
                }
            }
            return result;
        }

        public int editDistanceMetric() {
            int outOfPlace = 0;
            for (int i = 0; i < numbers.length; i++) {
                if (numbers[i] != i) {
                    outOfPlace++;
                }
            }
            return outOfPlace;
        }

        public List<BlockPuzzleMove> possibleMoves() {
            int zeroIndex = -1;
            for (int i = 0; i < numbers.length; i++) {
                if (numbers[i] == 0) {
                    zeroIndex = i;
                    break;
                }
            }
            List<BlockPuzzleMove> moves = new ArrayList<>();
            // up
            if (zeroIndex >= cubeLength) {
                moves.add(new BlockPuzzleMove(this, zeroIndex, zeroIndex - cubeLength));
            }
            // down
            if (zeroIndex < numbers.length - cubeLength) {
                moves.add(new BlockPuzzleMove(this, zeroIndex, zeroIndex + cubeLength));
            }
            // right
            if ((zeroIndex + 1) % cubeLength != 0) {
                moves.add(new BlockPuzzleMove(this, zeroIndex, zeroIndex + 1));
            }
            // left
            if (zeroIndex % cubeLength != 0) {
                moves.add(new BlockPuzzleMove(this, zeroIndex, zeroIndex - 1));
            }
            return moves;
        }

        private String print() {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < numbers.length; i++) {
                if (i != 0 && i % cubeLength == 0) {
                    result.append('\n');
                }
                if (numbers[i] == 0) {
                    result.append(' ');
                } else {
                    result.append(numbers[i]);
                }
                result.append(' ');
            }
            return result.toString();
        }

        public BlockPuzzle perform(BlockPuzzleMove move) {
            int[] result = Arrays.copyOf(this.numbers, numbers.length);
            if (move.from < 0 || move.from >= numbers.length || move.to <0 || move.to >= numbers.length) {
                System.out.println("Warning array out out bounds:" + move.from + ", " + move.to);
                System.out.println(move.current);
            }
            result[move.from] = this.numbers[move.to];
            result[move.to] = this.numbers[move.from];
            new BlockPuzzle(result, cubeLength);
            return new BlockPuzzle(result, cubeLength);
        }

        private boolean isGoal() {
            for (int i = 0; i < numbers.length; i++) {
                if (numbers[i] != i) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public List<BlockPuzzleMove> availableActions(BlockPuzzle current) {
            return current.possibleMoves();
        }

        @Override
        public Optional<BlockPuzzle> applyAction(BlockPuzzle current, BlockPuzzleMove action) {
            BlockPuzzle result = current.perform(action);
            return Optional.of(result);
        }

        @Override
        public boolean isInfinite() {
            return false;
        }

        @Override
        public String toString() {
            return print();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof BlockPuzzle) {
                BlockPuzzle other = (BlockPuzzle) obj;
                return Arrays.equals(this.numbers, other.numbers) && this.cubeLength == other.cubeLength;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(numbers) ^ this.cubeLength;
        }
    }


    @Test
    public void testQuicklyFound() {
        int[] numbers = new int[9];
        numbers[0] = 1;
        numbers[1] = 4;
        numbers[2] = 2;
        numbers[3] = 6;
        numbers[4] = 5;
        numbers[5] = 8;
        numbers[6] = 7;
        numbers[7] = 3;
        numbers[8] = 0;
        BlockPuzzle blockPuzzle = new BlockPuzzle(numbers, 3);
        SearchEngine<BlockPuzzle, BlockPuzzleMove> searchEngine = new SearchEngine<>(blockPuzzle);
        Pair<List<BlockPuzzleMove>, Integer> searchResult = searchEngine.searchWithTrace(SearchStrategy.Strategies.A_STAR,
                blockPuzzle,
                blockPuzzle1 -> blockPuzzle1.isGoal(),
                move -> 2,
                move -> move.current.perform(move).manhattanDistanceMetric());
        assertFalse(searchResult.getFirst().isEmpty());
        BlockPuzzle result = blockPuzzle;
        for (BlockPuzzleMove move : searchResult.getFirst()) {
            result = result.perform(move);
        }
        assertTrue(result.isGoal());
    }


    @Test
    public void testExampleFromBook() {
        int[] numbers = new int[9];
        numbers[0] = 7;
        numbers[1] = 2;
        numbers[2] = 4;
        numbers[3] = 5;
        numbers[4] = 0;
        numbers[5] = 6;
        numbers[6] = 8;
        numbers[7] = 3;
        numbers[8] = 1;
        BlockPuzzle blockPuzzle = new BlockPuzzle(numbers, 3);

        assertFalse(blockPuzzle.isGoal());
        assertEquals(4, blockPuzzle.possibleMoves().size());

        assertEquals(1, blockPuzzle.manhattanDistance(1, 2));
        assertEquals(2, blockPuzzle.manhattanDistance(7, 3));


        SearchEngine<BlockPuzzle, BlockPuzzleMove> searchEngine = new SearchEngine<>(blockPuzzle);
        Pair<List<BlockPuzzleMove>, Integer> searchResult = searchEngine.searchWithTrace(SearchStrategy.Strategies.A_STAR,
                blockPuzzle,
                blockPuzzle1 -> blockPuzzle1.isGoal(),
                move -> 0,
                move -> move.current.perform(move).manhattanDistanceMetric());
        assertFalse(searchResult.getFirst().isEmpty());
        BlockPuzzle result = blockPuzzle;
        for (BlockPuzzleMove move : searchResult.getFirst()) {
            result = result.perform(move);
        }
        assertTrue(result.isGoal());
    }

    @Test
    public void testSimpleCase() {
        int[] numbers = new int[4];
        numbers[0] = 0;
        numbers[1] = 2;
        numbers[2] = 3;
        numbers[3] = 1;
        BlockPuzzle blockPuzzle = new BlockPuzzle(numbers, 2);
        SearchEngine<BlockPuzzle, BlockPuzzleMove> searchEngine = new SearchEngine<>(blockPuzzle);
        Pair<List<BlockPuzzleMove>, Integer> searchResult = searchEngine.searchWithTrace(SearchStrategy.Strategies.A_STAR,
                blockPuzzle,
                blockPuzzle1 -> blockPuzzle1.isGoal(),
                move -> 3,
                move -> move.current.perform(move).manhattanDistanceMetric());
        assertFalse(searchResult.getFirst().isEmpty());
        System.out.println(searchResult.getFirst());

    }


    @Test
    public void testBlockPuzzleCornerCase() {
        int[] numbers = new int[9];
        numbers[0] = 7;
        numbers[1] = 2;
        numbers[2] = 5;
        numbers[3] = 6;
        numbers[4] = 3;
        numbers[5] = 4;
        numbers[6] = 8;
        numbers[7] = 1;
        numbers[8] = 0;
        BlockPuzzle blockPuzzle = new BlockPuzzle(numbers, 3);
        List<BlockPuzzleMove> poss = blockPuzzle.possibleMoves();
        assertEquals(2, poss.size());
    }

}
