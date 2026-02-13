package za.co.brian.word_sanitizer_app.base.util;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/dev")
@Profile("dev")
public class CacheResource {

    private final SensitiveWordCache cache;

    public CacheResource(SensitiveWordCache cache) {
        this.cache = cache;
    }

    @PostMapping("/refresh-cache")
    public Map<String, Object> refresh() {
        cache.refreshNow();
        return Map.of("status", "refreshed", "wordCount", cache.getActiveWordsUpper().size());
    }

    @GetMapping("/cache-stats")
    public Map<String, Object> stats() {
        return Map.of("wordCount", cache.getActiveWordsUpper().size());
    }
}

