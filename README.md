# **FurryBlack - Mirai**

## 自豪的使用[Mirai](https://github.com/mamoe/mirai)

![Java CI with Maven](https://github.com/Alceatraz/FurryBlack-Mirai/workflows/Java%20CI%20with%20Maven/badge.svg?branch=master)

### 消歧义

- 白熊机器人：指BTS为FurryBlack赋予的人物形象；
- FurryBlack框架：指FurryBlack开源框架，分为JCQ和Mirai版；
- FurryBlack机器人：指由BTS运营的基于FurryBlack框架的机器人。

## LICENSE

- 1: "你"，指的是正在阅读这段文字的人、或者即将运行此框架的人、或者即将对代码进行修改的人、或者二次发布的人、或者对二次发布品运营的人，只要和本项目有关，哪怕完全照着完全重写，无论是谁、是不是人、是不是碳基生物、是不是有机物，是不是一个人还是组织还是法人，一律包括在内；
- 2: "FurryBlack"字眼，拥有著作权保护，你创造的二次发布版不允许使用此名称，但是可以添加前后缀，比如FurryBlackPlus、X、Xs、Max、Pro Max等；
- 3: 白熊形象在且仅在4时以CC BY-NC-ND 4.0协议授权；
- 4: 你运营、修改、发布等任何与此项目有关的行为，必须显著的注明技术来自于FurryBlack：  
  如果是二次开发，则README中必须在开头声明使用了FurryBlack和Mirai，  
  如果是运营，则info信息必须含有本项目地址和mirai的地址，即  
  `https://github.com/mamoe/mirai`  
  `https://github.com/Alceatraz/FurryBlack-Mirai`
- 本项目使用AGPLv3协议开源，AGPLv3协议如果与以下内容有冲突，则以以下为覆盖：
- 你禁止任何形式的任何盈利行为，包括但不限于：商业用途、提供付费修改代码服务、提供付费运营服务；

## 介绍

没有Mirai这个世界会变成什么样？

## 关于2.0

Mirai正在急速开发2.0中，一旦稳定FurryBlack将迅速跟进

## 关于PR

架构和API随时会变，因为这是个人的项目，所以会完全的放飞自我，不按套路来，提PR需要慎重，极有可能PR还未提交就重构了。

## 软件架构

项目继承FurryBlack的设计，但是Mirai是一个库框架而非宿主，所以FurryBlackPlus有main方法。

- Mirai的Message是工具式的而非String，所以之前的所有关于Message的设计作废，将消息提取以后作为按照以前的方式处理。
- Mirai的Event比CoolQ多，而且是类似BukkitAPI的根据参数选择性注册，Mirai自身包含线程模型，而非纯调用库。
- 随着QQ改版，消息事件变成了：临时、好友和群，对应的，插件列表设计改为了USERS和GROUP。
- Mirai的可扩展性极高，所以不再将FurryBlack设计成CoolQ-JCQ版那样高扩展，严格规范的模式。

## 如何开发

- ComponentHandlerExecutor注解用于执行器，类需要继承自EventHandlerExecutor
- ComponentHandlerFilter注解用于过滤器，类需要继承自EventHandlerFilter
- 除非你知道你在干什么，不然绝对不要注册Mirai的EventHandler

## CHANGELOG

### 0.2.0

- 重新设计了命令解析
- 支持修改命令前缀
- 是否命令判断交给系统路由
- 命令简化为一种
- 系统路由一次性生成content无需过滤器多次执行拼接
- 修改了模块入参

### 0.1.12

- 添加Food v0.0
- 修复acon发送两遍消息的BUG
- 加强AbstractEventHandler
- 修改Chou 使用加强方法
- 修改Time 使用加强方法
- 修改UserDeny 使用加强方法
- 修改WordDeny 使用加强方法

### 0.1.11

- 紧急修复Chou的BUG

### 0.1.10

- 重写Acon

### 0.1.9

- 加强了Chou，虽然算法更合理了，但是我觉得他运行反而慢了
- 加强了BasicCommand
- 加强了Bot相关方法的穿透

### 0.1.8

- 研究了一下Lock和Condition，MiraBoot之前的写法好怪啊
- 加强控制台功能
- 加强关闭流程，添加了备用的kill

### 0.1.7

- 实现了真正的注解扫描 最终还是研究失败 使用了第三方库
- 修改了日志输出点和颜色 使其更易于阅读

### 0.1.6

- 修复Roulette，对很长的筹码判断写错了>号
- 强化Roulette，对单人模式体现更多临终关怀

### 0.1.5

- CHOU的NameCard是null，改成NickName

### 0.1.4

- 删掉JRRP中没用的代码

### 0.1.3

- 修复ECHO空消息Exception的BUG
- 修复ROLL空消息Exception的BUG
- 改进LoggerX的DUMP
- 群聊eula info help list在私聊不允许的情况下，提示消息增加try catch

### 0.1.2

- 增强控制台
- 增加重载插件的功能
- 合并模块的实力列表
- 为过滤器提供命令名注解项目
- 升级jrrp的计时器

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
