package com.github.lua.globals

import com.github.GraphenePlugin
import org.luaj.vm2.Varargs
import org.luaj.vm2.lib.VarArgFunction
import org.luaj.vm2.lib.jse.CoerceJavaToLua

class `GlobalLib$1` : VarArgFunction() {
    override fun invoke(args: Varargs?): Varargs {
        val s = Class.forName(args?.checkjstring(1), true, GraphenePlugin.classLoader)
        val coerceJavaToLua = CoerceJavaToLua.coerce(s.getConstructor())
        return CoerceJavaToLua.coerce(coerceJavaToLua).invoke(args?.subargs(2))
    }
}