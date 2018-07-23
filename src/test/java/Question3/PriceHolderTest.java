package Question3;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class PriceHolderTest {

    private static final String ENTITY_A = "a";
    private static final String ENTITY_B = "b";

    private static PriceHolder ph;

    @Before
    public void setUpPriceHolder(){
        ph = new PriceHolder();
    }

    @Test
    public void given_newPriceIsPut_WaitForNextPrice_returns() throws InterruptedException {
        ph.putPrice(ENTITY_A, BigDecimal.ONE);

        BigDecimal newPrice = ph.waitForNextPrice(ENTITY_A);

        assertEquals(newPrice, BigDecimal.ONE);
    }

    @Test
    public void given_newPriceWillBePut_WaitForNextPrice_returns() throws InterruptedException {
        CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ph.putPrice(ENTITY_A, BigDecimal.ONE);
        });

        BigDecimal newPrice = ph.waitForNextPrice(ENTITY_A);

        assertEquals(newPrice, BigDecimal.ONE);
    }

    @Test
    public void given_multipleThreadsInEntity_WaitForNextPrice_returnsUniqueNextPrices() throws InterruptedException {
        putNewPricesOnNewThread(2, ENTITY_A);

        final CountDownLatch countDownLatch = new CountDownLatch(2);
        final BigDecimal[] returnPrice = new BigDecimal[2];

        waitForNewPriceOnNewThread(countDownLatch, returnPrice, 0, ENTITY_A);

        waitForNewPriceOnNewThread(countDownLatch, returnPrice, 1, ENTITY_A);

        countDownLatch.await(20, TimeUnit.SECONDS);

        assertNotEquals(returnPrice[0].intValue(), returnPrice[1].intValue());
    }

    @Test
    public void given_multipleThreadsInEntity_andMultipleEntities_WaitForNextPrice_returnsUniqueNextPricesWithRespectToEntity() throws InterruptedException {
        putNewPricesOnNewThread(2, ENTITY_A);
        putNewPricesOnNewThread(2, ENTITY_B);

        final CountDownLatch countDownLatch = new CountDownLatch(4);
        final BigDecimal[] returnPriceA = new BigDecimal[2];
        final BigDecimal[] returnPriceB = new BigDecimal[2];

        waitForNewPriceOnNewThread(countDownLatch, returnPriceA, 0, ENTITY_A);
        waitForNewPriceOnNewThread(countDownLatch, returnPriceB, 0, ENTITY_B);

        waitForNewPriceOnNewThread(countDownLatch, returnPriceA, 1, ENTITY_A);
        waitForNewPriceOnNewThread(countDownLatch, returnPriceB, 1, ENTITY_B);

        countDownLatch.await(30, TimeUnit.SECONDS);

        assertNotEquals(returnPriceA[0].intValue(), returnPriceA[1].intValue());

        assertNotEquals(returnPriceB[0].intValue(), returnPriceB[1].intValue());
    }

    private void putNewPricesOnNewThread(final int count, final String entity) {
        CompletableFuture.runAsync(() -> {
            for(int i = 1; i <= count; ++i) {
                ph.putPrice(entity, BigDecimal.valueOf(i));
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void waitForNewPriceOnNewThread(final CountDownLatch countDownLatch,
                                            final BigDecimal[] returnPrice,
                                            final int index, final String entity) {
        CompletableFuture.runAsync(() -> {
            try {
                returnPrice[index] =  ph.waitForNextPrice(entity);
                countDownLatch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

}
