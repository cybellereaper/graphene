package com.github.lua.objects

import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.Varargs
import org.luaj.vm2.lib.VarArgFunction
import org.luaj.vm2.lib.jse.CoerceJavaToLua
import java.io.File
import java.util.regex.Matcher


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
                var s = args.checkjstring(1)
                val sep = "\\"
                s = s.replace("/".toRegex(), Matcher.quoteReplacement(sep))
                val data = File(s)
                return CoerceJavaToLua.coerce(data)
            }
        } as LuaValue
//        this["newYaml"] = object : VarArgFunction() {
//            override fun invoke(args: Varargs): Varargs {
//                val data = YamlFile(args.checkjstring(1))
//                return CoerceJavaToLua.coerce(data)
//            }
//        } as LuaValue
    }
}