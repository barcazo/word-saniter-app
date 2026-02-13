package za.co.brian.word_sanitizer_app.base.sanitization;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import za.co.brian.word_sanitizer_app.base.util.SensitiveWordCache;

import java.util.Map;

@RestController
@RequestMapping(value = "/api/external/v1/sanitizer", produces = MediaType.APPLICATION_JSON_VALUE)
@SecurityRequirement(name = "bearer-jwt")
@Tag(name = "Sanitizer (external)", description = "Sanitize messages by masking sensitive words")
public class SanitizerResource {

    private final SanitizationService sanitizationService;
    private final SensitiveWordCache sensitiveWordCache;

    public SanitizerResource(SanitizationService sanitizationService, SensitiveWordCache sensitiveWordCache) {
        this.sanitizationService = sanitizationService;
        this.sensitiveWordCache = sensitiveWordCache;
    }

    @Operation(summary = "Sanitize a message by masking sensitive words")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Message sanitized successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping(path = "/sanitize", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SanitizeResponse> sanitize(
            @Valid @RequestBody SanitizeRequest request) {

        String sanitized = sanitizationService.sanitize(request.getMessage());
        return ResponseEntity.ok(new SanitizeResponse(sanitized));
    }

    @Operation(summary = "Cache health and statistics")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cache status and metrics"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/health/cache")
    @Profile("dev")
    public Map<String, Object> cacheHealth() {
        return Map.of(
                "status", "UP",
                "wordCount", sensitiveWordCache.getActiveWordsUpper().size(),
                "refreshIntervalMs", 30000
        );
    }
}
