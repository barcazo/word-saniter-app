package za.co.brian.word_sanitizer_app.base.sanitization;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import za.co.brian.word_sanitizer_app.base.util.SensitiveWordCache;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SanitizerResourceTest {

    private SanitizationService service;
    private SensitiveWordCache cache;
    private SanitizerResource controller;

    @BeforeEach
    void setup() {
        service = mock(SanitizationService.class);
        cache = mock(SensitiveWordCache.class);
        controller = new SanitizerResource(service, cache);
    }

    @Test
    void sanitizeCallsServiceReturnsSanitizedResponse() {
        SanitizeRequest request = new SanitizeRequest();
        request.setMessage("Select word");

        when(service.sanitize("Select word")).thenReturn("******");
        ResponseEntity<SanitizeResponse> response = controller.sanitize(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getSanitizedMessage()).isEqualTo("******");
        verify(service).sanitize("Select word");
    }

    @Test
    void healthCacheReturnsMetrics() {
        when(cache.getActiveWordsUpper()).thenReturn(Set.of("test"));
        Map<String, Object> response = controller.cacheHealth();

        assertThat(response.get("status")).isEqualTo("UP");
        assertThat(response.get("wordCount")).isEqualTo(1);
    }

}
