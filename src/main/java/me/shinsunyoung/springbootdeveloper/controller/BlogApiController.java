package me.shinsunyoung.springbootdeveloper.controller;
import lombok.RequiredArgsConstructor;
import me.shinsunyoung.springbootdeveloper.domain.Article;
import me.shinsunyoung.springbootdeveloper.dto.AddArticleRequest;
import me.shinsunyoung.springbootdeveloper.dto.ArticleResponse;
import me.shinsunyoung.springbootdeveloper.dto.GeneratorThumbnailRequest;
import me.shinsunyoung.springbootdeveloper.dto.GeneratorThumbnailResponse;
import me.shinsunyoung.springbootdeveloper.dto.UpdateArticleRequest;
import me.shinsunyoung.springbootdeveloper.dto.WritingSuggestionRequest;
import me.shinsunyoung.springbootdeveloper.dto.WritingSuggestionsResponse;
import me.shinsunyoung.springbootdeveloper.service.BlogService;
import me.shinsunyoung.springbootdeveloper.service.ThumbnailGeneratorService;
import me.shinsunyoung.springbootdeveloper.service.WritingAssistantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
@RequiredArgsConstructor
@RestController
public class BlogApiController {
    private final BlogService blogService;
    private final WritingAssistantService writingAssistantService;
    private final ThumbnailGeneratorService thumbnailGeneratorService;
    @PostMapping("/api/articles")
    public ResponseEntity<Article> addArticle(@RequestBody AddArticleRequest request) {
        Article savedArticle = blogService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedArticle);
    }
    @GetMapping("/api/articles")
    public ResponseEntity<List<ArticleResponse>> findAllArticles() {
        List<ArticleResponse> articles = blogService.findAll()
                .stream()
                .map(ArticleResponse::new)
                .toList();
        return ResponseEntity.ok()
                .body(articles);
    }
    @GetMapping("/api/articles/{id}")
    public ResponseEntity<ArticleResponse> findArticle(@PathVariable long id) {
        Article article = blogService.findById(id);
        return ResponseEntity.ok()
                .body(new ArticleResponse(article));
    }
    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable long id) {
        blogService.delete(id);
        return ResponseEntity.ok()
                .build();
    }
    @PutMapping("/api/articles/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable long id,
                                                 @RequestBody UpdateArticleRequest request) {
        Article updatedArticle = blogService.update(id, request);
        return ResponseEntity.ok()
                .body(updatedArticle);
    }
    @PostMapping("/api/ai-suggestions")
    public ResponseEntity<WritingSuggestionsResponse> writingAssist(@RequestBody WritingSuggestionRequest request) {
        WritingSuggestionsResponse response = writingAssistantService.getWritingAssist(request);

        return ResponseEntity.ok()
                .body(response);
    }
    @PostMapping("/api/ai-thumbnails")
    public ResponseEntity<GeneratorThumbnailResponse> thumbnailGenerator(@RequestBody GeneratorThumbnailRequest request) {
        GeneratorThumbnailResponse response = thumbnailGeneratorService.generateThumbnail(request);

        return ResponseEntity.ok()
                .body(response);
    }
}
