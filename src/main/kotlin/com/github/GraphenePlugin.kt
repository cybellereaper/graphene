package com.github

import com.github.lua.EventRegistry
import com.github.lua.PluginObject
import com.github.lua.globals.GraphGlobals
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import org.luaj.vm2.Globals
import org.luaj.vm2.compiler.LuaC
import org.luaj.vm2.lib.jse.JsePlatform

class GraphenePlugin : JavaPlugin() {
    private val luaEvents: EventRegistry = EventRegistry(this)

    override fun onEnable() {
        reload()
        luaEvents.registerEvents()
    }

    override fun onDisable() {
        PluginObject.disablePlugins()
    }

    private fun reload() {
        LuaC.install(luaGlobals)
        luaGlobals.compiler = LuaC.instance
        PluginObject.disablePlugins()
        HandlerList.unregisterAll(this)
        GraphGlobals.register()
    }

    companion object {
        val luaGlobals: Globals = JsePlatform.standardGlobals()
        val classLoader: ClassLoader = Companion::class.java.classLoader

    }
}