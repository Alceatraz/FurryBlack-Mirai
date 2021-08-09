/*
 * Copyright (C) 2021 Alceatraz @ BlackTechStudio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the BTS Anti Commercial & GNU Affero General.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * BTS Anti Commercial & GNU Affero General Public License for more details.
 *
 * You should have received a copy of the BTS Anti Commercial & GNU Affero
 * General Public License along with this program in README or LICENSE.
 */

package studio.blacktech.furryblackplus.demo;


import studio.blacktech.furryblackplus.core.define.annotation.Runner;
import studio.blacktech.furryblackplus.core.define.moduel.EventHandlerRunner;

@Runner("demo-runner")
public class DemoRunner extends EventHandlerRunner {


    @Override
    public void init() {
        System.out.println("加载" + this.getClass().getName());
    }

    @Override
    public void boot() {
        System.out.println("启动" + this.getClass().getName());
    }

    @Override
    public void shut() {
        System.out.println("关闭" + this.getClass().getName());
    }

    public void demo() {
        this.logger.info("DemoRunner working!");
    }

    public boolean checkPermission(long userId, String permission) {
        return userId == 100000L && "demo.command.demo".equals(permission);
    }

}
