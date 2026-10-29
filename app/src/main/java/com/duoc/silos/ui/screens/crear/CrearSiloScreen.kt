package com.duoc.silos.ui.screens.crear

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.silos.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearSiloScreen(
    granjasExistentes: List<String>,
    onVolver: () -> Unit,
    onGuardarSilo: (
        id: String,
        granja: String,
        sector: String,
        capacidadKg: Double,
        nivelInicialKg: Double,
        tipoAlimento: String,
        consumoHora: Double,
        lat: Double,
        lon: Double
    ) -> Unit
) {
    var idSilo by remember { mutableStateOf("") }
    var granjaSeleccionada by remember { mutableStateOf(granjasExistentes.firstOrNull() ?: "Plantel Melipilla Central") }
    var nuevaGranjaTexto by remember { mutableStateOf("") }
    var usarNuevaGranja by remember { mutableStateOf(false) }
    var sectorPabellon by remember { mutableStateOf("") }
    var capacidadTexto by remember { mutableStateOf("22000") }
    var nivelInicialTexto by remember { mutableStateOf("15000") }
    var tipoAlimento by remember { mutableStateOf("Iniciador Aves Broiler") }
    var consumoHoraTexto by remember { mutableStateOf("120") }
    
    // GPS
    var latitud by remember { mutableStateOf(-33.6882) }
    var longitud by remember { mutableStateOf(-71.2185) }
    var gpsFijado by remember { mutableStateOf(false) }

    var errorId by remember { mutableStateOf(false) }
    var errorCapacidad by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Registrar Nuevo Silo", fontWeight = FontWeight.Bold, color = BlancoPuro)
                        Text("Alta de infraestructura y tag QR", fontSize = 11.sp, color = RojoContenedor)
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RojoPrincipal,
                    titleContentColor = BlancoPuro
                )
            )
        },
        containerColor = BlancoFondo
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Identificación y QR
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BlancoSuperficie),
                border = BorderStroke(1.dp, GrisBorde),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "IDENTIFICACIÓN DEL SILO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RojoPrincipal
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = idSilo,
                        onValueChange = {
                            idSilo = it.uppercase()
                            errorId = false
                        },
                        label = { Text("Código de Silo (Ej: SILO-07)") },
                        placeholder = { Text("SILO-07") },
                        isError = errorId,
                        supportingText = {
                            if (errorId) Text("El código no puede estar vacío", color = RojoPrincipal)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        leadingIcon = {
                            Icon(Icons.Default.Inventory2, contentDescription = null, tint = RojoPrincipal)
                        }
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Preview del Tag QR
                    val qrPreview = if (idSilo.isNotBlank()) "ARIZTIA-${idSilo.trim()}" else "ARIZTIA-SILO-XX"
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = RojoContenedor.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, RojoBorde)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.QrCode2, contentDescription = null, tint = RojoPrincipal, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Código QR asignado para etiquetado:", fontSize = 10.sp, color = TextoSecundario)
                                Text(qrPreview, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = RojoOscuro)
                            }
                        }
                    }
                }
            }

            // Plantel y Sector
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BlancoSuperficie),
                border = BorderStroke(1.dp, GrisBorde),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "UBICACIÓN Y PLANTEL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RojoPrincipal
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (!usarNuevaGranja && granjasExistentes.isNotEmpty()) {
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = granjaSeleccionada,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Plantel / Granja") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                                shape = RoundedCornerShape(8.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                granjasExistentes.distinct().forEach { granja ->
                                    DropdownMenuItem(
                                        text = { Text(granja) },
                                        onClick = {
                                            granjaSeleccionada = granja
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        OutlinedTextField(
                            value = nuevaGranjaTexto,
                            onValueChange = { nuevaGranjaTexto = it },
                            label = { Text("Nombre del Nuevo Plantel") },
                            placeholder = { Text("Ej: Plantel San Manuel") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Checkbox(
                            checked = usarNuevaGranja,
                            onCheckedChange = { usarNuevaGranja = it },
                            colors = CheckboxDefaults.colors(checkedColor = RojoPrincipal)
                        )
                        Text("Crear nuevo plantel / fundo", fontSize = 12.sp, color = TextoOscuro)
                    }

                    OutlinedTextField(
                        value = sectorPabellon,
                        onValueChange = { sectorPabellon = it },
                        label = { Text("Sector o Pabellón") },
                        placeholder = { Text("Ej: Pabellón Pavos 5 y 6") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            // Capacidad y Telemetría
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BlancoSuperficie),
                border = BorderStroke(1.dp, GrisBorde),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "CAPACIDAD Y TIPO DE ALIMENTO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RojoPrincipal
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = capacidadTexto,
                            onValueChange = {
                                capacidadTexto = it
                                errorCapacidad = false
                            },
                            label = { Text("Capacidad Total") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = errorCapacidad,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            trailingIcon = { Text("kg", fontSize = 11.sp, color = TextoSecundario, modifier = Modifier.padding(end = 8.dp)) }
                        )

                        OutlinedTextField(
                            value = nivelInicialTexto,
                            onValueChange = { nivelInicialTexto = it },
                            label = { Text("Nivel Inicial") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            trailingIcon = { Text("kg", fontSize = 11.sp, color = TextoSecundario, modifier = Modifier.padding(end = 8.dp)) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = tipoAlimento,
                        onValueChange = { tipoAlimento = it },
                        label = { Text("Tipo de Alimento") },
                        placeholder = { Text("Ej: Postura Alta Energía") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    // Sugerencias de Alimento
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Broiler", "Pavo Pellet", "Bovino TMR").forEach { sugerencia ->
                            SuggestionChip(
                                onClick = { tipoAlimento = sugerencia },
                                label = { Text(sugerencia, fontSize = 10.sp) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = GrisFondoCard
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = consumoHoraTexto,
                        onValueChange = { consumoHoraTexto = it },
                        label = { Text("Consumo Promedio Estimado") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        trailingIcon = { Text("kg/hora", fontSize = 11.sp, color = TextoSecundario, modifier = Modifier.padding(end = 8.dp)) }
                    )
                }
            }

            // Geolocalización GPS
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BlancoSuperficie),
                border = BorderStroke(1.dp, if (gpsFijado) EstadoSiloNormal else GrisBorde),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = if (gpsFijado) EstadoSiloNormal else RojoPrincipal)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "GEOLOCALIZACIÓN DEL SILO",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (gpsFijado) EstadoSiloNormal else RojoPrincipal
                            )
                        }
                        if (gpsFijado) {
                            Surface(
                                color = EstadoSiloNormal.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("GPS FIJADO", color = EstadoSiloNormal, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(4.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Coordenadas: Lat: $latitud · Lon: $longitud",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextoOscuro
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            // Captura las coordenadas simuladas de la faena
                            latitud = -33.6800 + (Math.random() * 0.02)
                            longitud = -71.2100 + (Math.random() * 0.02)
                            gpsFijado = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (gpsFijado) EstadoSiloNormal else RojoPrincipal
                        ),
                        border = BorderStroke(1.dp, if (gpsFijado) EstadoSiloNormal else RojoPrincipal),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (gpsFijado) "Actualizar Coordenadas GPS en Faena" else "Capturar Ubicación GPS de Terreno")
                    }
                }
            }

            // Botón Guardar
            Button(
                onClick = {
                    val capacidad = capacidadTexto.toDoubleOrNull()
                    val nivel = nivelInicialTexto.toDoubleOrNull() ?: 0.0
                    val consumo = consumoHoraTexto.toDoubleOrNull() ?: 120.0
                    val granjaFinal = if (usarNuevaGranja) nuevaGranjaTexto.ifBlank { "Plantel Nuevo" } else granjaSeleccionada

                    if (idSilo.isBlank()) {
                        errorId = true
                    } else if (capacidad == null || capacidad <= 0) {
                        errorCapacidad = true
                    } else {
                        onGuardarSilo(
                            idSilo,
                            granjaFinal,
                            sectorPabellon,
                            capacidad,
                            nivel,
                            tipoAlimento,
                            consumo,
                            latitud,
                            longitud
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RojoPrincipal,
                    contentColor = BlancoPuro
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.AddBusiness, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Guardar y Registrar Silo en Planta",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
