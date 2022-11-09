package com.github.lua.events

import com.github.lua.objects.PluginObject
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class ScriptEnableEvent(val script: PluginObject) : Event() {
    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }
}