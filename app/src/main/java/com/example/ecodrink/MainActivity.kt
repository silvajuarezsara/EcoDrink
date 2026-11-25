package com.example.ecodrink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ============================================================================
// PALETA DE COLORES
// ============================================================================
val DarkGreenButton = Color(0xFF13422B)
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
    // Estado para saber qué pantalla mostrar ("welcome", "login", "dashboard")
    var currentScreen by remember { mutableStateOf("welcome") }
    // Estado para guardar el nombre ingresado en el login y mostrarlo en el dashboard
    var userName by remember { mutableStateOf("Usuario 1") }

    // Evaluamos qué pantalla dibujar según el estado actual
    when (currentScreen) {
        "welcome" -> WelcomeScreen(
            onNavigateToLogin = { currentScreen = "login" }
        )
        "login" -> LoginScreen(
            onLoginSuccess = { enteredUser ->
                if (enteredUser.isNotBlank()) userName = enteredUser
                currentScreen = "dashboard"
            }
        )
        "dashboard" -> DashboardScreen(
            userName = userName
        )
    }
}

// ============================================================================
// PANTALLA 1: BIENVENIDA (SPLASH)
// ============================================================================
@Composable
fun WelcomeScreen(onNavigateToLogin: () -> Unit) {
    // Usamos un Box contenedor para superponer el fondo y los elementos
    Box(modifier = Modifier.fillMaxSize()) {

        // 1. Imagen de Fondo de Bienvenida
        Image(
            painter = painterResource(id = R.drawable.fondo_1),
            contentDescription = null, // Fondo imagen
            contentScale = ContentScale.Crop, // Escala la imagen para cubrir toda la pantalla sin deformar
            modifier = Modifier.fillMaxSize()
        )

        // 2. Contenido vertical centrado
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
                modifier = Modifier.size(130.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "EcoDrink",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.weight(1f))

            // Botón para avanzar a Login
            Button(
                onClick = onNavigateToLogin,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreenButton)
            ) {
                Text("INICIAR", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
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
        "abby" to "123",
        "rocio" to "123",
        "keyla" to "123",
        "azul" to "123",
        "blanca" to "123"
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
                modifier = Modifier.size(110.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "EcoDrink",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Campo de Texto: Usuario
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("USUARIO", color = Color(0xFFEA5A24), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = user,
                    onValueChange = {
                        user = it
                        errorMessage = "" // Oculta el error al empezar a escribir nuevamente
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White.copy(alpha = 0.85f),
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = DarkGreenButton
                    ),
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray) },
                    singleLine = true,
                    isError = errorMessage.isNotEmpty()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de Texto: Contraseña
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("CONTRASEÑA", color = Color(0xFFEA5A24), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    visualTransformation = PasswordVisualTransformation(), // Enmascara el texto ingresado
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White.copy(alpha = 0.85f),
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = DarkGreenButton
                    ),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray) },
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

            Spacer(modifier = Modifier.height(30.dp))

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
                    .fillMaxWidth(0.5f)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreenButton)
            ) {
                Text("INICIAR", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ============================================================================
// PANTALLA 3: DASHBOARD PRINCIPAL
// ============================================================================
@Composable
fun DashboardScreen(userName: String) {
    Box(modifier = Modifier.fillMaxSize()) {

        // Imagen de fondo para dasboard
        Image(
            painter = painterResource(id = R.drawable.fondo_2),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            containerColor = Color.Transparent, // Fondo transparente
            bottomBar = {
                // Barra de navegación inferior
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
                        // Ítem 1: Reportes
                        IconButton(
                            onClick = {},
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.icono_reportes),
                                contentDescription = "Reportes",
                                tint = DarkGreenButton,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        // Ítem 2: Inicio
                        IconButton(
                            onClick = {},
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.icono_inicio),
                                contentDescription = "Inicio",
                                tint = DarkGreenButton,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        // Ítem 3: Inventario
                        IconButton(
                            onClick = {},
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
        ) { paddingValues ->
            // Contenido con desplazamiento vertical si el dispositivo tiene pantalla pequeña
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Saludo personalizado con el nombre introducido en la pantalla anterior
                Text(
                    text = "Hola de nuevo $userName !",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFDC4D26)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tarjeta de "Impacto de hoy"
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = CardGreenHeader)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Impacto de hoy", color = DarkGreenButton, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "5.0 Kg",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Residuos aprovechados", fontSize = 11.sp, color = DarkGreenButton)
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { 0.5f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Meta diaria: 10Kg", fontSize = 10.sp, color = DarkGreenButton)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Resumen del día",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreenButton
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Cuadrícula 2x2 para tarjetas numéricas de métricas
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricCard(modifier = Modifier.weight(1f), title = "0.0Kg", subtitle = "Inventario", color = CardOrange)
                    MetricCard(modifier = Modifier.weight(1f), title = "0.0Lt", subtitle = "Inventario", color = CardYellow)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricCard(modifier = Modifier.weight(1f), title = "0.0Lt", subtitle = "Merma", color = CardOlive)
                    MetricCard(modifier = Modifier.weight(1f), title = "0.0Lt", subtitle = "Composta", color = CardRedOrange)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botón central flotante para "Añadir producción"
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FloatingActionButton(
                        onClick = { /* Acción añadir */ },
                        containerColor = DarkGreenButton,
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier.size(52.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Añadir producción")
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Añadir producción", fontSize = 11.sp, color = DarkGreenButton)
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

// Composable auxiliar reusable para dibujar cada métrica (Inventario, Merma, Composta)
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
            Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 12.sp, color = Color.White.copy(alpha = 0.9f))
        }
    }
}