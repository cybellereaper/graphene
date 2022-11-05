package com.github.lua

import com.github.GraphenePlugin
import com.github.commons.database.MongoStorage
import com.github.lua.events.ScriptDisableEvent
import com.github.lua.events.ScriptEnableEvent
import com.github.lua.events.commons.EventRegistry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import org.luaj.vm2.LuaError
import org.luaj.vm2.LuaFunction
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.jse.CoerceJavaToLua


@Serializable
data class PluginObject(
    @SerialName("_id") var _id: String = "Example",
    val description: String = "",
    val version: Int = 1
) {
    companion object {
        private val luaGlobals = GraphenePlugin.luaGlobals
        private val scriptStorage = MongoStorage(PluginObject::class.java, "test", "scripts")
        fun getPlugin(pluginObject: PluginObject): PluginObject? = scriptStorage.get(pluginObject._id)
        fun enablePlugins() = scriptStorage.getAll().forEach(PluginObject::enablePlugin)
        fun disablePlugins() = scriptStorage.getAll().forEach(PluginObject::disablePlugin)
    }

    private val pluginObjects: ArrayList<PluginObject> by lazy(::ArrayList)
    fun reloadPlugin() {
        disablePlugin()
        enablePlugin()
    }

    fun getFunction(path: PluginObject, func: String): LuaValue {
        luaGlobals.get("dofile").call(LuaValue.valueOf(path._id))
        return luaGlobals.get(func)
    }

    fun hookEvent(event: String, function: LuaFunction) {
        if (!function.isfunction() && !EventRegistry.isEvent(event)) return
        val functionAdd = EventRegistry.get(event) ?: return
        functionAdd.add(function)
    }

    fun disablePlugin() {
        pluginObjects.remove(this)
        val event: LuaValue = getFunction(this, "onDisable")
        if (!event.isfunction()) return
        event.call(CoerceJavaToLua.coerce(this))
        Bukkit.getServer().pluginManager.callEvent(ScriptDisableEvent(this))
    }

    fun enablePlugin() {
        try {
            val script = scriptStorage.get(_id) ?: return
            val event = script.getFunction(this, "OnEnable")
            if (!event.isfunction()) return
            event.call(CoerceJavaToLua.coerce(this))
            Bukkit.getServer().pluginManager.callEvent(ScriptEnableEvent(this))
            println("Enabling ${this._id}")
        } catch (e: LuaError) {
            println("Error while enabling ${this._id} Message:\n${e.message}")
        }
    }
}