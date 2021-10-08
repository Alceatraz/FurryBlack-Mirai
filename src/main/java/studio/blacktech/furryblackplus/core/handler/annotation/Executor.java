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


package studio.blacktech.furryblackplus.core.handler.annotation;


import studio.blacktech.furryblackplus.common.Api;
import studio.blacktech.furryblackplus.core.handler.common.Command;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Api(
    value = "执行器的注解",
    usage = "消息的处理顺序为 事件->过滤器->监听器->命令投递->检查器->执行器。",
    attention = {
        "执行器由消息触发的事件线程执行，不要执行高耗时操作。",
        "command名称需要遵循^?[a-zA-Z0-9]{2,16}规则(?代表设置的命令前缀)，不符合此规则的消息不会被认定是命令，不会进行执行流程。",
        "模块重启只执行shut->init->boot，对于类作用域对象(显示或隐式的在构造函数中初始化的对象)无法重新初始化，会导致发生内部状态未知的危险，请勿使用。",
        "定时器被重载后，旧的实例会被从IoC清除，但是依赖模块内部依然持有旧对象，形成畸形的对象持有关系。需要将所有依赖模块全部按顺序重启才可以正常工作。"
    },
    relativeClass = Command.class
)


@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Executor {

    @Api("模块名 全局唯一")
    String value();

    @Api("模块功能 控制在八个字以内")
    String outline();

    @Api("模块介绍 详细介绍")
    String description();

    @Api("模块注册的命令")
    String command();

    @Api("模块命令的用法")
    String[] usage();

    @Api("插件的隐私权限")
    String[] privacy() default {"不需要任何权限"};

    @Api("模块是否私聊可用")
    boolean users() default true;

    @Api("模块是否群聊可用")
    boolean group() default true;

}
