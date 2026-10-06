package com.paulchibamba.margin.data.llm

interface SecretBox {
    fun seal(plain: ByteArray): SealedSecret
    fun open(sealed: SealedSecret): ByteArray
}
