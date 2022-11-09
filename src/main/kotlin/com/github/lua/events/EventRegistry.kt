package com.github.lua.events

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.plugin.java.JavaPlugin
import org.luaj.vm2.LuaFunction
import org.luaj.vm2.lib.jse.CoerceJavaToLua


object EventRegistry : Listener {
    private val events: HashMap<String, ArrayList<LuaFunction>> = HashMap()

    fun registerEvents(javaPlugin: JavaPlugin) {
        "BlockBreakEvent".register()
        Bukkit.getServer().pluginManager.registerEvents(this, javaPlugin)
    }

    fun String.isEvent(): Boolean = events.contains(this)

    fun String.luaFunctions(): ArrayList<LuaFunction>? = events[this]

    private fun String.register() {
        events.putIfAbsent(this, ArrayList())
    }

    @EventHandler
    fun onBlockBreakEvent(e: BlockBreakEvent) {
        val callbacks: ArrayList<LuaFunction> = e.eventName.luaFunctions() ?: return
        callbacks.forEach { it.call(CoerceJavaToLua.coerce(e as Any)) }
    }
}