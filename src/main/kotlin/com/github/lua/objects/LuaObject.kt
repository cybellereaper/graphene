package com.github.lua.objects

import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.Varargs
import org.luaj.vm2.lib.VarArgFunction
import org.luaj.vm2.lib.jse.CoerceJavaToLua
import java.io.File
import java.util.regex.Matcher


object LuaObject : LuaTable() {
    init {
        (VarArgFunction1() as LuaValue).also { this["newFile"] = it }
        (VarArgFunction2() as LuaValue).also { this["newList"] = it }
    }

    internal class VarArgFunction2 : VarArgFunction() {
        override fun invoke(args: Varargs): Varargs = CoerceJavaToLua.coerce(arrayListOf<Any?>())
    }

    internal class VarArgFunction1 : VarArgFunction() {
        override fun invoke(args: Varargs): Varargs {
            var raw = args.checkjstring(1)
            raw = raw.replace("/".toRegex(), Matcher.quoteReplacement("\\"))
            val data = File(raw)
            return CoerceJavaToLua.coerce(data)
        }
    }
}