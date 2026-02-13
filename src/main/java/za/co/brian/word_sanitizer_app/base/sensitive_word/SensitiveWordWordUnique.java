package za.co.brian.word_sanitizer_app.base.sensitive_word;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import org.springframework.web.servlet.HandlerMapping;
import za.co.brian.word_sanitizer_app.base.util.SensitiveWordService;


/**
 * Validate that the word value isn't taken yet.
 */
@Target({ FIELD, METHOD, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = SensitiveWordWordUnique.SensitiveWordWordUniqueValidator.class
)
public @interface SensitiveWordWordUnique {

    String message() default "{Exists.sensitiveWord.word}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class SensitiveWordWordUniqueValidator implements ConstraintValidator<SensitiveWordWordUnique, String> {

        private final SensitiveWordService sensitiveWordService;
        private final HttpServletRequest request;

        public SensitiveWordWordUniqueValidator(final SensitiveWordService sensitiveWordService,
                final HttpServletRequest request) {
            this.sensitiveWordService = sensitiveWordService;
            this.request = request;
        }

        @Override
        public boolean isValid(final String value, final ConstraintValidatorContext cvContext) {
            if (value == null) {
                // no value present
                return true;
            }
            @SuppressWarnings("unchecked") final Map<String, String> pathVariables =
                    ((Map<String, String>)request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE));
            final String currentId = pathVariables.get("id");
            if (currentId != null && value.equalsIgnoreCase(sensitiveWordService.get(Long.parseLong(currentId)).getWord())) {
                // value hasn't changed
                return true;
            }
            return !sensitiveWordService.wordExists(value);
        }

    }

}
