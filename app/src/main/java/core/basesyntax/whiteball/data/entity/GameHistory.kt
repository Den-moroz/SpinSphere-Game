package core.basesyntax.whiteball.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_history")
data class GameHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val points: Int,
    val characterLifespan: Long,
    val backgroundColor: Int
)
