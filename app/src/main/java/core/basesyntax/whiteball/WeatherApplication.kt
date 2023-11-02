package core.basesyntax.whiteball

import android.app.Application
import core.basesyntax.whiteball.data.db.GameHistoryRoomDatabase

class WhiteBallApplication : Application() {
    val database: GameHistoryRoomDatabase by lazy { GameHistoryRoomDatabase.getDatabase(this) }
}
