package com.github.lua.globals

import org.luaj.vm2.Varargs
import org.luaj.vm2.lib.VarArgFunction
import org.luaj.vm2.lib.jse.CoerceJavaToLua


internal class `GlobalLib$2` : VarArgFunction() {
    override fun invoke(args: Varargs): Varargs {
        return CoerceJavaToLua.coerce(args.checkjstring(1))
    }
}