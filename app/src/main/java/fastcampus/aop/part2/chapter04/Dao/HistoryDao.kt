package fastcampus.aop.part2.chapter04.Dao

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.Query
import fastcampus.aop.part2.chapter04.model.History

@Dao
interface HistoryDao {

    @Query("SELECT * FROM history")
    fun getAll(): List<History>

    @Insert
    fun insertHistory(history: History)

    @Query("DELETE FROM history")
    fun deleteAll()

    /*@Delete
    fun delete(history: History)

    @Query("SELECT * FROM history where result Like :result LIMIT 1")
    fun findByResult(result: String) : History*/

}