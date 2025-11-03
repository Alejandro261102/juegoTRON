package com.example.tron.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tron.data.*

@Composable
fun GameScreen(
    gameState: GameState,
    onDirectionChange: (Direction) -> Unit,
    onPauseClick: () -> Unit,
    onResumeClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Controles de Dirección a la Izquierda
            DirectionalControls(
                onDirectionChange = onDirectionChange,
                modifier = Modifier.weight(0.25f)
            )

            // Área de Juego (Centro)
            Column(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxHeight(), // Ocupa toda la altura
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GameHeader(gameState, onPauseClick = onPauseClick)
                GameBoard(gameState)
            }

            // Controles de Acción a la Derecha
            ActionControls(
                modifier = Modifier.weight(0.25f)
            )
        }

        // Superposición de Pausa (de la implementación anterior)
        if (gameState.isPaused) {
            PauseOverlay(onResumeClick = onResumeClick)
        }
    }
}

// Encabezado (con botón de pausa añadido)
@Composable
private fun GameHeader(gameState: GameState, onPauseClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${gameState.player1.name}: ${gameState.player1.score}",
            color = gameState.player1.color?.color ?: Color.White,
            fontSize = 14.sp // Ligeramente más pequeño
        )
        Text(
            text = "${gameState.roundTimeLeft / 60}:${(gameState.roundTimeLeft % 60).toString().padStart(2, '0')}",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Button(onClick = onPauseClick, modifier = Modifier.padding(0.dp)) { // Botón de pausa
            Text("II", color = Color.White, fontSize = 14.sp)
        }
        Text(
            text = "${gameState.player2?.name ?: "P2"}: ${gameState.player2?.score ?: 0}",
            color = gameState.player2?.color?.color ?: Color.White,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun GameBoard(gameState: GameState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight() // Ocupa el espacio restante
            .aspectRatio(gameState.gameGrid.width.toFloat() / gameState.gameGrid.height.toFloat())
            .background(Color.Black)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawGrid(gameState.gameGrid)
            // ... (Dibujado de estelas y jugadores, sin cambios)
            gameState.player2?.let {
                val color = it.color?.color ?: return@let
                drawTrail(it.trail, color, gameState.gameGrid)
                it.bikePosition?.let { pos -> drawPlayer(pos, color, gameState.gameGrid) }
            }
            gameState.player1.let {
                val color = it.color?.color ?: return@let
                drawTrail(it.trail, color, gameState.gameGrid)
                it.bikePosition?.let { pos -> drawPlayer(pos, color, gameState.gameGrid) }
            }
        }
    }
}

// --- CONTROLES DIVIDIDOS EN DOS COMPOSABLES ---

@Composable
private fun DirectionalControls(
    onDirectionChange: (Direction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { onDirectionChange(Direction.UP) }) { Text("▲") }
        Row(horizontalArrangement = Arrangement.Center) {
            Button(onClick = { onDirectionChange(Direction.LEFT) }) { Text("◀") }
            Spacer(modifier = Modifier.width(32.dp)) // Espacio para los pulgares
            Button(onClick = { onDirectionChange(Direction.RIGHT) }) { Text("▶") }
        }
        Button(onClick = { onDirectionChange(Direction.DOWN) }) { Text("▼") }
    }
}

@Composable
private fun ActionControls(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { /*TODO*/ }, enabled = false) { Text("Acel.") }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { /*TODO*/ }, enabled = false) { Text("Freno") }
    }
}


private fun DrawScope.drawGrid(grid: GameGrid) {
    val (cellWidth, cellHeight) = Pair(size.width / grid.width, size.height / grid.height)
    for (i in 0..grid.width) {
        drawLine(
            color = Color.DarkGray,
            start = Offset(i * cellWidth, 0f),
            end = Offset(i * cellWidth, size.height),
            strokeWidth = 0.5f
        )
    }
    for (i in 0..grid.height) {
        drawLine(
            color = Color.DarkGray,
            start = Offset(0f, i * cellHeight),
            end = Offset(size.width, i * cellHeight),
            strokeWidth = 0.5f
        )
    }
}

private fun DrawScope.drawTrail(trail: List<Position>, color: Color, grid: GameGrid) {
    val (cellWidth, cellHeight) = Pair(size.width / grid.width, size.height / grid.height)
    trail.forEach { pos ->
        drawRect(
            color = color.copy(alpha = 0.5f), // Trail is semi-transparent
            topLeft = Offset(pos.x * cellWidth, pos.y * cellHeight),
            size = Size(cellWidth, cellHeight)
        )
    }
}

private fun DrawScope.drawPlayer(pos: Position, color: Color, grid: GameGrid) {
    val (cellWidth, cellHeight) = Pair(size.width / grid.width, size.height / grid.height)
    drawRect(
        color = color,
        topLeft = Offset(pos.x * cellWidth, pos.y * cellHeight),
        size = Size(cellWidth, cellHeight)
    )
}

@Composable
private fun GameControls(onDirectionChange: (Direction) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { onDirectionChange(Direction.UP) }) { Text("Arriba") }
            Row {
                Button(onClick = { onDirectionChange(Direction.LEFT) }) { Text("Izquierda") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { onDirectionChange(Direction.RIGHT) }) { Text("Derecha") }
            }
            Button(onClick = { onDirectionChange(Direction.DOWN) }) { Text("Abajo") }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { /*TODO*/ }, enabled = false) { Text("Acelerador") }
            Button(onClick = { /*TODO*/ }, enabled = false) { Text("Freno") }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun GameScreenPreview() {
    val previewState = GameState(
        // ... (el código de previewState se queda igual)
        player1 = Player(
            name = "Alfredo",
            score = 3,
            color = PlayerColor.ORANGE,
            bikePosition = Position(5, 5),
            trail = listOf(Position(3, 5), Position(4, 5), Position(5, 5))
        ),
        player2 = Player(
            name = "IA",
            score = 2,
            color = PlayerColor.BLUE,
            bikePosition = Position(35, 15),
            trail = listOf(Position(37, 15), Position(36, 15), Position(35, 15))
        )
    )
    GameScreen(
        gameState = previewState,
        onDirectionChange = {},
        onPauseClick = {},
        onResumeClick = {}
    )
}

@Composable
fun PauseOverlay(onResumeClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .clickable(enabled = false, onClick = {}),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "PAUSA", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onResumeClick) {
                Text(text = "Reanudar", fontSize = 24.sp)
            }
        }
    }
}