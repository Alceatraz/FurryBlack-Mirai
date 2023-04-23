/*
 * Copyright (C) 2021 Alceatraz @ BlackTechStudio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the BTS Anti-Commercial & GNU Affero General.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * BTS Anti-Commercial & GNU Affero General Public License for more details.
 *
 * You should have received a copy of the BTS Anti-Commercial & GNU Affero
 * General Public License along with this program in README or LICENSE.
 */

package studio.blacktech.furryblackplus.demo;

import studio.blacktech.furryblackplus.FurryBlack;
import studio.blacktech.furryblackplus.common.Api;
import studio.blacktech.furryblackplus.core.handler.EventHandlerRunner;
import studio.blacktech.furryblackplus.core.handler.annotation.Runner;

@Api("示例定时器")

@Runner("demo-runner")
public class DemoRunner extends EventHandlerRunner {

  @Override
  public void init() {
    FurryBlack.println("加载" + this.getClass().getName());
  }

  @Override
  public void boot() {
    FurryBlack.println("启动" + this.getClass().getName());
  }

  @Override
  public void shut() {
    FurryBlack.println("关闭" + this.getClass().getName());
  }

  @Api("自定义的一个方法-1")
  public void demo() {
    this.logger.info("DemoRunner working!");
  }

  @Api("自定义的一个方法-2 检查用户权限")
  public boolean checkPermission(long userId, String permission) {
    return userId == 100000L && "demo.command.demo".equals(permission);
  }

}
