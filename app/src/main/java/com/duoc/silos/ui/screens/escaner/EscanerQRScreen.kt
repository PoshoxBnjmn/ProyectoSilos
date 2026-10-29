package com.duoc.silos.ui.screens.escaner

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.silos.data.model.Silo
import com.duoc.silos.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EscanerQRScreen(
    silosDisponibles: List<Silo>,
    onVolver: () -> Unit,
    onSiloDetectado: (Silo) -> Unit
) {
    var flashEncendido by remember { mutableStateOf(false) }
    var mensajeEscaneo by remember { mutableStateOf("Alinee el código QR del silo dentro del recuadro") }

    // Animación de la línea láser del escáner
    val infiniteTransition = rememberInfiniteTransition(label = "laser")
    val laserOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_pos"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Escaneo QR de Silos", fontWeight = FontWeight.Bold, color = BlancoPuro)
                        Text("Identificación óptica en terreno", fontSize = 11.sp, color = RojoContenedor)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = BlancoPuro
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { flashEncendido = !flashEncendido }) {
                        Icon(
                            imageVector = if (flashEncendido) Icons.Default.FlashOn else Icons.Default.FlashOff,
                            contentDescription = "Flash",
                            tint = if (flashEncendido) Color.Yellow else BlancoPuro
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RojoPrincipal,
                    titleContentColor = BlancoPuro
                )
            )
        },
        containerColor = Color(0xFF121212)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Visor de Cámara con Marco de Escaneo
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                    .border(2.dp, RojoPrincipal, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Esquinas del marco
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    // Simulación de línea láser animada
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .align(Alignment.TopCenter)
                            .offset(y = (laserOffset * 250).dp)
                            .background(RojoPrincipal)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        tint = BlancoPuro.copy(alpha = 0.4f),
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Lente de Cámara Activo",
                        fontSize = 11.sp,
                        color = BlancoPuro.copy(alpha = 0.6f)
                    )
                }
            }

            Text(
                text = mensajeEscaneo,
                color = BlancoPuro,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            // Panel inferior de detección rápida para terreno (Simulación de Tags)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = BlancoSuperficie),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, GrisBorde)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "TAGS QR EN FAENA (SELECCIÓN RÁPIDA)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RojoPrincipal
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Toque un tag para simular el reconocimiento óptico del silo:",
                        fontSize = 12.sp,
                        color = TextoSecundario
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        silosDisponibles.take(4).forEach { silo ->
                            OutlinedButton(
                                onClick = {
                                    mensajeEscaneo = "¡Silo detectado: ${silo.id} - ${silo.granja}!"
                                    onSiloDetectado(silo)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, if (silo.estado == com.duoc.silos.data.model.EstadoSilo.CRITICO) RojoPrincipal else GrisBorde),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = TextoOscuro
                                )
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.QrCode2, contentDescription = null, tint = RojoPrincipal, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(silo.codigoQR, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text("${silo.id} · ${silo.granja}", fontSize = 10.sp, color = TextoSecundario)
                                        }
                                    }
                                    Text("${silo.porcentajeInt}%", fontWeight = FontWeight.Bold, color = if (silo.estado == com.duoc.silos.data.model.EstadoSilo.CRITICO) RojoPrincipal else EstadoSiloNormal)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
