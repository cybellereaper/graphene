package com.github.database

import com.mongodb.MongoClientSettings
import com.mongodb.client.model.UpdateOptions
import org.bson.UuidRepresentation
import org.litote.kmongo.*

class MongodbContainer<T : Any>(clazz: Class<T>, databaseName: String, collectionName: String) : Container<T> {
    private val client =
        KMongo.createClient(MongoClientSettings.builder().uuidRepresentation(UuidRepresentation.STANDARD).build())
    private val database = client.getDatabase(databaseName)
    private val collection = database.getCollection(collectionName, clazz)
    override fun insertOrUpdate(id: Id<T>, entity: T) {
        collection.updateOneById(id, entity, UpdateOptions().apply { upsert(true) })
    }

    override fun get(id: Id<T>): T? = collection.findOneById(id)

    override fun getAll(): List<T> = collection.find().toList()

    override fun remove(id: Id<T>) {
        collection.deleteOneById(id)
    }

}