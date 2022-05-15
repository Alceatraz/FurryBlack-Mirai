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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Api(
    value = "检查器的注解",
    usage = {
        "消息的处理顺序为 事件->过滤器->监听器->命令判断->检查器->执行器",
        "command=*时会作为全局检查其使用，任何命令都会触发。"
    },
    attention = {
        "检查器由消息触发的事件线程执行，不要执行高耗时操作",
        "模块重启只执行shut->initModule->bootModule，对于类作用域对象(显示或隐式的在构造函数中初始化的对象)无法重新初始化，会导致发生内部状态未知的危险，请勿使用。",
        "定时器被重载后，旧的实例会被从IoC清除，但是依赖模块内部依然持有旧对象，形成畸形的对象持有关系。需要将所有依赖模块全部按顺序重启才可以正常工作。"
    }
)


@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Checker {

    @Api("模块名")
    String value();

    @Api("模块权重")
    int priority() default 0;

    @Api("对私聊启用 默认启用")
    boolean users() default true;

    @Api("对群聊启用 默认启用")
    boolean group() default true;

    @Api("指定要检查的命令 *表示任意命令")
    String command();

}
