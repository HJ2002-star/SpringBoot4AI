package me.shinsunyoung.springbootdeveloper.ai;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.image.Image;
import org.springframework.ai.image.ImageGeneration;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class PollinationsImageModel implements ImageModel {

    private static final String IMAGE_URL = "https://image.pollinations.ai/prompt/";
    private static final String TRANSLATE_INSTRUCTION = """
            Convert the following request into ONE short English prompt for an image generation model.
            Describe a concrete visual scene that represents the topic. Use at most 40 words.
            No text or letters in the image. Output only the English prompt.

            """;
    private static final String STYLE = ", professional blog thumbnail, clean composition, soft lighting, no text";
    private static final int MAX_PROMPT_LENGTH = 400;

    private final ChatModel chatModel;

    public PollinationsImageModel(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public ImageResponse call(ImagePrompt request) {
        String original = request.getInstructions().get(0).getText();

        String english = chatModel.call(TRANSLATE_INSTRUCTION + original)
                .replace("\"", "").replace("\n", " ").trim();
        System.out.println("이미지 프롬프트: " + english);

        String fullPrompt = english + STYLE;
        if (fullPrompt.length() > MAX_PROMPT_LENGTH) {
            fullPrompt = fullPrompt.substring(0, MAX_PROMPT_LENGTH);
        }

        String encoded = URLEncoder.encode(fullPrompt, StandardCharsets.UTF_8).replace("+", "%20");
        String url = IMAGE_URL + encoded + "?width=1024&height=576";

        return new ImageResponse(List.of(new ImageGeneration(new Image(url, null))));
    }
}
