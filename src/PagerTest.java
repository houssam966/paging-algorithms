import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class PagerTest {

    @Test
    public void fifoWorks(){
        Pager pager = new Pager(new int[]{3, 1, 3, 1, 0, 1, 3, 1, 3, 0, 2, 3, 1, 3, 1}, 3);
        pager.fifo();
        assertEquals(pager.getFifoFaults(), 6);
        assertEquals(pager.getFifoOutput(), "YYNNYNNNNNYYYNN");

        Pager pager2 = new Pager(new int[] {1, 2, 3, 4, 2, 3, 4, 1, 2, 1, 1, 3, 1, 4}, 3);
        pager2.fifo();
        assertEquals(pager2.getFifoFaults(), 8);
        assertEquals(pager2.getFifoFinalFrames(), "2,3,4");

        Pager pager3 = new Pager(new int[]{1, 2, 3, 4, 1, 2, 5, 1, 2, 3, 4, 5}, 3);
        pager3.fifo();
        assertEquals(pager3.getFifoFaults(), 9);

    }
    @Test
    public void lruWorks(){
        Pager pager = new Pager(new int[]{3, 1, 3, 1, 0, 1, 3, 1, 3, 0, 2, 3, 1, 3, 1}, 3);
        pager.lru();
        assertEquals(pager.getLruFaults(), 5);
        assertEquals(pager.getLruOutput(), "YYNNYNNNNNYNYNN");
        Pager pager2 = new Pager(new int[] {1, 2, 3, 1, 2, 4, 1, 2, 3, 1, 2, 4, 1, 2, 3}, 3);
        pager2.lru();
        assertEquals(pager2.getLruFaults(), 7);
        assertEquals(pager2.getLruOutput(), "YYYNNYNNYNNYNNY");
    }
    @Test
    public void optimalWorks(){
        Pager pager = new Pager(new int[] {1, 2, 3, 1, 2, 4, 1, 2, 3, 1, 2, 4, 1, 2, 3}, 3);
        pager.optimal();
        assertEquals(pager.getOptimalFaults(), 7);
        assertEquals(pager.getOptimalOutput(), "YYYNNYNNYNNYNNY");
    }
}
