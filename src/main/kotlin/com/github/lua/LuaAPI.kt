package com.github.lua

import com.github.GraphenePlugin
import com.github.lua.events.Event
import com.github.lua.objects.LuaObject

object LuaAPI {
    fun register() {
        val globals = GraphenePlugin.luaGlobals
        globals.set("cEvent", Event);
        globals.set("cObject", LuaObject());
//        globals["tableFromCollection"] = toJson() as LuaValue
    }

//    fun toJson(): VarArgFunction {
//        return object : VarArgFunction() {
//            override fun invoke(args: Varargs): Varargs {
//                return LuaValue.valueOf(JsonValue.toJSONString(GraphGlobals.mapOfTable(args.checktable(1)) as Any))
//            }
//        }
//    }
}