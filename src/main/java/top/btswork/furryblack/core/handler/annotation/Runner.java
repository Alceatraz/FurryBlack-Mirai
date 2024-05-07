package top.btswork.furryblack.core.handler.annotation;

import top.btswork.furryblack.core.common.annotation.Comment;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Comment(
  value = "定时器的注解",
  usage = "使用FurryBlack.getRunner()可以从IoC容器获取其实例也是唯一允许从IoC容器获取的模块类型。推荐在定时器中编写公用方法，比如权限检查、数据库连接、Socket通信服务等需要常驻或者可提取为公共的功能。",
  attention = {
    "模块重启只执行shut->initModule->bootModule，对于类作用域对象(显示或隐式的在构造函数中初始化的对象)无法重新初始化，会导致发生内部状态未知的危险，请勿使用。",
    "定时器被重载后，旧的实例会被从IoC清除，但是依赖模块内部依然持有旧对象，形成畸形的对象持有关系。需要将所有依赖模块全部按顺序重启才可以正常工作。"
  }
)

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Runner {

  @Comment("模块名")
  String value();

  @Comment("模块权重")
  int priority() default 0;

}
