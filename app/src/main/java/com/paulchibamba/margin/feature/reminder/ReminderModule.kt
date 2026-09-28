package com.paulchibamba.margin.feature.reminder

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface ReminderModule {

    @Binds
    fun reminderScheduler(scheduler: WorkManagerReminderScheduler): ReminderScheduler
}
