package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme

// =====================================================================
// ACTIVIDAD PRINCIPAL
// =====================================================================
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // setContent reemplaza al antiguo setContentView(R.layout...) de XML
        setContent {
            MyApplicationTheme {
                // Scaffold ocupa toda la pantalla y nos entrega un padding seguro
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ThermostatScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// =====================================================================
// COMPONENTE PADRE (Stateful): dueño del estado de la temperatura
// =====================================================================
@Composable
fun ThermostatScreen(modifier: Modifier = Modifier) {
    // Estado: temperatura inicial de 20 °C.
    // remember -> conserva el valor entre recomposiciones.
    // mutableIntStateOf -> al cambiar, Compose redibuja la interfaz automáticamente.
    var temperatura by remember { mutableIntStateOf(20) }

    // Column centrada que organiza a los dos componentes hijos
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Termostato Inteligente",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        // STATE HOISTING: el estado BAJA al hijo como parámetro
        TemperatureDisplay(temperatura = temperatura)

        Spacer(modifier = Modifier.height(32.dp))

        // STATE HOISTING: los eventos SUBEN al padre mediante lambdas
        ThermostatControls(
            onIncrease = { temperatura++ },
            onDecrease = { temperatura-- }
        )
    }
}

// =====================================================================
// HIJO 1 (Stateless): solo muestra la temperatura recibida
// =====================================================================
@Composable
fun TemperatureDisplay(temperatura: Int) {
    // Lógica declarativa: según la temperatura se elige ícono, color y descripción
    val esCalor = temperatura >= 25
    val icono = if (esCalor) IconoSol else IconoCopoNieve
    val colorIcono = if (esCalor) Color.Red else Color.Blue
    val descripcion = if (esCalor) "Calor" else "Frío"

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icono,
            contentDescription = descripcion, // Accesibilidad para lectores de pantalla
            tint = colorIcono,
            modifier = Modifier.size(96.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "$temperatura °C",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = descripcion,
            fontSize = 18.sp,
            color = colorIcono
        )
    }
}

// =====================================================================
// HIJO 2 (Stateless): botones que solo notifican eventos al padre
// =====================================================================
@Composable
fun ThermostatControls(
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Botón Bajar: ejecuta la lambda recibida del padre
        Button(
            onClick = onDecrease,
            modifier = Modifier.height(48.dp) // Área táctil cómoda (≥ 44 dp)
        ) {
            Text(text = "Bajar", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.width(24.dp))

        // Botón Subir: ejecuta la lambda recibida del padre
        Button(
            onClick = onIncrease,
            modifier = Modifier.height(48.dp)
        ) {
            Text(text = "Subir", style = MaterialTheme.typography.titleMedium)
        }
    }
}

// =====================================================================
// ÍCONOS VECTORIALES PROPIOS (Sol y Copo de Nieve)
// Se dibujan a mano porque no vienen en la librería base de íconos,
// así evitamos depender de material-icons-extended.
// =====================================================================

// Función auxiliar: dibuja una línea recta entre dos puntos
private fun PathBuilder.linea(x1: Float, y1: Float, x2: Float, y2: Float) {
    moveTo(x1, y1)
    lineTo(x2, y2)
}

// Ícono de Sol: un círculo central con 8 rayos
private val IconoSol: ImageVector by lazy {
    ImageVector.Builder(
        name = "Sol",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        // Círculo relleno (centro 12,12 y radio 4)
        path(fill = SolidColor(Color.Black)) {
            moveTo(16f, 12f)
            arcTo(4f, 4f, 0f, true, true, 8f, 12f)
            arcTo(4f, 4f, 0f, true, true, 16f, 12f)
            close()
        }
        // Rayos del sol
        path(
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round
        ) {
            linea(12f, 2f, 12f, 5f)            // arriba
            linea(12f, 19f, 12f, 22f)          // abajo
            linea(2f, 12f, 5f, 12f)            // izquierda
            linea(19f, 12f, 22f, 12f)          // derecha
            linea(4.93f, 4.93f, 7.05f, 7.05f)  // diagonal sup. izquierda
            linea(16.95f, 16.95f, 19.07f, 19.07f) // diagonal inf. derecha
            linea(4.93f, 19.07f, 7.05f, 16.95f)   // diagonal inf. izquierda
            linea(16.95f, 7.05f, 19.07f, 4.93f)   // diagonal sup. derecha
        }
    }.build()
}

// Ícono de Copo de Nieve: 6 brazos con pequeñas ramas en forma de "V"
private val IconoCopoNieve: ImageVector by lazy {
    ImageVector.Builder(
        name = "CopoNieve",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round
        ) {
            // Tres líneas que cruzan el centro (forman los 6 brazos)
            linea(12f, 2f, 12f, 22f)
            linea(3.34f, 7f, 20.66f, 17f)
            linea(3.34f, 17f, 20.66f, 7f)

            // Ramas en "V" al final de cada brazo
            moveTo(9.5f, 3f); lineTo(12f, 5.5f); lineTo(14.5f, 3f)             // arriba
            moveTo(9.5f, 21f); lineTo(12f, 18.5f); lineTo(14.5f, 21f)          // abajo
            moveTo(18.54f, 5.33f); lineTo(17.63f, 8.75f); lineTo(21.04f, 9.67f)   // sup. derecha
            moveTo(21.04f, 14.33f); lineTo(17.63f, 15.25f); lineTo(18.54f, 18.67f) // inf. derecha
            moveTo(5.46f, 5.33f); lineTo(6.37f, 8.75f); lineTo(2.96f, 9.67f)      // sup. izquierda
            moveTo(2.96f, 14.33f); lineTo(6.37f, 15.25f); lineTo(5.46f, 18.67f)   // inf. izquierda
        }
    }.build()
}

// =====================================================================
// VISTA PREVIA en Android Studio
// =====================================================================
@Preview(showBackground = true)
@Composable
fun ThermostatScreenPreview() {
    MyApplicationTheme {
        ThermostatScreen()
    }
}