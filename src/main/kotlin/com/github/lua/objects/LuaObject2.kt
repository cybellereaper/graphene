package com.github.lua.objects

import org.luaj.vm2.Varargs
import org.luaj.vm2.lib.VarArgFunction
import org.luaj.vm2.lib.jse.CoerceJavaToLua
import java.io.File
import java.util.regex.Matcher


internal class LuaObject2 : VarArgFunction() {
    override fun invoke(args: Varargs): Varargs {
        var s = args.checkjstring(1)
        val sep = "\\"
        s = s.replace("/".toRegex(), Matcher.quoteReplacement(sep))
        val data = File(s)
        return CoerceJavaToLua.coerce(data)
    }
}