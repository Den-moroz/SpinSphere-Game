package core.basesyntax.whiteball.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import core.basesyntax.whiteball.data.entity.GameHistory
import core.basesyntax.whiteball.data.repository.GameHistoryDao
import kotlinx.coroutines.launch

class GameHistoryViewModel(private val gameHistoryDao: GameHistoryDao) : ViewModel() {
    val gameHistory: LiveData<List<GameHistory>> = gameHistoryDao.getAll().asLiveData()

    val bestRecord: LiveData<GameHistory> = gameHistoryDao.getBestRecord().asLiveData()

    fun insert(gameHistory: GameHistory) {
        viewModelScope.launch {
            gameHistoryDao.insert(gameHistory)
        }
    }
}

class GameHistoryViewModelFactory(private val gameHistoryDao: GameHistoryDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameHistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameHistoryViewModel(gameHistoryDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
