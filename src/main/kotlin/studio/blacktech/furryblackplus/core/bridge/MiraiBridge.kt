package studio.blacktech.furryblackplus.core.bridge

import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.Bot
import studio.blacktech.furryblackplus.core.annotation.Api


object MiraiBridge {


    @JvmStatic
    @Api("以Bot阻塞")
    fun join(bot: Bot) = runBlocking {
        bot.join();
    }


    @JvmStatic
    @Api("关闭Bot")
    fun shut(bot: Bot) = runBlocking {
        bot.close(null)
    }


}
