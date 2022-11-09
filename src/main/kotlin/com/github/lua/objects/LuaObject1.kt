package com.github.lua.objects

import org.luaj.vm2.Varargs
import org.luaj.vm2.lib.VarArgFunction
import org.luaj.vm2.lib.jse.CoerceJavaToLua


internal class LuaObject1 : VarArgFunction() {
    override fun invoke(args: Varargs): Varargs {
        val data: ArrayList<*> = ArrayList<Any?>()
        return CoerceJavaToLua.coerce(data)
    }
}