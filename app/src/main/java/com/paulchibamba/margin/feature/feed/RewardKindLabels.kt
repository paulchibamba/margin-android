package com.paulchibamba.margin.feature.feed

import androidx.annotation.DrawableRes
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.domain.model.Post
import com.paulchibamba.margin.domain.progress.RewardKind

fun RewardKind.label(): String = when (this) {
    RewardKind.ZoomOut -> "Zoom out"
    RewardKind.ComingUp -> "Coming up"
    RewardKind.Callback -> "Callback"
    RewardKind.Comeback -> "Comeback"
    RewardKind.Quote -> "From your reading"
    RewardKind.NowYouCan -> "Now you can"
    RewardKind.Milestone -> "Milestone"
    RewardKind.ReExplain -> "Another way in"
}

@DrawableRes
fun RewardKind.iconOf(): Int = when (this) {
    RewardKind.ZoomOut -> MarginIcons.PinchZoomOut
    RewardKind.ComingUp -> MarginIcons.Schedule
    RewardKind.Callback -> MarginIcons.Replay
    RewardKind.Comeback -> MarginIcons.Autorenew
    RewardKind.Quote -> MarginIcons.AutoStories
    RewardKind.NowYouCan -> MarginIcons.VerifiedUser
    RewardKind.Milestone -> MarginIcons.DoneAll
    RewardKind.ReExplain -> MarginIcons.Psychology
}

fun Post.formatLabel(): String = rewardKind?.label() ?: format.label()
