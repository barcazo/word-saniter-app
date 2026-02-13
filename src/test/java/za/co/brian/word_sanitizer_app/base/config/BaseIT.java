package za.co.brian.word_sanitizer_app.base.config;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.http.ContentType;
import io.restassured.path.json.config.JsonPathConfig;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.util.StreamUtils;
import org.testcontainers.mssqlserver.MSSQLServerContainer;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
/**
 * Abstract base class to be extended by every IT test. Starts the Spring Boot context with a
 * Datasource connected to the Testcontainers Docker instance. The instance is reused for all tests,
 * with all data wiped out before each test.
 */
@ActiveProfiles("it")
@Sql("/data/clearAll.sql")
public abstract class BaseIT {

    @ServiceConnection
    private static final MSSQLServerContainer mSSQLServerContainer =
            new MSSQLServerContainer("mcr.microsoft.com/mssql/server:2022-latest")
                    .withInitScript("data/schema.sql");

    private static final KeycloakContainer keycloakContainer =
            new KeycloakContainer("quay.io/keycloak/keycloak:26.0.8");

    public static final String ADMIN = "admin@invalid.brian.io";
    public static final String VIEWER = "viewer@invalid.brian.io";
    public static final String PASSWORD = "Brian!";

    static {
        mSSQLServerContainer.acceptLicense()
                .withReuse(true)
                .start();

        keycloakContainer.withRealmImportFile("keycloak-realm.json")
                .withReuse(true)
                .start();
    }

    @LocalServerPort
    public int serverPort;

    @Autowired
    public ObjectMapper objectMapper;

    private final HashMap<String, String> keycloakBrianFlashTokens = new HashMap<>();

    @PostConstruct
    public void initRestAssured() {
        RestAssured.port = serverPort;
        RestAssured.urlEncodingEnabled = false;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.config = RestAssured.config()
                .jsonConfig(JsonConfig.jsonConfig()
                        .numberReturnType(JsonPathConfig.NumberReturnType.DOUBLE));
    }

    @DynamicPropertySource
    public static void setDynamicProperties(final DynamicPropertyRegistry registry) {

        registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
                () -> keycloakContainer.getAuthServerUrl()
                        + "/realms/SensitiveWordAPI/protocol/openid-connect/certs");

        String url = mSSQLServerContainer.getJdbcUrl()
                .replaceFirst(";databaseName=.*", "")
                + ";databaseName=word_sanitizer_db";

        registry.add("spring.datasource.url", () -> url);
        registry.add("spring.datasource.username", mSSQLServerContainer::getUsername);
        registry.add("spring.datasource.password", mSSQLServerContainer::getPassword);
        registry.add("spring.datasource.driver-class-name",
                () -> "com.microsoft.sqlserver.jdbc.SQLServerDriver");
    }

    @SneakyThrows
    public String readResource(final String resourceName) {
        return StreamUtils.copyToString(
                getClass().getResourceAsStream(resourceName),
                StandardCharsets.UTF_8);
    }

    public String keycloakBrianFlashToken(final String username) {
        String token = keycloakBrianFlashTokens.get(username);
        if (token == null) {
            final String tokenUrl = keycloakContainer.getAuthServerUrl()
                    + "/realms/SensitiveWordAPI/protocol/openid-connect/token";

            final String response = RestAssured.given()
                    .accept(ContentType.JSON)
                    .contentType(ContentType.URLENC)
                    .formParam("grant_type", "password")
                    .formParam("client_id", "backend-api")
                    .formParam("client_secret", "00F7A058181A8CE10D8F9647825174C0")
                    .formParam("username", username)
                    .formParam("password", PASSWORD)
                    .when()
                    .post(tokenUrl)
                    .body()
                    .asString();

            final Map<String, Object> map = objectMapper.readValue(response,
                    new TypeReference<>() {});
            token = "Bearer " + map.get("access_token");
            keycloakBrianFlashTokens.put(username, token);
        }
        return token;
    }
}