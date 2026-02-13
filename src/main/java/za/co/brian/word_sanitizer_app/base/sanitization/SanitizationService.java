package za.co.brian.word_sanitizer_app.base.sanitization;

import org.springframework.stereotype.Service;
import za.co.brian.word_sanitizer_app.base.util.SensitiveWordCache;

import java.text.BreakIterator;
import java.util.Locale;
import java.util.Set;
@Service
public class SanitizationService {

    private final SensitiveWordCache sensitiveWordCache;

    public SanitizationService(SensitiveWordCache sensitiveWordCache) {
        this.sensitiveWordCache = sensitiveWordCache;
    }

    public String sanitize(String message) {
        if (message == null || message.isBlank()) {
            return message;
        }

        // Read from in-memory cache instead of hitting DB
        Set<String> activeWordsUpper = sensitiveWordCache.getActiveWordsUpper();

        StringBuilder result = new StringBuilder();
        BreakIterator boundary = BreakIterator.getWordInstance(Locale.ENGLISH);
        boundary.setText(message);

        int start = boundary.first();
        for (int end = boundary.next(); end != BreakIterator.DONE; start = end, end = boundary.next()) {
            String token = message.substring(start, end);
            if (isWordToken(token) && isSensitive(token, activeWordsUpper)) {
                result.append(mask(token.length()));
            } else {
                result.append(token);
            }
        }

        return result.toString();
    }

    private boolean isWordToken(String token) {
        return token.chars().anyMatch(Character::isLetterOrDigit);
    }

    private boolean isSensitive(String token, Set<String> wordsUpper) {
        return wordsUpper.contains(token.toUpperCase(Locale.ROOT));
    }

    private String mask(int length) {
        return "*".repeat(length);
    }
}
