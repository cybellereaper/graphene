package com.github.lua.objects

import com.github.Graphene
import com.github.database.MongoStorage
import com.github.lua.events.ScriptDisableEvent
import com.github.lua.events.ScriptEnableEvent
import com.github.lua.events.EventRegistry.luaFunctions
import com.github.lua.events.EventRegistry.isEvent
import com.github.lua.objects.PluginObject.Companion.enable
import kotlinx.serialization.Serializable
import org.bukkit.plugin.java.JavaPlugin
import org.litote.kmongo.id.StringId
import org.luaj.vm2.Globals
import org.luaj.vm2.LuaError
import org.luaj.vm2.LuaFunction
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.jse.CoerceJavaToLua
import java.io.File

@Serializable
data class PluginObject(
    val _id: String = "example",
    val script: String = "function onEnable(plugin)\n" +
            "    plugin:hookEvent(\"BlockBreakEvent\", onBlockBreakEvent)\n" +
            "end\n" +
            "function onBlockBreakEvent(event) \n" +
            "    player = event:getPlayer()\n" +
            "    player:sendMessage(\"kaboom!\")\n" +
            "end "
) {
    fun hookEvent(event: String?, function: LuaFunction) {
        if (!function.isfunction() && event?.isEvent() == false) return
        val eventful =  event?.luaFunctions() ?: return
        eventful.add(function)
    }


    private fun getFunction(f: String, function: String?): LuaValue? {
        val _H: Globals = Graphene.globals
        _H.load(f).call()
//        _H["dofile"].call(LuaValue.valueOf(f.path))
        return _H[function]
    }

    companion object {
        private val plugins: HashSet<PluginObject> by lazy(::HashSet)
        private val scriptStorage = MongoStorage(PluginObject::class.java, "test", "scripts")



        fun createDefaultScript(name: String) = StringId<PluginObject>(name).also {
            scriptStorage.get(it) ?: run {
                val scriptEnt = PluginObject(name)
                scriptStorage.insertOrUpdate(it, scriptEnt)
                scriptEnt
            }
        }

        private fun JavaPlugin.enable(pluginObject: PluginObject) {
            try {
                if (pluginObject.script.isEmpty()) return
                println(pluginObject.script)
                Graphene.globals.load(pluginObject.script).call()
                val event = pluginObject.getFunction(pluginObject.script, "onEnable") ?: return
                plugins.add(pluginObject)
                event.call(CoerceJavaToLua.coerce(pluginObject))
                server.pluginManager.callEvent(ScriptEnableEvent(pluginObject))
                println("[${pluginObject._id}] is enabled!")
            } catch (e: LuaError) {
                e.printStackTrace()
            }
        }

        fun JavaPlugin.reload(pluginObject: PluginObject) {
            disable(pluginObject)
            enable(pluginObject)
        }

        private fun JavaPlugin.disable(pluginObject: PluginObject) {
            try {
                plugins.remove(pluginObject)
                println("[${pluginObject._id}] has been disabled!")
                Graphene.globals.load(pluginObject.script).call()
                val event = pluginObject.getFunction(pluginObject.script, "onDisable") ?: return
                event.call(CoerceJavaToLua.coerce(pluginObject))
                server.pluginManager.callEvent(ScriptDisableEvent(pluginObject))
            } catch (e: LuaError) {
                e.printStackTrace()
            }
        }

        fun JavaPlugin.enablePlugins() {
            scriptStorage.getAll()
                .map { PluginObject(it._id, it.script) }.
                forEach { enable(it) }
        }

//        fun JavaPlugin.enablePlugins() =
//            File("scripts").listFiles()?.map { PluginObject(it.name, it.absoluteFile) }?.forEach { enable(it) }

        fun JavaPlugin.disablePlugins() = scriptStorage.getAll().forEach { disable(it) }
    }
}



