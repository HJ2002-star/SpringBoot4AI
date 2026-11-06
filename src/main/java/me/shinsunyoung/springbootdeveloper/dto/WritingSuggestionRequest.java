package me.shinsunyoung.springbootdeveloper.dto;

import java.util.Map;

public record WritingSuggestionRequest(
        String title,
        String content,
        String question
) {
    public Map<String, Object> toMap(String format){
        return Map.of(
                "title",title,
                "content",content,
                "question",question,
                "format",format
        );
    }
}
