package com.github.lua

import com.github.Graphene
import com.github.lua.events.Event
import com.github.lua.objects.LuaObject

object LuaAPI {
    fun register() {
        val globals = Graphene.globals
        globals.set("cEvent", Event)
        globals.set("cObject", LuaObject)
    }
}