package studio.blacktech.furryblackplus;

import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class StreamTest {


    @Test
    void test() {

        int count = 1000;

        long time1 = 0;
        long time2 = 0;
        long time3 = 0;
        long time4 = 0;
        long time5 = 0;
        long time6 = 0;


        List<Long> EXCLUSIVE = new LinkedList<>();
        EXCLUSIVE.add(1000000332L);
        EXCLUSIVE.add(1000001332L);
        EXCLUSIVE.add(1000001352L);
        EXCLUSIVE.add(1000001432L);
        EXCLUSIVE.add(1000001132L);
        EXCLUSIVE.add(1000000032L);
        EXCLUSIVE.add(1000001842L);
        EXCLUSIVE.add(1000000502L);

        List<Long> members = new LinkedList<>();

        for (long j = 1000000000; j < 1000001900; j++) {
            members.add(j);
        }

        long botID = 1000001433;
        long userID = 1000000623;


        for (int i = 0; i < count; i++) {

            long a = System.nanoTime();
            List<Long> collect11 = members.stream().filter(item -> !(EXCLUSIVE.contains(item) || item == botID || item == userID)).collect(Collectors.toList());
            long b = System.nanoTime();
            List<Long> collect12 = members.stream().filter(item -> item != botID).filter(item -> item != userID).filter(item -> !EXCLUSIVE.contains(item)).collect(Collectors.toList());
            long c = System.nanoTime();
            List<Long> collect13 = members.stream().filter(item -> !EXCLUSIVE.contains(item)).filter(item -> item != botID && item != userID).collect(Collectors.toList());

            long d = System.nanoTime();

            List<Long> collect21 = members.parallelStream().filter(item -> !(EXCLUSIVE.contains(item) || item == botID || item == userID)).collect(Collectors.toList());
            long e = System.nanoTime();
            List<Long> collect22 = members.parallelStream().filter(item -> item != botID).filter(item -> item != userID).filter(item -> !EXCLUSIVE.contains(item)).collect(Collectors.toList());
            long f = System.nanoTime();
            List<Long> collect23 = members.parallelStream().filter(item -> !EXCLUSIVE.contains(item)).filter(item -> item != botID && item != userID).collect(Collectors.toList());
            long g = System.nanoTime();

            time1 = time1 + b - a;
            time2 = time2 + c - b;
            time3 = time3 + d - c;
            time4 = time4 + e - d;
            time5 = time5 + f - e;
            time6 = time6 + g - f;
        }


        System.out.println(time1 / count);
        System.out.println(time2 / count);
        System.out.println(time3 / count);
        System.out.println(time4 / count);
        System.out.println(time5 / count);
        System.out.println(time6 / count);


        /* For AMD 4800H
         * 60075
         * 86182
         * 87841
         * 79094
         * 79914
         * 76447
         */

    }


}
