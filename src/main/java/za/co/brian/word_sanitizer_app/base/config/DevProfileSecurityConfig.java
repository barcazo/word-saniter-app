package za.co.brian.word_sanitizer_app.base.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class DevProfileSecurityConfig {

    @Value("${spring.profiles.active:}")
    private String activeProfiles;

    public boolean isDev() {
        return activeProfiles.contains("dev");
    }
}

