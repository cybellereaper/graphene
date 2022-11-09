package com.github.lua.objects

import com.github.Graphene
import com.github.database.MongoStorage
import com.github.lua.events.EventRegistry.isEvent
import com.github.lua.events.EventRegistry.luaFunctions
import com.github.lua.events.LoadScriptEvent
import kotlinx.serialization.Serializable
import org.bukkit.plugin.java.JavaPlugin
import org.litote.kmongo.id.StringId
import org.luaj.vm2.LuaError
import org.luaj.vm2.LuaFunction
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.jse.CoerceJavaToLua

@Serializable
data class Script(
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
        val eventful = event?.luaFunctions() ?: return
        eventful.add(function)
    }

    private fun getFunction(f: String, function: String?): LuaValue? {
        val globals = Graphene.globals
        globals.load(f).call()
        return globals[function]
    }

    companion object {
        private val eventful: HashSet<Script> by lazy(::HashSet)
        private val scriptStorage = MongoStorage(Script::class.java, "test", "scripts")

        fun getOrDefault(name: String) = StringId<Script>(name).also {
            scriptStorage.get(it) ?: run {
                val scriptEnt = Script(name)
                scriptStorage.insertOrUpdate(it, scriptEnt)
                scriptEnt
            }
        }

        private fun JavaPlugin.enable(script: Script) {
            try {
                if (script.script.isEmpty()) return
                println(script.script)
                Graphene.globals.load(script.script).call()
                val event = script.getFunction(script.script, "onEnable") ?: return
                eventful.add(script)
                event.call(CoerceJavaToLua.coerce(script))
                server.pluginManager.callEvent(LoadScriptEvent(script))
                println("[${script._id}] is enabled!")
            } catch (e: LuaError) {
                e.printStackTrace()
            }
        }

        fun JavaPlugin.reload(script: Script) {
            disable(script)
            enable(script)
        }

        private fun JavaPlugin.disable(script: Script) {
            try {
                eventful.remove(script)
                println("[${script._id}] has been disabled!")
                Graphene.globals.load(script.script).call()
                val event = script.getFunction(script.script, "onDisable") ?: return
                event.call(CoerceJavaToLua.coerce(script))
                server.pluginManager.callEvent(LoadScriptEvent(script))
            } catch (e: LuaError) {
                e.printStackTrace()
            }
        }

        fun JavaPlugin.enablePlugins() = scriptStorage.getAll()
            .map { Script(it._id, it.script) }
            .forEach { enable(it) }

        fun JavaPlugin.disablePlugins() = scriptStorage.getAll().forEach { disable(it) }
    }
}



