package com.lanjii.ai.controller;

import com.lanjii.ai.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * AI 智能问答
 *
 * @author lanjii
 */
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatBotController {

    private final ChatService chatService;

    /**
     * 流式问答
     */
    @PreAuthorize("hasAuthority('ai:chats:stream')")
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chatStream(String message, String conversationId) {
        return chatService.chatStream(message, conversationId)
                .map(content -> ServerSentEvent.<String>builder().data(content).build())
                .concatWithValues(ServerSentEvent.<String>builder().event("close").data("").build());
    }

}
