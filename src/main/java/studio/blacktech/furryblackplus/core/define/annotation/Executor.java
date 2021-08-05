/*
 * Copyright (C) 2021 Alceatraz @ BlackTechStudio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package studio.blacktech.furryblackplus.core.define.annotation;

import studio.blacktech.furryblackplus.common.Api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


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
