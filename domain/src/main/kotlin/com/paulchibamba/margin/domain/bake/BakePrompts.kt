package com.paulchibamba.margin.domain.bake

import com.paulchibamba.margin.domain.llm.LlmSchema

object BakePrompts {

    val REWARDS = """
        You write short reward posts for one reader who is learning software security from books they own.
        Each post is built ONLY from the facts given for its seed. Never add numbers, dates, concept names or claims
        that are not in those facts.

        Tone: state evidence of competence (dates, counts, what changed), name what the reader can now do, open a
        curiosity loop about what comes next, and understate. One idea per post.
        Never cheer ("great job", "well done", "amazing", "keep it up"), never rescue ("don't worry", "it's ok to
        struggle"), never flatter, never use exclamation marks or emoji, never ask the reader guilt questions, and
        never make claims about what other people know ("most developers", "90% of").

        Limits: title at most 60 characters, body at most 280 characters, plain text only (no markdown, no HTML).
        For a quote seed, copy ONE sentence from its excerpt exactly, character for character, into "quote", and
        write only a short title. For other seeds, set "quote" to null.
        Return one post per seed, in the same order, echoing each seedId.
        Also write one headline (at most 50 characters) for the last post, from that seed's facts.

        Examples of the tone (the facts in them are made up):
        - Comeback: "On 3 Oct, input validation tripped you up twice. Since then, four right answers in a row."
        - Now you can: "Output encoding is now in long-term memory. The next time a template renders user input,
          you'll see the gap before it ships."
        - Coming up: "Read on. A few notes ahead, you'll see why a token's scope matters more than its lifetime."
    """.trimIndent()

    val RE_EXPLAIN = """
        Re-explain ONE concept for a reader who is struggling with it. Use only the concept summary and the excerpt
        from the source note as truth. Take a DIFFERENT angle from the ones listed as already shown. You may use an
        Android-development analogy, or build on the anchor concept the reader already knows.
        Explain in your own plain words: never copy a run of words from the excerpt. Do not add numbers unless they
        appear in the summary or excerpt.
        Title at most 60 characters, body at most 280 characters, plain text only. No questions, no exclamation
        marks, no cheering, no "don't worry".
    """.trimIndent()

    val REWARDS_SCHEMA = LlmSchema(
        name = "reward_posts",
        json = """
            {"type":"object","additionalProperties":false,"required":["posts","headline"],"properties":{
            "posts":{"type":"array","items":{"type":"object","additionalProperties":false,
            "required":["seedId","title","body","quote"],"properties":{
            "seedId":{"type":"string"},"title":{"type":"string"},"body":{"type":"string"},
            "quote":{"type":["string","null"]}}}},
            "headline":{"type":"string"}}}
        """.trimIndent(),
    )

    val RE_EXPLAIN_SCHEMA = LlmSchema(
        name = "re_explain",
        json = """
            {"type":"object","additionalProperties":false,"required":["title","body"],"properties":{
            "title":{"type":"string"},"body":{"type":"string"}}}
        """.trimIndent(),
    )
}
