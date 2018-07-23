package Question3;

import java.math.BigDecimal;
import java.util.concurrent.*;

public final class PriceHolder {

    //map each entity to object which contains the current price, the hasPriceChanged boolean and a queue of countdownlatchs

    private final ConcurrentHashMap<String, BigDecimal> prices = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Boolean> hasPriceChanged = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, CountDownLatchManager> entityThreadCountDownLatches = new ConcurrentHashMap<>();

    public PriceHolder() { }

    /** Called when a price ‘p’ is received for an entity ‘e’ */
    public void putPrice(final String e, final BigDecimal p) {
        prices.compute(e, (key, value) -> {
            hasPriceChanged.put(e, true);
            entityThreadCountDownLatches
                    .getOrDefault(e, new CountDownLatchManager())
                    .unblockFirstWaitingThread();
            return p;
        });
    }

    /** Called to get the latest price for entity ‘e’ */
    public BigDecimal getPrice(final String e) {
        return prices.compute(e, (k, v) -> {
            hasPriceChanged.put(e, false);
            return v;
        });
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
        entityThreadCountDownLatches.merge(e, new CountDownLatchManager() , (oldLatches, newLatches) -> {
            oldLatches.addNewLatch();
            return oldLatches;
        }).awaitOnLastLatch();
    }
}
