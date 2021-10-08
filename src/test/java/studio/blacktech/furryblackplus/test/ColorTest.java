/*
 * Copyright (C) 2021 Alceatraz @ BlackTechStudio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the BTS Anti-Commercial & GNU Affero General
 * Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * BTS Anti-Commercial & GNU Affero General Public License for more details.
 *
 * You should have received a copy of the BTS Anti-Commercial & GNU Affero
 * General Public License along with this program.
 *
 */

package studio.blacktech.furryblackplus.test;

import org.junit.jupiter.api.Test;
import studio.blacktech.furryblackplus.core.common.logger.base.LoggerX.Color;

public class ColorTest {

    @Test
    public void test() {

        System.out.println(Color.RED + "Quick brown fox jump over the lazy dog" + Color.RESET);
        System.out.println(Color.GREEN + "Quick brown fox jump over the lazy dog" + Color.RESET);
        System.out.println(Color.YELLOW + "Quick brown fox jump over the lazy dog" + Color.RESET);
        System.out.println(Color.BLUE + "Quick brown fox jump over the lazy dog" + Color.RESET);
        System.out.println(Color.MAGENTA + "Quick brown fox jump over the lazy dog" + Color.RESET);
        System.out.println(Color.CYAN + "Quick brown fox jump over the lazy dog" + Color.RESET);

        System.out.println(Color.BRIGHT_RED + "Quick brown fox jump over the lazy dog" + Color.RESET);
        System.out.println(Color.BRIGHT_GREEN + "Quick brown fox jump over the lazy dog" + Color.RESET);
        System.out.println(Color.BRIGHT_YELLOW + "Quick brown fox jump over the lazy dog" + Color.RESET);
        System.out.println(Color.BRIGHT_BLUE + "Quick brown fox jump over the lazy dog" + Color.RESET);
        System.out.println(Color.BRIGHT_MAGENTA + "Quick brown fox jump over the lazy dog" + Color.RESET);
        System.out.println(Color.BRIGHT_CYAN + "Quick brown fox jump over the lazy dog" + Color.RESET);

        System.out.println(Color.WHITE + "Quick brown fox jump over the lazy dog" + Color.RESET);
        System.out.println(Color.GRAY + "Quick brown fox jump over the lazy dog" + Color.RESET);
        System.out.println(Color.BRIGHT_BLACK + "Quick brown fox jump over the lazy dog" + Color.RESET);
        System.out.println(Color.BRIGHT_WHITE + "Quick brown fox jump over the lazy dog" + Color.RESET);


    }

}
