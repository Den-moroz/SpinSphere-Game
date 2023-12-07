package core.basesyntax.whiteball.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import core.basesyntax.whiteball.R
import core.basesyntax.whiteball.WhiteBallApplication
import core.basesyntax.whiteball.data.entity.GameHistory
import core.basesyntax.whiteball.presentation.navigation.Screen
import core.basesyntax.whiteball.presentation.viewmodel.GameHistoryViewModel
import core.basesyntax.whiteball.presentation.viewmodel.GameHistoryViewModelFactory

@Composable
fun GameHistoryScreen(navController: NavController) {
    val viewModel: GameHistoryViewModel = viewModel(
        factory = GameHistoryViewModelFactory((LocalContext.current.applicationContext as WhiteBallApplication).database.gameHistoryDao())
    )
    val gameHistory by viewModel.gameHistory.observeAsState(initial = emptyList())
    val bestRecord by viewModel.bestRecord.observeAsState(initial = null)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painterResource(id = R.drawable.background_page),
                contentScale = ContentScale.FillWidth
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButton(navController)

                Text(
                    text = stringResource(id = R.string.history_title),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(start = 16.dp, bottom = 16.dp, top = 10.dp)
                )
            }
            BestRecord(bestRecord)

            Spacer(modifier = Modifier.height(16.dp))

            HistoryList(gameHistory)

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun BestRecord(record: GameHistory?, modifier: Modifier = Modifier) {
    record?.let {
        val backgroundColor = Color(record.backgroundColor)
        val characterLifespan = stringResource(id = R.string.time_representation,
            record.characterLifespan / 1000 / 60,
            record.characterLifespan / 1000 % 60)
        val surfaceBackgroundColor = Color(0xFFADD8E6)

        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
                .shadow(elevation = 4.dp),
            color = surfaceBackgroundColor
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color.Yellow,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = stringResource(id = R.string.best_record),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )
                Text(
                    text = "Points: ${it.points}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Lifespan: $characterLifespan",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Color: ",
                        style = MaterialTheme.typography.bodyLarge,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(backgroundColor)
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryList(history: List<GameHistory>) {
    LazyColumn {
        items(history) { item ->
            HistoryItem(item)
        }
    }
}

@Composable
fun HistoryItem(historyItem: GameHistory) {
    val backgroundColor = Color(historyItem.backgroundColor)
    val characterLifespan = stringResource(id = R.string.time_representation,
        historyItem.characterLifespan / 1000 / 60,
        historyItem.characterLifespan / 1000 % 60)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.test),
                contentDescription = null,
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .padding(end = 15.dp)
            )
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Points: ${historyItem.points}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Lifespan: $characterLifespan",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Color: ",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(backgroundColor)
                    )
                }
            }
        }
    }
}

@Composable
fun BackButton(navController: NavController) {
    IconButton(
        onClick = {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(Screen.MainScreen.route, inclusive = true)
                .build()

            navController.navigate(Screen.MainScreen.route, navOptions)
        },
        modifier = Modifier.padding(end = 88.dp)
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = null,
            tint = Color.Black
        )
    }
}
