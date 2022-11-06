package com.github

import com.github.lua.LuaAPI
import com.github.lua.PluginObject
import com.github.lua.events.commons.EventRegistry
import com.github.lua.globals.GraphGlobals
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import org.luaj.vm2.Globals
import org.luaj.vm2.compiler.LuaC
import org.luaj.vm2.lib.jse.JsePlatform
import java.lang.System.setProperty

class GraphenePlugin : JavaPlugin() {
    override fun onEnable() {
        setProperty(
            "org.litote.mongo.test.mapping.service",
            "org.litote.kmongo.jackson.JacksonClassMappingTypeService"
        )
        reload()
        EventRegistry.registerEvents(this)
        LuaAPI.register()
    }

    override fun onDisable() {
        PluginObject.disablePlugins()
    }

    private fun reload() {
        PluginObject.createDefaultScript("test")
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