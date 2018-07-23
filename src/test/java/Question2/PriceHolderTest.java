package Question2;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class PriceHolderTest {

    private PriceHolder ph;

    @Before
    public void setUpPriceHolder(){
        ph = new PriceHolder();
    }

    @Test
    public void testPriceHolder() {
        BigDecimal two = new BigDecimal(2);
        BigDecimal ten = new BigDecimal(10);
        BigDecimal eleven = new BigDecimal(11);
        BigDecimal twelve = new BigDecimal(12);

        ph.putPrice("a", ten);

        assertEquals(ph.hasPriceChanged("a"), true);
        assertEquals(ph.getPrice("a"), ten);
        assertEquals(ph.hasPriceChanged("a"), false);
        assertEquals(ph.hasPriceChanged("b"), false);

        ph.putPrice("a", twelve);
        ph.putPrice("b", two);
        ph.putPrice("a", eleven);

        assertEquals(ph.getPrice("a"), eleven);
        assertEquals(ph.getPrice("a"), eleven);
        assertEquals(ph.getPrice("b"), two);
    }
}
