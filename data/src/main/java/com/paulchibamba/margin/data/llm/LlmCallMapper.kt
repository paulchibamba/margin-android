package com.paulchibamba.margin.data.llm

import com.paulchibamba.margin.data.database.dao.LlmSpendRow
import com.paulchibamba.margin.data.database.entity.LlmCallEntity
import com.paulchibamba.margin.domain.llm.LlmCall
import com.paulchibamba.margin.domain.llm.LlmSpend
import com.paulchibamba.margin.domain.llm.MicroDollars

object LlmCallMapper {

    fun toEntity(call: LlmCall) = LlmCallEntity(
        at = call.at.toEpochMilli(),
        purpose = call.purpose.name.lowercase(),
        model = call.model.id,
        tokensIn = call.usage.tokensIn,
        tokensCached = call.usage.tokensCached,
        tokensOut = call.usage.tokensOut,
        costMicros = call.cost.value,
        ok = call.isOk,
        error = call.error,
    )

    fun toSpend(row: LlmSpendRow) = LlmSpend(calls = row.calls, cost = MicroDollars(row.costMicros))
}
