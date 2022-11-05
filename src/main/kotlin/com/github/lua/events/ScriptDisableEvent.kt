package com.github.lua.events

import com.github.lua.PluginObject
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class ScriptDisableEvent(val script: PluginObject) : Event() {
    override fun getHandlers(): HandlerList = handlerList

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}