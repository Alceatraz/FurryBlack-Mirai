package top.btswork.furryblack.core.handler;

import top.btswork.furryblack.core.common.annotation.Comment;
import top.btswork.furryblack.core.handler.annotation.Runner;
import top.btswork.furryblack.core.handler.common.AbstractEventHandler;

@Comment(value = "定时器父类", relativeClass = Runner.class)
public abstract class EventHandlerRunner extends AbstractEventHandler {

}
