# **FurryBlack - Mirai**

## 自豪的使用[Mirai](https://github.com/mamoe/mirai)

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/fcb9badd8cc1414d944c0965cba13ab5)](https://www.codacy.com/gh/Alceatraz/FurryBlack-Mirai/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=Alceatraz/FurryBlack-Mirai&amp;utm_campaign=Badge_Grade)

### 消歧义

- 白熊机器人：指BTS为FurryBlack赋予的人物形象；
- FurryBlack框架：指FurryBlack开源框架，分为JCQ和Mirai版；
- FurryBlack扩展：指基于FurryBlack框架编写的插件；
- FurryBlack机器人：指由BTS运营的基于FurryBlack框架的机器人。

### 许可证

See LICENSE

```text
           BTS Anti-Commercial GNU AFFERO GENERAL PUBLIC LICENSE

  This GNU Affero General Public License is extends from The GNU Affero General
Public License, But as Additional Terms, Here is override:

  1: Override A-GPL No.5, "I" keep power to "discriminate against any person or
group of persons.", But for one proper's and for one proper's only:
Any form commercial activity relay on this framework is forbidden, And I will
revoke the permission of "You" to see/use/modify. otherwise, If you use it in
commercial activity (framework itself or your extension or ghostwrite) You will
banned by this project, This project equivalent close source for you. Any read/
copy/use/modify/develop-base-on-it will be legal.

For example:

    If you are charged service provider, You MUST provide OpenAPI for everyone
and create wrapper by FurryBlack into QQ, Clients pay for you OpenAPI NOT the
FurryBlack or your extensions, You extensions must implements A-GPL. Must be
opensource and free to use.

  2: Downstream code/project/your-extension must annotation as BTS Anti 
Commercial GNU AFFERO GENERAL PUBLIC LICENSE, Using another LICENSE equivalent
as commercial usage. You is banned if you do it.
```

## 如何开发

### 基本概念

**FurryBlack框架使用模块扫描和事件监听提供功能。**

框架会扫描插件目录，尝试加载插件包（以JarFile读取)，并扫描模块（以JarEntry扫描，以ClassLoader加载），一个类为一个模块，注册到IoC容器中。模块系统按照功能可以分为以下五种类型：

- 定时器：通用模块，用于承载基础业务；
- 过滤器：过滤用户消息，可以阻止某条消息；
- 监视器：监听用户消息，在异步线程池中执行；
- 检查器：执行执行器之前按照指定的命令进行过滤；
- 执行器：功能的核心，用于处理用户的命令。

### HOW-TO:

- 编写插件需要继承接口并填写注解，`studio.blacktech.furryblackplus.demo`包含示例模块代码；
- 插件包必须是Jar，需要包含Manifest信息以供框架识别和加载；
- 插件包的名字写在Manifest中，和文件名无关；
- 框架会在依赖目录中查找插件同名目录，将其中文件作为依赖，创建URLCLassLoader；
- 框架会在加载插件包中的类时，再新建一个URLCLassLoader，并将依赖加载器设置为其父级；

```manifest
Manifest-Version: 1.0
Created-By: Maven Jar Plugin 3.2.0
Build-Jdk-Spec: 17

Name: furryblack
Loader-Version: 1
Extension-Name: extension-official
Extension-Author: Alceatraz-BlackTechStudio
Extension-Source: https://github.com/Alceatraz/FurryBlack-Mirai-Extensions


```

可以使用maven-jar-plugin自动完成（例子来自于[官方扩展包](https://github.com/Alceatraz/FurryBlack-Mirai-Extensions) ）：

内容：

- Loader-Version 必填，目前值必须为1
- Extension-Name 必填，插件包的名字，必须满足`^[0-9a-z_-]{8,64}$`
- Extension-Author 选填，代码不使用此字段，仅作署名
- Extension-Source 选填，代码不使用此字段，仅作署名

注意：

- 手写Manifest极其容易格式错误，除非你知道你在干什么不然不要手写
- Manifest不能填写任意字符，注意不要使用非法字符
- 注意其内容不是MainAttribute组，而是单独的furryblack标签组

如果你不知道我在说什么则请使用maven来精确无误的完成这个操作：

```xml

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-jar-plugin</artifactId>
    <version>3.2.0</version>
    <configuration>
        <archive>
            <manifestSections>
                <manifestSection>
                    <name>FurryBlack-Extension</name>
                    <manifestEntries>
                        <Loader-Version>1</Loader-Version>
                        <Extension-Name>extension-official</Extension-Name>
                        <Extension-Author>Alceatraz-BlackTechStudio</Extension-Author>
                        <Extension-Source>https://github.com/Alceatraz/FurryBlack-Mirai-Extensions</Extension-Source>
                    </manifestEntries>
                </manifestSection>
            </manifestSections>
        </archive>
    </configuration>
</plugin>
```

### 需要注意

- IoC容器仅允许获取定时器（Runner），其他模块禁止获取。

### 启停顺序

- 加载顺序为：机器人初始化，定时器，过滤器，监听器，检查器，执行器；
- 启动顺序为：机器人登录，定时器，过滤器，监听器，检查器，执行器，开始接收消息；
- 关闭顺序为：关闭消息接收，执行器，检查器，监听器，过滤器，定时器，机器人登出；

### 线程模型

- 主线程：启动完成后，主线程阻塞，无限等待；
- 控制台：控制台为独立线程；
- 监听器池：所有的监听器执行，均由线程池执行`ThreadPoolExecutor`；
- 计划任务：框架提供了统一的定时任务线程池`ScheduledThreadPoolExecutor`；

## DEVELOPING

- 无

## CHANGELOG

### 2.0.4

- 控制台颜色调节

### 2.0.3

- 执行init/boot/shut时会将插件的独有的依赖类加载器注入到CurrentThread中

### 2.0.2

- 控制台颜色调节

### 2.0.1

- 修复BUG
- 提升代码质量

### 2.0.0

- 插件二进制不兼容
- 完全重写插件模型
- 大规模变动类的包路径
- 删除插件模型的热操作功能
- 重命名主类，因为和JDBC的Driver会冲突

### 0.8.9 = 1.0.5

**回退版本号是不好的行为，但是我还是回退了，注意将本地maven缓存删除**

- 更新Java17
- 更新Mirai-2.8.0-M1

### 0.8.8

- 调整了获取昵称的接口

### 0.8.7

- 添加导出昵称

### 0.8.6

- 完善注释和`@Api`注释
- 美化代码
- 替换Hash工具类
- 添加AES和RSA的Cipher工具类
- 放宽command判定为`[a-zA-Z0-9]{2,16}`

### 0.8.5

- readFile不再trim避免破坏有些设置项目(同一行中后半段有注释的才会trim)
- 抽取模块工具类
- 控制台支持重载nickname

### 0.8.4

- 更新Mirai-2.7.0

### 0.8.3

- Revoke 0.9.x
- 更新Mirai-2.7-RC
- 去掉Hack写法 容易减寿
- 修复checker执行的一个BUG
- 修复filter启动两遍的一个BUG
- 调整plugin module schema的控制台显示
- 修复大量热重载导致的BUG
- 调整监听器和过滤器的执行顺序
- 新增检查器，用于检查命令，在执行器前运行

### 0.8.2

- 修复reload导致NPE的BUG

### 0.8.1

- 加强控制台 自动补全跟随`plugin`和`module`更新

### 0.8.0

- 内核版本 Mirai-2.7-M2
- 删除了0.8.0-SNAPSHOT的CHANGELOG
- 回退了0.8.0-SNAPSHOT的Component合并
- 提升控制台使用体验
- 将插件加载从扫描式变更为文件式(模仿Bukkit)
- 调整包结构
- 完善异常
- 使用Codacy提升代码质量

**在制造了大约4000行垃圾和18个容器之后，终于可以重载插件和模块了**
**插件和模块的热卸载重载涉及到可能破坏运行时依赖的风险 需要自行甄别插件依赖关系**

- 支持插件包热加载
- 支持插件包热卸载
- 支持模块热卸载
- 支持模块热重载

### 0.7.8-M6

- 貌似Mira更新了某种东西，没有login的时候closeAndJoin反而会开始登录

### 0.7.8-M5

- 修复不使用jline导致无法退出的问题

### 0.7.8-M4

- 添加LoggerX的功能

### 0.7.8-M3

- 修复部分烂代码

### 0.7.8-M2

- 更新[Mirai-2.7-M1](https://github.com/mamoe/mirai/releases/tag/v2.7-M1)

### 0.7.7

- 更新Mirai-2.6.7

### 0.7.6

- 提取控制台为接口
- 修复分开close/join导致的异常
- 添加了结束模式控制台，此时使用kill可以强制结束jvm（将会开发一个跳过某些模块卡住导致无法关闭的功能）
- cleanup代码

### 0.7.5

- 修复关闭线程池时不等待的问题

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
