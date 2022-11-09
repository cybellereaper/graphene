package com.github.lua.globals

import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.Varargs
import org.luaj.vm2.lib.VarArgFunction


internal class LuaGlobalVar2 : VarArgFunction() {
    override fun invoke(args: Varargs): Varargs {
        return LuaTable(listOf(args.checkuserdata(1) as Array<LuaValue?>))
    }
}