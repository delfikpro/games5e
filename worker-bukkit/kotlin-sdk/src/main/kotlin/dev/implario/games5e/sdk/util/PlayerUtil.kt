package dev.implario.games5e.sdk.util

import io.netty.buffer.Unpooled
import net.minecraft.server.v1_12_R1.PacketDataSerializer
import net.minecraft.server.v1_12_R1.PacketPlayOutCustomPayload
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import java.util.*

fun Player.sendPayload(channel: String, content: PacketDataSerializer.() -> Unit = {}) =
    (this.player as CraftPlayer).handle.playerConnection.sendPacket(
        PacketPlayOutCustomPayload(channel, PacketDataSerializer(Unpooled.buffer()).also(content))
    )

class UserStats(uuid: UUID)
open class PlayerWrapper(uuid: UUID, name: String)

class User(uuid: UUID, name: String, stats: UserStats?): PlayerWrapper(uuid, name) {

    val stats = stats ?: UserStats(uuid)
    private lateinit var dungeon: Dungeon

    var state: State? = null
    set(value) {
        if (value != field)
            field?.leaveState(this, this.dungeon)
        field = value
        PrepareScoreBoard.setupScoreboard(this)
    }

}



object PrepareScoreBoard {
    fun setupScoreboard(user: User) {
        user.stats.toString() + user.state.toString()

    }
}

class State {
    fun leaveState(user: User, dungeon: Dungeon) {}
}

class Dungeon
