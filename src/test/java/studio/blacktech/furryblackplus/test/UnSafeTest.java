package studio.blacktech.furryblackplus.test;

import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

public class UnSafeTest {


    @Test
    public void test() throws PrivilegedActionException {

        PrivilegedExceptionAction<Unsafe> action = () -> {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Unsafe unsafe = (Unsafe) theUnsafe.get(null);
            theUnsafe.setAccessible(false);
            return unsafe;
        };

        Unsafe unsafe = AccessController.doPrivileged(action);

    }

}
