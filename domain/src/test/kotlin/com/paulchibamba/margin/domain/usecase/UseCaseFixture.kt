package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.drop.FakeDailyDropRepository
import com.paulchibamba.margin.domain.memory.NoFuzz
import com.paulchibamba.margin.domain.tracking.FakeEventLog
import kotlin.random.Random

class UseCaseFixture {
    val content = FakeContentRepository()
    val progress = FakeProgressRepository()
    val settings = FakeSettingsRepository()
    val generatedPosts = FakeGeneratedPostRepository()
    val eventLog = FakeEventLog()
    val clock = FixedClock()
    val lock = FeedStateLock()
    val drops = FakeDailyDropRepository()
    val engines = LearningEngines(Random(seed = 42), NoFuzz)
    private val stateSource = FeedStateSource(progress, settings, engines)

    val libraryLoader = LibraryLoader(content, progress, settings, generatedPosts, eventLog, clock)
    val getNextPost =
        GetNextPost(libraryLoader, stateSource, progress, settings, engines, clock, lock, generatedPosts, drops)
    val recordPostExit = RecordPostExit(stateSource, progress, settings, engines, clock, lock)
    val applyPostAction = ApplyPostAction(stateSource, progress, settings, engines, clock, lock, generatedPosts)
    val markNoteRead = MarkNoteRead(progress, clock)
    val markChapterKnown = MarkChapterKnown(progress, clock)
    val unmarkChapterKnown = UnmarkChapterKnown(progress)
    val observeNote = ObserveNote(content, progress)
    val rememberLastNote = RememberLastNote(progress)
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
    val observeAppearance = ObserveAppearance(settings)
    val setDarkPosts = SetDarkPosts(settings)
    val resetProgress = ResetProgress(progress)
}
