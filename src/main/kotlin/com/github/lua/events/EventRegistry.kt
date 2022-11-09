package com.github.lua.events

import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import org.luaj.vm2.LuaFunction
import org.luaj.vm2.lib.jse.CoerceJavaToLua


object EventRegistry : Listener {
    private val events: HashMap<String, ArrayList<LuaFunction>> = HashMap()

    fun registerEvents(javaPlugin: JavaPlugin) {
        SpigotEvents.values().forEach { it.name.register() }
        Bukkit.getServer().pluginManager.registerEvents(this, javaPlugin)
    }

    fun String.isEvent(): Boolean = events.contains(this)

    fun String.luaFunctions(): ArrayList<LuaFunction>? = events[this]

    private fun String.register() {
        events.putIfAbsent(this, ArrayList())
    }

    private fun String.call(e: Event) {
        val callbacks = this.luaFunctions() ?: return
        callbacks.forEach { it.call(CoerceJavaToLua.coerce(e as Any)) }
    }

    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent) {
        e.eventName.call(e)
    }

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        e.eventName.call(e)
    }

    @EventHandler
    fun onBlockBreakEvent(e: BlockBreakEvent) {
        e.eventName.call(e)

    }

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        if (e.clickedInventory == null) return
        e.eventName.call(e)
    }

    @EventHandler
    fun onInventoryOpen(e: InventoryOpenEvent) {
        e.eventName.call(e)
    }

    @EventHandler
    fun onInventoryClose(e: InventoryCloseEvent) = e.eventName.call(e)
}