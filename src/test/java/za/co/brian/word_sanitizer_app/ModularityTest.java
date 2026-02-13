package za.co.brian.word_sanitizer_app;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;


public class ModularityTest {

    private final ApplicationModules modules = ApplicationModules.of(WordSanitizerAppApplication.class);

    @Test
    void verifyModuleStructure() {
        modules.verify();
    }

    /**
     * Generates documentation in target/spring-modulith-docs.
     */
    @Test
    void createModuleDocumentation() {
        new Documenter(modules).writeDocumentation();
    }

}
