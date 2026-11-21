package com.moraswood.mwidashboard.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity
data class Project(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val quotedPrice: Double = 0.0,
    val wood: String = "",
    val hardware: String = "",
    val comments: String = ""
)

@Entity
data class Payment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val amount: Double,
    val date: String,
    val note: String = ""
)

@Dao
interface ProjectDao {
    @Query("SELECT * FROM Project ORDER BY id DESC")
    fun getAll(): Flow<List<Project>>

    @Insert
    suspend fun insert(project: Project): Long

    @Update
    suspend fun update(project: Project)

    @Delete
    suspend fun delete(project: Project)
}

@Dao
interface PaymentDao {
    @Query("SELECT * FROM Payment WHERE projectId = :projectId ORDER BY date DESC")
    fun getForProject(projectId: Long): Flow<List<Payment>>

    @Insert
    suspend fun insert(payment: Payment)

    @Delete
    suspend fun delete(payment: Payment)
}

@Database(entities = [Project::class, Payment::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun paymentDao(): PaymentDao
}
