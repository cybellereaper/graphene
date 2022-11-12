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

        private fun LuaScript.callFunc(fn: String) {
            with(Graphene.globals) { this.load(code).call() ?: return }
            val onFunc = getFunction(code, fn) ?: return
            if (onFunc == LuaValue.NIL) return
            val coerceJavaToLua = CoerceJavaToLua.coerce(this) ?: return
            if (coerceJavaToLua == LuaValue.NIL) return
            onFunc.call(coerceJavaToLua)
            println("$_id is enabled!")
        }

        fun enablePlugins() = luaScriptStorage.getAll().forEach { it.callFunc("onEnable") }

        fun disablePlugins() = luaScriptStorage.getAll().forEach { it.callFunc("onDisable") }
    }
}



