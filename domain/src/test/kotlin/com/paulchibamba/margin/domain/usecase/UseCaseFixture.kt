package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.memory.NoFuzz
import kotlin.random.Random

class UseCaseFixture {
    val content = FakeContentRepository()
    val progress = FakeProgressRepository()
    val settings = FakeSettingsRepository()
    val clock = FixedClock()
    val lock = FeedStateLock()
    val engines = LearningEngines(Random(seed = 42), NoFuzz)
    private val stateSource = FeedStateSource(progress, settings, engines)

    val getNextPost = GetNextPost(LibraryLoader(content, progress, settings), stateSource, progress, settings,
        engines, clock, lock)
    val recordPostExit = RecordPostExit(stateSource, progress, settings, engines, clock, lock)
    val applyPostAction = ApplyPostAction(stateSource, progress, settings, engines, clock, lock)
    val markNoteRead = MarkNoteRead(progress, clock)
    val markChapterKnown = MarkChapterKnown(progress, clock)
    val unmarkChapterKnown = UnmarkChapterKnown(progress)
    val observeNote = ObserveNote(content, progress)
    val rememberLastNote = RememberLastNote(progress)
    val libraryLoader = LibraryLoader(content, progress, settings)
    val unlockedPostCount = UnlockedPostCount(content, settings)
    val observeReadingHome = ObserveReadingHome(content, progress, settings, unlockedPostCount)
    val observeBook = ObserveBook(content, progress, settings)
    val observeStreak = ObserveStreak(progress, clock)
    val observeStats = ObserveStats(content, progress, clock)
    val updateBookSettings = UpdateBookSettings(settings)
    val observeLearningSettings = ObserveLearningSettings(content, settings)
    val setDesiredRetention = SetDesiredRetention(settings)
    val setChapterReadingOnly = SetChapterReadingOnly(settings)
    val consumeNewBadges = ConsumeNewBadges(content, progress, settings, lock)
    val describePost = DescribePost(content, progress, settings)
    val getCaughtUp = GetCaughtUp(content, progress, settings, clock, unlockedPostCount)
    val previewIntervals = PreviewIntervals(stateSource, settings, engines, clock)
    val countDueReviews = CountDueReviews(progress, clock)
    val getReviewReminder = GetReviewReminder(settings, progress, countDueReviews, clock)
    val setReviewReminder = SetReviewReminder(settings)
    val setDarkMode = SetDarkMode(settings)
    val observeDarkMode = ObserveDarkMode(settings)
}
