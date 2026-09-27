package com.paulchibamba.margin.designsystem.component

import androidx.annotation.DrawableRes

data class NudgeAction(val label: String, @param:DrawableRes val icon: Int? = null, val onClick: () -> Unit)
