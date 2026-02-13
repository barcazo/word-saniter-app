package za.co.brian.word_sanitizer_app.base.sensitive_word;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.test.context.jdbc.Sql;
import za.co.brian.word_sanitizer_app.WordSanitizerAppApplication;
import za.co.brian.word_sanitizer_app.base.config.BaseIT;
import za.co.brian.word_sanitizer_app.base.util.SensitiveWordRepository;


@ApplicationModuleTest(
        classes = WordSanitizerAppApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        mode = ApplicationModuleTest.BootstrapMode.ALL_DEPENDENCIES
)

@Disabled
public class SensitiveWordResourceTest extends BaseIT {

    @Autowired
    public SensitiveWordRepository sensitiveWordRepository;

    @Test
    @Sql("/data/sensitiveWordData.sql")
    void getAllSensitiveWords_success() {
        RestAssured
                .given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/sensitiveWords")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("page.totalElements", Matchers.equalTo(2))
                .body("_embedded.sensitiveWordDTOList.get(0).id", Matchers.equalTo(1000))
                .body("_links.self.href", Matchers.endsWith("/api/sensitiveWords?page=0&size=20&sort=id,asc"));
    }

    @Test
    @Sql("/data/sensitiveWordData.sql")
    void getAllSensitiveWords_filtered() {
        RestAssured
                .given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/sensitiveWords?filter=1001")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("page.totalElements", Matchers.equalTo(1))
                .body("_embedded.sensitiveWordDTOList.get(0).id", Matchers.equalTo(1001));
    }

    @Test
    @Sql("/data/sensitiveWordData.sql")
    void getSensitiveWord_success() {
        RestAssured
                .given()
                .header(HttpHeaders.AUTHORIZATION, keycloakBrianFlashToken(VIEWER))
                .accept(ContentType.JSON)
                .when()
                .get("/api/sensitiveWords/1000")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("word", Matchers.equalTo("Commodo consequat."))
                .body("_links.self.href", Matchers.endsWith("/api/sensitiveWords/1000"));
    }

    @Test
    void getSensitiveWord_notFound() {
        RestAssured
                .given()
                .header(HttpHeaders.AUTHORIZATION, keycloakBrianFlashToken(VIEWER))
                .accept(ContentType.JSON)
                .when()
                .get("/api/sensitiveWords/1666")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("code", Matchers.equalTo("NOT_FOUND"));
    }

    @Test
    void getSensitiveWord_unauthorized() {
        RestAssured
                .given()
                .redirects().follow(false)
                .accept(ContentType.JSON)
                .when()
                .get("/api/sensitiveWords/1000")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("code", Matchers.equalTo("AUTHORIZATION_DENIED"));
    }

    @Test
    void createSensitiveWord_success() {
        RestAssured
                .given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(readResource("/requests/sensitiveWordDTORequest.json"))
                .when()
                .post("/api/sensitiveWords")
                .then()
                .statusCode(HttpStatus.CREATED.value());
        assertEquals(1, sensitiveWordRepository.count());
    }

    @Test
    void createSensitiveWord_missingField() {
        RestAssured
                .given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(readResource("/requests/sensitiveWordDTORequest_missingField.json"))
                .when()
                .post("/api/sensitiveWords")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("code", Matchers.equalTo("VALIDATION_FAILED"))
                .body("fieldErrors.get(0).property", Matchers.equalTo("word"))
                .body("fieldErrors.get(0).code", Matchers.equalTo("REQUIRED_NOT_NULL"));
    }

    @Test
    @Sql("/data/sensitiveWordData.sql")
    void updateSensitiveWord_success() {
        RestAssured
                .given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(readResource("/requests/sensitiveWordDTORequest.json"))
                .when()
                .put("/api/sensitiveWords/1000")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("_links.self.href", Matchers.endsWith("/api/sensitiveWords/1000"));
        assertEquals("Stet clita kasd.", sensitiveWordRepository.findById(((long)1000)).orElseThrow().getWord());
        assertEquals(2, sensitiveWordRepository.count());
    }

    @Test
    @Sql("/data/sensitiveWordData.sql")
    void deleteSensitiveWord_success() {
        RestAssured
                .given()
                .accept(ContentType.JSON)
                .when()
                .delete("/api/sensitiveWords/1000")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
        assertEquals(1, sensitiveWordRepository.count());
    }

}

