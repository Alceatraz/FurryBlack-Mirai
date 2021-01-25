package studio.blacktech.furryblackplus.core.bridge

import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.Bot


/**
 * Mirai使用Kotlin编写
 * 这里提供suspend方法的runBlocking桥
 */
object MiraiBridge {


    @JvmStatic
    fun join(bot: Bot) = runBlocking {
        bot.join();
    }


    @JvmStatic
    fun shut(bot: Bot) = runBlocking {
        bot.closeAndJoin(null)
    }


}
