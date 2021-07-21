package studio.blacktech.furryblackplus.test;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public class SkipListTest {


    @Test
    public void test1() {
        Map<Integer, Integer> map = new ConcurrentSkipListMap<>();
        map.put(1, 1);
        map.put(3, 1);
        map.put(2, 1);
        map.put(5, 1);
        map.put(4, 1);
        map.forEach((k, v) -> System.out.println(k + " > " + v));
    }

}
