package com.github.lua.events

import com.github.lua.objects.LuaScript
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class LoadLuaScript(val luaScript: LuaScript) : Event() {
    var isEnabled: Boolean = false

    init {
        isEnabled = !isEnabled
    }

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }

    override fun getHandlers() = handlerList
}