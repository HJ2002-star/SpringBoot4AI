package me.shinsunyoung.springbootdeveloper.config;

import me.shinsunyoung.springbootdeveloper.ai.PollinationsChatModel;
import me.shinsunyoung.springbootdeveloper.ai.PollinationsImageModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.image.ImageModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public ChatModel chatModel() {
        return new PollinationsChatModel();
    }

    @Bean
    public ChatClient.Builder chatClientBuilder(ChatModel chatModel) {
        return ChatClient.builder(chatModel);
    }

    @Bean
    public ImageModel imageModel(ChatModel chatModel) {
        return new PollinationsImageModel(chatModel);
    }
}
