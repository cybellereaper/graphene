package com.github.commons.database

import org.litote.kmongo.Id

interface Storage<T : Any> {
    fun insertOrUpdate(id: Id<T>, entity: T)
    fun get(id: Id<T>): T?
    fun getAll(): List<T>
    fun remove(id: Id<T>)
}