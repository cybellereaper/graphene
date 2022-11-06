package com.github.lua.events.commons

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import org.luaj.vm2.LuaFunction
import org.luaj.vm2.lib.jse.CoerceJavaToLua


object EventRegistry : Listener {
    private val events: HashMap<String, ArrayList<LuaFunction>> by lazy(::HashMap)

    init {
        Events.values().forEach { it.name.register() }
    }

    fun registerEvents(javaPlugin: JavaPlugin) {
        Bukkit.getServer().pluginManager.registerEvents(this, javaPlugin)
    }

    fun isEvent(string: String): Boolean = events.contains(string)

    fun get(string: String): ArrayList<LuaFunction>? = events[string]

    private fun String.register() {
        events.putIfAbsent(this, ArrayList())
    }

    @EventHandler
    fun onEvent(e: PlayerJoinEvent) {
        val callbacks: ArrayList<LuaFunction> = events[e.eventName] ?: return
        callbacks.forEach {
            it.call(CoerceJavaToLua.coerce(e as Any))
        }
    }

}