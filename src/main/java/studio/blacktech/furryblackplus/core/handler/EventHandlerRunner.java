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

package studio.blacktech.furryblackplus.core.handler;

import studio.blacktech.furryblackplus.core.common.annotation.Comment;
import studio.blacktech.furryblackplus.core.handler.annotation.Runner;
import studio.blacktech.furryblackplus.core.handler.common.AbstractEventHandler;

@Comment(value = "定时器父类", relativeClass = Runner.class)
public abstract class EventHandlerRunner extends AbstractEventHandler {

}
