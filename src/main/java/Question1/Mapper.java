package Question1;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class Mapper {

    /**
     * Generic method that takes a function and a list and modifies each element in the list with the given function
     * @param modifier This is the function that will modify each element
     * @param list  This is the list whose elements will be modified
     * @return list Returns a new list with elements modified by the function
     */
    public static <T> List<T> map(final Function<T, T> modifier, final List<T> list) {
        final List<T> modifiedList = new ArrayList<>();
        for (T t: list) {
            modifiedList.add(modifier.apply(t));
        }
        return modifiedList;
    }
}
