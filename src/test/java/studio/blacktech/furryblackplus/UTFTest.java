package studio.blacktech.furryblackplus;

import org.junit.jupiter.api.Test;

import java.util.Map;

public class UTFTest {


    @Test
    void test() {

        for (Map.Entry<Object, Object> entry : System.getProperties().entrySet()) {

            System.out.println(entry.getKey() + "=" + entry.getValue());

        }

    }

}
