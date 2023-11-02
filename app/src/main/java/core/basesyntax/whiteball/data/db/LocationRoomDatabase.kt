package core.basesyntax.whiteball.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import core.basesyntax.whiteball.data.entity.GameHistory
import core.basesyntax.whiteball.data.repository.GameHistoryDao

@Database(entities = arrayOf(GameHistory::class), version = 3, exportSchema = false)
abstract class GameHistoryRoomDatabase: RoomDatabase() {

    abstract fun gameHistoryDao(): GameHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: GameHistoryRoomDatabase? = null

        fun getDatabase(context: Context): GameHistoryRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GameHistoryRoomDatabase::class.java,
                    "game_history__database")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
