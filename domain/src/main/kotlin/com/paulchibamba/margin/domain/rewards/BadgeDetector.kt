package com.paulchibamba.margin.domain.rewards

class BadgeDetector {

    fun newlyEarned(completion: BookCompletion, alreadyShown: Set<Badge>): List<Badge> =
        earned(completion).filterNot(alreadyShown::contains)

    private fun earned(completion: BookCompletion): List<Badge> = buildList {
        if (completion.isFullyIntroduced) add(Badge(completion.bookSlug, BadgeKind.INTRODUCED))
        if (completion.isFullyRemembered) add(Badge(completion.bookSlug, BadgeKind.REMEMBERED))
    }
}
