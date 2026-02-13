package za.co.brian.word_sanitizer_app.base.util;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SensitiveWordCache {

    private static final Logger log = LoggerFactory.getLogger(SensitiveWordCache.class);

    private final SensitiveWordRepository sensitiveWordRepository;

    @Getter
    private volatile Set<String> activeWordsUpper = Set.of();

    public SensitiveWordCache(SensitiveWordRepository sensitiveWordRepository) {
        this.sensitiveWordRepository = sensitiveWordRepository;
    }

    @Scheduled(fixedDelayString = "${sensitiveWords.cache.refresh-ms:600000}")  // 600s
    public void refresh() {
        log.info("Refreshing sensitive word cache from MSSQL...");
        long start = System.currentTimeMillis();

        this.activeWordsUpper = sensitiveWordRepository.findAllActiveWords().stream()
                .map(w -> w.toUpperCase(Locale.ROOT))
                .collect(Collectors.toUnmodifiableSet());

        long duration = System.currentTimeMillis() - start;
        log.info("Cache loaded: {} words in {}ms", activeWordsUpper.size(), duration);
    }

    /**
     * Manual refresh for dev demos (call from controller/endpoint)
     */
    public void refreshNow() {
        refresh();
    }
}

