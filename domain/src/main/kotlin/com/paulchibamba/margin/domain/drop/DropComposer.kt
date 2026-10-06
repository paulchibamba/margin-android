package com.paulchibamba.margin.domain.drop

import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.feed.RecallEstimate
import com.paulchibamba.margin.domain.feed.source.NewConceptProvider
import com.paulchibamba.margin.domain.feed.source.ReviewProvider
import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.model.Post
import com.paulchibamba.margin.domain.progress.RewardKind
import java.time.LocalDate
import kotlin.random.Random

class DropComposer(private val recallEstimate: RecallEstimate, private val random: Random) {

    fun compose(date: LocalDate, request: DropRequest): DailyDrop {
        val picks = Picks(request)
        val closing = picks.closing()
        val reviews = picks.reviews()
        val new = picks.newConcept()
        val surprise = picks.surprise()
        val opening = picks.opening(avoiding = closing?.rewardKind)
        val chosen = listOfNotNull(opening, new, closing).size + reviews.size + listOfNotNull(surprise).size
        val extras = picks.extras(count = MINIMUM_SIZE - chosen)
        val items = listOfNotNull(opening, new) + withSurprise(reviews, surprise) + extras + listOfNotNull(closing)
        return DailyDrop(date, request.now, request.state.step, items.map(Pick::toItem))
    }

    private fun withSurprise(reviews: List<Pick>, surprise: Pick?): List<Pick> {
        if (surprise == null) return reviews
        if (reviews.size < 2) return reviews + surprise
        return listOf(reviews.first(), surprise) + reviews.drop(1)
    }

    private inner class Picks(private val request: DropRequest) {
        private val library = request.library
        private val state = request.state
        private val used = mutableSetOf<Post>()
        private val unseenProgress = library.rewardPosts.filterNot { post -> state.hasSeen(post.id) }.reversed()

        fun closing(): Pick? {
            val headlined = unseenProgress.firstOrNull { post -> post.id == request.headlinePost }
            val post = headlined ?: quotesLast(unseenProgress).firstOrNull() ?: request.fallbackClosings.firstOrNull()
            return post?.let { take(it, DropSlot.CLOSING, CandidateSource.REWARD) }
        }

        fun opening(avoiding: RewardKind?): Pick? {
            val progress = quotesLast(unseenProgress).firstOrNull { isFree(it) && it.rewardKind != avoiding }
            if (progress != null) return take(progress, DropSlot.OPENING, CandidateSource.REWARD)
            val teach = newestIntroduced()?.let(::teachPostOf) ?: return null
            return take(teach, DropSlot.OPENING, CandidateSource.ANGLE)
        }

        fun newConcept(): Pick? = NewConceptProvider().candidates(library, state, request.now)
            .firstOrNull { candidate -> isFree(candidate.post) }
            ?.let { candidate -> take(candidate.post, DropSlot.NEW, CandidateSource.NEW) }

        fun reviews(): List<Pick> = ReviewProvider().candidates(library, state, request.now)
            .groupBy { candidate -> candidate.post.conceptId }
            .entries
            .sortedBy { (concept, _) -> recallEstimate.of(state.progressOf(concept), request.now) }
            .take(MAXIMUM_REVIEWS)
            .mapNotNull { (_, tests) -> tests.firstOrNull { isFree(it.post) } }
            .map { candidate -> take(candidate.post, DropSlot.REVIEW, CandidateSource.REVIEW) }

        fun surprise(): Pick? {
            val memes = introducedConcepts().flatMap(library::memesOf)
            val quotes = unseenProgress.filter { post -> post.rewardKind == RewardKind.Quote }
            val post = (memes + quotes).filter { isFree(it) && !state.hasSeen(it.id) }.randomOrNull(random)
            return post?.let { take(it, DropSlot.SURPRISE, CandidateSource.REWARD) }
        }

        fun extras(count: Int): List<Pick> = introducedConcepts()
            .sortedByDescending { concept -> state.progressOf(concept).introducedAtStep }
            .filterNot { concept -> concept.id in usedConcepts() }
            .mapNotNull { concept -> library.teachPostsOf(concept).firstOrNull { !state.hasSeen(it.id) } }
            .take(count.coerceAtLeast(0))
            .map { post -> take(post, DropSlot.EXTRA, CandidateSource.ANGLE) }

        private fun newestIntroduced(): Concept? = introducedConcepts()
            .sortedByDescending { concept -> state.progressOf(concept).introducedAtStep ?: 0 }
            .sortedBy { concept -> concept.id in usedConcepts() }
            .firstOrNull()

        private fun teachPostOf(concept: Concept): Post? =
            library.teachPostsOf(concept).filter(::isFree).minByOrNull { post -> state.seenPosts[post.id] ?: -1 }

        private fun introducedConcepts(): List<Concept> = library.conceptsInBookOrder.filter(state::isIntroduced)

        private fun usedConcepts(): Set<ConceptId> = used.map(Post::conceptId).toSet()

        private fun quotesLast(posts: List<Post>): List<Post> =
            posts.sortedBy { post -> post.rewardKind == RewardKind.Quote }

        private fun isFree(post: Post): Boolean = post !in used

        private fun take(post: Post, slot: DropSlot, source: CandidateSource): Pick {
            used += post
            return Pick(post, slot, source)
        }
    }

    private class Pick(val post: Post, val slot: DropSlot, val source: CandidateSource) {
        val rewardKind: RewardKind? get() = post.rewardKind

        fun toItem() = DropItem(post.id, slot, source)
    }

    companion object {
        const val MINIMUM_SIZE = 4
        const val MAXIMUM_SIZE = 7
        private const val MAXIMUM_REVIEWS = 3
    }
}
