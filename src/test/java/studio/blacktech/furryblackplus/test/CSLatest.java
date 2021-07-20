package studio.blacktech.furryblackplus.test;

import org.junit.jupiter.api.Test;
import studio.blacktech.furryblackplus.core.annotation.Component;
import studio.blacktech.furryblackplus.core.exception.BotException;
import studio.blacktech.furryblackplus.core.exception.initlization.BootException;
import studio.blacktech.furryblackplus.core.interfaces.AbstractEventHandler;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class CSLatest {


    @Test
    public void test1() {

        ConcurrentSkipListMap<Component, Class<?>> map = new ConcurrentSkipListMap<>(Comparator.comparingInt(Component::priority));

        map.put(c1.class.getAnnotation(Component.class), c1.class);
        map.put(c2.class.getAnnotation(Component.class), c2.class);

        for (Map.Entry<Component, Class<?>> entry : map.entrySet()) {
            System.out.println(entry.getKey().artificial() + " " + entry.getValue().getName());
        }

    }

    @Test
    public void test2() {

        TreeMap<Component, Class<?>> map = new TreeMap<>(Comparator.comparingInt(Component::priority));

        map.put(c1.class.getAnnotation(Component.class), c1.class);
        map.put(c2.class.getAnnotation(Component.class), c2.class);

        for (Map.Entry<Component, Class<?>> entry : map.entrySet()) {
            System.out.println(entry.getKey().artificial() + " " + entry.getValue().getName());
        }

    }


    @Test
    public void test3() {

        LinkedHashMap<Integer, String> test = new LinkedHashMap<>();

        test.put(1, "A");
        test.put(2, "B");
        test.put(3, "C");
        test.put(4, "D");

        for (Map.Entry<Integer, String> entry : test.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }

        test.put(2, "new B");


        for (Map.Entry<Integer, String> entry : test.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }

    }


    @Component(artificial = "c1")
    public static class c1 extends AbstractEventHandler {

        @Override
        protected void init() throws BootException {

        }

        @Override
        protected void boot() throws BotException {

        }

        @Override
        protected void shut() throws BotException {

        }
    }

    @Component(artificial = "c2")
    public static class c2 extends AbstractEventHandler {

        @Override
        protected void init() throws BootException {

        }

        @Override
        protected void boot() throws BotException {

        }

        @Override
        protected void shut() throws BotException {

        }
    }

    @Component(artificial = "c3")
    public static class c3 extends AbstractEventHandler {

        @Override
        protected void init() throws BootException {

        }

        @Override
        protected void boot() throws BotException {

        }

        @Override
        protected void shut() throws BotException {

        }
    }

}
