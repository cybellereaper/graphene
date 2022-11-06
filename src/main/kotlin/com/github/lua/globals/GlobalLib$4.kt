package com.github.lua.globals

import org.json.simple.JSONValue
import org.luaj.vm2.Varargs
import org.luaj.vm2.lib.VarArgFunction


class `GlobalLib$4` : VarArgFunction() {
    override fun invoke(args: Varargs): Varargs? {
        return valueOf(JSONValue.toJSONString(GraphGlobals.mapOfTable(args.checktable(1)) as Any?))
    }
}