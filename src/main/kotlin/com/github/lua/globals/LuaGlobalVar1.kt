package com.github.lua.globals

import com.github.Graphene
import org.luaj.vm2.Varargs
import org.luaj.vm2.lib.VarArgFunction
import org.luaj.vm2.lib.jse.CoerceJavaToLua


internal class LuaGlobalVar1 : VarArgFunction() {
    override fun invoke(args: Varargs): Varargs {
        val s: Class<*> = Class.forName(args.checkjstring(1), true, Graphene.classLoader)
        val coerceJavaToLua = CoerceJavaToLua.coerce(s?.getConstructor()).invoke(args.subargs(2))
        return CoerceJavaToLua.coerce(coerceJavaToLua)
    }
}