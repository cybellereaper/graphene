package com.github.lua.objects

import com.github.Graphene
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.Varargs
import org.luaj.vm2.lib.VarArgFunction
import org.luaj.vm2.lib.jse.CoerceJavaToLua


object LuaGlobals {
    fun register() {
        val globals = Graphene.globals
        globals["listFromTable"] = listFromTable
        globals["tableFromValues"] = tableFromValues
        globals["newInstance"] = newInstance
    }

    private val tableFromValues by lazy {
        return@lazy object : VarArgFunction() {
            override fun invoke(args: Varargs): LuaTable =
                LuaTable(listOf(args.checkuserdata(1) as Array<out LuaValue>))
        }
    }

    private val listFromTable by lazy {
        return@lazy object : VarArgFunction() {
            override fun invoke(args: Varargs): Varargs = CoerceJavaToLua.coerce(listFromTable(args.checktable(1)))
        }
    }

    fun listFromTable(luaTable: LuaTable): List<*> {
        val map = HashMap<String, Any>()
        luaTable.keys()
            .map { luaTable[it] }.forEach {
                when {
                    it.isboolean() -> map[it.tojstring()] = it.toboolean()
                    it.isint() -> map[it.tojstring()] = it.toint()
                    it.isnumber() -> map[it.tojstring()] = it.tonumber()
                    it.istable() -> map[it.tojstring()] = it.istable()
                    it.isstring() -> map[it.tojstring()] = it.isstring()
                }
            }
        return listOf(map.values)
    }

    private val newInstance by lazy {
        return@lazy object : VarArgFunction() {
            override fun invoke(args: Varargs): Varargs {
                val s: Class<*> = Class.forName(args.checkjstring(1), true, Graphene.classLoader)
                val varargs = CoerceJavaToLua.coerce(s.getConstructor()).invoke(args.subargs(2))
                return CoerceJavaToLua.coerce(varargs)
            }
        }
    }

    fun mapOfTable(luaTable: LuaTable): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        luaTable.keys()
            .map { luaTable[it.tojstring()] }
            .forEach {
                when {
                    it.isboolean() -> map[it.tojstring()] = it.toboolean()
                    it.isint() -> map[it.tojstring()] = it.isint()
                    it.isnumber() -> map[it.tojstring()] = it.todouble()
                    it.istable() -> mapOfTable(it.checktable())
                    it.isstring() -> map[it.tojstring()] = it.tojstring()
                }
            }
        return map
    }

    internal class LuaGlobalVar1 : VarArgFunction() {
        override fun invoke(args: Varargs): Varargs {
            val s: Class<*> = Class.forName(args.checkjstring(1), true, Graphene.classLoader)
            val coerceJavaToLua = CoerceJavaToLua.coerce(s.getConstructor()).invoke(args.subargs(2))
            return CoerceJavaToLua.coerce(coerceJavaToLua)
        }
    }

    internal class LuaGlobalVar2 : VarArgFunction() {
        override fun invoke(args: Varargs): Varargs = LuaTable(listOf(args.checkuserdata(1) as Array<LuaValue?>))
    }

    internal class LuaGlobalVar3 : VarArgFunction() {
        override fun invoke(args: Varargs): Varargs = CoerceJavaToLua.coerce(listFromTable(args.checktable(1)))
    }
}