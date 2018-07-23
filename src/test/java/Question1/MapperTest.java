package Question1;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class MapperTest {
    @Test
    public void testMapWithInteger() {
        ArrayList<Integer> three = new ArrayList<>(Arrays.asList(1, 2, 3));

        List<Integer> modifiedThree = Mapper.map((Integer i) -> ++i, three);

        assertEquals(modifiedThree, new ArrayList<>(Arrays.asList(2, 3, 4)));
    }
}
