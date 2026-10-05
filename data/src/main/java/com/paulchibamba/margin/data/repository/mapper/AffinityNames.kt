package com.paulchibamba.margin.data.repository.mapper

import com.paulchibamba.margin.data.database.entity.FormatAffinityEntity
import com.paulchibamba.margin.domain.signals.FormatAffinity

object AffinityNames {
    private const val REWARD_PREFIX = "reward:"

    fun entitiesOf(affinity: FormatAffinity): List<FormatAffinityEntity> =
        affinity.values.map { (format, value) -> FormatAffinityEntity(FormatNames.nameOf(format), value) } +
            affinity.rewardKindValues.map { (kind, value) -> FormatAffinityEntity("$REWARD_PREFIX$kind", value) }

    fun affinityOf(entities: List<FormatAffinityEntity>): FormatAffinity {
        val (rewardKinds, formats) = entities.partition { entity -> entity.format.startsWith(REWARD_PREFIX) }
        return FormatAffinity(
            values = formats.associate { entity -> FormatNames.formatOf(entity.format) to entity.value },
            rewardKindValues = rewardKinds.associate { entity -> kindOf(entity) to entity.value },
        )
    }

    private fun kindOf(entity: FormatAffinityEntity): String = entity.format.removePrefix(REWARD_PREFIX)
}
