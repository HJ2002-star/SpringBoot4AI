package me.shinsunyoung.springbootdeveloper.dto;

import java.util.Map;

public record GeneratorThumbnailRequest (
        String title,
        String content
){
    public Map<String, Object> toMap(){
        return Map.of(
                "title",title,
                "content",content
        );
    }
}
