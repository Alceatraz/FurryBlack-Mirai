# **FurryBlack - Mirai**

## 自豪的使用[Mirai](https://github.com/mamoe/mirai)

![Java CI with Maven](https://github.com/Alceatraz/FurryBlack-Mirai/workflows/Java%20CI%20with%20Maven/badge.svg?branch=master)

### 消歧义

- 白熊机器人：指BTS为FurryBlack赋予的人物形象；
- FurryBlack框架：指FurryBlack开源框架，分为JCQ和Mirai版；
- FurryBlack扩展：指基于FurryBlack框架编写的插件，
- FurryBlack机器人：指由B`TS运营的基于FurryBlack框架的机器人。

## LICENSE

- 见LICENSE

## 如何开发

框架的功能扩充使用了插件系统，用户添加的功能为模块，有四种模块类型，框架使用扫描和反射的方法统一进行生命周期管理，`studio.blacktech.furryblackplus.demo`包含示例模块代码，模块必须继承对应的模块类并填写注解。包扫描是以子类为基准进行的扫描而非注解，设计目的是防止出现无实际代码的模块。

### 模块用途

- 监视器：任何消息都会触发，在线程池中执行；
- 过滤器：过滤用户消息，可以阻止某条消息；
- 执行器：功能的核心，用于处理用户的命令；
- 定时器：通用模块，用于承载基础业务；

### 启停顺序

- 加载顺序为：机器人初始化，定时器，监听器，过滤器，执行器；
- 启动顺序为：机器人登录，定时器，监听器，过滤器，执行器，开始接收消息；
- 关闭顺序为：机器人登出，关闭消息接收，执行器，过滤器，监听器，定时器；

### 线程模型

- 主线程：启动完成后，以Mirai.bot阻塞，无限等待；
- 控制台：控制台为独立线程；
- 监听器池：所有的监听器执行，均由线程池执行`ThreadPoolExecutor`；
- 计划任务：框架提供了统一的定时任务线程池`ScheduledExecutorService`；

### 实例获取

框架禁止模块互相获取实例，强约束以保证模块的代码质量。但有例外：定时器Runner的设计目的是为了提供基础公代码，比如连接数据库、提供API接口等，则涉及到模块之间的调用。想要从IoC容器中获取模块实例必须使用class获取，禁止名称获取，其设计目的是禁止获取无代码的实例，保护其他模块不被篡改。 `Driver.getRunner(DemoRunner.class)`如果调用的代码不是与`DemoRunner.class`
一同编译，即使是同样的类名，也无法获取相同的class。

## CHANGELOG

### 0.7.4

- 更新Mirai-2.6.6

### 0.7.3

- 更新Mirai-2.6.5

### 0.7.2

- 更新GitIgnore

### 0.7.1

- 修复了一个严重的BUG

### 0.7.0

- 添加了模块优先级
- 修改了线程池
- 拆分了模块注册与加载

### 0.6.7

- 修复String.isEmpty

### 0.6.6

- 更新jline
- 强化控制台
- 启用补全功能
- 控制台解析命令时移除末尾空格

### 0.6.5

- 使用JSR310替换掉所有经典Date
- 统一时间格式化调用

### 0.6.4

- 调整昵称表系统

### 0.6.3

- 更新Mirai-2.6.4

### 0.6.2

- 更新Mirai-2.6.3

### 0.6.1

- 修复了一个很古老的控制台BUG

### 0.6.0

- 添加启动参数及其逻辑
- 添加启动后切换日志级别的系统属性
- 去掉了Systemd种init+boot的设定
- 为了避免意外发生 将init改为load实现二进制不兼容
- 美化了控制台(Mirai-Net有一项定时三秒的任务)

### 0.5.7

- 更新2.6.2

### 0.5.6-no-2.6.1

- Mirai-2.6.1只更新了mirai-console，故不跟进

### 0.5.6

- 更新Mirai-2.6.0
- 优化POM，剔除一些不需要的RT库

### 0.5.4-M

- Mirai发布了2.5.2但是有可能导致再次出现#1149所以依然保持2.6.1-M1
- 添加了出入群的日志

### 0.5.4-M

- 紧急修复Mirai#1149

### 0.5.3

- 优化代码

### 0.5.2

- 更新Mirai-2.5.1

### 0.5.1

- 更新2.5.0
- 优化代码

### 0.5.0

- 移除Kotlin
- 调整项目结构
- 只允许获取Runner模块
- 提取Demo到test
- 更新README文档
- 提取LICENSE

### 0.4.21

- 修改demo

### 0.4.20

- 添加统一的计划任务执行器

### 0.4.19

- 添加注释
- 修正DateTool

### 0.4.18

- 规范了注释和一些字段名
- 取消了Command的验证,使用者应自己负责

### 0.4.17

- 更改DateTool

### 0.4.16

- 升级Mirai-2.4.2

### 0.4.15

- 添加了注释
- 添加了final

### 0.4.14

- 升级Mirai-2.4.1

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
