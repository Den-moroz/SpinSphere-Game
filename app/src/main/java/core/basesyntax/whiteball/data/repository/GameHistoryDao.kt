package core.basesyntax.whiteball.data.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import core.basesyntax.whiteball.data.entity.GameHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface GameHistoryDao {
    @Query("SELECT * FROM game_history ORDER BY id DESC")
    fun getAll(): Flow<List<GameHistory>>

    @Query("SELECT * FROM game_history ORDER BY points DESC LIMIT 1")
    fun getBestRecord(): Flow<GameHistory>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(gameHistory: GameHistory)
}
