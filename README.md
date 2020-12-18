# FurryBlack - Mirai

**自豪的使用Mirai**

## LICENSE

- "你"，指的是正在阅读这段文字、即将对代码进行修改、二次发布、运营的人，只要和本项目有关，无论是谁、是不是人、是不是有机物，一律包括在内；
- 本项目使用AGPLv3协议开源，AGPLv3协议如果与以下内容有冲突，则以以下为覆盖：
- 此项目以及其衍生项目，禁止任何形式的任何盈利行为，包括但不限于，提供付费服务和用于商业用途；
- 不允许"你"使用FurryBlack你运营机器人或者衍生品及其运营的宣传字样，"FurryBlack"不属于开源部分；
- 不允许"你"使用白熊管家作为你运营机器人或者衍生品及其运营的艺术形象，"白熊管家"不属于开源部分；

### 消歧义

- 白熊机器人：指BTS为FurryBlack赋予的人物形象；
- FurryBlack框架：指FurryBlack开源框架，分为JCQ和Mirai版；
- FurryBlack机器人：指由BTS运营的基于FurryBlack框架的机器人。

## 介绍

没有Mirai这个世界会变成什么样？

## 关于PR

架构和API随时会变，因为这是个人的项目，所以会完全的放飞自我，不按套路来，提PR需要慎重，极有可能PR还未提交就重构了。

## 软件架构

项目继承FurryBlack的设计，但是Mirai是一个库框架而非宿主，所以FurryBlackPlus有main方法。

- Mirai的Message是工具式的而非String，Mirai作为唯一存活的框架是因为他的协议库不开源、不可反编译、不可查看，所以无法跳过extract Mirai code步骤，也不能拼接后再调用extract。所以之前的所有关于Message的设计作废，强行将消息提取以后作为String按照以前的方式处理。
- Mirai的Event比CoolQ多，而且是类似BukkitAPI的根据参数选择性注册，Mirai自身包含线程模型，而非纯调用库。
- 随着QQ改版，消息事件变成了：临时、好友和群，对应的，插件列表设计改为了USERS和GROUP。
- Mirai的可扩展性极高，所以不再将FurryBlack设计成CoolQ-JCQ版那样高扩展，严格规范的模式。

## 如何开发

- ComponentHandlerExecutor注解用于执行器，类需要继承自EventHandlerExecutor
- ComponentHandlerFilter注解用于过滤器，类需要继承自EventHandlerFilter
- 除非你知道你在干什么，不然绝对不要注册Mirai的EventHandler

## CHANGELOG

### 0.1.1

- 注解增加了users/group的标志位，用于控制是否注册
- 添加time命令
- 添加词组过滤功能
- 为info添加实际功能
- 为eula添加实际功能
- 为help添加实际功能
- 完善LICENSE

### 0.1.0

- 增加了注解+反射式插件注册
- 添加私聊模式
- 支持了过滤器
- 编写了用户过滤模块

### 0.0.0

- 勉强能用的版本