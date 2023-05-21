// do not remove imports

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

class ArrayUtils {
    // define getFirst method here

    public static <T> T getFirst(T[] arr) {
        return arr.length != 0 ? arr[0] : null;
    }
}