package za.co.brian.word_sanitizer_app.web;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import za.co.brian.word_sanitizer_app.WordSanitizerAppApplication;
import za.co.brian.word_sanitizer_app.base.config.BaseIT;


@SpringBootTest(
        classes = WordSanitizerAppApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class WordSanitizerAppApplicationTest extends BaseIT {

    @Test
    void contextLoads() {
    }

}
