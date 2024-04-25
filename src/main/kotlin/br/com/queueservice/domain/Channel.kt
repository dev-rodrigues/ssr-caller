package br.com.queueservice.domain

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.LinkedList
import java.util.Queue

data class Channel(
    val name: String,
    var subscribers: MutableList<SseEmitterIdentifier> = mutableListOf(),
    var queue: Queue<String> = LinkedList()
)

data class SseEmitterIdentifier(
    val id: String,
    val emitter: SseEmitter,
    val type: SseEmitterType,
)

enum class SseEmitterType {
    VIEWER,
    AGENT,
}