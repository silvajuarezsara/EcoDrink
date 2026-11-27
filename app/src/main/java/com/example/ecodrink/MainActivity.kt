package com.example.ecodrink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecodrink.ui.theme.poppins
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

// ============================================================================
// MODELOS Y NAVEGACIÓN
// ============================================================================
enum class Pantalla {
    Bienvenida,
    Login,
    Dashboard,
    NuevoLote,
    Inventario,
    Reportes
}

data class Lote(
    val id: String,
    val fruta: String,
    val kilos: Double,
    val litros: Double,
    val merma: Double = 0.0,
    val composta: Double = 0.0,
    val fecha: String = ""
)

const val META_DIARIA = 100.0

fun formato(valor: Double): String = "%.1f".format(valor)

// ============================================================================
// PALETA DE COLORES
// ============================================================================
val DarkGreenButton = Color(0xFF13422B)
val CreamButton = Color(0xFFF4E8CA)
val Maintext = Color(0xFF000000)
val CardGreenHeader = Color(0xFFA3CE27)
val CardOrange = Color(0xFFE9933B)
val CardYellow = Color(0xFFD9BF42)
val CardOlive = Color(0xFF8C9E27)
val CardRedOrange = Color(0xFFDC4D26)
val BottomNavColor = Color(0xFFC7D751)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                EcoDrinkApp()
            }
        }
    }
}

// ============================================================================
// Administra el flujo de pantallas y el estado del usuario
// ============================================================================
@Composable
fun EcoDrinkApp() {
    // Pantalla que se está mostrando
    var pantalla by remember { mutableStateOf(Pantalla.Bienvenida) }
    // Nombre ingresado en el login para saludar en el dashboard
    var userName by remember { mutableStateOf("Usuario 1") }
    // Lotes registrados (todas las pantallas leen de esta misma lista)
    val lotes = remember { mutableStateListOf<Lote>() }
    // Cuántas veces se intentó registrar un lote con datos inconsistentes
    var inconsistencias by remember { mutableIntStateOf(0) }

    // Botón "atrás" del teléfono: desde Login vuelve a Bienvenida,
    // desde Nuevo lote, Inventario o Reportes vuelve al Dashboard
    BackHandler(enabled = pantalla != Pantalla.Bienvenida && pantalla != Pantalla.Dashboard) {
        pantalla = if (pantalla == Pantalla.Login) Pantalla.Bienvenida else Pantalla.Dashboard
    }

    when (pantalla) {
        Pantalla.Bienvenida -> WelcomeScreen(
            onNavigateToLogin = { pantalla = Pantalla.Login }
        )
        Pantalla.Login -> LoginScreen(
            onLoginSuccess = { enteredUser ->
                if (enteredUser.isNotBlank()) {
                    // "SARA" o "sara" se muestran como "Sara"
                    userName = enteredUser.lowercase().replaceFirstChar { c: Char -> c.uppercaseChar() }
                }
                pantalla = Pantalla.Dashboard
            }
        )
        Pantalla.Dashboard -> DashboardScreen(
            userName = userName,
            lotes = lotes,
            onNavigate = { destino -> pantalla = destino }
        )
        Pantalla.NuevoLote -> NuevoLoteScreen(
            onVolver = { pantalla = Pantalla.Dashboard },
            onGuardar = { lote -> lotes.add(lote) },
            onInconsistencia = { inconsistencias++ }
        )
        Pantalla.Inventario -> InventarioScreen(
            lotes = lotes,
            onNavigate = { destino -> pantalla = destino }
        )
        Pantalla.Reportes -> ReportesScreen(
            lotes = lotes,
            inconsistencias = inconsistencias,
            onNavigate = { destino -> pantalla = destino }
        )
    }
}

// ============================================================================
// PANTALLA 1: BIENVENIDA (SPLASH)
// ============================================================================
@Composable
fun WelcomeScreen(onNavigateToLogin: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fondo_1),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(id = R.drawable.logo_ecodrink),
                contentDescription = "Logo EcoDrink",
                modifier = Modifier.size(155.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "EcoDrink",
                fontSize = 32.sp,
                fontFamily = poppins,
                fontWeight = FontWeight.Bold,
                color = CreamButton
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onNavigateToLogin,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .dropShadow(
                        shape = RoundedCornerShape(30.dp),
                        shadow = Shadow(
                            radius = 3.dp,
                            spread = 2.dp,
                            color = Color(0x40000000),
                            offset = DpOffset(x = 0.dp, 2.dp)
                        )
                    )
                    .width(200.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(42.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreenButton)
            ) {
                Text("INICIAR", fontSize = 23.sp, fontFamily = poppins, fontWeight = FontWeight.SemiBold, color = CreamButton)
            }

            Spacer(modifier = Modifier.height(135.dp))
        }
    }
}

// ============================================================================
// PANTALLA 2: LOGIN
// ============================================================================
@Composable
fun LoginScreen(onLoginSuccess: (String) -> Unit) {
    // Variables de estado para los inputs y mensajes de error
    var user by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    // Diccionario de usuarios con sus contraseñas correspondientes
    val validUsers = mapOf(
        "sara" to "123",
        "SARA" to "123",
        "abby" to "123",
        "ABBY" to "123",
        "rocio" to "123",
        "ROCIO" to "123",
        "keyla" to "123",
        "KEYLA" to "123",
        "azul" to "123",
        "AZUL" to "123",
        "blanca" to "123",
        "BLANCA" to "123"
    )


    Box(modifier = Modifier.fillMaxSize()) {

        // Imagen de Fondo compartida con la pantalla de bienvenida
        Image(
            painter = painterResource(id = R.drawable.fondo_1),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Logo EcoDrink
            Image(
                painter = painterResource(id = R.drawable.logo_ecodrink),
                contentDescription = "Logo EcoDrink",
                modifier = Modifier
                    .size(118.dp)
                    .width(155.dp)
                    .height(126.dp)
            )

            Text(
                text = "EcoDrink",
                fontSize = 32.sp,
                fontFamily = poppins,
                fontWeight = FontWeight.Bold,
                color = CreamButton,
                modifier = Modifier.height(10.dp)
            )
            // Campo de Texto: Usuario

            Column(modifier = Modifier.fillMaxWidth(0.8f)) {
                Text("USUARIO", color = Color(0xFFEA5A24), fontFamily = poppins, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(1.dp))
                OutlinedTextField(
                    value = user,
                    onValueChange = {
                        user = it
                        errorMessage = "" // Oculta el error al empezar a escribir nuevamente
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(40.dp)
                        .height(100.dp)
                        .width(300.dp),
                    shape = RoundedCornerShape(40.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = CreamButton,
                        focusedContainerColor = CreamButton,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = CardOrange
                    ),
                    singleLine = true,
                    isError = errorMessage.isNotEmpty()
                )
            }

            Spacer(modifier = Modifier.height(27.dp))

            // Campo de Texto: Contraseña
            Column(modifier = Modifier.fillMaxWidth(0.8f)) {
                Text("CONTRASEÑA", color = Color(0xFFEA5A24), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(1.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = ""
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(40.dp)
                        .height(100.dp)
                        .width(300.dp),
                    shape = RoundedCornerShape(20.dp),
                    visualTransformation = PasswordVisualTransformation(), // Enmascara el texto ingresado
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = CreamButton,
                        focusedContainerColor = CreamButton,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = CardOrange
                    ),
                    singleLine = true,
                    isError = errorMessage.isNotEmpty()
                )
            }

            // Muestra mensaje de alerta si falla la validación
            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(85.dp))

            // Botón con lógica de validación de credenciales
            Button(
                onClick = {
                    val inputUser = user.trim()
                    val inputPass = password.trim()

                    when {
                        // 1. Campos vacíos
                        inputUser.isEmpty() || inputPass.isEmpty() -> {
                            errorMessage = "Por favor ingresa usuario y contraseña"
                        }
                        // 2. Verifica si el usuario existe en el Map Y si la contraseña coincide
                        validUsers[inputUser.lowercase()] == inputPass -> {
                            onLoginSuccess(inputUser)
                        }
                        // 3. Credenciales no coincidentes
                        else -> {
                            errorMessage = "Usuario o contraseña incorrectos"
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(48.dp)
                    .dropShadow(
                        shape = RoundedCornerShape(30.dp),
                        shadow = Shadow(
                            radius = 3.dp,
                            spread = 2.dp,
                            color = Color(0x40000000),
                            offset = DpOffset(x = 0.dp, 2.dp)
                    )
            )
                .width(200.dp)
                .height(56.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreenButton)
            ) {
                Text("INICIAR", fontSize = 17.sp, fontFamily = poppins, fontWeight = FontWeight.SemiBold, color = CreamButton)
            }
        }
    }
}

// ============================================================================
// PANTALLA 3: DASHBOARD PRINCIPAL
// ============================================================================
@Composable
fun DashboardScreen(
    userName: String,
    lotes: List<Lote>,
    onNavigate: (Pantalla) -> Unit
) {
    // Los números se calculan a partir de los lotes registrados
    val inventario = lotes.sumOf { it.kilos }
    val produccion = lotes.sumOf { it.litros }
    val merma = lotes.sumOf { it.merma }
    val composta = lotes.sumOf { it.composta }
    val progreso = (composta / META_DIARIA).toFloat().coerceIn(0f, 1f)

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fondo_2),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                EcoDrinkBottomNavigation(onNavigate = onNavigate)
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Saludo personalizado con el nombre introducido en el login
                Text(
                    text = "Hola de nuevo $userName !",
                    fontSize = 16.sp,
                    fontStyle = FontStyle.Italic,
                    color = Maintext
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tarjeta de "Impacto de hoy"
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 154.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = CardDefaults.cardColors(containerColor = CardGreenHeader)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "Impacto de hoy",
                            color = DarkGreenButton,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "${formato(composta)} Kg",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Residuos aprovechados", fontSize = 11.sp, color = DarkGreenButton)
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { progreso },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Meta diaria: ${formato(META_DIARIA)} Kg",
                            fontSize = 10.sp,
                            fontStyle = FontStyle.Italic,
                            color = DarkGreenButton
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Resumen del día",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGreenButton
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Cuadrícula 2x2 de tarjetas con métricas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        title = "${formato(inventario)} Kg",
                        subtitle = "Inventario",
                        color = CardOrange
                    )
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        title = "${formato(produccion)} Kg",
                        subtitle = "Producción",
                        color = CardYellow
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        title = "${formato(merma)} Kg",
                        subtitle = "Merma",
                        color = CardOlive
                    )
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        title = "${formato(composta)} Kg",
                        subtitle = "Composta",
                        color = CardRedOrange
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botón central para registrar un nuevo lote
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FloatingActionButton(
                        onClick = { onNavigate(Pantalla.NuevoLote) },
                        containerColor = DarkGreenButton,
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier.size(52.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Nuevo lote")
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Nuevo lote",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = DarkGreenButton
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

// Composable reutilizable para cada métrica (Inventario, Producción, Merma, Composta)
@Composable
fun MetricCard(modifier: Modifier = Modifier, title: String, subtitle: String, color: Color) {
    Card(
        modifier = modifier.height(95.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 12.sp, color = Color.White.copy(alpha = 0.9f))
        }
    }
}

// ============================================================================
// PANTALLA 4: NUEVO LOTE
// ============================================================================
@Composable
fun NuevoLoteScreen(
    onVolver: () -> Unit,
    onGuardar: (Lote) -> Unit,
    onInconsistencia: () -> Unit
) {
    var idLote by remember { mutableStateOf("") }
    var fruta by remember { mutableStateOf("") }
    var kilosStr by remember { mutableStateOf("") }
    var litrosStr by remember { mutableStateOf("") }
    var mermaStr by remember { mutableStateOf("") }
    var compostaStr by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fondo_2),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onVolver) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = DarkGreenButton
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Nuevo Lote de Producción",
                    fontSize = 20.sp,
                    fontFamily = poppins,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreenButton
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CreamButton)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = idLote,
                        onValueChange = { idLote = it; errorMessage = "" },
                        label = { Text("Código / ID del Lote") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DarkGreenButton,
                            unfocusedBorderColor = Color.Gray
                        )
                    )

                    OutlinedTextField(
                        value = fruta,
                        onValueChange = { fruta = it; errorMessage = "" },
                        label = { Text("Fruta / Materia Prima") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DarkGreenButton,
                            unfocusedBorderColor = Color.Gray
                        )
                    )

                    OutlinedTextField(
                        value = kilosStr,
                        onValueChange = { kilosStr = it; errorMessage = "" },
                        label = { Text("Kilos de Residuo (Kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DarkGreenButton,
                            unfocusedBorderColor = Color.Gray
                        )
                    )

                    OutlinedTextField(
                        value = litrosStr,
                        onValueChange = { litrosStr = it; errorMessage = "" },
                        label = { Text("Litros Obtenidos (Lt)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DarkGreenButton,
                            unfocusedBorderColor = Color.Gray
                        )
                    )

                    OutlinedTextField(
                        value = mermaStr,
                        onValueChange = { mermaStr = it; errorMessage = "" },
                        label = { Text("Merma (Lt)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DarkGreenButton,
                            unfocusedBorderColor = Color.Gray
                        )
                    )

                    OutlinedTextField(
                        value = compostaStr,
                        onValueChange = { compostaStr = it; errorMessage = "" },
                        label = { Text("Composta (Kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DarkGreenButton,
                            unfocusedBorderColor = Color.Gray
                        )
                    )

                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            val kilosVal = kilosStr.toDoubleOrNull()
                            val litrosVal = litrosStr.toDoubleOrNull()
                            val mermaVal = mermaStr.toDoubleOrNull() ?: 0.0
                            val compostaVal = compostaStr.toDoubleOrNull() ?: 0.0

                            val esInconsistente = idLote.isBlank() ||
                                    fruta.isBlank() ||
                                    kilosVal == null || kilosVal <= 0 ||
                                    litrosVal == null || litrosVal < 0 ||
                                    litrosVal > kilosVal * 3

                            if (esInconsistente) {
                                onInconsistencia()
                                errorMessage = "Datos inconsistentes o incompletos. Intento registrado."
                            } else {
                                val nuevoLote = Lote(
                                    id = idLote.trim(),
                                    fruta = fruta.trim(),
                                    kilos = kilosVal,
                                    litros = litrosVal,
                                    merma = mermaVal,
                                    composta = compostaVal
                                )
                                onGuardar(nuevoLote)
                                onVolver()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkGreenButton)
                    ) {
                        Text("GUARDAR LOTE", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = CreamButton)
                    }
                }
            }
        }
    }
}

// ============================================================================
// PANTALLA 5: INVENTARIO
// ============================================================================
@Composable
fun InventarioScreen(
    lotes: List<Lote>,
    onNavigate: (Pantalla) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fondo_2),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                EcoDrinkBottomNavigation(onNavigate = onNavigate)
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Inventario de Lotes",
                        fontSize = 22.sp,
                        fontFamily = poppins,
                        fontWeight = FontWeight.Bold,
                        color = DarkGreenButton
                    )

                    Button(
                        onClick = { onNavigate(Pantalla.NuevoLote) },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkGreenButton),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("+ Añadir", color = CreamButton, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (lotes.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay lotes registrados aún.\nPresiona '+ Añadir' para registrar uno.",
                            color = Color.DarkGray,
                            fontSize = 16.sp,
                            fontStyle = FontStyle.Italic
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(lotes) { lote ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = CreamButton)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Lote: ${lote.id} - ${lote.fruta}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DarkGreenButton
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Residuo: ${lote.kilos} Kg", fontSize = 13.sp)
                                        Text("Obtenido: ${lote.litros} Lt", fontSize = 13.sp)
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Merma: ${lote.merma} Lt", fontSize = 13.sp)
                                        Text("Composta: ${lote.composta} Kg", fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// PANTALLA 6: REPORTES
// ============================================================================
@Composable
fun ReportesScreen(
    lotes: List<Lote>,
    inconsistencias: Int,
    onNavigate: (Pantalla) -> Unit
) {
    val totalKilos = lotes.sumOf { it.kilos }
    val totalLitros = lotes.sumOf { it.litros }
    val totalMerma = lotes.sumOf { it.merma }
    val totalComposta = lotes.sumOf { it.composta }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fondo_2),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                EcoDrinkBottomNavigation(onNavigate = onNavigate)
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Reportes de Producción",
                    fontSize = 22.sp,
                    fontFamily = poppins,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreenButton
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = CardGreenHeader)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Resumen General", color = DarkGreenButton, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Total de Lotes Registrados: ${lotes.size}", fontSize = 14.sp, color = Color.White)
                        Text("Total Residuos Aprovechados: ${"%.1f".format(totalKilos)} Kg", fontSize = 14.sp, color = Color.White)
                        Text("Total Bebida Producida: ${"%.1f".format(totalLitros)} Lt", fontSize = 14.sp, color = Color.White)
                        Text("Total Merma: ${"%.1f".format(totalMerma)} Lt", fontSize = 14.sp, color = Color.White)
                        Text("Total Composta: ${"%.1f".format(totalComposta)} Kg", fontSize = 14.sp, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = CardOrange)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Registro de Inconsistencias", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Intentos con datos inconsistentes: $inconsistencias",
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

// ============================================================================
// COMPONENTES AUXILIARES
// ============================================================================
@Composable
fun EcoDrinkBottomNavigation(onNavigate: (Pantalla) -> Unit) {
    Surface(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = BottomNavColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onNavigate(Pantalla.Reportes) },
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icono_reportes),
                    contentDescription = "Reportes",
                    tint = DarkGreenButton,
                    modifier = Modifier.size(32.dp)
                )
            }

            IconButton(
                onClick = { onNavigate(Pantalla.Dashboard) },
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icono_inicio),
                    contentDescription = "Inicio",
                    tint = DarkGreenButton,
                    modifier = Modifier.size(32.dp)
                )
            }

            IconButton(
                onClick = { onNavigate(Pantalla.Inventario) },
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icono_inventario),
                    contentDescription = "Inventario",
                    tint = DarkGreenButton,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}


