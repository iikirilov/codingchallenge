package Question3;

import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;

public class CountDownLatchManager {

    private LinkedList<CountDownLatch> countDownLatches = new LinkedList<>();

    /** If the list is populated, the first CountDownLatch will be counted down */
    public void unblockFirstWaitingThread() {
        if (countDownLatches.size() > 0) {
            countDownLatches.removeFirst().countDown();
        }
    }

    /** Adds a single step CountDownLatch to the back on the list */
    public void addNewLatch() {
        countDownLatches.addLast(new CountDownLatch(1));
    }

    /** Awaits for the last CountDownLatch to be counted down */
    public void awaitOnLastLatch() throws InterruptedException {
        if (countDownLatches.size() > 0) {
            countDownLatches.getLast().await();
        }
    }
}
