package com.github.lua.events

import com.github.lua.objects.Script
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class LoadScriptEvent(val script: Script) : Event() {
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