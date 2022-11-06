package com.github.lua

import com.github.GraphenePlugin
import com.github.commons.database.MongoStorage
import com.github.lua.events.ScriptDisableEvent
import com.github.lua.events.ScriptEnableEvent
import com.github.lua.events.commons.EventRegistry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import org.litote.kmongo.id.StringId
import org.luaj.vm2.LuaError
import org.luaj.vm2.LuaFunction
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.jse.CoerceJavaToLua


@Serializable
data class PluginObject(
    @SerialName("_id") var _id: String = "Example",
    val description: String = "",
    val version: Int = 1,
    val pluginObjects: ArrayList<PluginObject> = arrayListOf()
) {
    companion object {
        private val luaGlobals = GraphenePlugin.luaGlobals
        private val scriptStorage = MongoStorage(PluginObject::class.java, "test", "scripts")

        fun createDefaultScript(name: String) = StringId<PluginObject>(name).also {
            scriptStorage.get(it) ?: run {
                val scriptEnt = PluginObject(name)
                scriptStorage.insertOrUpdate(it, scriptEnt)
                scriptEnt
            }
        }

        fun getPlugin(pluginObject: PluginObject): PluginObject? = scriptStorage.get(StringId(pluginObject._id))
        fun enablePlugins() = scriptStorage.getAll().forEach(PluginObject::enablePlugin)
        fun disablePlugins() = scriptStorage.getAll().forEach(PluginObject::disablePlugin)
    }

    fun reloadPlugin() {
        disablePlugin()
        enablePlugin()
    }

    fun getFunction(func: String): LuaValue {
        luaGlobals.get("dofile").call(LuaValue.valueOf(this._id))
        return luaGlobals.get(func)
    }

    fun hookEvent(event: String, function: LuaFunction) {
        if (!function.isfunction() && !EventRegistry.isEvent(event)) return
        val functionAdd = EventRegistry.get(event) ?: return
        functionAdd.add(function)
    }

    fun disablePlugin() {
        pluginObjects.remove(this)
        val event: LuaValue = getFunction("onDisable")
        if (!event.isfunction()) return
        event.call(CoerceJavaToLua.coerce(this))
        Bukkit.getServer().pluginManager.callEvent(ScriptDisableEvent(this))
    }

    fun enablePlugin() {
        try {
            val script = scriptStorage.get(StringId(_id)) ?: return
            val pluginObject = PluginObject()
            script.pluginObjects.add(pluginObject)
            scriptStorage.insertOrUpdate(StringId(_id), pluginObject)
            val event = script.getFunction("OnEnable")
            if (!event.isfunction()) return
            event.call(CoerceJavaToLua.coerce(this))
            Bukkit.getServer().pluginManager.callEvent(ScriptEnableEvent(this))
            println("Enabling ${this._id}")
        } catch (e: LuaError) {
            println("Error while enabling ${this._id} Message:\n${e.message}")
        }
    }
}