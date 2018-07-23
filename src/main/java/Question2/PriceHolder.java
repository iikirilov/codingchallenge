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
            hasPriceChanged.put(e, true);
            return p;
        });
    }

    /** Called to get the latest price for entity ‘e’ */
    public BigDecimal getPrice(String e) {
        return prices.compute(e, (k, v) -> {
            hasPriceChanged.put(e, false);
           return v;
        });
    }

    /**
     * Called to determine if the price for entity ‘e’ has
     * changed since the last call to getPrice(e).
     */
    public boolean hasPriceChanged(String e) {
        return hasPriceChanged.getOrDefault(e, false);
    }
}
