package com.malha.app.core.database

import androidx.room.TypeConverter
import com.malha.app.domain.model.*

class RoomConverters {
    @TypeConverter
    fun fromCraftType(value: CraftType) = value.name

    @TypeConverter
    fun toCraftType(value: String) = CraftType.valueOf(value)

    @TypeConverter
    fun fromSkillLevel(value: SkillLevel) = value.name

    @TypeConverter
    fun toSkillLevel(value: String) = SkillLevel.valueOf(value)

    @TypeConverter
    fun fromSourceType(value: SourceType) = value.name

    @TypeConverter
    fun toSourceType(value: String) = SourceType.valueOf(value)

    @TypeConverter
    fun fromVerificationStatus(value: VerificationStatus) = value.name

    @TypeConverter
    fun toVerificationStatus(value: String) = VerificationStatus.valueOf(value)

    @TypeConverter
    fun fromStepType(value: StepType) = value.name

    @TypeConverter
    fun toStepType(value: String) = StepType.valueOf(value)
}
