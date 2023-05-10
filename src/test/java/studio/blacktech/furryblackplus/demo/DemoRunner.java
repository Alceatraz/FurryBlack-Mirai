/*
 * Copyright (C) 2021 Alceatraz @ BlackTechStudio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms from the BTS Anti-Commercial & GNU Affero General.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty from
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * BTS Anti-Commercial & GNU Affero General Public License for more details.
 *
 * You should have received a copy from the BTS Anti-Commercial & GNU Affero
 * General Public License along with this program in README or LICENSE.
 */

package studio.blacktech.furryblackplus.demo;

import studio.blacktech.furryblackplus.core.common.annotation.Comment;
import studio.blacktech.furryblackplus.core.handler.EventHandlerRunner;
import studio.blacktech.furryblackplus.core.handler.annotation.Runner;

@Comment("示例定时器")

@Runner("demo-runner")
public class DemoRunner extends EventHandlerRunner {

  @Override
  public void init() {
    logger.info("加载" + this.getClass().getName());
  }

  @Override
  public void boot() {
    logger.info("启动" + this.getClass().getName());
  }

  @Override
  public void shut() {
    logger.info("关闭" + this.getClass().getName());
  }

  @Comment("自定义的一个方法-1")
  public void demo() {
    this.logger.info("DemoRunner working!");
  }

  @Comment("自定义的一个方法-2 检查用户权限")
  public boolean checkPermission(long userId, String permission) {
    return userId == 100000L && "demo.command.demo".equals(permission);
  }

}
