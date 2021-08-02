package studio.blacktech.furryblackplus.test;

import org.junit.jupiter.api.Test;
import studio.blacktech.furryblackplus.Driver;

public class MainTest {

    @Test
    public void test() {

        String[] args = {
            "--debug",
            "--no-jline",
            "--no-login",
        };

        Driver.main(args);
    }

}
