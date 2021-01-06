package studio.blacktech.furryblackplus;

import org.junit.jupiter.api.Test;

public class RegexTest {


    @Test
    void test() {


        String[] test = new String[]{
                "123:1234567890",
                "!:1234567890",
                "*:1234567890",
                "1234567890:*",
                "1234567890:1234567890",
        };


        for (String temp : test) {
            boolean matches = temp.matches("^(?:\\*|[0-9]{6,14}):(?:\\*|[0-9]{6,14})$");
            System.out.println(matches);
        }

    }

}
