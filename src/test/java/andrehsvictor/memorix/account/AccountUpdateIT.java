package andrehsvictor.memorix.account;

import org.junit.jupiter.api.DisplayName;
import org.springframework.transaction.annotation.Transactional;

import andrehsvictor.memorix.BaseIntegrationTest;
import io.restassured.response.ValidatableResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Transactional
@DisplayName("Account Update Integration Test")
class AccountUpdateIT extends BaseIntegrationTest {

    private static final String PATH = "/api/v1/account";

    @PersistenceContext
    private EntityManager entityManager;
}
