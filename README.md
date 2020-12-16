# FurryBlackPlus

## 自豪的使用Mirai

### 介绍

没有Mirai这个世界会变成什么样？

### 这是0.0.0版本，到处都是临时代码

### 软件架构

项目继承FurryBlack的设计，但是Mirai是一个库框架而非宿主，所以FurryBlackPlus有main方法。

- Mirai的Message是工具式的而非String，Mirai作为唯一存活的框架是因为他的协议库不开源、不可反编译、不可查看，所以无法跳过extract Mirai code步骤，也不能拼接后再调用extract。所以之前的所有关于Message的设计作废，强行将消息提取以后作为String按照以前的方式处理。
- Mirai的Event比CoolQ多，而且是选择性注册，Mirai自身包含线程模型，而非纯调用库。
- 随着QQ改版，消息事件变成了：临时、好友和群。
- Mirai的可扩展性极高，所以不再将FurryBlack设计成CoolQ-JCQ版那样高扩展，严格规范的模式。  