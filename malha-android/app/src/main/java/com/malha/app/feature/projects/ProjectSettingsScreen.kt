package com.malha.app.feature.projects

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

private val PROJECT_PALETTE = listOf(
    "#FF8A65", // Terracotta
    "#FFB74D", // Orange/Gold
    "#FFD54F", // Amber
    "#AED581", // Light Green
    "#4DB6AC", // Teal
    "#4FC3F7", // Sky Blue
    "#7986CB", // Indigo
    "#BA68C8", // Purple
    "#F06292", // Pink
    "#A1887F", // Warm Brown
    "#90A4AE", // Blue-Grey
    "#3F6B57", // Sage (brand colour)
    // Color.Raspberry
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectSettingsScreen(
    projectId: String,
    onStartProject: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: ProjectSettingsViewModel = viewModel(
        factory = ProjectSettingsViewModel.factory(projectId)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    var showBuyYarnDialog by remember { mutableStateOf(false) }
    var showBuyNeedleDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurar Projeto") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    Row(
                        modifier = Modifier.padding(end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "🪙 ${uiState.coins}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Name Input
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = viewModel::updateName,
                    label = { Text("Nome do Projeto") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Size Selector
                if (uiState.availableSizes.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Tamanho do Projeto",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            uiState.availableSizes.forEach { size ->
                                val isSelected = uiState.selectedSize == size
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.updateSize(size) },
                                    label = { Text(size) }
                                )
                            }
                        }
                    }
                }

                // Color Picker
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Cor do Projeto",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PROJECT_PALETTE.forEach { hex ->
                            val swatchColor = remember(hex) {
                                runCatching { Color(android.graphics.Color.parseColor(hex)) }.getOrElse { Color.Gray }
                            }
                            val isSelected = hex.equals(uiState.colorHex, ignoreCase = true)
                            Surface(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .clickable { viewModel.updateColor(hex) },
                                shape = CircleShape,
                                color = swatchColor,
                                border = BorderStroke(
                                    width = if (isSelected) 3.dp else 1.5.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                                )
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selecionado",
                                            tint = if (swatchColor.luminance() < 0.35f) Color.White else Color.Black,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Divider()

                // Materials Matcher - YARN
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Fio Necessário",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Requerido pelo modelo: Espessura \"medium 4\"",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    var yarnExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = yarnExpanded,
                        onExpandedChange = { yarnExpanded = it }
                    ) {
                        val selectedYarn = uiState.yarns.firstOrNull { it.id == uiState.selectedYarnId }
                        OutlinedTextField(
                            value = selectedYarn?.let { "${it.name} (${it.weightCategory ?: "medium 4"})" } ?: "Nenhum fio disponível",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Selecionar do meu inventário") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = yarnExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        if (uiState.yarns.isNotEmpty()) {
                            ExposedDropdownMenu(
                                expanded = yarnExpanded,
                                onDismissRequest = { yarnExpanded = false }
                            ) {
                                uiState.yarns.forEach { yarn ->
                                    val isRecommended = yarn.weightCategory?.contains("4") == true || yarn.weightCategory?.contains("medium") == true
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "${yarn.name} (${yarn.weightCategory ?: "sem espessura"})${if (isRecommended) " ⭐ Recomendado" else ""}"
                                            )
                                        },
                                        onClick = {
                                            viewModel.selectYarn(yarn.id)
                                            yarnExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    if (uiState.recommendedYarn == null) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = "Loja")
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Fio recomendado em falta!",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    Text(
                                        text = "Comprar Fio Medium Acrylic na loja por 15 moedas.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                                Button(
                                    onClick = { showBuyYarnDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text("Comprar")
                                }
                            }
                        }
                    }
                }

                // Materials Matcher - NEEDLES
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Agulhas Necessárias",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Requerido pelo modelo: Tamanho 4.0mm",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    var needleExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = needleExpanded,
                        onExpandedChange = { needleExpanded = it }
                    ) {
                        val selectedNeedle = uiState.needles.firstOrNull { it.id == uiState.selectedNeedleId }
                        OutlinedTextField(
                            value = selectedNeedle?.let { "${it.name} (${it.sizeMm}mm)" } ?: "Nenhuma agulha disponível",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Selecionar do meu inventário") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = needleExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        if (uiState.needles.isNotEmpty()) {
                            ExposedDropdownMenu(
                                expanded = needleExpanded,
                                onDismissRequest = { needleExpanded = false }
                            ) {
                                uiState.needles.forEach { needle ->
                                    val isRecommended = needle.sizeMm == 4.0
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "${needle.name} (${needle.sizeMm}mm)${if (isRecommended) " ⭐ Recomendada" else ""}"
                                            )
                                        },
                                        onClick = {
                                            viewModel.selectNeedle(needle.id)
                                            needleExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    if (uiState.recommendedNeedle == null) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = "Loja")
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Agulhas recomendadas em falta!",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    Text(
                                        text = "Comprar Agulhas de Bambu 4.0mm na loja por 10 moedas.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                                Button(
                                    onClick = { showBuyNeedleDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text("Comprar")
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Error message
                if (uiState.error != null) {
                    Text(
                        text = uiState.error.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Start button
                Button(
                    onClick = {
                        viewModel.saveSettings { pid ->
                            onStartProject(pid)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Começar Projeto", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }

    // Purchase Yarn Confirmation
    if (showBuyYarnDialog) {
        AlertDialog(
            onDismissRequest = { showBuyYarnDialog = false },
            title = { Text("Comprar Fio") },
            text = { Text("Deseja gastar 15 moedas para comprar o Fio recomendado \"Medium Acrylic Yarn\"?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.buyYarnFromStore()
                        showBuyYarnDialog = false
                    }
                ) {
                    Text("Comprar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBuyYarnDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Purchase Needle Confirmation
    if (showBuyNeedleDialog) {
        AlertDialog(
            onDismissRequest = { showBuyNeedleDialog = false },
            title = { Text("Comprar Agulhas") },
            text = { Text("Deseja gastar 10 moedas para comprar as Agulhas recomendadas de Bambu 4.0mm?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.buyNeedlesFromStore()
                        showBuyNeedleDialog = false
                    }
                ) {
                    Text("Comprar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBuyNeedleDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
