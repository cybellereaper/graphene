package com.github.lua.globals

import com.github.lua.PluginObject
import org.luaj.vm2.Varargs
import org.luaj.vm2.lib.VarArgFunction
import org.luaj.vm2.lib.jse.CoerceJavaToLua


internal class `GlobalLib$3` : VarArgFunction() {
    override fun invoke(args: Varargs): Varargs {
        return CoerceJavaToLua.coerce(PluginObject.createDefaultScript(args.checkjstring(1)))
    }
}