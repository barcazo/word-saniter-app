package za.co.brian.word_sanitizer_app.base.sanitization;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.co.brian.word_sanitizer_app.base.util.SensitiveWordCache;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SanitizationServiceTest {

    private SanitizationService service;

    @BeforeEach
    void setup() {
        SensitiveWordCache cache = mock(SensitiveWordCache.class);
        when(cache.getActiveWordsUpper()).thenReturn(Set.of("SELECT", "FROM"));
        service = new SanitizationService(cache);
    }

    @Test
    void sanitizesSensitiveWords() {
        String result = service.sanitize("This is FROM");
        assertEquals("This is ****", result);
    }

    @Test
    void preservesPunctuation() {
        String result = service.sanitize("SELECT!!! good");
        assertEquals("******!!! good", result);
    }

    @Test
    void handlesNullEmpty() {
        assertNull(service.sanitize(null));
        assertEquals("", service.sanitize(""));
    }

    @Test
    void ignoresNonWords() {
        String result = service.sanitize("!!!123!!!");
        assertEquals("!!!123!!!", result);
    }
}

