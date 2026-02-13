package za.co.brian.word_sanitizer_app.base.sensitive_word;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SensitiveWordDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    @SensitiveWordWordUnique
    private String word;

    @NotNull
    @JsonProperty("isActive")
    private Boolean isActive;

}
