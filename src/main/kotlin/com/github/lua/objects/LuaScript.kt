package com.github.lua.objects

import com.github.Graphene
import com.github.database.MongoStorage
import com.github.lua.events.EventRegistry.isEvent
import com.github.lua.events.EventRegistry.luaFunctions
import kotlinx.serialization.Serializable
import org.litote.kmongo.id.StringId
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
        private val luaScriptStorage = MongoStorage(LuaScript::class.java, "test", "scripts")

        internal fun getOrDefault(name: String) = StringId<LuaScript>(name).also {
            luaScriptStorage.get(it) ?: run {
                with(LuaScript(name)) {
                    luaScriptStorage.insertOrUpdate(it, this)
                    return@run this
                }
            }
        }

        private fun enable(luaScript: LuaScript): Unit = with(luaScript) with2@{
            with(Graphene.globals) { this.load(code).call() ?: return@with2 }
            val onEnable = getFunction(code, "onEnable")
            if (onEnable == LuaValue.NIL || onEnable == null) return@with2
            val coerceJavaToLua = CoerceJavaToLua.coerce(this)
            if (coerceJavaToLua == LuaValue.NIL) return@with2
            onEnable.call(coerceJavaToLua)
            println("$_id is enabled!")
        }

        private fun disable(luaScript: LuaScript): Unit = with(luaScript) with2@{
            with(Graphene.globals) { this.load(code).call() ?: return@with2 }
            val onEnable = getFunction(code, "onDisable")
            if (onEnable == LuaValue.NIL || onEnable == null) return@with2
            val coerceJavaToLua = CoerceJavaToLua.coerce(this)
            if (coerceJavaToLua == LuaValue.NIL) return@with2
            onEnable.call(coerceJavaToLua)
            println("$_id is disabled!")
        }

        fun enablePlugins() = luaScriptStorage.getAll().forEach { enable(it) }

        fun disablePlugins() = luaScriptStorage.getAll().forEach { disable(it) }
    }
}



