package com.github.lua.events

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.Varargs
import org.luaj.vm2.lib.VarArgFunction


object Event : LuaTable() {
    init {
        (VarArgFunction1() as LuaValue).also { this["callCommand"] = it }
        (VarArgFunction2() as LuaValue).also { this["callConsoleCommand"] = it }
    }

    internal class VarArgFunction2 : VarArgFunction() {
        override fun invoke(args: Varargs): Varargs {
            Bukkit.dispatchCommand(
                Bukkit.getConsoleSender() as CommandSender,
                args.checkjstring(1) as String
            )
            return NIL
        }
    }

    internal class VarArgFunction1 : VarArgFunction() {
        override fun invoke(args: Varargs): Varargs {
            val data = args.checkuserdata(1) as Player
            data.performCommand(args.checkjstring(2))
            return NIL
        }
    }
}