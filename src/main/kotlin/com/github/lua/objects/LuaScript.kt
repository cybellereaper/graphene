package com.github.lua.objects

import com.github.Graphene
import com.github.database.MongoStorage
import com.github.lua.events.EventRegistry.isEvent
import com.github.lua.events.EventRegistry.luaFunctions
import com.github.lua.events.LoadLuaScript
import kotlinx.serialization.Serializable
import org.bukkit.plugin.java.JavaPlugin
import org.litote.kmongo.id.StringId
import org.luaj.vm2.LuaError
import org.luaj.vm2.LuaFunction
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.jse.CoerceJavaToLua

@Serializable
data class LuaScript(
    val _id: String = "example",
    val code: String = "function onEnable(plugin)\n" +
            "    plugin:hookEvent(\"BlockBreakEvent\", onBlockBreakEvent)\n" +
            "end\n" +
            "function onBlockBreakEvent(event) \n" +
            "    player = event:getPlayer()\n" +
            "    player:sendMessage(\"kaboom!\")\n" +
            "end ",
    val inherit: HashSet<LuaScript> = hashSetOf(),
    var isEnabled: Boolean = false
) {
    fun hookEvent(event: String?, function: LuaFunction) {
        if (!function.isfunction() && event?.isEvent() == false) return
        val eventful = event?.luaFunctions() ?: return
        eventful.add(function)
    }

    private fun getFunction(f: String, function: String?): LuaValue? = with(Graphene.globals) {
        load(f).call()
        return this[function]
    }

    companion object {
        private val eventful: HashSet<LuaScript> by lazy(::HashSet)
        private val luaScriptStorage = MongoStorage(LuaScript::class.java, "test", "scripts")

        fun getOrDefault(name: String) = StringId<LuaScript>(name).also {
            luaScriptStorage.get(it) ?: run {
                with(LuaScript(name)) {
                    luaScriptStorage.insertOrUpdate(it, this)
                    return@run this
                }
            }
        }

        private fun JavaPlugin.enable(luaScript: LuaScript) = try {
            with(luaScript, fun LuaScript.() {
                if (code.isEmpty()) return
                with(Graphene.globals) { load(code).call() }
                val onEnable = getFunction(code, "onEnable") ?: return
                eventful.plusAssign(this)
                onEnable.call(CoerceJavaToLua.coerce(this))
                server.pluginManager.callEvent(LoadLuaScript(this))
                println("$_id is enabled!")
                isEnabled = !isEnabled
            })
        } catch (e: LuaError) {
            e.printStackTrace()
        }

        fun JavaPlugin.reload(luaScript: LuaScript) {
            disable(luaScript)
            enable(luaScript)
        }

        private fun JavaPlugin.disable(luaScript: LuaScript) = try {
            with(luaScript, fun LuaScript.() {
                if (!eventful.contains(luaScript)) return
                eventful.minusAssign(this)
                println("$_id has been disabled!")
                with(Graphene.globals) { load(code).call() }
                val onDisable = getFunction(code, "onDisable") ?: return
                onDisable.call(CoerceJavaToLua.coerce(this))
                server.pluginManager.callEvent(LoadLuaScript(this))
                isEnabled = !isEnabled
            })
        } catch (e: LuaError) {
            e.printStackTrace()
        }

        fun JavaPlugin.enablePlugins() = luaScriptStorage.getAll()
            .map { LuaScript(it._id, it.code) }
            .forEach { enable(it) }

        fun JavaPlugin.disablePlugins() = eventful.forEach { disable(it) }
    }
}



