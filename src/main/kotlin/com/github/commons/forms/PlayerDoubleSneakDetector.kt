package com.github.commons.forms

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.plugin.PluginManager

class PlayerDoubleSneakDetector : Listener {
    private val pluginManager: PluginManager = Bukkit.getServer().pluginManager
    private val sneak: HashMap<Player, Long> by lazy(::HashMap)

    @EventHandler(priority = EventPriority.MONITOR)
    fun onDoubleTapSneak(e: PlayerToggleSneakEvent) {
        if (!e.isSneaking) return
        val currentTime = sneak.putIfAbsent(e.player, System.currentTimeMillis()) ?: return
        val lastTime = sneak.remove(e.player) ?: return
        if (currentTime - lastTime >= 500L) return
        pluginManager.callEvent(PlayerDoubleSneakEvent(e.player))
    }
}