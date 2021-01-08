package studio.blacktech.furryblackplus.bridge

import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.AbstractEvent
import net.mamoe.mirai.event.broadcast
import net.mamoe.mirai.event.events.BotOfflineEvent
import net.mamoe.mirai.join


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
    fun broadcast(event: AbstractEvent) = runBlocking {
        event.broadcast()
    }

    @JvmStatic
    fun broadcastBotOfflineEvent(bot: Bot) = runBlocking {
        BotOfflineEvent.Active(bot, null).broadcast()
    }

}
