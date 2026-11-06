package com.malha.app.feature.projects

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.malha.app.MalhaApplication
import com.malha.app.core.app.appContainer
import com.malha.app.core.database.DatabaseProvider
import com.malha.app.domain.adaptation.AdaptationResult
import com.malha.app.domain.adaptation.PatternAdaptationEngine
import com.malha.app.domain.adaptation.PatternRequirement
import com.malha.app.domain.adaptation.PatternRequirementExtractor
import com.malha.app.domain.adaptation.RuleBasedAdaptationEngine
import com.malha.app.domain.model.*
import com.malha.app.domain.validation.ProjectStartValidator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProjectSettingsViewModel(
    application: Application,
    private val projectId: String,
    private val adaptationEngine: PatternAdaptationEngine = RuleBasedAdaptationEngine()
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ProjectSettingsUiState(projectId = projectId))
    val uiState: StateFlow<ProjectSettingsUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            combine(
                appContainer.projectRepository.observeProject(projectId),
                appContainer.patternRepository.observePatterns(),
                appContainer.materialRepository.observeMaterials(),
                appContainer.socialRepository.observeCurrentUser(),
                appContainer.projectRepository.observeProjectMaterials(projectId)
            ) { project, patterns, materials, user, projectMaterialIds ->
                LoadedData(project, patterns, materials, user, projectMaterialIds)
            }.collect { data ->
                val project = data.project
                if (project == null) {
                    _uiState.update { it.copy(isLoading = false, error = "Project not found") }
                    return@collect
                }

                val allPatterns = data.patterns
                val materials = data.materials
                val yarns = materials.filter { it.type == MaterialType.Yarn }
                val needles = materials.filter { it.type == MaterialType.Needle }

                _uiState.update { state ->
                    val initialName = if (state.name.isEmpty()) project.name else state.name
                    val initialColor = state.colorHex ?: project.colorHex ?: "#3F6B57"

                    // Determine active pattern:
                    val currentPatternId = state.selectedPatternId ?: project.patternId
                    val currentPattern = allPatterns.firstOrNull { it.id == currentPatternId }
                        ?: (if (project.patternTitle != null && currentPatternId != null) {
                            Pattern(id = currentPatternId, title = project.patternTitle)
                        } else null)

                    val availableSizes = currentPattern?.availableSizes?.takeIf { it.isNotEmpty() }
                        ?: listOf("Standard")

                    val initialSize = state.selectedSize
                        ?: project.selectedSize
                        ?: currentPattern?.selectedSize
                        ?: availableSizes.firstOrNull()

                    // Compute requirements
                    val requirement = currentPattern?.let {
                        PatternRequirementExtractor.extract(it, initialSize)
                    }

                    // Matching materials
                    val initialYarnId = state.selectedYarnId
                        ?: data.projectMaterialIds.firstOrNull { mid -> yarns.any { it.id == mid } }
                        ?: yarns.firstOrNull { it.weightCategory?.contains("4") == true || it.weightCategory?.contains("medium") == true }?.id
                        ?: yarns.firstOrNull()?.id

                    val initialNeedleId = state.selectedNeedleId
                        ?: data.projectMaterialIds.firstOrNull { mid -> needles.any { it.id == mid } }
                        ?: needles.firstOrNull { it.sizeMm == (requirement?.recommendedNeedleMm ?: 4.0) }?.id
                        ?: needles.firstOrNull()?.id

                    val selectedYarn = yarns.firstOrNull { it.id == initialYarnId }
                    val selectedNeedle = needles.firstOrNull { it.id == initialNeedleId }

                    val recommendedYarn = yarns.firstOrNull {
                        val req = requirement?.recommendedYarnWeight
                        req != null && it.weightCategory?.contains(req, ignoreCase = true) == true
                    }
                    val recommendedNeedle = needles.firstOrNull {
                        it.sizeMm == (requirement?.recommendedNeedleMm ?: 4.0)
                    }

                    // Adaptation
                    val adaptation = requirement?.let { req ->
                        adaptationEngine.adapt(req, selectedNeedle, selectedYarn)
                    }

                    // Validation
                    val validation = ProjectStartValidator.validate(
                        patternId = currentPattern?.id,
                        selectedSize = initialSize,
                        selectedYarnId = initialYarnId,
                        selectedNeedleId = initialNeedleId
                    )

                    val projectStatus = project.projectStatus
                    val isReadOnly = projectStatus == ProjectStatus.FINISHED ||
                        (projectStatus == ProjectStatus.ACTIVE && project.progressPercent > 0)

                    val filteredPatterns = filterPatterns(allPatterns, state.patternSearchQuery)

                    state.copy(
                        isLoading = false,
                        name = initialName,
                        colorHex = initialColor,
                        projectStatus = projectStatus,
                        isReadOnlyConfig = isReadOnly,
                        patterns = allPatterns,
                        filteredPatterns = filteredPatterns,
                        selectedPatternId = currentPattern?.id,
                        selectedPattern = currentPattern,
                        availableSizes = availableSizes,
                        selectedSize = initialSize,
                        patternRequirement = requirement,
                        yarns = yarns,
                        needles = needles,
                        selectedYarnId = initialYarnId,
                        selectedNeedleId = initialNeedleId,
                        selectedYarn = selectedYarn,
                        selectedNeedle = selectedNeedle,
                        recommendedYarn = recommendedYarn,
                        recommendedNeedle = recommendedNeedle,
                        adaptationResult = adaptation,
                        isReadyToStart = validation.isReady,
                        missingRequirements = validation.missingRequirements,
                        coins = data.user?.coins ?: 0
                    )
                }
            }
        }
    }

    private fun filterPatterns(patterns: List<Pattern>, query: String): List<Pattern> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return patterns
        return patterns.filter {
            it.title.contains(trimmed, ignoreCase = true) ||
            it.designer?.contains(trimmed, ignoreCase = true) == true ||
            it.craft.name.contains(trimmed, ignoreCase = true)
        }
    }

    private var autoSaveJob: Job? = null

    private fun triggerAutoSave(debounceMs: Long = 0L) {
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            if (debounceMs > 0) {
                delay(debounceMs)
            }
            val state = _uiState.value
            val materialIds = listOfNotNull(state.selectedYarnId, state.selectedNeedleId)
            val patternTitle = state.selectedPattern?.title
            val snapshotJson = state.selectedPattern?.let {
                "{\"title\":\"${it.title}\",\"sizes\":${it.availableSizes.map { s -> "\"$s\"" }}}"
            }
            val targetStatus = when {
                state.projectStatus == ProjectStatus.FINISHED -> ProjectStatus.FINISHED.value
                state.projectStatus == ProjectStatus.ACTIVE -> ProjectStatus.ACTIVE.value
                state.isReadyToStart -> ProjectStatus.READY.value
                else -> ProjectStatus.DRAFT.value
            }

            runCatching {
                if (state.isReadOnlyConfig) {
                    appContainer.projectRepository.updateProjectColor(projectId, state.colorHex)
                    appContainer.projectRepository.updateProjectSettings(
                        projectId = projectId,
                        name = state.name,
                        colorHex = state.colorHex,
                        selectedSize = state.selectedSize,
                        materialIds = materialIds
                    )
                } else {
                    appContainer.projectRepository.updateProjectConfiguration(
                        projectId = projectId,
                        name = state.name,
                        colorHex = state.colorHex,
                        selectedSize = state.selectedSize,
                        patternId = state.selectedPatternId,
                        patternTitle = patternTitle,
                        patternSnapshotJson = snapshotJson,
                        materialIds = materialIds,
                        status = targetStatus
                    )
                }
            }
        }
    }

    fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
        triggerAutoSave(debounceMs = 400L)
    }

    fun updateColor(colorHex: String) {
        _uiState.update { it.copy(colorHex = colorHex) }
        triggerAutoSave(debounceMs = 0L)
    }

    fun updatePatternSearchQuery(query: String) {
        _uiState.update { state ->
            state.copy(
                patternSearchQuery = query,
                filteredPatterns = filterPatterns(state.patterns, query)
            )
        }
    }

    fun selectPattern(patternId: String) {
        if (_uiState.value.isReadOnlyConfig) return
        _uiState.update { state ->
            val pattern = state.patterns.firstOrNull { it.id == patternId } ?: return@update state
            val sizes = pattern.availableSizes.takeIf { it.isNotEmpty() } ?: listOf("Standard")
            val newSize = if (state.selectedSize in sizes) state.selectedSize else sizes.firstOrNull()
            val req = PatternRequirementExtractor.extract(pattern, newSize)
            val selectedNeedle = state.needles.firstOrNull { it.id == state.selectedNeedleId }
            val selectedYarn = state.yarns.firstOrNull { it.id == state.selectedYarnId }
            val adaptation = adaptationEngine.adapt(req, selectedNeedle, selectedYarn)
            val validation = ProjectStartValidator.validate(
                patternId = pattern.id,
                selectedSize = newSize,
                selectedYarnId = state.selectedYarnId,
                selectedNeedleId = state.selectedNeedleId
            )

            state.copy(
                selectedPatternId = pattern.id,
                selectedPattern = pattern,
                availableSizes = sizes,
                selectedSize = newSize,
                patternRequirement = req,
                adaptationResult = adaptation,
                isReadyToStart = validation.isReady,
                missingRequirements = validation.missingRequirements
            )
        }
        triggerAutoSave(debounceMs = 0L)
    }

    fun updateSize(size: String) {
        if (_uiState.value.isReadOnlyConfig) return
        _uiState.update { state ->
            val pattern = state.selectedPattern
            val req = pattern?.let { PatternRequirementExtractor.extract(it, size) }
                ?: state.patternRequirement
            val selectedNeedle = state.needles.firstOrNull { it.id == state.selectedNeedleId }
            val selectedYarn = state.yarns.firstOrNull { it.id == state.selectedYarnId }
            val adaptation = req?.let { adaptationEngine.adapt(it, selectedNeedle, selectedYarn) }
            val validation = ProjectStartValidator.validate(
                patternId = state.selectedPatternId,
                selectedSize = size,
                selectedYarnId = state.selectedYarnId,
                selectedNeedleId = state.selectedNeedleId
            )

            state.copy(
                selectedSize = size,
                patternRequirement = req,
                adaptationResult = adaptation,
                isReadyToStart = validation.isReady,
                missingRequirements = validation.missingRequirements
            )
        }
        triggerAutoSave(debounceMs = 0L)
    }

    fun selectYarn(yarnId: String?) {
        if (_uiState.value.isReadOnlyConfig) return
        _uiState.update { state ->
            val selectedYarn = state.yarns.firstOrNull { it.id == yarnId }
            val req = state.patternRequirement
            val selectedNeedle = state.needles.firstOrNull { it.id == state.selectedNeedleId }
            val adaptation = req?.let { adaptationEngine.adapt(it, selectedNeedle, selectedYarn) }
            val validation = ProjectStartValidator.validate(
                patternId = state.selectedPatternId,
                selectedSize = state.selectedSize,
                selectedYarnId = yarnId,
                selectedNeedleId = state.selectedNeedleId
            )

            state.copy(
                selectedYarnId = yarnId,
                selectedYarn = selectedYarn,
                adaptationResult = adaptation,
                isReadyToStart = validation.isReady,
                missingRequirements = validation.missingRequirements
            )
        }
        triggerAutoSave(debounceMs = 0L)
    }

    fun selectNeedle(needleId: String?) {
        if (_uiState.value.isReadOnlyConfig) return
        _uiState.update { state ->
            val selectedNeedle = state.needles.firstOrNull { it.id == needleId }
            val req = state.patternRequirement
            val selectedYarn = state.yarns.firstOrNull { it.id == state.selectedYarnId }
            val adaptation = req?.let { adaptationEngine.adapt(it, selectedNeedle, selectedYarn) }
            val validation = ProjectStartValidator.validate(
                patternId = state.selectedPatternId,
                selectedSize = state.selectedSize,
                selectedYarnId = state.selectedYarnId,
                selectedNeedleId = needleId
            )

            state.copy(
                selectedNeedleId = needleId,
                selectedNeedle = selectedNeedle,
                adaptationResult = adaptation,
                isReadyToStart = validation.isReady,
                missingRequirements = validation.missingRequirements
            )
        }
        triggerAutoSave(debounceMs = 0L)
    }

    fun startOrContinueProject(onNavigateToExecution: (String) -> Unit) {
        val state = _uiState.value
        viewModelScope.launch {
            runCatching {
                val materialIds = listOfNotNull(state.selectedYarnId, state.selectedNeedleId)
                val patternTitle = state.selectedPattern?.title
                val snapshotJson = state.selectedPattern?.let {
                    "{\"title\":\"${it.title}\",\"sizes\":${it.availableSizes.map { s -> "\"$s\"" }}}"
                }

                if (state.isReadOnlyConfig) {
                    // Update only editable fields (Name & Color)
                    appContainer.projectRepository.updateProjectColor(projectId, state.colorHex)
                    appContainer.projectRepository.updateProjectSettings(
                        projectId = projectId,
                        name = state.name,
                        colorHex = state.colorHex,
                        selectedSize = state.selectedSize,
                        materialIds = materialIds
                    )
                    onNavigateToExecution(projectId)
                } else if (state.isReadyToStart) {
                    // Save full configuration and transition to ACTIVE
                    appContainer.projectRepository.updateProjectConfiguration(
                        projectId = projectId,
                        name = state.name,
                        colorHex = state.colorHex,
                        selectedSize = state.selectedSize,
                        patternId = state.selectedPatternId,
                        patternTitle = patternTitle,
                        patternSnapshotJson = snapshotJson,
                        materialIds = materialIds,
                        status = ProjectStatus.ACTIVE.value
                    )
                    // Generate immutable ProjectPatternSnapshot and expanded instructions
                    val pId = state.selectedPatternId
                    val sSize = state.selectedSize
                    if (!pId.isNullOrBlank() && !sSize.isNullOrBlank()) {
                        appContainer.projectSnapshotRepository.createAndSaveSnapshot(
                            projectId = projectId,
                            patternId = pId,
                            sizeKey = sSize
                        )
                    }
                    appContainer.projectRepository.startProject(projectId)
                    onNavigateToExecution(projectId)
                } else {
                    val missing = state.missingRequirements.joinToString(", ")
                    _uiState.update { it.copy(error = "Cannot start project: missing $missing") }
                }
            }.onFailure { err ->
                _uiState.update { it.copy(error = err.message ?: "Failed to start project") }
            }
        }
    }

    fun saveDraftSettings() {
        val state = _uiState.value
        viewModelScope.launch {
            runCatching {
                val materialIds = listOfNotNull(state.selectedYarnId, state.selectedNeedleId)
                val patternTitle = state.selectedPattern?.title
                val snapshotJson = state.selectedPattern?.let {
                    "{\"title\":\"${it.title}\",\"sizes\":${it.availableSizes.map { s -> "\"$s\"" }}}"
                }
                val newStatus = if (state.isReadyToStart) ProjectStatus.READY.value else ProjectStatus.DRAFT.value

                appContainer.projectRepository.updateProjectConfiguration(
                    projectId = projectId,
                    name = state.name,
                    colorHex = state.colorHex,
                    selectedSize = state.selectedSize,
                    patternId = state.selectedPatternId,
                    patternTitle = patternTitle,
                    patternSnapshotJson = snapshotJson,
                    materialIds = materialIds,
                    status = if (state.projectStatus == ProjectStatus.ACTIVE || state.projectStatus == ProjectStatus.FINISHED) {
                        state.projectStatus.value
                    } else newStatus
                )
                _uiState.update { it.copy(isSaved = true) }
            }.onFailure { err ->
                _uiState.update { it.copy(error = err.message ?: "Failed to save settings") }
            }
        }
    }

    fun duplicateProject(newName: String? = null, onDuplicated: (String) -> Unit) {
        viewModelScope.launch {
            runCatching {
                appContainer.projectRepository.duplicateProject(projectId, newName)
            }.onSuccess { newId ->
                onDuplicated(newId)
            }.onFailure { err ->
                _uiState.update { it.copy(error = err.message ?: "Failed to duplicate project") }
            }
        }
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
                selectYarn(materialId)
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
                selectNeedle(materialId)
            }.onFailure { err ->
                _uiState.update { it.copy(error = err.message ?: "Failed to purchase needles") }
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

private data class LoadedData(
    val project: Project?,
    val patterns: List<Pattern>,
    val materials: List<Material>,
    val user: User?,
    val projectMaterialIds: List<String>
)

data class ProjectSettingsUiState(
    val isLoading: Boolean = true,
    val projectId: String = "",
    val name: String = "",
    val colorHex: String? = null,
    val projectStatus: ProjectStatus = ProjectStatus.DRAFT,
    val isReadOnlyConfig: Boolean = false,

    // Pattern Selection
    val patterns: List<Pattern> = emptyList(),
    val filteredPatterns: List<Pattern> = emptyList(),
    val selectedPatternId: String? = null,
    val selectedPattern: Pattern? = null,
    val patternSearchQuery: String = "",

    // Size Selection
    val availableSizes: List<String> = emptyList(),
    val selectedSize: String? = null,

    // Requirements & Materials
    val patternRequirement: PatternRequirement? = null,
    val yarns: List<Material> = emptyList(),
    val needles: List<Material> = emptyList(),
    val selectedYarnId: String? = null,
    val selectedNeedleId: String? = null,
    val selectedYarn: Material? = null,
    val selectedNeedle: Material? = null,
    val recommendedYarn: Material? = null,
    val recommendedNeedle: Material? = null,

    // Adaptation Engine
    val adaptationResult: AdaptationResult? = null,

    // Validation
    val isReadyToStart: Boolean = false,
    val missingRequirements: List<String> = emptyList(),

    // User economy & feedback
    val coins: Int = 0,
    val error: String? = null,
    val isSaved: Boolean = false
)
