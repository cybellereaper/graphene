package com.github.lua.objects

import com.github.Graphene
import com.github.database.MongoStorage
import com.github.lua.events.EventRegistry.isEvent
import com.github.lua.events.EventRegistry.luaFunctions
import kotlinx.serialization.Serializable
import org.litote.kmongo.id.StringId
import org.luaj.vm2.LuaError
import org.luaj.vm2.LuaFunction
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.jse.CoerceJavaToLua

@Serializable
data class LuaScript(
    val _id: String = "example",
    val code: String = "function onEnable(a)a:hookEvent(\"BlockBreakEvent\",onBlockBreakEvent)end;function onBlockBreakEvent(b)player=b:getPlayer()player:sendMessage(\"kaboom!\")end",
) {

    fun hookEvent(event: String, function: LuaFunction) {
        if (!function.isfunction() && !event.isEvent()) return
        val eventful = event.luaFunctions() ?: return
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

        private fun enable(luaScript: LuaScript) = try {
            with(luaScript) with1@{
                with(Graphene.globals) { this.load(code).call() ?: return@with1 }
                val onEnable = getFunction(code, "onEnable")
                if (onEnable == LuaValue.NIL || onEnable == null) return@with1
                val coerceJavaToLua = CoerceJavaToLua.coerce(this)
                if (coerceJavaToLua == LuaValue.NIL || coerceJavaToLua == LuaValue.NILS) return@with1
                eventful.plusAssign(this)
                onEnable.call(coerceJavaToLua)
                println("$_id is enabled!")
            }
        } catch (e: LuaError) {
            e.printStackTrace()
        }

        private fun disable(luaScript: LuaScript) = try {
            with(luaScript, fun LuaScript.() {
                if (!eventful.contains(luaScript)) return
                println("$_id has been disabled!")
                with(Graphene.globals) { this.load(code).call() }
                val onDisable = getFunction(code, "onDisable")
                if (onDisable == LuaValue.NIL || onDisable == null) return
                val coerceJavaToLua = CoerceJavaToLua.coerce(this)
                if (coerceJavaToLua == LuaValue.NIL) return
                onDisable.call(coerceJavaToLua)
                eventful.clear()
            })
        } catch (e: LuaError) {
            e.printStackTrace()
        }

        fun enablePlugins() = luaScriptStorage.getAll()
            .map { LuaScript(it._id, it.code) }
            .forEach { enable(it) }

        fun disablePlugins() = eventful.forEach { disable(it) }
    }
}



