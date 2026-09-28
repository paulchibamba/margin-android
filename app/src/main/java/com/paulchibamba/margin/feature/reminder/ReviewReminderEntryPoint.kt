package com.paulchibamba.margin.feature.reminder

import com.paulchibamba.margin.domain.usecase.GetReviewReminder
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ReviewReminderEntryPoint {
    fun getReviewReminder(): GetReviewReminder
    fun notifier(): ReviewReminderNotifier
}
