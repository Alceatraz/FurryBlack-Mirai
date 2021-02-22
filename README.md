# **FurryBlack - Mirai**

## 自豪的使用[Mirai](https://github.com/mamoe/mirai)

![Java CI with Maven](https://github.com/Alceatraz/FurryBlack-Mirai/workflows/Java%20CI%20with%20Maven/badge.svg?branch=master)

### 消歧义

- 白熊机器人：指BTS为FurryBlack赋予的人物形象；
- FurryBlack框架：指FurryBlack开源框架，分为JCQ和Mirai版；
- FurryBlack机器人：指由BTS运营的基于FurryBlack框架的机器人。

## LICENSE

- 本项目使用BTS协议开源，请认真阅读并理解LICENSE；
- 你除了查看代码外的一切行为比如明确注明本框架的存在，不得扭曲、隐藏、弱化FurryBlack和Mirai的本质；
- 作为使用、运营着者：你不被允许修改任何代码，在你的运行实体中，Info功能必须表明技术来自于FurryBlack和Mirai；
- 作为开发、二次开发者：你被允许修改代码，必须在README中明确声明：  
  `https://github.com/mamoe/mirai`  
  `https://github.com/Alceatraz/FurryBlack-Mirai`
  且Info功能必须明确表明由FurryBlack二次开发，以及Mirai的存在；
- 白熊机器人和FurryBlack包含著作权，你不得使用，但：
- 在你的使用、运营、开发、二次开发的过程中，允许以CC4.0 BY NC ND 协议使用，且仅可用于介绍FurryBlack

## 关于PR

FurryBlack和全功能框架的最大区别在于，极度强化文本处理，所以发展方向并不符合大众口味，建议Fork。

## 软件架构

项目继承FurryBlackJCQ的设计，但是Mirai是一个库框架而非宿主，所以FurryBlackPlus有main方法。

- 暂时不考虑用kotlin写
- Java更新到11
- Mirai的Event比CoolQ多，而且是类似BukkitAPI的根据参数选择性注册，Mirai自身包含线程模型回调注册的Handler，而非被动库
- Mirai的Message是工具式的而非String，但是FurryBlack极度强化文本处理，所以使用了toContentString按照一半文本处理
- 随着QQ改版，消息事件变成了私聊和群组，对应的事件是UserMessageEvent和GroupMessageEvent，所以插件列表设计改为了USERS和GROUP
- Mirai的封装比Jcq高级，优雅的继承方便的实现了统一处理事件（即? extends XXX)
- 强化后的插件注册机制，使用注解携带信息，而不是JCQ版的笨拙final String
- 全面使用面向对象，CoolQ是32位的，Mirai则可以64位，而且完全自行编写完全不需要考虑任何性能与内存问题
- 全面使用parallelStream，提升过滤器效率
- Mirai可以自行编写所有代码，不再需要/admin功能，所有管理命令都可从shell执行
- 抛弃了所有统计功能

## 如何开发

- `ComponentHandlerFilter`注解用于过滤器，类需要继承自`EventHandlerFilter`
- `ComponentHandlerExecutor`注解用于执行器，类需要继承自`EventHandlerExecutor`
- 除非你知道你在干什么，不然绝对不要注册Mirai的EventHandler
- 发送消息务必使用Driver中的方法，而不是直接调用Contract对象，实现统一管理

## CHANGELOG

### 0.4.13

- 修改Systemd初始化参数
- 把Mirai的cache文件夹藏到config中

### 0.4.12

- 升级Mirai-2.4.0

### 0.4.11

- 有些时候无法收到好友邀请 很怪

### 0.4.10

- 修复list无参数抛出异常的bug

### 0.4.9

- 更新Mirai 2.3.2
- 优化代码

### 0.4.8

- 更新Mirai 2.3.1

### 0.4.7

- 增加Debug开关

### 0.4.6

- 更新Mirai 2.2.2

### 0.4.5

- 添加API方法
- 微调格式化

### 0.4.4

- 修复Console的Bug
- 修复反射库扫描不到任何东西时会报错

### 0.4.3

- Mirai 2.1.1发布修复了无限重连BUG

### 0.4.2

- Mirai 2.1.0提供了用户Profile查询

### 0.4.1

- Mirai释出了 2.1.0
- 添加了自定义昵称表
- 支持获取模块实例(要是有友元就不用这么搞了)

### 0.4.0

- Mirai释出了2.0.0
- 调整项目结构 分离框架和模块
- 调整包结构
- 提供了Demo示例模块
- 调整线程模型
- 规范异常类型
- 修改了Handler的类型 变为 好友/临时/陌生人 三合一
- 统一了发送消息的工具类
- 修改了模块注册逻辑
- 添加了INFO的新字段，以替代ClassName
- 调整了模块的工具方法
- 添加了经典监听器和定时器
- 添加了Shell的一些命令
- 去掉了传入的contentToString()因为Kotlin的val lazy有缓存

### 0.3.0-RC

- RC是因为Mirai2.0仍处于RC阶段
- 参数修改为--no-jline
- Driver 删除Kill使用Exit(-1)代替
- Systemd 修改了实例化BOT和注册事件的新方式
- Systemd 完全抛弃了之前Mirai-Boot的写法 因为没有必要, FurryBlack本身会有主线程阻塞
- AbstractEventHandler 修改了参数类型 GroupTempMessageEvent
- 修改所有使用At的代码

### 0.2.4

- 调整了POM文件
- 修改了开源协议！

### 0.2.3

- 增强chou的配置读取 裁剪掉多余空格
- 增强userDeny的配置读取 裁剪掉多余空格

### 0.2.2

- 修复0.2.0引入的/info /eula的BUG
- 发现Mirai的BUG

### 0.2.1

- 修复Dark的潜在bug

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
