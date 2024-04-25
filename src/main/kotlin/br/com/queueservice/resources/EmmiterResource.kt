package br.com.queueservice.resources

import br.com.queueservice.domain.SseEmitterType
import br.com.queueservice.services.EmitterService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.UUID
import kotlin.Long.Companion.MAX_VALUE

@RestController
@RequestMapping("/emmiter")
class EmmiterResource(
    private val service: EmitterService
) {

    @PostMapping("/create")
    fun createChannel(
        @RequestBody request: CreateChannelRequest
    ): ResponseEntity<Void> {
        service.create(request.name)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/subscribe/{channel}/{client}/{type}")
    fun subscribe(
        @PathVariable(required = true) channel: String,
        @PathVariable(required = true) client: String,
        @PathVariable type: SseEmitterType,
    ): SseEmitter {
        val emitter = SseEmitter(MAX_VALUE)
        val id = UUID.randomUUID().toString()

        service.register(
            channelName = channel,
            clientType = type,
            emitter = emitter,
            id = id
        )

        return emitter
    }

    @PostMapping("/to-queue")
    fun toQueue(@RequestBody request: ToQueueRequest): ResponseEntity<ToQueueResponse> {
        val code = service.toQueue(request.channel, request.cpf)
        return ResponseEntity.ok(ToQueueResponse(code))
    }

    @GetMapping("/next/{channel}")
    fun getNext(@PathVariable(required = true) channel: String) {
        service.next(channel)
    }
}

data class CreateChannelRequest(
    val name: String
)

data class ToQueueRequest(
    val channel: String,
    val cpf: String
)

data class ToQueueResponse(
    val code: Int
)