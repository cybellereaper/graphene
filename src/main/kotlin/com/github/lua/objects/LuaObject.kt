package com.github.lua.objects

import com.github.lua.PluginObject
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.Varargs
import org.luaj.vm2.lib.VarArgFunction
import org.luaj.vm2.lib.jse.CoerceJavaToLua


class LuaObject : LuaTable() {
    init {
        this["newList"] = object : VarArgFunction() {
            override fun invoke(args: Varargs): Varargs {
                val data: ArrayList<*> = ArrayList<Any?>()
                return CoerceJavaToLua.coerce(data)
            }
        } as LuaValue
        this["newFile"] = object : VarArgFunction() {
            override fun invoke(args: Varargs): Varargs {
                val data = PluginObject(args.checkjstring(1))
                return CoerceJavaToLua.coerce(data)
            }
        } as LuaValue
    }
}