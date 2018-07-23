package Question2;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;

public final class PriceHolder {

    private final ConcurrentHashMap<String, BigDecimal> prices = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Boolean> hasPriceChanged = new ConcurrentHashMap<>();

    public PriceHolder() { }

    /** Called when a price ‘p’ is received for an entity ‘e’ */
    public void putPrice(final String e, final BigDecimal p) {
        prices.compute(e, (key, value) -> {
            hasPriceChanged.merge(e, true, (old, newB) -> newB);
            return p;
        });
    }

    /** Called to get the latest price for entity ‘e’ */
    public synchronized BigDecimal getPrice(String e) {
        hasPriceChanged.merge(e, false, (old, newB) -> newB);
        return prices.get(e);
    }

    /**
     * Called to determine if the price for entity ‘e’ has
     * changed since the last call to getPrice(e).
     */
    public boolean hasPriceChanged(String e) {
        return hasPriceChanged.getOrDefault(e, false);
    }
}
