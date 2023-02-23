package com.github.lua

import com.github.Graphene
import com.github.lua.events.Event
import com.github.lua.objects.LuaObject

object LuaAPI {
    fun register() = with(Graphene.globals) {
        set("cEvent", Event)
        set("cObject", LuaObject)
    }
}