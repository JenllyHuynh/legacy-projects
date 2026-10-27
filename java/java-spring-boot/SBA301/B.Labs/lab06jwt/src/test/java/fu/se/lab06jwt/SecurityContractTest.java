package fu.se.lab06jwt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fu.se.lab06jwt.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Test chạy bằng H2 in-memory nên không cần bật SQL Server
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class SecurityContractTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ObjectMapper mapper;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
    }

    private String json(String fullName, String email, String password) {
        return "{\"fullName\":\"" + fullName + "\",\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
    }

    private void signup(String email) throws Exception {
        mvc.perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON)
                        .content(json("Test User", email, "P@ssword123")))
                .andExpect(status().isCreated());
    }

    private String login(String email) throws Exception {
        MvcResult r = mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"P@ssword123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();
        JsonNode node = mapper.readTree(r.getResponse().getContentAsString());
        return node.get("token").asText();
    }

    @Test
    void usersMe_withoutToken_isUnauthorized() throws Exception {
        mvc.perform(get("/users/me")).andExpect(status().isUnauthorized());
    }

    @Test
    void signup_hashesPassword_andDoesNotLeakIt() throws Exception {
        mvc.perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON)
                        .content(json("Test User", "a@test.com", "P@ssword123")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.password").doesNotExist());
        String stored = userRepository.findByEmail("a@test.com").orElseThrow().getPassword();
        assertTrue(stored.startsWith("$2"), "password phải là BCrypt hash");
    }

    @Test
    void signup_duplicate_isConflict() throws Exception {
        signup("dup@test.com");
        mvc.perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON)
                        .content(json("Test User", "dup@test.com", "P@ssword123")))
                .andExpect(status().isConflict());
    }

    @Test
    void signup_invalidInput_isBadRequest() throws Exception {
        mvc.perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON)
                        .content(json("Test User", "not-an-email", "P@ssword123")))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON)
                        .content(json("Test User", "b@test.com", "")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_badPassword_isUnauthorized() throws Exception {
        signup("c@test.com");
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"c@test.com\",\"password\":\"wrong-password\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void fullFlow_login_thenUsersMe_andUsers() throws Exception {
        signup("d@test.com");
        String token = login("d@test.com");
        mvc.perform(get("/users/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("d@test.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
        mvc.perform(get("/users").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].password").doesNotExist());
    }

    @Test
    void malformedAndTamperedToken_areUnauthorized() throws Exception {
        signup("e@test.com");
        String token = login("e@test.com");
        mvc.perform(get("/users/me").header("Authorization", "Bearer abc"))
                .andExpect(status().isUnauthorized());
        // đổi 1 ký tự ở phần signature (sửa ký tự cuối sang ký tự khác)
        char last = token.charAt(token.length() - 1);
        String tampered = token.substring(0, token.length() - 1) + (last == 'A' ? 'B' : 'A');
        mvc.perform(get("/users/me").header("Authorization", "Bearer " + tampered))
                .andExpect(status().isUnauthorized());
    }
}
