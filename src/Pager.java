
import java.util.*;

public class Pager {

    private String fifoOutput = "", lruOutput = "", optimalOutput = "", fifoFinalFrames = "", lruFinalFrames = "", optimalFinalFrames = "";

    private int[] pages;
    private int frames;
    private int fifoFaults = 0, lruFaults = 0, optimalFaults = 0;

    public static void main(String args[]) {

        int pages[] = {1, 2, 3, 4, 2, 3, 4, 1, 2, 1, 1, 3, 1, 4};
        int frames = 3;
        Pager pager = new Pager(pages, frames);

        pager.fifo();
        pager.lru();
        pager.optimal();
        pager.print();
    }

    public Pager(int[] pages, int frames) {
        this.pages = pages;
        this.frames = frames;
    }

    // Method to find page faults using FIFO
    public void fifo() {
        //Should be kept in sync with the queue
        HashSet<Integer> s = new HashSet<>(frames);
        // To store the pages in FIFO manner
        Queue<Integer> indexes = new LinkedList<>();

        // Start from initial page
        for (int i = 0; i < pages.length; i++) {

            if (!s.contains(pages[i])) {
                if (s.size() < frames) {
                    s.add(pages[i]);
                    // Push the current page into the queue
                    indexes.add(pages[i]);

                    // increment page fault
                    fifoFaults++;
                    fifoOutput += "Y";
                } else {
                    //Pop the first page from the queue
                    int val = indexes.poll();
                    // Remove the indexes page
                    s.remove(val);

                    // insert the current page
                    s.add(pages[i]);

                    // push the current page into the queue
                    indexes.add(pages[i]);

                    // Increment page faults
                    fifoFaults++;
                    fifoOutput += "Y";
                }

            } else {
                fifoOutput += "N";
            }
            //System.out.println(indexes.peek() + " x" + "x");
        }
        while(!indexes.isEmpty()){
            fifoFinalFrames+= indexes.poll() +",";
        }
        fifoFinalFrames = fifoFinalFrames.substring(0,fifoFinalFrames.length()-1);
    }

    public void lru() {

        HashSet<Integer> s = new HashSet<>(frames);
        // To store least recently used indexes of pages.
        HashMap<Integer, Integer> indexes = new HashMap<>();

        for (int i = 0; i < pages.length; i++) {
            // Check if the set can hold more pages
            if (s.size() < frames) {
                // Insert it into set if not present
                // already which represents page fault
                if (!s.contains(pages[i])) {
                    s.add(pages[i]);

                    // increment page fault
                    lruFaults++;
                    lruOutput += "Y";
                } else {
                    lruOutput += "N";
                }
                // Store the recently used index of
                // each page
                indexes.put(pages[i], i);
            }

            // If the set is full then need to perform lru
            // i.e. remove the least recently used page
            // and insert the current page
            else {
                // Check if current page is not already
                // present in the set
                if (!s.contains(pages[i])) {
                    // Find the least recently used pages
                    // that is present in the set
                    int lru = Integer.MAX_VALUE, val = Integer.MIN_VALUE;

                    Iterator<Integer> itr = s.iterator();

                    while (itr.hasNext()) {
                        int temp = itr.next();
                        if (indexes.get(temp) < lru) {
                            lru = indexes.get(temp);
                            val = temp;
                        }
                    }

                    // Remove the indexes page
                    s.remove(val);
                    indexes.remove(val);
                    // insert the current page
                    s.add(pages[i]);

                    // Increment page faults
                    lruFaults++;
                    lruOutput += "Y";
                } else {
                    lruOutput += "N";
                }

                // Update the current page index
                indexes.put(pages[i], i);
            }
        }

        s.forEach(frame -> lruFinalFrames+=frame+",");
        lruFinalFrames = lruFinalFrames.substring(0,lruFinalFrames.length()-1);
    }

    public void optimal() {
        HashSet<Integer> s = new HashSet<>(frames);
        // To store least recently used indexes of pages.
        ArrayList<Integer> indexes = new ArrayList<>();

        // Traverse through page reference array
        // and check for miss and hit.
        for (int i = 0; i < pages.length; i++) {
            if (!s.contains(pages[i])) {
                optimalOutput += "Y";
                optimalFaults++;
                // If there is space available in frames.
                if (s.size() < frames) {
                    // insert the current page
                    s.add(pages[i]);
                    indexes.add(pages[i]);
                }
                // Find the page to be replaced.
                else {
                    int j = predict(indexes, i + 1);
                    int victim = indexes.get(j);
                    indexes.remove(j);
                    s.remove(victim);
                    // insert the current page
                    s.add(pages[i]);
                    indexes.add(pages[i]);
                }
            } else {
                optimalOutput += "N";
            }
        }
        s.forEach(frame -> optimalFinalFrames+=frame+",");
        optimalFinalFrames = optimalFinalFrames.substring(0,optimalFinalFrames.length()-1);

    }

    private int predict(ArrayList<Integer> indexes, int index) {

        // Store the index of pages which are going
        // to be used recently in future
        int res = -1, farthest = index;
        for (int i = 0; i < indexes.size(); i++) {
            int j;
            for (j = index; j < pages.length; j++) {
                if (indexes.get(i) == pages[j]) {
                    if (j > farthest) {
                        farthest = j;
                        res = i;
                    }
                    break;
                }
            }

            // If a page is never referenced in future,
            // return it.
            if (j == pages.length)
                return i;
        }

        // If all of the frames were not in future,
        // return any of them, we return 0. Otherwise
        // we return res.
        return (res == -1) ? 0 : res;
    }


    public void print() {
        System.out.println("Y means there was a page fault (miss), and N means there was not (hit).");
        final Object[][] table = new String[4][pages.length + 1];
        table[0][0] = "";
        table[1][0] = "FIFO";
        table[2][0] = "LRU";
        table[3][0] = "OPT";

        String format = "";
        for (int i = 0; i < pages.length; i++) {
            table[0][i + 1] = pages[i] + "";
            table[1][i + 1] = fifoOutput.charAt(i) + "";
            table[2][i + 1] = lruOutput.charAt(i) + "";
            table[3][i + 1] = optimalOutput.charAt(i) + "";
            format += "%5s";
        }
        format += "%5s\n";

        for (final Object[] row : table) {
            System.out.format(format, row);
            System.out.println("------------------------------------------------------------------------------------");
        }

        System.out.println("\n***********************************************************************************************************\n");
        System.out.printf("%10s %22s %19s %40s\n", "Replacement Algorithm", "No. of Page Faults", "Compact Output", "Final Frames Content (NOT ORDERED)");
        System.out.println("---------------------------------------------------------------------------------------------------------");
        System.out.format("%10s %22s %30s %25s\n", "FIFO", fifoFaults, fifoOutput, fifoFinalFrames);
        System.out.println("---------------------------------------------------------------------------------------------------------");
        System.out.format("%10s %22s %30s %25s\n", "LRU", lruFaults, lruOutput, lruFinalFrames);
        System.out.println("---------------------------------------------------------------------------------------------------------");
        System.out.format("%10s %22s %30s %25s\n", "Optimal", optimalFaults, optimalOutput, optimalFinalFrames);
        System.out.println("---------------------------------------------------------------------------------------------------------");
    }

    public int getFifoFaults() {
        return fifoFaults;
    }

    public int getLruFaults() {
        return lruFaults;
    }

    public int getOptimalFaults() {
        return optimalFaults;
    }

    public String getFifoOutput() {
        return fifoOutput;
    }

    public String getLruOutput() {
        return lruOutput;
    }

    public String getOptimalOutput() {
        return optimalOutput;
    }

    public String getFifoFinalFrames() {
        return fifoFinalFrames;
    }

    public String getLruFinalFrames() {
        return lruFinalFrames;
    }

    public String getOptimalFinalFrames() {
        return optimalFinalFrames;
    }
}

