package studio.blacktech.furryblackplus.bridge

import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.Bot
import net.mamoe.mirai.closeAndJoin

/**
 * Mirai 承诺 2.0时将会完全修复JvmBridge
 */

@JvmName("jvmCloseAndJoin")
fun closeAndJoin(bot: Bot) = runBlocking {
    bot.closeAndJoin(null)
}
