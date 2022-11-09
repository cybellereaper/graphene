package com.github.lua.globals

import org.luaj.vm2.Varargs
import org.luaj.vm2.lib.VarArgFunction
import org.luaj.vm2.lib.jse.CoerceJavaToLua


internal class LuaGlobalVar3 : VarArgFunction() {
    override fun invoke(args: Varargs): Varargs {
        return CoerceJavaToLua.coerce(LuaGlobals.listFromTable(args.checktable(1)))
    }
}