package com.github.lua

import com.github.GraphenePlugin
import com.github.commons.database.MongoStorage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.luaj.vm2.Globals
import org.luaj.vm2.LuaError
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.jse.JsePlatform

@Serializable
data class PluginObject(
    @SerialName("_id") var _id: String = "Example",
    val description: String = "",
    val version: Int = 1
) {
    companion object {
        private val luaGlobals = GraphenePlugin.luaGlobals
        private val scriptStorage = MongoStorage(PluginObject::class.java, "test", "scripts")
        fun getPlugin(str: String): PluginObject? = scriptStorage.get(str)
        fun enablePlugins() = scriptStorage.getAll().forEach(PluginObject::enablePlugin)
        fun disablePlugins() = scriptStorage.getAll().forEach(PluginObject::disablePlugin)
    }

    private val pluginObjects: ArrayList<PluginObject> by lazy(::ArrayList)

    fun reloadPlugin() {
        disablePlugin()
        enablePlugin()
    }

    fun getFunction(path: String, func: String): LuaValue {
        luaGlobals.get("database").call(LuaValue.valueOf(path))
        return luaGlobals.get(func)
    }

    fun disablePlugin() {
        val removePlugin = pluginObjects.remove(this)
        println(removePlugin)
    }
    fun enablePlugin() {
        try {

        } catch (e: LuaError) {
            println(e.message)
        }
    }
}