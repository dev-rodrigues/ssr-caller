package br.com.queueservice.services

import br.com.queueservice.domain.Channel
import br.com.queueservice.domain.SseEmitterIdentifier
import br.com.queueservice.domain.SseEmitterType
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter


@Service
class EmitterService {

    private var channels = mutableListOf<Channel>()

    fun create(name: String) {
        channels.add(
            Channel(
                name = name,
                subscribers = mutableListOf()
            )
        )
    }

    fun register(
        channelName: String,
        clientType: SseEmitterType,
        emitter: SseEmitter,
        id: String
    ) {

        val localizedChannel = getChannel(
            channelName = channelName
        )

        localizedChannel.subscribers.add(
            SseEmitterIdentifier(
                id = id,
                type = clientType,
                emitter = emitter,
            )
        )
    }

    fun toQueue(channel: String, cpf: String): Int {
        val localizedChannel = getChannel(channel)
        localizedChannel.queue.add(cpf)
        return localizedChannel.queue.size
    }

    fun next(channel: String) {
        val localizedChannel = getChannel(channel)
        val next = localizedChannel.queue.remove()

        val viewers = localizedChannel.subscribers.filter { it.type == SseEmitterType.VIEWER }

        viewers.forEach {
            it.emitter.send(Message(next))
        }
    }

    private fun getChannel(channelName: String): Channel {
        return channels.find { it.name == channelName } ?: throw Exception("Channel not found")
    }
}

data class Message(
    val message: String
)