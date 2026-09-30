package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.repository.ProgressRepository
import javax.inject.Inject

class ResetProgress @Inject constructor(private val progress: ProgressRepository) {

    suspend operator fun invoke() {
        progress.clearProgress()
    }
}
