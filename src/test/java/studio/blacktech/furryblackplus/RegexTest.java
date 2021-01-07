package studio.blacktech.furryblackplus;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


    @Test
    void test2() {
        String[] test = new String[]{
                "/food",
                "/list",
                "/list 1234 `1234 1234 1234`",
                "/list 1234 `1234 \\`1234 1234\\` 123`",
        };


        Pattern pattern = Pattern.compile("^(?:/[a-z]{3,8})");


        for (String temp : test) {
            Matcher matcher = pattern.matcher(temp);
            System.out.println(matcher.find());
        }
    }

}
