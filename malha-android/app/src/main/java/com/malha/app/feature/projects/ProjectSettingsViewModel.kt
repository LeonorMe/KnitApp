package com.malha.app.feature.projects

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.malha.app.MalhaApplication
import com.malha.app.core.app.appContainer
import com.malha.app.core.database.DatabaseProvider
import com.malha.app.domain.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProjectSettingsViewModel(
    application: Application,
    private val projectId: String
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ProjectSettingsUiState())
    val uiState: StateFlow<ProjectSettingsUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            combine(
                appContainer.projectRepository.observeProject(projectId),
                appContainer.materialRepository.observeMaterials(),
                appContainer.socialRepository.observeCurrentUser()
            ) { project, materials, user ->
                Triple(project, materials, user)
            }.collect { (project, materials, user) ->
                if (project == null) {
                    _uiState.update { it.copy(isLoading = false, error = "Project not found") }
                    return@collect
                }

                val pattern = project.patternId?.let { pid ->
                    appContainer.patternRepository.observePattern(pid).firstOrNull()
                }

                val availableSizes = pattern?.availableSizes ?: listOf("S")
                val defaultSize = project.selectedSize ?: pattern?.selectedSize ?: availableSizes.firstOrNull() ?: "S"

                _uiState.update { state ->
                    val initialName = if (state.name.isEmpty()) project.name else state.name
                    val initialColor = if (state.colorHex == null) project.colorHex ?: "#BA68C8" else state.colorHex
                    val initialSize = if (state.selectedSize == null) defaultSize else state.selectedSize

                    val yarns = materials.filter { it.type == MaterialType.Yarn }
                    val needles = materials.filter { it.type == MaterialType.Needle }

                    val recommendedYarn = yarns.firstOrNull { 
                        it.weightCategory?.contains("4") == true || 
                        it.weightCategory?.contains("medium") == true ||
                        it.name.contains("Acrylic", ignoreCase = true) 
                    }
                    val recommendedNeedle = needles.firstOrNull { it.sizeMm == 4.0 }

                    val selectedYarnId = state.selectedYarnId ?: recommendedYarn?.id ?: yarns.firstOrNull()?.id
                    val selectedNeedleId = state.selectedNeedleId ?: recommendedNeedle?.id ?: needles.firstOrNull()?.id

                    state.copy(
                        isLoading = false,
                        name = initialName,
                        colorHex = initialColor,
                        selectedSize = initialSize,
                        availableSizes = availableSizes,
                        yarns = yarns,
                        needles = needles,
                        selectedYarnId = selectedYarnId,
                        selectedNeedleId = selectedNeedleId,
                        recommendedYarn = recommendedYarn,
                        recommendedNeedle = recommendedNeedle,
                        coins = user?.coins ?: 0,
                        pattern = pattern
                    )
                }
            }
        }
    }

    fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun updateColor(colorHex: String) {
        _uiState.update { it.copy(colorHex = colorHex) }
    }

    fun updateSize(size: String) {
        _uiState.update { it.copy(selectedSize = size) }
    }

    fun selectYarn(yarnId: String?) {
        _uiState.update { it.copy(selectedYarnId = yarnId) }
    }

    fun selectNeedle(needleId: String?) {
        _uiState.update { it.copy(selectedNeedleId = needleId) }
    }

    fun buyYarnFromStore() {
        val state = _uiState.value
        if (state.coins < 15) {
            _uiState.update { it.copy(error = "Insufficient coins! You need 15 coins.") }
            return
        }
        viewModelScope.launch {
            runCatching {
                appContainer.socialRepository.addCoins(-15)
                val materialId = appContainer.materialRepository.createMaterial(
                    name = "Medium Acrylic Yarn",
                    type = MaterialType.Yarn,
                    quantity = 1.0,
                    unit = "skeins"
                )
                DatabaseProvider.getDatabase(getApplication()).materialDao().insertMaterial(
                    com.malha.app.core.database.entity.MaterialEntity(
                        id = materialId,
                        name = "Medium Acrylic Yarn",
                        imageUri = null,
                        type = MaterialType.Yarn.storageValue,
                        quantity = 1.0,
                        unit = "skeins",
                        fiber = "100% Acrylic",
                        weightCategory = "medium 4",
                        gramsPerBall = 100,
                        needleType = null,
                        sizeMm = null,
                        lengthCm = null,
                        updatedAt = System.currentTimeMillis()
                    )
                )
                _uiState.update { it.copy(selectedYarnId = materialId, error = null) }
            }.onFailure { err ->
                _uiState.update { it.copy(error = err.message ?: "Failed to purchase yarn") }
            }
        }
    }

    fun buyNeedlesFromStore() {
        val state = _uiState.value
        if (state.coins < 10) {
            _uiState.update { it.copy(error = "Insufficient coins! You need 10 coins.") }
            return
        }
        viewModelScope.launch {
            runCatching {
                appContainer.socialRepository.addCoins(-10)
                val materialId = appContainer.materialRepository.createMaterial(
                    name = "Straight Bamboo Needles 4.0mm",
                    type = MaterialType.Needle,
                    quantity = 1.0,
                    unit = "pairs"
                )
                DatabaseProvider.getDatabase(getApplication()).materialDao().insertMaterial(
                    com.malha.app.core.database.entity.MaterialEntity(
                        id = materialId,
                        name = "Straight Bamboo Needles 4.0mm",
                        imageUri = null,
                        type = MaterialType.Needle.storageValue,
                        quantity = 1.0,
                        unit = "pairs",
                        fiber = "Bamboo",
                        weightCategory = null,
                        gramsPerBall = null,
                        needleType = "straight",
                        sizeMm = 4.0,
                        lengthCm = 35,
                        updatedAt = System.currentTimeMillis()
                    )
                )
                _uiState.update { it.copy(selectedNeedleId = materialId, error = null) }
            }.onFailure { err ->
                _uiState.update { it.copy(error = err.message ?: "Failed to purchase needles") }
            }
        }
    }

    fun saveSettings(onSuccess: (String) -> Unit) {
        val state = _uiState.value
        viewModelScope.launch {
            runCatching {
                val materialIds = listOfNotNull(state.selectedYarnId, state.selectedNeedleId)
                appContainer.projectRepository.updateProjectSettings(
                    projectId = projectId,
                    name = state.name,
                    colorHex = state.colorHex,
                    selectedSize = state.selectedSize,
                    materialIds = materialIds
                )
            }.onSuccess {
                onSuccess(projectId)
            }.onFailure { err ->
                _uiState.update { it.copy(error = err.message ?: "Failed to save project settings") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    companion object {
        fun factory(projectId: String) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                ProjectSettingsViewModel(MalhaApplication.instance, projectId) as T
        }
    }
}

data class ProjectSettingsUiState(
    val isLoading: Boolean = true,
    val name: String = "",
    val colorHex: String? = null,
    val selectedSize: String? = null,
    val availableSizes: List<String> = emptyList(),
    val yarns: List<Material> = emptyList(),
    val needles: List<Material> = emptyList(),
    val selectedYarnId: String? = null,
    val selectedNeedleId: String? = null,
    val recommendedYarn: Material? = null,
    val recommendedNeedle: Material? = null,
    val coins: Int = 0,
    val pattern: Pattern? = null,
    val error: String? = null
)
