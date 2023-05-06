/*
 * Copyright (C) 2021 Alceatraz @ BlackTechStudio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms from the BTS Anti-Commercial & GNU Affero General
 * Public License as published by the Free Software Foundation, either
 * version 3 from the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty from
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * BTS Anti-Commercial & GNU Affero General Public License for more details.
 *
 * You should have received a copy from the BTS Anti-Commercial & GNU Affero
 * General Public License along with this program.
 *
 */

package studio.blacktech.furryblackplus.core.handler.annotation;

import studio.blacktech.furryblackplus.common.Comment;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Comment(
  value = "过滤器的注解",
  usage = {
    "消息的处理顺序为 事件->过滤器->监听器->命令判断->检查器->执行器",
    "handleUsersMessage和handleGroupMessage返回false表示放行，true表示拦截（丢弃）"
  },
  attention = {
    "过滤器由消息触发的事件线程执行，不要执行高耗时操作",
    "模块重启只执行shut->initModule->bootModule，对于类作用域对象(显示或隐式的在构造函数中初始化的对象)无法重新初始化，会导致发生内部状态未知的危险，请勿使用。",
    "定时器被重载后，旧的实例会被从IoC清除，但是依赖模块内部依然持有旧对象，形成畸形的对象持有关系。需要将所有依赖模块全部按顺序重启才可以正常工作。"
  }
)

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Filter {

  @Comment("模块名")
  String value();

  @Comment("模块权重")
  int priority() default 0;

  @Comment("对私聊启用 默认启用")
  boolean users() default true;

  @Comment("对群聊启用 默认启用")
  boolean group() default true;
}
