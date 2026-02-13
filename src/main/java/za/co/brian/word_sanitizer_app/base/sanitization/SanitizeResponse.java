package za.co.brian.word_sanitizer_app.base.sanitization;

public class SanitizeResponse {
    private String sanitizedMessage;

    public SanitizeResponse() {}

    public SanitizeResponse(String sanitizedMessage) {
        this.sanitizedMessage = sanitizedMessage;
    }

    public String getSanitizedMessage() { return sanitizedMessage; }
}


