package com.fluxtah.ask.api.repository

import com.fluxtah.askpluginsdk.io.getUserConfigDirectory
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object Threads : IntIdTable() {
    val threadId = varchar("thread_id", 512).index()
    val title = varchar("title", 512)
}

class Thread(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Thread>(Threads)

    var threadId by Threads.threadId
    var title by Threads.title
}

class ThreadRepository {

    init {
        val dbPath = getUserConfigDirectory().resolve("ask-api.db").absolutePath
        Database.connect("jdbc:sqlite:$dbPath", driver = "org.sqlite.JDBC")

        transaction {
            create(Threads)
        }
    }

    fun createThread(threadId: String, title: String) {
        transaction {
            Thread.new {
                this.threadId = threadId
                this.title = title
            }
        }
    }

    fun renameThread(threadId: String, newName: String) {
        transaction {
            Thread.find { Threads.threadId eq threadId }.firstOrNull()?.let {
                Threads.update({ Threads.threadId eq threadId }) {
                    it[title] = newName
                }
            }
        }
    }

    fun listThreads(): List<Thread> {
        return transaction {
            Thread.all().toList()
        }
    }

    fun getThreadById(threadId: String): Thread? {
        return transaction {
            Thread.find { Threads.threadId eq threadId }.firstOrNull()
        }
    }

    fun deleteThread(threadId: String) {
        transaction {
            Thread.find { Threads.threadId eq threadId }.firstOrNull()?.delete()
        }
    }
}
