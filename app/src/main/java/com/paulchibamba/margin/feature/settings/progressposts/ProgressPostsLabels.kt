package com.paulchibamba.margin.feature.settings.progressposts

import com.paulchibamba.margin.domain.llm.LlmFailureReason
import com.paulchibamba.margin.domain.llm.LlmModel
import com.paulchibamba.margin.domain.llm.LlmReply
import com.paulchibamba.margin.domain.llm.LlmSpend
import com.paulchibamba.margin.domain.llm.MicroDollars
import java.math.BigDecimal

private const val MICRO_DOLLAR_DIGITS = 6
private const val CENT_DIGITS = 2

fun dollarsLabel(amount: MicroDollars): String {
    val dollars = BigDecimal.valueOf(amount.value, MICRO_DOLLAR_DIGITS).stripTrailingZeros()
    return "$" + dollars.setScale(maxOf(dollars.scale(), CENT_DIGITS)).toPlainString()
}

fun spendLabel(spend: LlmSpend): String {
    val calls = if (spend.calls == 1) "1 call" else "${spend.calls} calls"
    return "Spent today: ${dollarsLabel(spend.cost)} · $calls"
}

fun modelLabel(model: LlmModel): String = model.id.removePrefix("gpt-").replace('-', ' ')

fun keyDetail(maskedKey: String?): String =
    if (maskedKey == null) "Templates only. Paste an OpenAI key to write progress posts with a model" else
        "Saved $maskedKey. Stored encrypted on this phone"

fun connectionLabel(check: ConnectionCheck): String = when (check) {
    ConnectionCheck.Idle -> "Sends one tiny request and logs its cost"
    ConnectionCheck.Running -> "Testing…"
    is ConnectionCheck.Done -> replyLabel(check.reply)
}

private fun replyLabel(reply: LlmReply): String = when (reply) {
    is LlmReply.Answered -> "Connected to ${reply.model.id}"
    LlmReply.TemplatesOnly -> "Templates only: add a key first"
    LlmReply.OverCap -> "Today's cap is used up. Nothing was sent"
    is LlmReply.Failed -> failureLabel(reply.reason)
}

private fun failureLabel(reason: LlmFailureReason): String = when (reason) {
    LlmFailureReason.KEY_REJECTED -> "OpenAI rejected the key"
    LlmFailureReason.REQUEST_REJECTED -> "OpenAI rejected the request"
    LlmFailureReason.RATE_LIMITED -> "Rate limited, or the project is out of credit"
    LlmFailureReason.SERVER_ERROR -> "OpenAI had a server error"
    LlmFailureReason.TIMED_OUT -> "No reply within 20 seconds"
    LlmFailureReason.OFFLINE -> "Couldn't reach OpenAI. Check the connection"
    LlmFailureReason.REFUSED -> "The model refused"
    LlmFailureReason.INCOMPLETE -> "The reply was cut short"
    LlmFailureReason.UNREADABLE -> "Couldn't read the reply"
}
