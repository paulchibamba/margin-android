package com.paulchibamba.margin.domain.progress

sealed class RewardKind(val key: String, val isSupport: Boolean = false) {
    data object ZoomOut : RewardKind("zoom_out")
    data object ComingUp : RewardKind("coming_up")
    data object Callback : RewardKind("callback")
    data object Comeback : RewardKind("comeback")
    data object Quote : RewardKind("quote")
    data object NowYouCan : RewardKind("now_you_can")
    data object Milestone : RewardKind("milestone")
    data object ReExplain : RewardKind("re_explain", isSupport = true)

    val isReward: Boolean
        get() = !isSupport

    companion object {
        val all: List<RewardKind> by lazy {
            listOf(ZoomOut, ComingUp, Callback, Comeback, Quote, NowYouCan, Milestone, ReExplain)
        }

        fun fromKey(key: String): RewardKind? = all.firstOrNull { kind -> kind.key == key }
    }
}
