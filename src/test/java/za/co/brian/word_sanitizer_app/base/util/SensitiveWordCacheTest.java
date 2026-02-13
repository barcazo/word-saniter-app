package za.co.brian.word_sanitizer_app.base.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SensitiveWordCacheTest {

    @Mock
    SensitiveWordRepository repo;
    @InjectMocks
    SensitiveWordCache cache;

    @Test
    void refreshLoadsActiveWords() {
        when(repo.findAllActiveWords()).thenReturn(List.of("select", "from"));
        cache.refresh();
        assertThat(cache.getActiveWordsUpper())
                .containsExactlyInAnyOrder("SELECT", "FROM");
    }

    @Test
    void refreshLogsErrorOnFailure() {
        when(repo.findAllActiveWords()).thenThrow(new RuntimeException("DB error"));
        assertThrows(RuntimeException.class, cache::refresh);
    }
}

