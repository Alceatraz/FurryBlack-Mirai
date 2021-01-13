package studio.blacktech.furryblackplus.bridge

import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.AbstractEvent
import net.mamoe.mirai.event.broadcast
import net.mamoe.mirai.event.events.BotOfflineEvent


/**
 * Mirai使用Kotlin编写
 * 这里提供suspend方法的runBlocking桥
 */
object MiraiBridge {


    @JvmStatic
    fun join(bot: Bot) = runBlocking {
        bot.join()
    }


    @JvmStatic
    fun close(bot: Bot) = runBlocking {
        bot.close()
    }


    /**
     * 没用
     */
    @JvmStatic
    fun broadcast(event: AbstractEvent) = runBlocking {
        event.broadcast()
    }


    /**
     * 没用
     */
    @JvmStatic
    fun broadcastBotOfflineEvent(bot: Bot) = runBlocking {
        BotOfflineEvent.Active(bot, null).broadcast()
    }

}
