/*
 * Copyright (C) 2021 Alceatraz @ BlackTechStudio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the BTS Anti Commercial & GNU Affero General
 * Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * BTS Anti Commercial & GNU Affero General Public License for more details.
 *
 * You should have received a copy of the BTS Anti Commercial & GNU Affero
 * General Public License along with this program.
 *
 */

package studio.blacktech.furryblackplus.test;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public class CSLMTest {

    private static class Entity {

        public int i;

        public Entity(int i) {
            this.i = i;
        }
    }


    // @Test
    public void test() {

        ConcurrentSkipListMap<Entity, Integer> map = new ConcurrentSkipListMap<>((o1, o2) -> {
            if (o1 == o2) {
                return 0;
            } else {
                int i = o1.i - o2.i;
                return i == 0 ? 1 : i;
            }
        });

        map.put(new Entity(1), 1);
        map.put(new Entity(1), 2);
        map.put(new Entity(1), 3);
        map.put(new Entity(1), 4);
        map.put(new Entity(1), 5);

        System.out.println(">> --------------------------------------------");

        for (Map.Entry<Entity, Integer> entry : map.entrySet()) {
            System.out.println(entry.getKey().i + " -> " + entry.getValue());
        }

        System.out.println(">> --------------------------------------------");

        for (Map.Entry<Entity, Integer> entry : map.descendingMap().entrySet()) {
            System.out.println(entry.getKey().i + " -> " + entry.getValue());
        }
    }
}
