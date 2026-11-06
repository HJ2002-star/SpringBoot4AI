package me.shinsunyoung.springbootdeveloper.ai;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

// Spring AI의 ChatModel 규격을 무료 Pollinations 텍스트 API로 구현
public class PollinationsChatModel implements ChatModel {

    private static final String TEXT_URL = "https://text.pollinations.ai/";
    private static final int MAX_RETRY = 20;          // ★ 실습실 공인 IP 공유 대비 (20회 ≈ 11분)
    private static final long RETRY_WAIT_MS = 32_000; // ★ 무료 제한 30초 + 여유 2초 (줄이지 말 것)

    private final RestClient restClient = RestClient.create();

    @Override
    public ChatResponse call(Prompt prompt) {
        List<Map<String, String>> messages = prompt.getInstructions().stream()
                .map(message -> Map.of(
                        "role", toRole(message.getMessageType()),
                        "content", message.getText() == null ? "" : message.getText()))
                .toList();

        Map<String, Object> body = Map.of("model", "openai", "messages", messages);

        String text = requestWithRetry(body);

        return new ChatResponse(List.of(new Generation(new AssistantMessage(text == null ? "" : text))));
    }

    // ★ 402/429(무료 제한)이면 기다렸다가 다시 요청
    private String requestWithRetry(Map<String, Object> body) {
        for (int attempt = 1; ; attempt++) {
            try {
                return restClient.post()
                        .uri(TEXT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body)
                        .retrieve()
                        .body(String.class);
            } catch (HttpClientErrorException e) {
                int code = e.getStatusCode().value();
                if ((code == 402 || code == 429) && attempt <= MAX_RETRY) {
                    System.out.println("Pollinations 무료 제한, " + RETRY_WAIT_MS / 1000
                            + "초 후 다시 시도합니다 (" + attempt + "/" + MAX_RETRY + ")");
                    try {
                        Thread.sleep(RETRY_WAIT_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw e;
                    }
                } else {
                    throw e;
                }
            }
        }
    }

    private String toRole(MessageType type) {
        return switch (type) {
            case SYSTEM -> "system";
            case ASSISTANT -> "assistant";
            default -> "user";
        };
    }
}
