package Question3;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;

public final class PriceHolder {

    //map each entity to object which contains the current price, the hasPriceChanged boolean and a queue of countdownlatchs

    private final ConcurrentHashMap<String, BigDecimal> prices = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Boolean> hasPriceChanged = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Queue<CountDownLatch>> entityThreadCountDownLatches = new ConcurrentHashMap<>();

    public PriceHolder() { }

    /** Called when a price ‘p’ is received for an entity ‘e’ */
    public void putPrice(final String e, final BigDecimal p) {
        prices.compute(e, (key, value) -> {
            hasPriceChanged.merge(e, true, (old, newB) -> newB);
            unblockWaitingThreadForEntity(e);
            return p;
        });
    }

    private void unblockWaitingThreadForEntity(String e) {
        Queue<CountDownLatch> entityCountDownLatches = entityThreadCountDownLatches.getOrDefault(e, null);
        if (entityCountDownLatches != null && entityCountDownLatches.size() > 0) {
            entityCountDownLatches.remove().countDown();
        }
    }

    /** Called to get the latest price for entity ‘e’ */
    public synchronized BigDecimal getPrice(final String e) {
        hasPriceChanged.merge(e, false, (old, newB) -> newB);
        return prices.get(e);
    }

    /**
     * Called to determine if the price for entity ‘e’ has
     * changed since the last call to getPrice(e).
     */
    public boolean hasPriceChanged(final String e) {
        return hasPriceChanged.getOrDefault(e, false);
    }

    /**
     * Returns the next price for entity ‘e’. If the price has changed since the last
     * call to getPrice() or waitForNextPrice(), it returns immediately that price.
     * Otherwise it blocks until the next price change for entity ‘e’.
     * If multiple threads from the same entity ‘e’ call this function they will
     * wait in a FIFO manner and each thread will receive a new price.
     */
    public BigDecimal waitForNextPrice(final String e) throws InterruptedException {
        while (!hasPriceChanged(e)) {
            waitForPutPriceToBeCalledForEntity(e);
        }
        return getPrice(e);
    }

    private void waitForPutPriceToBeCalledForEntity(String e) throws InterruptedException {
        CountDownLatch oneStepLatch = new CountDownLatch(1);
        LinkedList<CountDownLatch> oneStepLatchAsList = new LinkedList<>();
        oneStepLatchAsList.add(oneStepLatch);
        entityThreadCountDownLatches.merge(e, oneStepLatchAsList , (oldLatchs, newLatchs) -> {
            oldLatchs.addAll(newLatchs);
            return oldLatchs;
        });
        oneStepLatch.await();
    }
}
