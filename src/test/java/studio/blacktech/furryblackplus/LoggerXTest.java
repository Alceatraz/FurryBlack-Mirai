package studio.blacktech.furryblackplus;

import org.junit.jupiter.api.Test;
import studio.blacktech.furryblackplus.system.common.logger.LoggerX;

public class LoggerXTest {


    @Test
    void test() {

        LoggerX logger = new LoggerX(this.getClass());

        logger.verbose("This is verbose");
        logger.debug0("This is debug");
        logger.info("This is info");
        logger.warning("This is warning");
        logger.error("This is error");
    }


}
