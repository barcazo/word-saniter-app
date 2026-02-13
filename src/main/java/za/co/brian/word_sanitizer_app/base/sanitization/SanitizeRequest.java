package za.co.brian.word_sanitizer_app.base.sanitization;

import jakarta.validation.constraints.NotBlank;

public class SanitizeRequest {

    @NotBlank(message = "message must not be blank")
    private String message;

    public SanitizeRequest() {}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}

